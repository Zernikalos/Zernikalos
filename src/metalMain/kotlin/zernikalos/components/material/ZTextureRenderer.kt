package zernikalos.components.material

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.Metal.*
import platform.MetalKit.MTKTextureLoader
import platform.MetalKit.MTKTextureLoaderOptionTextureStorageMode
import platform.MetalKit.MTKTextureLoaderOptionTextureUsage
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.context.ZMtlRenderingContext

actual class ZTextureRenderer actual constructor(ctx: ZRenderingContext, data: ZTextureData) : ZComponentRender<ZTextureData>(ctx, data) {

    var texture: MTLTextureProtocol? = null

    actual override fun initialize() {
        ctx as ZMtlRenderingContext

        val descriptor = createTextureDescriptor()

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

        var err: CPointer<ObjCObjectVar<NSError?>>? = null

        val options: Map<Any?, *> = mapOf(MTKTextureLoaderOptionTextureUsage to MTLTextureUsageShaderRead,
            MTKTextureLoaderOptionTextureStorageMode to MTLStorageModePrivate)

        data.dataArray.usePinned { pinned ->
            val textureData = NSData.create(bytes = pinned.addressOf(0), data.dataArray.size.toULong())
            texture = textureLoader.newTextureWithData(textureData, options, err)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun copyTextureData() {
        val region = MTLRegionMake3D(0u,0u, 0u, data.width.toULong(), data.height.toULong(), 1u)
        val bytesPerRow = 4 * data.width

        data.dataArray.usePinned { pinned ->
            texture?.replaceRegion(
                region,
                0u,
                pinned.addressOf(0),
                bytesPerRow.toULong()
            )
        }
    }

    private fun createTextureDescriptor(): MTLTextureDescriptor {
        val textureDescriptor = MTLTextureDescriptor()

        textureDescriptor.pixelFormat = MTLPixelFormatRGBA8Unorm_sRGB

        textureDescriptor.width = data.width.toULong()
        textureDescriptor.height = data.height.toULong()
        return textureDescriptor
    }

}