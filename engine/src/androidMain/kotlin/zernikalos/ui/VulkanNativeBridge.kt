package zernikalos.ui

import android.view.Surface

internal object VulkanNativeBridge {
    val isLoaded: Boolean

    init {
        isLoaded = try {
            System.loadLibrary("zernikalos_vulkan")
            true
        } catch (_: UnsatisfiedLinkError) {
            false
        }
    }

    fun create(surface: Surface, width: Int, height: Int): Long {
        if (!isLoaded) return 0L
        return nativeCreate(surface, width, height)
    }

    fun resize(handle: Long, surface: Surface, width: Int, height: Int) {
        if (!isLoaded || handle == 0L) return
        nativeResize(handle, surface, width, height)
    }

    fun render(handle: Long) {
        if (!isLoaded || handle == 0L) return
        nativeRender(handle)
    }

    fun dispose(handle: Long) {
        if (!isLoaded || handle == 0L) return
        nativeDispose(handle)
    }

    fun getLastError(rendererHandle: Long): String {
        if (!isLoaded || rendererHandle == 0L) return ""
        return nativeGetLastError(rendererHandle)
    }

    fun createBuffer(rendererHandle: Long, bytes: ByteArray, isIndexBuffer: Boolean): Long {
        if (!isLoaded || rendererHandle == 0L || bytes.isEmpty()) return 0L
        return nativeCreateBuffer(rendererHandle, bytes, isIndexBuffer)
    }

