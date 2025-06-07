/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.*

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, private val data: ZViewportData): ZBaseComponentRender(ctx) {
    var clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value

    actual override fun initialize() {
        ctx as ZGLRenderingContext
        // TODO: This hardcoded code NEEDS to be changed asap
//        ctx.viewport(0, 0, 1080, 1934)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    actual override fun render() {
        ctx as ZGLRenderingContext
        val v = data.clearColor
        ctx.clearColor(v.r, v.g, v.b, v.a)
        ctx.clear(clearMask)
    }

    actual fun onViewportResize(width: Int, height: Int) {
        ctx as ZGLRenderingContext
        ctx.viewport(
            data.viewBox.top,
            data.viewBox.left,
            data.viewBox.width,
            data.viewBox.height
        )
    }

}