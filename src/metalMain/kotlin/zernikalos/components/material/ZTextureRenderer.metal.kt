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
import platform.Metal.MTLStorageModePrivate
import platform.Metal.MTLTextureProtocol
import platform.Metal.MTLTextureUsageShaderRead
import platform.MetalKit.MTKTextureLoader
import platform.MetalKit.MTKTextureLoaderOptionTextureStorageMode
import platform.MetalKit.MTKTextureLoaderOptionTextureUsage
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZMtlRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, private val data: ZTextureData) : ZComponentRenderer(ctx) {

    // TODO: Implement all texture fields in order to create a https://developer.apple.com/documentation/metal/mtlsamplerdescriptor

    var texture: MTLTextureProtocol? = null

    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        //val descriptor = createTextureDescriptor()

//        texture = ctx.device.newTextureWithDescriptor(descriptor)
//
//        copyTextureData()
        createTexture()
    }

    override fun bind() {
        ctx as ZMtlRenderingContext

        ctx.renderEncoder?.setFragmentTexture(texture, 0u)
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

//    @OptIn(ExperimentalForeignApi::class)
//    private fun copyTextureData() {
//        val region = MTLRegionMake3D(0u,0u, 0u, data.width.toULong(), data.height.toULong(), 1u)
//        val bytesPerRow = 4 * data.width
//
//        data.dataArray.usePinned { pinned ->
//            texture?.replaceRegion(
//                region,
//                0u,
//                pinned.addressOf(0),
//                bytesPerRow.toULong()
//            )
//        }
//    }
//
//    private fun createTextureDescriptor(): MTLTextureDescriptor {
//        val textureDescriptor = MTLTextureDescriptor()
//
//        textureDescriptor.pixelFormat = MTLPixelFormatRGBA8Unorm_sRGB
//
//        textureDescriptor.width = data.width.toULong()
//        textureDescriptor.height = data.height.toULong()
//        return textureDescriptor
//    }

}