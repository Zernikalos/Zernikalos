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
import zernikalos.context.webgpu.GPUShaderModule
import zernikalos.logger.logger

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderProgramData): ZComponentRenderer(ctx) {

    var shaderModule: GPUShaderModule? = null

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        logger.info("Initializing shader program...")
        shaderModule = ctx.device.createShaderModule(data.shaderSource.wgpuShaderSource)
        logger.debug(data.shaderSource.wgpuShaderSource)

        // TODO: Only blocks are being considered
        data.uniforms.blocks.forEach { block ->
            block.initialize(ctx)
        }
    }

    actual override fun bind() {
        data.uniforms.blocks.forEach { block ->
            block.bind()
        }
    }

    actual override fun unbind() {
    }
}
