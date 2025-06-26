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

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.*

actual class ZUniformBlockRenderer actual constructor(ctx: ZRenderingContext, private val data: ZUniformBlockData): ZComponentRenderer(ctx) {

    var uniformBuffer: GPUBuffer? = null

    var bindGroupLayoutEntry: GPUBindGroupLayoutEntry? = null
    var bindGroupEntry: GPUBindGroupEntry? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        uniformBuffer = ctx.device.createBuffer(
            data.byteSize,
            GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST,
            false,
            "uniformBuffer-${data.uniformBlockName}"
        )

        // TODO: Review options
        bindGroupLayoutEntry = GPUBindGroupLayoutEntry(
            binding = data.id,
            visibility = GPUShaderStage.VERTEX,
            buffer = GPUBufferBindingLayout(
                type = GPUBufferBindingType.UNIFORM
            )
        )

        bindGroupEntry = GPUBindGroupEntry(
            binding = data.id,
            resource = GPUBindGroupResource(
                buffer = uniformBuffer!!
            )
        )
    }

    actual override fun bind() {
        ctx as ZWebGPURenderingContext
        ctx.queue.writeBuffer(uniformBuffer!!, 0, data.value.byteArray)
    }

    actual override fun unbind() {
    }
}
