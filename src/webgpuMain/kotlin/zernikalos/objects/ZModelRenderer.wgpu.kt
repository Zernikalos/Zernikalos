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

package zernikalos.objects

import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*

actual class ZModelRenderer actual constructor(private val ctx: ZRenderingContext, private val model: ZModel) {

    var pipeline: GPURenderPipeline? = null
    var bindGroup: GPUBindGroup? = null
    var textureBindGroup: GPUBindGroup? = null

    actual fun initialize() {
        ctx as ZWebGPURenderingContext

        // Group 0: Uniforms
        val bindGroupLayoutEntries = model.shaderProgram.uniforms.blocks.asSequence().map { block ->
            block.renderer.bindGroupLayoutEntry!!
        }.toList()

        val bindGroupLayout = ctx.device.createBindGroupLayout(
            GPUBindGroupLayoutDescriptor(
                entries = bindGroupLayoutEntries.toTypedArray(),
                label = "Uniforms BindGroupLayout"
            ).toGpu()
        )

        val bindGroupEntries = model.shaderProgram.uniforms.blocks.asSequence().map { block ->
            block.renderer.bindGroupEntry!!
        }.toList()

        bindGroup = ctx.device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = bindGroupLayout,
                entries = bindGroupEntries.toTypedArray()
            ).toGpu()
        )

        // Group 1: Texture and Sampler
        val bindGroupLayouts = mutableListOf(bindGroupLayout)
        val texture = model.material?.texture

        if (texture != null) {
            val textureBindGroupLayout = ctx.device.createBindGroupLayout(
                GPUBindGroupLayoutDescriptor(
                    label = "Texture BindGroupLayout",
                    entries = arrayOf(
                        GPUBindGroupLayoutEntry(
                            binding = 0,
                            visibility = GPUShaderStage.FRAGMENT,
                            texture = GPUTextureBindingLayout()
                        ),
                        GPUBindGroupLayoutEntry(
                            binding = 1,
                            visibility = GPUShaderStage.FRAGMENT,
                            sampler = GPUSamplerBindingLayout()
                        )
                    )
                ).toGpu()
            )
            bindGroupLayouts.add(textureBindGroupLayout)

            textureBindGroup = ctx.device.createBindGroup(
                GPUBindGroupDescriptor(
                    layout = textureBindGroupLayout,
                    entries = arrayOf(
                        GPUBindGroupEntry(
                            binding = 0,
                            resource = texture.renderer.texture!!.createView()
                        ),
                        GPUBindGroupEntry(
                            binding = 1,
                            resource = texture.renderer.sampler!!
                        )
                    )
                ).toGpu()
            )
        }

        val renderPipelineDescriptor = GPURenderPipelineDescriptor(
            layout = ctx.device.createPipelineLayout(
                GPUPipelineLayoutDescriptor(
                    bindGroupLayouts = bindGroupLayouts.toTypedArray()
                )
            ),
            // TODO: hardcoded names
            vertex = GPUVertexState(
                module = model.shaderProgram.renderer.shaderModule!!,
                entryPoint = "vs_main",
                buffers = model.mesh.renderer.vertexBuffersLayout
            ),
            fragment = GPUFragmentState(
                module = model.shaderProgram.renderer.shaderModule!!,
                entryPoint = "fs_main",
                targets = arrayOf(
                    GPUColorTargetState(
                        format = ctx.getPreferredCanvasFormat().toString()
                    )
                )
            ),
            depthStencil = GPUDepthStencilState(
                format = GPUTextureFormat.Depth24Plus,
                depthWriteEnabled = true,
                depthCompare = GPUCompareFunction.LESS
            ),
            primitive = GPUPrimitiveState(
                cullMode = GPUCullMode.NONE,
                topology = GPUPrimitiveTopology.TRIANGLE_LIST
            )
        )

        pipeline = ctx.device.createRenderPipeline(renderPipelineDescriptor.toGpu())
    }

    actual fun render() {
        ctx as ZWebGPURenderingContext

        if (pipeline == null) {
            return
        }
        ctx.renderPass?.setPipeline(pipeline!!)
        // TODO: hardcoded bind group slots
        ctx.renderPass?.setBindGroup(0, bindGroup!!)
        if (textureBindGroup != null) {
            ctx.renderPass?.setBindGroup(1, textureBindGroup!!)
        }

        model.shaderProgram.bind()
        model.mesh.bind()
        model.mesh.render()
        ctx.renderPass?.end()
    }
}
