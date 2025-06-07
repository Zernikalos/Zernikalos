/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZRenderer
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderData): ZRenderer(ctx) {

    var shader: GLWrap = GLWrap()

    actual override fun initialize() {
    }

    actual fun initialize(source: ZShaderSource) {
        val type: ZGlShaderType = if (data.type == ZShaderType.VERTEX_SHADER) ZGlShaderType.VERTEX_SHADER else ZGlShaderType.FRAGMENT_SHADER
        val shad = createShader(ctx, type)
        // TODO: Take care with the cast since this breaks js
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(ctx, shad, source)
        checkShader(ctx, shad)

        shader = shad
    }

    private fun getSourceAsString(source: ZShaderSource): String {
        if (data.type == ZShaderType.VERTEX_SHADER) {
            return source.glslVertexShaderSource
        } else {
            return source.glslFragmentShaderSource
        }
    }

    private fun createShader(ctx: ZRenderingContext, shaderType: ZGlShaderType): GLWrap {
        ctx as ZGLRenderingContext

        return ctx.createShader(shaderType.value)
    }

    private fun compileShader(ctx: ZRenderingContext, shader: GLWrap, source: ZShaderSource) {
        ctx as ZGLRenderingContext

        val sourceStr = getSourceAsString(source)
        logger.debug("Compiling ${data.type} shader")
        logger.debug(sourceStr)

        ctx.shaderSource(shader, sourceStr)
        ctx.compileShader(shader)
    }

    private fun checkShader(ctx: ZRenderingContext, shader: GLWrap) {
        ctx as ZGLRenderingContext

        val compilerStatus = ctx.getShaderInfoLog(shader)
        val compilerError = ctx.getError()
        if (compilerStatus != "" || compilerError > 0) {
            ctx.deleteShader(shader)
            throw Error("Error compiling ${data.type} shader $compilerError : $compilerStatus")
        }
    }

}