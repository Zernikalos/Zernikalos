 package zernikalos.context

import zernikalos.context.webgpu.*

/**
 * A wrapper class for GPUQueue to provide additional functionality and type-safe operations
 * in the Zernikalos WebGPU rendering context.
 *
 * @property queue The underlying native GPUQueue object
 */
class ZWebGPUQueue(val queue: GPUQueue) {
    /**
     * Submits the given command encoders to the GPU queue for execution.
     *
     * @param commandBuffers A list of GPUCommandEncoder to submit
     */
    fun submit(commandBuffers: Array<GPUCommandBuffer>) {
        queue.submit(commandBuffers)
    }

    /**
     * Writes buffer data to the GPU.
     *
     * @param destination The destination buffer
     * @param bufferSource The source data to write
     * @param bufferSize Optional size of the buffer
     */
    fun writeBuffer(
        buffer: GPUBuffer,
        offset: Int,
        dataArray: Any
    ) {
        queue.writeBuffer(buffer, offset, dataArray)
    }

    /**
     * Copies the underlying native GPUQueue object.
     *
     * @return The native GPUQueue object
     */
    fun getRawQueue(): GPUQueue = queue

    /**
     * Provides direct access to the underlying queue for advanced operations.
     *
     * @param block A lambda function that receives the native GPUQueue for custom operations
     * @return The result of the block
     */
    inline fun <R> withQueue(block: (GPUQueue) -> R): R {
        return block(queue)
    }
}
