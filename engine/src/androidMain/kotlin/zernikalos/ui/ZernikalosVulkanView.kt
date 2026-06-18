package zernikalos.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import zernikalos.events.ZEventQueue
import java.util.concurrent.atomic.AtomicBoolean

open class ZernikalosVulkanView: SurfaceView, SurfaceHolder.Callback, ZVulkanSurfaceView {

    private val touchEventConverter = AndroidTouchEventConverter()
    private val running = AtomicBoolean(false)
    private val renderLock = Any()

    private var renderThread: Thread? = null
    private var nativeHandle: Long = 0L
    private var currentSurface: Surface? = null
    private var readyNotified = false

    var onRendererStatusChanged: ((isReady: Boolean, message: String) -> Unit)? = null

    override val vulkanHandle: Long
        get() = nativeHandle

    override val surfaceWidth: Int
        get() = width

    override val surfaceHeight: Int
        get() = height

    override fun getVulkanLastError(): String =
        VulkanNativeBridge.getLastError(nativeHandle)

    override var eventHandler: ZSurfaceViewEventHandler? = null
        set(value) {
            field = value
            notifyReadyIfNeeded()
        }

    override var eventQueue: ZEventQueue? = null

    constructor(context: Context): super(context) {
        initializeSurface()
    }

    constructor(context: Context, attrSet: AttributeSet): super(context, attrSet) {
        initializeSurface()
    }

