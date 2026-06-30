/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import platform.Metal.*
import zernikalos.context.ZMtlRenderingContext
import zernikalos.components.mesh.ZDrawMode
import zernikalos.components.mesh.convertDrawMode
import zernikalos.components.shader.ZShaderType
import zernikalos.context.ZCullMode
import zernikalos.context.ZFrontFace

actual class ZGpuRenderPass internal constructor(
    private val encoder: MTLRenderCommandEncoderProtocol,
    private val depthState: MTLDepthStencilStateProtocol?,
    private val renderingContext: ZMtlRenderingContext? = null,
) {
    private var indexBuffer: MTLBufferProtocol? = null
    private var indexFormat: ULong = MTLIndexTypeUInt16

    actual fun applyPassState(state: ZGpuPassState) {
        encoder.setCullMode(state.cullMode.toMetal())
        encoder.setFrontFacingWinding(state.frontFace.toMetal())
        if (depthState != null) {
            encoder.setDepthStencilState(depthState)
        }
    }

    actual fun end() {
        encoder.popDebugGroup()
        encoder.endEncoding()
        renderingContext?.clearActivePass()
    }

    fun setPipeline(pipeline: MTLRenderPipelineStateProtocol) {
        encoder.setRenderPipelineState(pipeline)
    }

    fun setVertexBuffer(slot: Int, buffer: MTLBufferProtocol, offset: Long = 0) {
        encoder.setVertexBuffer(buffer, offset.toULong(), slot.toULong())
    }

    fun setIndexBuffer(buffer: MTLBufferProtocol, format: ULong = MTLIndexTypeUInt16) {
        indexBuffer = buffer
        indexFormat = format
    }

    fun drawIndexed(indexCount: Int, drawMode: ZDrawMode, firstIndex: Int = 0) {
        val buffer = indexBuffer ?: return
        encoder.drawIndexedPrimitives(
            convertDrawMode(drawMode),
            indexCount.toULong(),
            indexFormat,
            buffer,
            firstIndex.toULong()
        )
    }

    fun setUniformBuffer(stage: ZShaderType, slot: Int, buffer: MTLBufferProtocol, offset: Long = 0) {
        when (stage) {
            ZShaderType.VERTEX_SHADER -> encoder.setVertexBuffer(buffer, offset.toULong(), slot.toULong())
            ZShaderType.FRAGMENT_SHADER -> encoder.setFragmentBuffer(buffer, offset.toULong(), slot.toULong())
        }
    }

    fun setFragmentTexture(slot: Int, texture: MTLTextureProtocol, sampler: MTLSamplerStateProtocol) {
        encoder.setFragmentTexture(texture, slot.toULong())
        encoder.setFragmentSamplerState(sampler, slot.toULong())
    }
}

private fun ZCullMode.toMetal(): ULong = when (this) {
    ZCullMode.None -> MTLCullModeNone
    ZCullMode.Front -> MTLCullModeFront
    ZCullMode.Back -> MTLCullModeBack
}

private fun ZFrontFace.toMetal(): ULong = when (this) {
    ZFrontFace.CCW -> MTLWindingCounterClockwise
    ZFrontFace.CW -> MTLWindingClockwise
}
