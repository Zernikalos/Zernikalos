/*
 *
 *  * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package zernikalos.context

import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import zernikalos.context.webgpu.*
import zernikalos.ui.ZJsSurfaceView
import zernikalos.ui.ZSurfaceView
import kotlin.js.Promise

class ZWebGPURenderingContext(val surfaceView: ZSurfaceView): ZRenderingContext {
    var webGPUContext: GPUCanvasContext? = null
    private var nativeDevice: GPUDevice? = null
    private var nativeQueue: GPUQueue? = null
    private var swapChain: GPUSwapChain? = null
    private var depthTexture: GPUTexture? = null
    private var depthTextureView: GPUTextureView? = null
    var commandEncoder: GPUCommandEncoder? = null
    var renderPass: GPURenderPassEncoder? = null
    private var adapter: GPUAdapter? = null

    val device: ZWebGPUDevice
        get() = ZWebGPUDevice(nativeDevice!!)
    val queue: ZWebGPUQueue
        get() = ZWebGPUQueue(nativeQueue!!)

    init {
        initWithSurfaceView(surfaceView)
    }

    override fun initWithSurfaceView(surfaceView: ZSurfaceView) {
        surfaceView as ZJsSurfaceView
        val canvas = surfaceView.canvas
        webGPUContext = createWebGPUContext(canvas)

        requestAdapter().then { adapter: GPUAdapter? ->
            this.adapter = adapter
            requestDevice().then { gpuDevice ->
                if (gpuDevice == null) {
                    return@then
                }
                nativeDevice = gpuDevice
                nativeQueue = gpuDevice.queue

                val preferredFormat = getPreferredCanvasFormat()

                swapChain = webGPUContext?.configure(object : GPUCanvasConfiguration {
                    override var device: GPUDevice = nativeDevice!!
                    override var format = preferredFormat
                    //override var usage = GPUTextureUsage.RENDER_ATTACHMENT
                    override var alphaMode: String? = "premultiplied"
                    //override var viewFormats: Array<GPUTextureFormat>? = arrayOf()
                })
            }
        }
    }

    fun getPreferredCanvasFormat(): GPUTextureFormat {
        return window.navigator.gpu!!.getPreferredCanvasFormat()
    }

    private fun createWebGPUContext(canvas: HTMLCanvasElement): GPUCanvasContext? {
        val ctx = canvas.getContext("webgpu") as? GPUCanvasContext
        if (ctx == null) {
            throw RuntimeException("Failed to create WebGPU context")
        }
        return ctx
    }

    private fun requestAdapter(): Promise<GPUAdapter?> {
        return window.navigator.gpu!!.requestAdapter()
    }

    private fun requestDevice(): Promise<GPUDevice?> {
        return adapter!!.requestDevice()
    }

    fun createCommandEncoder(): GPUCommandEncoder? {
        this.commandEncoder = device.createCommandEncoder()
        return commandEncoder
    }

    fun createRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder? {
        this.renderPass = commandEncoder?.beginRenderPass(descriptor)
        return renderPass
    }
//
//    fun makeCommandBuffer(): ZCommandBuffer {
//        commandEncoder = device?.createCommandEncoder()
//        return ZWebGPUCommandBuffer(commandEncoder!!)
//    }
//
//    fun makeRenderCommandEncoder(descriptor: ZRenderPassDescriptor): ZRenderCommandEncoder {
//        val gpuDescriptor = descriptor.toGPU()
//        renderPassEncoder = commandEncoder?.beginRenderPass(gpuDescriptor)
//        return ZWebGPURenderCommandEncoder(renderPassEncoder!!)
//    }
//
//    fun getCurrentTextureView(): ZTextureView {
//        return ZWebGPUTextureView(swapChain?.getCurrentTexture()?.createView()!!)
//    }
//
//    fun resizeCanvas(width: Int, height: Int) {
//        swapChain?.configure(
//            device = device!!,
//            format = GPUTextureFormat.BGRA8Unorm,
//            usage = GPUTextureUsage.RENDER_ATTACHMENT,
//            size = GPUExtent3D(width = width, height = height)
//        )
//    }
//
//    actual override fun destroy() {
//        commandEncoder?.destroy()
//        renderPassEncoder?.destroy()
//        swapChain = null
//        device = null
//        queue = null
//    }
//
//        val canvas = surfaceView.canvas
//        webGPUContext = canvas.getContext("webgpu") as GPUCanvasContext?
//
//        window.navigator.gpu?.requestAdapter()?.then
//        {
//            adapter ->
//            adapter.requestDevice().then { device ->
//                this.device = device
//                queue = device.queue
//            }
//        }
//    }

// OLDEST CODE

//        // Configurar el swap chain
//        swapChain = webGPUContext!!.configureSwapChain(
//            device = device!!,
//            format = GPUTextureFormat.BGRA8Unorm,
//            usage = GPUTextureUsage.RENDER_ATTACHMENT,
//            size = GPUExtent3D(width = canvas.width, height = canvas.height)
//        )
//
//        // Crear textura de profundidad
//        depthTexture = device!!.createTexture(
//            GPUTextureDescriptor(
//                dimension = GPUTextureDimension._2D,
//                size = GPUExtent3D(width = canvas.width, height = canvas.height, depth = 1),
//                format = GPUTextureFormat.Depth24Plus,
//                usage = GPUTextureUsage.RENDER_ATTACHMENT
//            )
//        )
//        depthTextureView = depthTexture!!.createView()

//    fun makeCommandBuffer() {
//        commandEncoder = device!!.createCommandEncoder()
//    }
//
//    fun makeRenderCommandEncoder(renderPassDescriptor: GPURenderPassDescriptor) {
//        renderPassEncoder = commandEncoder!!.beginRenderPass(renderPassDescriptor)
//    }
//
//    fun submitCommands() {
//        renderPassEncoder!!.end()
//        val commandBuffer = commandEncoder!!.finish()
//        queue!!.submit(arrayOf(commandBuffer))
//    }
}

