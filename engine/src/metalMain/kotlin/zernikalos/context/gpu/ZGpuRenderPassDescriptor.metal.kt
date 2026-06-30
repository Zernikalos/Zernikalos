/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Metal.*
import zernikalos.context.ZGpuRenderPassDescriptor
import zernikalos.context.ZLoadOp
import zernikalos.context.ZStoreOp

private fun ZLoadOp.toMetalLoadAction(): ULong = when (this) {
    ZLoadOp.Load -> MTLLoadActionLoad
    ZLoadOp.Clear -> MTLLoadActionClear
}

private fun ZStoreOp.toMetalStoreAction(): ULong = when (this) {
    ZStoreOp.Store -> MTLStoreActionStore
    ZStoreOp.Discard -> MTLStoreActionDontCare
}

@OptIn(ExperimentalForeignApi::class)
internal fun applyPassDescriptorToMetal(
    desc: ZGpuRenderPassDescriptor,
    renderPassDescriptor: MTLRenderPassDescriptor,
) {
    val colorDesc = desc.colorAttachments.firstOrNull() ?: return
    val colorAttachment = renderPassDescriptor.colorAttachments.objectAtIndexedSubscript(0u)
    colorAttachment.loadAction = colorDesc.loadOp.toMetalLoadAction()
    colorAttachment.storeAction = colorDesc.storeOp.toMetalStoreAction()
    colorAttachment.clearColor = MTLClearColorMake(
        colorDesc.clearValue.r.toDouble(),
        colorDesc.clearValue.g.toDouble(),
        colorDesc.clearValue.b.toDouble(),
        colorDesc.clearValue.a.toDouble(),
    )

    val depthDesc = desc.depthStencilAttachment ?: return
    val depthAttachment = renderPassDescriptor.depthAttachment
    depthAttachment.loadAction = depthDesc.depthLoadOp.toMetalLoadAction()
    depthAttachment.storeAction = depthDesc.depthStoreOp.toMetalStoreAction()
    depthAttachment.clearDepth = depthDesc.depthClearValue.toDouble()
}
