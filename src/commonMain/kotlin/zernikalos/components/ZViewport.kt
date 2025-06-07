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
import zernikalos.context.ZRenderingContext
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
    ZRenderizableComponent<ZViewportData, ZViewportRenderer>(data),
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


}

@Serializable
data class ZViewportData(
    var clearColor: ZColor = ZColor(.2f, .2f, .2f, 1.0f),
//    var clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData() {
    var viewBox: ZBox2D = ZBox2D(0, 0, 0, 0)
}

expect class ZViewportRenderer(ctx: ZRenderingContext, data: ZViewportData): ZRenderer {
    override fun initialize()

    override fun render()

    fun onViewportResize(width: Int, height: Int)

}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData>() {
    override val kSerializer: KSerializer<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createComponentInstance(data: ZViewportData): ZViewport {
        return ZViewport(data)
    }

}
