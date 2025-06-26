/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUExtent3D
import zernikalos.context.webgpu.GPUSampler
import zernikalos.context.webgpu.GPUSamplerDescriptor
import zernikalos.context.webgpu.GPUTexture
import zernikalos.context.webgpu.GPUTextureDescriptor
import zernikalos.context.webgpu.GPUTextureFormat
import zernikalos.context.webgpu.GPUTextureUsage
import zernikalos.context.webgpu.GPUAddressMode
import zernikalos.context.webgpu.GPUFilterMode
import zernikalos.context.webgpu.GPUImageCopyExternalImage
import zernikalos.context.webgpu.GPUImageCopyTexture

actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZTextureData
) : ZComponentRenderer(ctx) {

    var texture: GPUTexture? = null
    var sampler: GPUSampler? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        val device = ctx.device

        val bitmap = ZBitmap(data.dataArray)

        texture = device.createTexture(
            GPUTextureDescriptor(
                size = GPUExtent3D(data.width, data.height, 1),
                format = GPUTextureFormat.RGBA8Unorm,
                usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.COPY_DST or GPUTextureUsage.RENDER_ATTACHMENT
            )
        )

        sampler = ctx.device.createSampler(
            GPUSamplerDescriptor(
                addressModeU = GPUAddressMode.CLAMP_TO_EDGE,
                addressModeV = GPUAddressMode.CLAMP_TO_EDGE,
                magFilter = GPUFilterMode.LINEAR,
                minFilter = GPUFilterMode.LINEAR
            )
        )

        bitmap.isLoading.then {
            val imageBitmap = bitmap.imageBitmap!!

            ctx.queue.copyExternalImageToTexture(
                GPUImageCopyExternalImage(imageBitmap),
                GPUImageCopyTexture(texture!!),
                GPUExtent3D(data.width, data.height, 1)
            )

            bitmap.dispose()
        }
    }

    override fun bind() {}

    override fun unbind() {}

}
