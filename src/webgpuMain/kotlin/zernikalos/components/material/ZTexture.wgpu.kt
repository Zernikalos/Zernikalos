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
import zernikalos.context.webgpu.*

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

        val textureFormat = mapTextureFormat(data.format)

        texture = device.createTexture(
            GPUTextureDescriptor(
                size = GPUExtent3D(data.width, data.height, 1),
                format = textureFormat,
                usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.COPY_DST or GPUTextureUsage.RENDER_ATTACHMENT
            )
        )

        sampler = ctx.device.createSampler(
            GPUSamplerDescriptor(
                addressModeU = mapAddressMode(data.wrapModeU),
                addressModeV = mapAddressMode(data.wrapModeV),
                magFilter = mapFilterMode(data.magFilter),
                minFilter = mapFilterMode(data.minFilter)
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

private fun mapFilterMode(filter: ZTextureFilterMode): String {
    return when (filter) {
        ZTextureFilterMode.NEAREST -> GPUFilterMode.NEAREST
        ZTextureFilterMode.LINEAR -> GPUFilterMode.LINEAR
    }
}

private fun mapAddressMode(mode: ZTextureWrapMode): String {
    return when (mode) {
        ZTextureWrapMode.REPEAT -> GPUAddressMode.REPEAT
        ZTextureWrapMode.CLAMP_TO_EDGE -> GPUAddressMode.CLAMP_TO_EDGE
        ZTextureWrapMode.MIRROR_REPEAT -> GPUAddressMode.MIRROR_REPEAT
    }
}

private fun mapTextureFormat(format: ZTextureFormat): String {
    return when (format) {
        ZTextureFormat.RGBA8UNORM -> GPUTextureFormat.RGBA8Unorm
        ZTextureFormat.RGBA8UNORM_SRGB -> GPUTextureFormat.RGBA8UnormSRGB
        ZTextureFormat.BGRA8UNORM -> GPUTextureFormat.BGRA8Unorm
        ZTextureFormat.BGRA8UNORM_SRGB -> GPUTextureFormat.BGRA8UnormSRGB
        ZTextureFormat.R8UNORM -> GPUTextureFormat.R8Unorm
        ZTextureFormat.RG8UNORM -> GPUTextureFormat.RG8Unorm
        ZTextureFormat.RGB8UNORM -> GPUTextureFormat.RGBA8Unorm // Fallback, RGB8Unorm might not be available
    }
}
