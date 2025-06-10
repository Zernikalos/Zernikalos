/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.Metal.MTLRenderPipelineDescriptor
import platform.Metal.MTLRenderPipelineStateProtocol
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZModelRenderer actual constructor(
    val ctx: ZRenderingContext,
    val model: ZModel
) {

    private var pipelineState: MTLRenderPipelineStateProtocol? = null

    actual fun initialize() {
        pipelineState = createPipelineDescriptor()
    }

    actual fun render() {
        ctx as ZMtlRenderingContext

        ctx.renderEncoder?.setRenderPipelineState(pipelineState!!)

        model.shaderProgram.bind()
        model.material?.bind()

        model.mesh.bind()
        model.mesh.render()
        model.mesh.unbind()

        model.material?.unbind()
        model.shaderProgram.unbind()
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun createPipelineDescriptor(): MTLRenderPipelineStateProtocol? {
        ctx as ZMtlRenderingContext

        val mtkView = ctx.surfaceView.nativeView

        val pipelineDescriptor = MTLRenderPipelineDescriptor()
        pipelineDescriptor.label = "ZernikalosRenderPipeline"
        pipelineDescriptor.rasterSampleCount = mtkView.sampleCount
        pipelineDescriptor.vertexFunction = model.shaderProgram.renderer.vertexShader
        pipelineDescriptor.fragmentFunction = model.shaderProgram.renderer.fragmentShader
        pipelineDescriptor.vertexDescriptor = model.mesh.renderer.vertexDescriptor

        pipelineDescriptor.colorAttachments.objectAtIndexedSubscript(0.toULong()).pixelFormat = mtkView.colorPixelFormat
        pipelineDescriptor.depthAttachmentPixelFormat = mtkView.depthStencilPixelFormat
        pipelineDescriptor.stencilAttachmentPixelFormat = mtkView.depthStencilPixelFormat

        memScoped {
            val err = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
            val pipelineState = ctx.device.newRenderPipelineStateWithDescriptor(pipelineDescriptor, err.ptr)

            if (err.value != null) {
                println("Error: ${err.value?.localizedDescription}")
                nativeHeap.free(err)
                return null
            }

            nativeHeap.free(err)
            return pipelineState
        }
    }

}