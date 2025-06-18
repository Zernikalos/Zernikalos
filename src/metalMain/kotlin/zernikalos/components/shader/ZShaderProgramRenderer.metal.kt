/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.Metal.*
import platform.posix.memcpy
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderProgramData) : ZComponentRenderer(ctx) {

    var library: MTLLibraryProtocol? = null
    lateinit var vertexShader: MTLFunctionProtocol
    lateinit var fragmentShader: MTLFunctionProtocol

    actual override fun initialize() {
        initializeShader()
        initializeUniformBuffer()
    }

    private fun initializeUniformBuffer() {
        ctx as ZMtlRenderingContext

        data.uniforms.blocks.forEach { uniform ->
            logger.debug("Initializing uniformBlock: ${uniform.uniformName}")

            uniform.initialize(ctx)
        }

    }

    @OptIn(ExperimentalForeignApi::class)
    private fun initializeShader() {
        ctx as ZMtlRenderingContext

        val err: CPointer<ObjCObjectVar<NSError?>>? = null

        logger.debug("Shader Source in use:")
        logger.debug("\n${data.shaderSource.metalShaderSource}")

        try {
            library = ctx.device.newLibraryWithSource(data.shaderSource.metalShaderSource, MTLCompileOptions(), err)!!
        } catch (_: Error) {
            throw Error("Error creating the shader library")
        }

        if (library == null) {
            throw Error("Error creating the shader library")
        }

        vertexShader = library?.newFunctionWithName("vertexShader")!!
        fragmentShader = library?.newFunctionWithName("fragmentShader")!!
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun bind() {
        ctx as ZMtlRenderingContext

        data.uniforms.blocks.forEach { uniformBlock ->
            uniformBlock.bind()
        }
    }

    actual override fun unbind() {
    }
}

