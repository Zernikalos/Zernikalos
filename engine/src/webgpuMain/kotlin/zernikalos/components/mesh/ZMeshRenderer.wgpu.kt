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

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZWebGPURenderingContext
import zernikalos.context.webgpu.GPUVertexBufferLayout
import zernikalos.context.gpu.ZGpuRenderPass

actual class ZMeshRenderer actual constructor(ctx: ZRenderingContext, private val data: ZMeshData): ZComponentRenderer(ctx) {

    var vertexBuffersLayout: Array<GPUVertexBufferLayout?> = arrayOf()

    val enabledVertexBuffers: List<ZBuffer>
        get() = data.buffers.values.filter { buff -> buff.enabled && !buff.isIndexBuffer }

    actual override fun initialize() {
        ctx as ZWebGPURenderingContext

        data.buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buff ->
            buff.initialize(ctx)
        }

        vertexBuffersLayout = buildVertexBuffersLayout()
    }

    private fun buildVertexBuffersLayout(): Array<GPUVertexBufferLayout?> {
        // Determine the highest attribute index to size the array correctly
        val maxIndex = enabledVertexBuffers.maxOf { it.attributeId.id }
        val attributes: Array<GPUVertexBufferLayout?> = arrayOfNulls(maxIndex + 1)

        // Populate the array so that attributes[i] corresponds to attributeId == i
        enabledVertexBuffers.forEach { buff ->
            attributes[buff.attributeId.id] = buff.renderer.createLayout()
        }

        return attributes
    }

    override fun bind() {
        enabledVertexBuffers
            .sortedBy { it.id } // Use the same consistent order
            .forEach{ buff -> // Use index for the slot
                buff.bind()
            }
        val indices = data.indexBuffer!!
        indices.renderer.bindIndexBuffer()
    }

    actual override fun render() {
        val pass = ctx.activePass ?: return
        val indices = data.indexBuffer!!
        pass.drawIndexed(indices.count)
    }

    actual override fun dispose() {
        data.buffers.values.filter { it.enabled }.forEach { it.dispose() }
    }
}
