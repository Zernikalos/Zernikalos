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
import zernikalos.context.gpu.ZGpuRenderPass
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
    private var nativeRenderPass: GPURenderPassEncoder? = null
    private var adapter: GPUAdapter? = null

    override var activePass: ZGpuRenderPass? = null

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

    override fun <R> withActivePass(pass: ZGpuRenderPass, block: () -> R): R {
        val previous = activePass
        activePass = pass
        try {
            return block()
        } finally {
            activePass = previous
        }
    }

    internal fun createCommandEncoder(): GPUCommandEncoder? {
        this.commandEncoder = device.createCommandEncoder()
        return commandEncoder
    }

    internal fun beginRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder? {
        nativeRenderPass = commandEncoder?.beginRenderPass(descriptor)
        return nativeRenderPass
    }

    internal fun clearActivePass() {
        activePass = null
        nativeRenderPass = null
    }
}

