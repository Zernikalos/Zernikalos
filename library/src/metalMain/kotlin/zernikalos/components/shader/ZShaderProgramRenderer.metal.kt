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
import platform.Metal.MTLCompileOptions
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLLibraryProtocol
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

        logger.debug("Shader Source in use:")
        logger.debug("\n${data.shaderSource.metalShaderSource}")

        memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            library = ctx.device.newLibraryWithSource(data.shaderSource.metalShaderSource, MTLCompileOptions(), errorVar.ptr)

            if (library == null) {
                val error = errorVar.value
                throw Error("Error creating the shader library: \n${error?.localizedDescription}")
            }
        }

        vertexShader = library?.newFunctionWithName("vertexShader")
            ?: throw Error("Vertex shader function not found")
        fragmentShader = library?.newFunctionWithName("fragmentShader")
            ?: throw Error("Fragment shader function not found")
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

