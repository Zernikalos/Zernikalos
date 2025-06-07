/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import zernikalos.components.ZRenderer
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZTextureData
) : ZRenderer(ctx) {

    lateinit var textureHandler: GLWrap

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        val bitmap = ZBitmap(data.dataArray)

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMin()
        ctx.texParameterMag()

        // Main difference, in JS you need to fully load the img
        bitmap.isLoading.then {
            ctx.texImage2D(bitmap)

            bitmap.dispose()
        }
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    override fun unbind() {
    }

}