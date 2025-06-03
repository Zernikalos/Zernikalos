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
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZBox2D
import zernikalos.math.ZColor
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a viewport box for rendering objects in Zernikalos.
 */
@JsExport
@Serializable
abstract class ZBaseViewport: ZComponent2, ZRenderizable2, ZResizable2 {

    override val isRenderizable: Boolean = true


    /**
     * Represents the clear color used for rendering in a viewport.
     *
     * @property x The red component of the clear color.
     * @property y The green component of the clear color.
     * @property z The blue component of the clear color.
     * @property w The alpha component of the clear color.
     */
    @ProtoNumber(4)
    var clearColor: ZColor = ZColor(.2f, .2f, .2f, 1.0f)

    /**
     * Represents the view box used as viewport for rendering objects.
     * The view box defines the boundaries and dimensions of the viewport.
     *
     * @property top The top coordinate of the view box.
     * @property left The left coordinate of the view box.
     * @property width The width of the view box.
     * @property height The height of the view box.
     */
    var viewBox: ZBox2D = ZBox2D(0, 0, 0, 0)

    @JsName("initWithArgs")
    constructor(
        clearColor: ZColor,
        viewBox: ZBox2D
    ) {
        this.clearColor = clearColor
        this.viewBox = viewBox
    }

    @JsName("init")
    constructor(): this(ZColor(.2f, .2f, .2f, 1.0f), ZBox2D(0, 0, 0, 0))

    override fun onViewportResize(ctx: ZRenderingContext, width: Int, height: Int) {
        viewBox.width = width
        viewBox.height = height
    }

}

expect open class ZViewportRender: ZBaseViewport {
    constructor()
    constructor(clearColor: ZColor, viewBox: ZBox2D)

    override fun render(ctx: ZRenderingContext)
    override fun onViewportResize(ctx: ZRenderingContext, width: Int, height: Int)
}

@JsExport
@Serializable
class ZViewport: ZViewportRender {
    @JsName("init")
    constructor(): super()

    @JsName("initWithArgs")
    constructor(clearColor: ZColor, viewBox: ZBox2D): super(clearColor, viewBox)
}

