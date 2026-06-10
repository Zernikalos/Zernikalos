/*
 * Copyright (c) 2024-2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import zernikalos.context.ZRenderingContext
import zernikalos.ui.ZSurfaceView

class ZComponentLifecycleTest {

    @Test
    fun dataRenderComponentCanBeDisposedBeforeInitialization() {
        val counters = LifecycleCounters()
        val component = TestDataRenderComponent(counters)

        component.dispose()
        component.dispose()
        component.initialize(TestRenderingContext)

        assertTrue(component.isDisposed)
        assertFalse(component.isInitialized)
        assertEquals(1, counters.componentDisposals)
        assertEquals(0, counters.rendererInitializations)
        assertEquals(0, counters.rendererDisposals)
        assertFailsWith<Error> { component.renderer }
    }

    @Test
    fun renderizableComponentCanBeDisposedBeforeInitialization() {
        val counters = LifecycleCounters()
        val component = TestRenderizableComponent(counters)

        component.dispose()
        component.initialize(TestRenderingContext)

        assertTrue(component.isDisposed)
        assertFalse(component.isInitialized)
        assertEquals(1, counters.componentDisposals)
        assertEquals(0, counters.rendererInitializations)
        assertEquals(0, counters.rendererDisposals)
    }

    @Test
    fun initializedRendererIsDisposedOnlyOnce() {
        val counters = LifecycleCounters()
        val component = TestDataRenderComponent(counters)

        component.initialize(TestRenderingContext)
        component.dispose()
        component.dispose()

        assertTrue(component.isInitialized)
        assertTrue(component.isDisposed)
        assertEquals(1, counters.rendererInitializations)
        assertEquals(1, counters.componentDisposals)
        assertEquals(1, counters.rendererDisposals)
        assertFailsWith<Error> { component.renderer }
    }

    @Test
    fun rendererIsDisposedWhenComponentCleanupFails() {
        val counters = LifecycleCounters()
        val component = TestDataRenderComponent(counters, failOnDispose = true)

        component.initialize(TestRenderingContext)

        assertFailsWith<IllegalStateException> { component.dispose() }
        assertTrue(component.isDisposed)
        assertEquals(1, counters.componentDisposals)
        assertEquals(1, counters.rendererDisposals)
    }

    @Test
    fun partiallyInitializedRendererCanStillBeDisposed() {
        val counters = LifecycleCounters()
        val component = TestDataRenderComponent(counters, failOnRendererInitialize = true)

        assertFailsWith<IllegalStateException> {
            component.initialize(TestRenderingContext)
        }
        component.dispose()

        assertTrue(component.isInitialized)
        assertTrue(component.isDisposed)
        assertEquals(1, counters.rendererInitializations)
        assertEquals(1, counters.rendererDisposals)
    }
}

private data class LifecycleCounters(
    var rendererInitializations: Int = 0,
    var rendererDisposals: Int = 0,
    var componentDisposals: Int = 0,
)

private object TestRenderingContext : ZRenderingContext {
    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {}
}

private class TestComponentRenderer(
    ctx: ZRenderingContext,
    private val counters: LifecycleCounters,
    private val failOnInitialize: Boolean = false,
) : ZComponentRenderer(ctx) {

    override fun initialize() {
        counters.rendererInitializations++
        if (failOnInitialize) {
            throw IllegalStateException("Renderer initialization failed")
        }
    }

    override fun dispose() {
        counters.rendererDisposals++
    }
}

private class TestRenderizableComponent(
    private val counters: LifecycleCounters,
) : ZRenderizableComponent<TestComponentRenderer>() {

    override fun createRenderer(ctx: ZRenderingContext): TestComponentRenderer {
        return TestComponentRenderer(ctx, counters)
    }

    override fun internalDispose() {
        counters.componentDisposals++
    }
}

private class TestDataRenderComponent(
    private val counters: LifecycleCounters,
    private val failOnDispose: Boolean = false,
    private val failOnRendererInitialize: Boolean = false,
) : ZDataRenderComponent<TestComponentData, TestComponentRenderer>(TestComponentData()) {

    override fun createRenderer(ctx: ZRenderingContext): TestComponentRenderer {
        return TestComponentRenderer(ctx, counters, failOnRendererInitialize)
    }

    override fun internalDispose() {
        counters.componentDisposals++
        if (failOnDispose) {
            throw IllegalStateException("Component cleanup failed")
        }
    }
}

private class TestComponentData : ZComponentData() {
    override fun toString(): String = "TestComponentData"
}
