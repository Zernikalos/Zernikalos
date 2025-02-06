/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.components.ZBaseComponentRender
import zernikalos.components.ZComponentRender
import zernikalos.context.DrawModes
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger
import zernikalos.toOglBaseType

actual class ZMeshRenderer
internal actual constructor(ctx: ZRenderingContext, internal val data: ZMeshData): ZBaseComponentRender(ctx) {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    actual override fun initialize() {
        // TODO: This change was required to make it work with WebGL
        data.indexBuffer?.initialize(ctx)
        vao.initialize(ctx)

        data.buffers.values.filter {buff ->
            // TODO: This change was required to make it work with WebGL
            buff.enabled && !buff.isIndexBuffer
        }.forEach { buff ->
            buff.initialize(ctx)
            buff.bind()
        }
    }

    actual override fun render() {
        ctx as ZGLRenderingContext

        vao.bind()
        if (data.hasIndexBuffer) {
            logger.debugOnce("Using indexed buffer rendering")
            // If we have the index buffer for sure this will not be null
            val indexBuffer = data.indexBuffer!!
            // TODO: This change was required to make it work with WebGL
            indexBuffer.bind()
            val count = indexBuffer.count
            // TODO: you don't need to draw triangles all the time
            ctx.drawElements(DrawModes.TRIANGLES.value, count, toOglBaseType(indexBuffer.dataType), 0)
        } else {
            logger.debugOnce("Using vertices list rendering")
            // TODO: Fix this
            val count = data.buffers["position"]?.count!!
            ctx.drawArrays(DrawModes.TRIANGLES.value, 0, count)
        }
        vao.unbind()
    }

}