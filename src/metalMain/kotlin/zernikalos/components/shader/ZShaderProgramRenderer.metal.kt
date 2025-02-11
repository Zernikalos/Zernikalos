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
import zernikalos.components.ZComponentRender
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderProgramData) : ZComponentRender<ZShaderProgramData>(ctx, data) {

    var uniformBuffer: MTLBufferProtocol? = null

    var library: MTLLibraryProtocol? = null
    lateinit var vertexShader: MTLFunctionProtocol
    lateinit var fragmentShader: MTLFunctionProtocol

    private val uniformsSize: Int
        get() = data.uniforms.singles.asSequence().fold(0) { acc, zUniform -> acc + zUniform.dataType.byteSize }

    actual override fun initialize() {
        initializeShader()
        initializeUniformBuffer()
    }

    private fun initializeUniformBuffer() {
        ctx as ZMtlRenderingContext

        data.uniforms.singles.forEach { uniform ->
            uniform.initialize(ctx)
        }

        uniformBuffer = ctx.device.newBufferWithLength(uniformsSize.toULong(), MTLResourceStorageModeShared)
        uniformBuffer?.label = "UniformBuffer"
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

        var offset: Long = 0
        data.uniforms.singles.forEach { uniform ->
            // Memory pointer where to copy the content from uniform pinned data
            val contentPointer = uniformBuffer?.contents().rawValue + offset
            uniform.value?.floatArray?.usePinned { pinned ->
                // Dest, Src and how many bytes
                memcpy(
                    interpretCPointer<CPointed>(contentPointer),
                    pinned.addressOf(0),
                    uniform.dataType.byteSize.toULong()
                )
            }
            offset += uniform.dataType.byteSize
        }

        // TODO: This location is still hardcoded
        ctx.renderEncoder?.setVertexBuffer(uniformBuffer, 0u, 7u)
        ctx.renderEncoder?.setFragmentBuffer(uniformBuffer, 0u, 7u)
    }

    actual override fun unbind() {
    }
}

