/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import zernikalos.ZBaseType
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*
import zernikalos.logger.logger

actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZTextureData
) : ZComponentRenderer(ctx) {

    var texture: GPUTexture? = null
    var textureBindGroup: GPUBindGroup? = null
    var textureBindGroupLayout: GPUBindGroupLayout? = null
    var sampler: GPUSampler? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        val device = ctx.device

        logger.debug("[ZTextureRenderer] Initializing texture id=${data.id}")
        logger.debug("[ZTextureRenderer] data.width=${data.width}, data.height=${data.height}")

        val bitmap = ZBitmap(data.dataArray)

        val textureFormat = mapTextureFormat(data)

        if (data.width == 0 || data.height == 0) {
            logger.error("[ZTextureRenderer] ERROR: width or height is 0! Texture will fail.")
        }

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
            logger.debug("[ZTextureRenderer] Bitmap loaded: imageBitmap.width=${imageBitmap.width}, imageBitmap.height=${imageBitmap.height}")

            ctx.queue.copyExternalImageToTexture(
                GPUImageCopyExternalImage(imageBitmap),
                GPUImageCopyTexture(texture!!),
                GPUExtent3D(data.width, data.height, 1)
            )

            bitmap.dispose()
        }
    }

    fun createTextureBindGroup() {
        ctx as ZWebGPURenderingContext

        if (texture == null) return

        textureBindGroupLayout = ctx.device.createBindGroupLayout(
            GPUBindGroupLayoutDescriptor(
                label = "Texture BindGroupLayout",
                entries = arrayOf(
                    GPUBindGroupLayoutEntry(
                        binding = 0,
                        visibility = GPUShaderStage.FRAGMENT,
                        texture = GPUTextureBindingLayout()
                    ),
                    GPUBindGroupLayoutEntry(
                        binding = 1,
                        visibility = GPUShaderStage.FRAGMENT,
                        sampler = GPUSamplerBindingLayout()
                    )
                )
            ).toGpu()
        )
        // bindGroupLayouts.add(textureBindGroupLayout)

        textureBindGroup = ctx.device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = textureBindGroupLayout!!,
                label = "Texture BindGroup",
                entries = arrayOf(
                    GPUBindGroupEntry(
                        binding = 0,
                        resource = texture!!.createView()
                    ),
                    GPUBindGroupEntry(
                        binding = 1,
                        resource = sampler!!
                    )
                )
            ).toGpu()
        )
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

private fun mapTextureFormat(data: ZTextureData): String {
    return when {
        data.channels == ZTextureChannels.RGBA && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && data.colorSpace == ZTextureColorSpace.LINEAR ->
            GPUTextureFormat.RGBA8Unorm

        data.channels == ZTextureChannels.RGBA && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && data.colorSpace == ZTextureColorSpace.SRGB ->
            GPUTextureFormat.RGBA8UnormSRGB

        data.channels == ZTextureChannels.BGRA && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && data.colorSpace == ZTextureColorSpace.LINEAR ->
            GPUTextureFormat.BGRA8Unorm

        data.channels == ZTextureChannels.BGRA && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && data.colorSpace == ZTextureColorSpace.SRGB ->
            GPUTextureFormat.BGRA8UnormSRGB

        data.channels == ZTextureChannels.R && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized ->
            GPUTextureFormat.R8Unorm

        data.channels == ZTextureChannels.RG && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized ->
            GPUTextureFormat.RG8Unorm

        data.channels == ZTextureChannels.RGB && data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized ->
            GPUTextureFormat.RGBA8Unorm // Fallback, RGB8Unorm might not be available

        else -> GPUTextureFormat.RGBA8Unorm // Default fallback
    }
}
