/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.CullModeType
import zernikalos.context.Enabler
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, data: ZViewportData): ZComponentRender<ZViewportData>(ctx, data) {
    actual override fun initialize() {
        ctx as ZGLRenderingContext
        // TODO: This hardcoded code NEEDS to be changed asap
        ctx.viewport(0, 0, 1080, 1934)
        ctx.enable(Enabler.DEPTH_TEST.value)
        ctx.cullFace(CullModeType.FRONT.value)
    }

    actual override fun render() {
        ctx as ZGLRenderingContext
        val v = data.clearColor
        ctx.clearColor(v.x, v.y, v.z, v.w)
        ctx.clear(data.clearMask)
    }

}