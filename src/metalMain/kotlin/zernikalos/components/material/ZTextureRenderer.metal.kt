/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.Metal.*
import platform.MetalKit.MTKTextureLoader
import platform.MetalKit.MTKTextureLoaderOptionTextureStorageMode
import platform.MetalKit.MTKTextureLoaderOptionTextureUsage
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, private val data: ZTextureData) : ZComponentRenderer(ctx) {

    var texture: MTLTextureProtocol? = null
    var samplerState: MTLSamplerStateProtocol? = null

    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        createTexture()
        createSampler()
    }

    override fun bind() {
        ctx as ZMtlRenderingContext

        ctx.renderEncoder?.setFragmentTexture(texture, 0u)
        ctx.renderEncoder?.setFragmentSamplerState(samplerState, 0u)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun createTexture() {
        ctx as ZMtlRenderingContext
        val textureLoader = MTKTextureLoader(ctx.device)

        val err: CPointer<ObjCObjectVar<NSError?>>? = null

        val options: Map<Any?, *> = mapOf(MTKTextureLoaderOptionTextureUsage to MTLTextureUsageShaderRead,
            MTKTextureLoaderOptionTextureStorageMode to MTLStorageModePrivate)

        data.dataArray.usePinned { pinned ->
            val textureData = NSData.create(bytes = pinned.addressOf(0), data.dataArray.size.toULong())
            texture = textureLoader.newTextureWithData(textureData, options, err)
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun createSampler() {
        ctx as ZMtlRenderingContext

        val descriptor = MTLSamplerDescriptor()
        descriptor.minFilter = mapFilterMode(data.minFilter)
        descriptor.magFilter = mapFilterMode(data.magFilter)
        descriptor.sAddressMode = mapAddressMode(data.wrapModeU)
        descriptor.tAddressMode = mapAddressMode(data.wrapModeV)

        samplerState = ctx.device.newSamplerStateWithDescriptor(descriptor)
    }

}

private fun mapFilterMode(filter: ZTextureFilterMode): ULong {
    return when (filter) {
        ZTextureFilterMode.NEAREST -> MTLSamplerMinMagFilterNearest
        ZTextureFilterMode.LINEAR -> MTLSamplerMinMagFilterLinear
    }
}

private fun mapAddressMode(mode: ZTextureWrapMode): ULong {
    return when (mode) {
        ZTextureWrapMode.REPEAT -> MTLSamplerAddressModeRepeat
        ZTextureWrapMode.CLAMP_TO_EDGE -> MTLSamplerAddressModeClampToEdge
        ZTextureWrapMode.MIRROR_REPEAT -> MTLSamplerAddressModeMirrorRepeat
    }
}
