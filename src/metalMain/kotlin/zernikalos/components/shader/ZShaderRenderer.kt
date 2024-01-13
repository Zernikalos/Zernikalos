package zernikalos.components.shader

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import platform.Foundation.NSError
import platform.Metal.MTLCompileOptions
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLLibraryProtocol
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.context.ZMtlRenderingContext

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderData) : ZComponentRender<ZShaderData>(ctx, data) {

    lateinit var library: MTLLibraryProtocol
    lateinit var vertexShader: MTLFunctionProtocol
    lateinit var fragmentShader: MTLFunctionProtocol

    @OptIn(ExperimentalForeignApi::class)
    actual override fun initialize() {
        ctx as ZMtlRenderingContext
        var err: CPointer<ObjCObjectVar<NSError?>>? = null

        library = ctx.device.newLibraryWithSource(shaderSource, MTLCompileOptions(), err)!!

        vertexShader = library.newFunctionWithName("vertexShader")!!
        fragmentShader = library.newFunctionWithName("fragmentShader")!!
    }

}