package zernikalos

import platform.Metal.*
import platform.MetalKit.MTKView
import zernikalos.ui.ZSurfaceView

class ZMtlRenderingContext constructor(val surfaceView: ZSurfaceView): ZRenderingContext {

    val device: MTLDeviceProtocol
    val commandQueue: MTLCommandQueueProtocol

    var commandBuffer: MTLCommandBufferProtocol? = null
    var renderEncoder: MTLRenderCommandEncoderProtocol? = null

    init {
        device = surfaceView.nativeView.device!!
        commandQueue = device.newCommandQueue()!!    }

    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {

    }

    fun makeCommandBuffer() {
        commandBuffer = commandQueue.commandBuffer()
    }

    fun makeRenderCommandEncoder(renderPassDescriptor: MTLRenderPassDescriptor) {
        renderEncoder = commandBuffer?.renderCommandEncoderWithDescriptor(renderPassDescriptor)
    }

}

actual object ExpectEnabler {
    actual val DEPTH_TEST: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectTypes {
    actual val BYTE: Int
        get() = 1
    actual val UNSIGNED_BYTE: Int
        get() = 1
    actual val INT: Int
        get() = 1
    actual val UNSIGNED_INT: Int
        get() = 1
    actual val SHORT: Int
        get() = 1
    actual val UNSIGNED_SHORT: Int
        get() = 1
    actual val FLOAT: Int
        get() = 1
    actual val DOUBLE: Int
        get() = 1
}

actual object ExpectBufferBit {
    actual val COLOR_BUFFER: Int
        get() = 1
    actual val DEPTH_BUFFER: Int
        get() = 1
}

actual object ExpectShaderType {
    actual val VERTEX_SHADER: Int
        get() = TODO("Not yet implemented")
    actual val FRAGMENT_SHADER: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectBufferTargetType {
    actual val ARRAY_BUFFER: Int
        get() = TODO("Not yet implemented")
    actual val ELEMENT_ARRAY_BUFFER: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectBufferUsageType {
    actual val STATIC_DRAW: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectDrawModes {
    actual val TRIANGLES: Int
        get() = TODO("Not yet implemented")
    actual val LINES: Int
        get() = TODO("Not yet implemented")
}

actual object ExpectCullModeType {
    actual val FRONT: Int
        get() = TODO("Not yet implemented")
    actual val BACK: Int
        get() = TODO("Not yet implemented")
    actual val FRONT_AND_BACK: Int
        get() = TODO("Not yet implemented")
}