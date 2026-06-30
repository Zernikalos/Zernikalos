/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import zernikalos.context.ZGpuColorAttachmentDesc
import zernikalos.context.ZGpuDepthStencilAttachmentDesc
import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZLoadOp
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZStoreOp
import zernikalos.math.ZBox2D
import zernikalos.math.ZColor
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a viewport box for rendering objects in Zernikalos.
 */
@JsExport
@Serializable(with = ZViewportSerializer::class)
class ZViewport
internal constructor(data: ZViewportData):
    ZDataRenderComponent<ZViewportData, ZViewportRenderer>(data),
    ZRenderizable, ZResizable {

    @JsName("init")
    constructor(): this(ZViewportData())

    /**
     * Represents the clear color used for rendering in a viewport.
     *
     * @property x The red component of the clear color.
     * @property y The green component of the clear color.
     * @property z The blue component of the clear color.
     * @property w The alpha component of the clear color.
     */
    var clearColor: ZColor by data::clearColor

    /**
     * Represents the view box used as viewport for rendering objects.
     * The view box defines the boundaries and dimensions of the viewport.
     *
     * @property top The top coordinate of the view box.
     * @property left The left coordinate of the view box.
     * @property width The width of the view box.
     * @property height The height of the view box.
     */
    var viewBox: ZBox2D by data::viewBox

    override fun createRenderer(ctx: ZRenderingContext): ZViewportRenderer {
        return ZViewportRenderer(ctx, data)
    }

    override fun onViewportResize(width: Int, height: Int) {
        data.viewBox.width = width
        data.viewBox.height = height
        renderer.onViewportResize(width, height)
    }

    /**
     * Per-frame GPU setup for scene-graph consumers. On OpenGL this clears the framebuffer.
     * On WebGPU this is a no-op; the renderer calls [buildRenderPassDescriptor] before the pass.
     */
    override fun render() = renderer.render()

    /**
     * Builds or refreshes the GPU render-pass descriptor for this viewport.
     * Returns null when attachments are unavailable (e.g. zero-sized view box).
     *
     * This is GPU preparation only (attachments, clear values) — not scene traversal.
     */
    fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor? = renderer.buildRenderPassDescriptor()

}

internal fun buildSwapchainPassDescriptor(clearColor: ZColor): ZGpuRenderPassDescriptor =
    ZGpuRenderPassDescriptor(
        label = "Main viewport pass",
        colorAttachments = listOf(
            ZGpuColorAttachmentDesc(
                loadOp = ZLoadOp.Clear,
                storeOp = ZStoreOp.Store,
                clearValue = clearColor,
            )
        ),
        depthStencilAttachment = ZGpuDepthStencilAttachmentDesc(
            depthLoadOp = ZLoadOp.Clear,
            depthStoreOp = ZStoreOp.Store,
            depthClearValue = 1.0f,
        ),
    )

@Serializable
data class ZViewportData(
    /** Default framebuffer clear (linear RGB, opaque). */
    var clearColor: ZColor = ZColor(.2f, .2f, .2f, 1.0f),
//    var clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData() {
    var viewBox: ZBox2D = ZBox2D(0, 0, 0, 0)
}

expect class ZViewportRenderer(ctx: ZRenderingContext, data: ZViewportData): ZComponentRenderer {
    override fun initialize()

    /** Per-frame GPU setup: no-op on WebGPU and Metal; glClear on OpenGL. */
    override fun render()

    /** Explicit render-pass descriptor build. WebGPU: attachments + clear; Metal: clear policy; OGL: null. */
    fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?

    fun onViewportResize(width: Int, height: Int)
}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData>() {
    override val kSerializer: KSerializer<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createComponentInstance(data: ZViewportData): ZViewport {
        return ZViewport(data)
    }

}