    private fun initializeSurface() {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        currentSurface = holder.surface
        if (width > 0 && height > 0) {
            createNativeRenderer(holder.surface, width, height)
        }
        notifyReadyIfNeeded()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        currentSurface = holder.surface
        if (nativeHandle == 0L) {
            createNativeRenderer(holder.surface, width, height)
        } else {
            VulkanNativeBridge.resize(nativeHandle, holder.surface, width, height)
        }
        notifyReadyIfNeeded()
        if (readyNotified) {
            eventHandler?.onResize(width, height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopRenderThread()
        disposeNativeRenderer()
        currentSurface = null
        readyNotified = false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        val queue = eventQueue
        if (queue != null) {
            val touchEvents = touchEventConverter.convert(event)
            for (touchEvent in touchEvents) {
                queue.enqueueTouch(touchEvent)
            }
            return true
        }

        return super.onTouchEvent(event)
    }

    override fun dispose() {
        touchEventConverter.clear()
        stopRenderThread()
        eventHandler?.dispose()
        eventHandler = null
        disposeNativeRenderer()
        holder.removeCallback(this)
    }

    override fun renderVulkanFrame() {
        val handle = nativeHandle
        if (handle != 0L) {
            VulkanNativeBridge.render(handle)
        }
    }

    override fun createVulkanBuffer(bytes: ByteArray, isIndexBuffer: Boolean): Long =
        VulkanNativeBridge.createBuffer(nativeHandle, bytes, isIndexBuffer)

    override fun disposeVulkanBuffer(bufferHandle: Long) {
        VulkanNativeBridge.disposeBuffer(nativeHandle, bufferHandle)
    }

    override fun createVulkanUniformBuffer(byteSize: Int): Long =
        VulkanNativeBridge.createUniformBuffer(nativeHandle, byteSize)

    override fun updateVulkanUniformBuffer(uniformBufferHandle: Long, bytes: ByteArray) {
        VulkanNativeBridge.updateUniformBuffer(nativeHandle, uniformBufferHandle, bytes)
    }

    override fun disposeVulkanUniformBuffer(uniformBufferHandle: Long) {
        VulkanNativeBridge.disposeUniformBuffer(nativeHandle, uniformBufferHandle)
    }

    override fun createVulkanPipeline(
        vertexShaderSource: String,
        fragmentShaderSource: String,
        vertexAttributes: IntArray,
        uniformBindings: IntArray,
        drawMode: Int,
    ): Long =
        VulkanNativeBridge.createPipeline(
            nativeHandle,
            vertexShaderSource,
            fragmentShaderSource,
            vertexAttributes,
            uniformBindings,
            drawMode,
        )

    override fun disposeVulkanPipeline(pipelineHandle: Long) {
        VulkanNativeBridge.disposePipeline(nativeHandle, pipelineHandle)
    }

    override fun createVulkanDescriptorSet(
        pipelineHandle: Long,
        uniformBindings: IntArray,
        uniformBufferHandles: LongArray,
    ): Long =
        VulkanNativeBridge.createDescriptorSet(nativeHandle, pipelineHandle, uniformBindings, uniformBufferHandles)

    override fun disposeVulkanDescriptorSet(descriptorSetHandle: Long) {
        VulkanNativeBridge.disposeDescriptorSet(nativeHandle, descriptorSetHandle)
    }

    override fun setVulkanClearColor(r: Float, g: Float, b: Float, a: Float) {
        VulkanNativeBridge.setClearColor(nativeHandle, r, g, b, a)
    }

    override fun setVulkanPipeline(pipelineHandle: Long) {
        VulkanNativeBridge.setPipeline(nativeHandle, pipelineHandle)
    }

    override fun bindVulkanDescriptorSet(descriptorSetHandle: Long) {
        VulkanNativeBridge.bindDescriptorSet(nativeHandle, descriptorSetHandle)
    }

    override fun bindVulkanUniformBuffer(binding: Int, uniformBufferHandle: Long) {
        VulkanNativeBridge.bindUniformBuffer(nativeHandle, binding, uniformBufferHandle)
    }

    override fun bindVulkanVertexBuffer(slot: Int, bufferHandle: Long) {
        VulkanNativeBridge.bindVertexBuffer(nativeHandle, slot, bufferHandle)
    }

    override fun bindVulkanIndexBuffer(bufferHandle: Long) {
        VulkanNativeBridge.bindIndexBuffer(nativeHandle, bufferHandle)
    }

    override fun drawVulkanIndexed(indexCount: Int) {
        VulkanNativeBridge.drawIndexed(nativeHandle, indexCount)
    }

    override fun createVulkanMesh(positions: ByteArray, vertexCount: Int, indices: ByteArray, indexCount: Int): Long =
        VulkanNativeBridge.createMesh(nativeHandle, positions, vertexCount, indices, indexCount)

    override fun drawVulkanMesh(meshHandle: Long) {
        VulkanNativeBridge.drawMesh(nativeHandle, meshHandle)
    }

    override fun disposeVulkanMesh(meshHandle: Long) {
        VulkanNativeBridge.disposeMesh(nativeHandle, meshHandle)
    }

    private fun createNativeRenderer(surface: Surface, width: Int, height: Int) {
        if (nativeHandle != 0L || width <= 0 || height <= 0) return
        if (!VulkanNativeBridge.isLoaded) {
            visibility = View.INVISIBLE
            onRendererStatusChanged?.invoke(false, "No se pudo cargar libzernikalos_vulkan.so")
            return
        }
        nativeHandle = VulkanNativeBridge.create(surface, width, height)
        if (nativeHandle == 0L) {
            visibility = View.INVISIBLE
            onRendererStatusChanged?.invoke(false, "Vulkan no esta disponible en este dispositivo/emulador")
        } else {
            visibility = View.VISIBLE
            onRendererStatusChanged?.invoke(true, "Vulkan renderer activo")
        }
    }

    private fun notifyReadyIfNeeded() {
        val handler = eventHandler ?: return
        if (readyNotified || nativeHandle == 0L) return
        readyNotified = true
        handler.onReady()
        startRenderThread()
    }

    private fun disposeNativeRenderer() {
        val handle = nativeHandle
        nativeHandle = 0L
        if (handle != 0L) {
            VulkanNativeBridge.dispose(handle)
        }
    }

    private fun startRenderThread() {
        if (!running.compareAndSet(false, true)) return
        renderThread = Thread {
            while (running.get()) {
                synchronized(renderLock) {
                    eventHandler?.onRender()
                }
                Thread.sleep(16L)
            }
        }.apply {
            name = "ZernikalosVulkanRenderThread"
            start()
        }
    }

    private fun stopRenderThread() {
        if (!running.compareAndSet(true, false)) return
        renderThread?.join(500L)
        renderThread = null
    }
}
