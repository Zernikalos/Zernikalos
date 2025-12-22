/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import zernikalos.components.ZComponentRenderer
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.context.ExpectTextureFilter
import zernikalos.context.ExpectTextureWrap

actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZTextureData
) : ZComponentRenderer(ctx) {

    lateinit var textureHandler: GLWrap

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        val bitmap = ZBitmap(data.dataArray)

        textureHandler = ctx.genTexture()

        ctx.bindTexture(textureHandler)

        ctx.texParameterMinFilter(mapFilterMode(data.minFilter))
        ctx.texParameterMagFilter(mapFilterMode(data.magFilter))
        ctx.texParameterWrapS(mapWrapMode(data.wrapModeU))
        ctx.texParameterWrapT(mapWrapMode(data.wrapModeV))

        ctx.texImage2D(bitmap)

        bitmap.dispose()
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.activeTexture()
        ctx.bindTexture(textureHandler)
    }

    override fun unbind() {
    }

}

private fun mapFilterMode(filter: ZTextureFilterMode): Int {
    return when (filter) {
        ZTextureFilterMode.NEAREST -> ExpectTextureFilter.NEAREST
        ZTextureFilterMode.LINEAR -> ExpectTextureFilter.LINEAR
    }
}

private fun mapWrapMode(mode: ZTextureWrapMode): Int {
    return when (mode) {
        ZTextureWrapMode.REPEAT -> ExpectTextureWrap.REPEAT
        ZTextureWrapMode.CLAMP_TO_EDGE -> ExpectTextureWrap.CLAMP_TO_EDGE
        ZTextureWrapMode.MIRROR_REPEAT -> ExpectTextureWrap.MIRRORED_REPEAT
    }
}