/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZLoadOp
import zernikalos.context.ZStoreOp
import zernikalos.context.webgpu.GPUColor
import zernikalos.context.webgpu.GPULoadOp
import zernikalos.context.webgpu.GPURenderPassColorAttachment
import zernikalos.context.webgpu.GPURenderPassDepthStencilAttachment
import zernikalos.context.webgpu.GPURenderPassDescriptor
import zernikalos.context.webgpu.GPUStoreOp

private fun ZLoadOp.toWebGpu(): String = when (this) {
    ZLoadOp.Load -> GPULoadOp.LOAD
    ZLoadOp.Clear -> GPULoadOp.CLEAR
}

private fun ZStoreOp.toWebGpu(): String = when (this) {
    ZStoreOp.Store -> GPUStoreOp.STORE
    ZStoreOp.Discard -> GPUStoreOp.DISCARD
}

internal fun encodeWebGPURenderPassDescriptor(
    desc: ZGpuRenderPassDescriptor,
    colorAttachment: GPURenderPassColorAttachment,
    depthAttachment: GPURenderPassDepthStencilAttachment,
): GPURenderPassDescriptor {
    val colorDesc = desc.colorAttachments.first()
    colorAttachment.loadOp = colorDesc.loadOp.toWebGpu()
    colorAttachment.storeOp = colorDesc.storeOp.toWebGpu()
    colorAttachment.clearValue = GPUColor(
        colorDesc.clearValue.r,
        colorDesc.clearValue.g,
        colorDesc.clearValue.b,
        colorDesc.clearValue.a,
    )

    val depthDesc = desc.depthStencilAttachment
    if (depthDesc != null) {
        depthAttachment.depthLoadOp = depthDesc.depthLoadOp.toWebGpu()
        depthAttachment.depthStoreOp = depthDesc.depthStoreOp.toWebGpu()
        depthAttachment.depthClearValue = depthDesc.depthClearValue
    }

    return GPURenderPassDescriptor(
        colorAttachments = arrayOf(colorAttachment),
        depthStencilAttachment = depthAttachment,
    )
}