    fun disposeBuffer(rendererHandle: Long, bufferHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || bufferHandle == 0L) return
        nativeDisposeBuffer(rendererHandle, bufferHandle)
    }

    fun createUniformBuffer(rendererHandle: Long, byteSize: Int): Long {
        if (!isLoaded || rendererHandle == 0L || byteSize <= 0) return 0L
        return nativeCreateUniformBuffer(rendererHandle, byteSize)
    }

    fun updateUniformBuffer(rendererHandle: Long, uniformBufferHandle: Long, bytes: ByteArray) {
        if (!isLoaded || rendererHandle == 0L || uniformBufferHandle == 0L || bytes.isEmpty()) return
        nativeUpdateUniformBuffer(rendererHandle, uniformBufferHandle, bytes)
    }

    fun disposeUniformBuffer(rendererHandle: Long, uniformBufferHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || uniformBufferHandle == 0L) return
        nativeDisposeUniformBuffer(rendererHandle, uniformBufferHandle)
    }

    fun createPipeline(
        rendererHandle: Long,
        vertexShaderSource: String,
        fragmentShaderSource: String,
        vertexAttributes: IntArray,
        uniformBindings: IntArray,
        drawMode: Int,
    ): Long {
        if (
            !isLoaded ||
            rendererHandle == 0L ||
            vertexShaderSource.isBlank() ||
            fragmentShaderSource.isBlank() ||
            vertexAttributes.isEmpty()
        ) {
            return 0L
        }
        return nativeCreatePipeline(
            rendererHandle,
            vertexShaderSource,
            fragmentShaderSource,
            vertexAttributes,
            uniformBindings,
            drawMode,
        )
    }

    fun disposePipeline(rendererHandle: Long, pipelineHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || pipelineHandle == 0L) return
        nativeDisposePipeline(rendererHandle, pipelineHandle)
    }

    fun createDescriptorSet(
        rendererHandle: Long,
        pipelineHandle: Long,
        uniformBindings: IntArray,
        uniformBufferHandles: LongArray,
    ): Long {
        if (
            !isLoaded ||
            rendererHandle == 0L ||
            pipelineHandle == 0L ||
            uniformBindings.size != uniformBufferHandles.size
        ) {
            return 0L
        }
        return nativeCreateDescriptorSet(rendererHandle, pipelineHandle, uniformBindings, uniformBufferHandles)
    }

    fun disposeDescriptorSet(rendererHandle: Long, descriptorSetHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || descriptorSetHandle == 0L) return
        nativeDisposeDescriptorSet(rendererHandle, descriptorSetHandle)
    }

    fun setClearColor(rendererHandle: Long, r: Float, g: Float, b: Float, a: Float) {
        if (!isLoaded || rendererHandle == 0L) return
        nativeSetClearColor(rendererHandle, r, g, b, a)
    }

    fun setPipeline(rendererHandle: Long, pipelineHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || pipelineHandle == 0L) return
        nativeSetPipeline(rendererHandle, pipelineHandle)
    }

    fun bindDescriptorSet(rendererHandle: Long, descriptorSetHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || descriptorSetHandle == 0L) return
        nativeBindDescriptorSet(rendererHandle, descriptorSetHandle)
    }

    fun bindUniformBuffer(rendererHandle: Long, binding: Int, uniformBufferHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || uniformBufferHandle == 0L) return
        nativeBindUniformBuffer(rendererHandle, binding, uniformBufferHandle)
    }

    fun bindVertexBuffer(rendererHandle: Long, slot: Int, bufferHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || bufferHandle == 0L) return
        nativeBindVertexBuffer(rendererHandle, slot, bufferHandle)
    }

    fun bindIndexBuffer(rendererHandle: Long, bufferHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || bufferHandle == 0L) return
        nativeBindIndexBuffer(rendererHandle, bufferHandle)
    }

    fun drawIndexed(rendererHandle: Long, indexCount: Int) {
        if (!isLoaded || rendererHandle == 0L || indexCount <= 0) return
        nativeDrawIndexed(rendererHandle, indexCount)
    }

    fun createMesh(
        rendererHandle: Long,
        positions: ByteArray,
        vertexCount: Int,
        indices: ByteArray,
        indexCount: Int,
    ): Long {
        if (!isLoaded || rendererHandle == 0L) return 0L
        return nativeCreateMesh(rendererHandle, positions, vertexCount, indices, indexCount)
    }

    fun drawMesh(rendererHandle: Long, meshHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || meshHandle == 0L) return
        nativeDrawMesh(rendererHandle, meshHandle)
    }

    fun disposeMesh(rendererHandle: Long, meshHandle: Long) {
        if (!isLoaded || rendererHandle == 0L || meshHandle == 0L) return
        nativeDisposeMesh(rendererHandle, meshHandle)
    }

    private external fun nativeCreate(surface: Surface, width: Int, height: Int): Long
    private external fun nativeResize(handle: Long, surface: Surface, width: Int, height: Int)
    private external fun nativeRender(handle: Long)
    private external fun nativeDispose(handle: Long)
    private external fun nativeGetLastError(rendererHandle: Long): String
    private external fun nativeCreateBuffer(rendererHandle: Long, bytes: ByteArray, isIndexBuffer: Boolean): Long
    private external fun nativeDisposeBuffer(rendererHandle: Long, bufferHandle: Long)
    private external fun nativeCreateUniformBuffer(rendererHandle: Long, byteSize: Int): Long
    private external fun nativeUpdateUniformBuffer(rendererHandle: Long, uniformBufferHandle: Long, bytes: ByteArray)
    private external fun nativeDisposeUniformBuffer(rendererHandle: Long, uniformBufferHandle: Long)
    private external fun nativeCreatePipeline(
        rendererHandle: Long,
        vertexShaderSource: String,
        fragmentShaderSource: String,
        vertexAttributes: IntArray,
        uniformBindings: IntArray,
        drawMode: Int,
    ): Long
    private external fun nativeDisposePipeline(rendererHandle: Long, pipelineHandle: Long)
    private external fun nativeCreateDescriptorSet(
        rendererHandle: Long,
        pipelineHandle: Long,
        uniformBindings: IntArray,
        uniformBufferHandles: LongArray,
    ): Long
    private external fun nativeDisposeDescriptorSet(rendererHandle: Long, descriptorSetHandle: Long)
    private external fun nativeSetClearColor(rendererHandle: Long, r: Float, g: Float, b: Float, a: Float)
    private external fun nativeSetPipeline(rendererHandle: Long, pipelineHandle: Long)
    private external fun nativeBindDescriptorSet(rendererHandle: Long, descriptorSetHandle: Long)
    private external fun nativeBindUniformBuffer(rendererHandle: Long, binding: Int, uniformBufferHandle: Long)
    private external fun nativeBindVertexBuffer(rendererHandle: Long, slot: Int, bufferHandle: Long)
    private external fun nativeBindIndexBuffer(rendererHandle: Long, bufferHandle: Long)
    private external fun nativeDrawIndexed(rendererHandle: Long, indexCount: Int)
    private external fun nativeCreateMesh(
        rendererHandle: Long,
        positions: ByteArray,
        vertexCount: Int,
        indices: ByteArray,
        indexCount: Int,
    ): Long
    private external fun nativeDrawMesh(rendererHandle: Long, meshHandle: Long)
    private external fun nativeDisposeMesh(rendererHandle: Long, meshHandle: Long)
}
