/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.Transient
import zernikalos.components.ZComponentRenderer
import zernikalos.context.DrawModes
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger
import zernikalos.toOglBaseType

actual class ZMeshRenderer
internal actual constructor(ctx: ZRenderingContext, internal val data: ZMeshData): ZComponentRenderer(ctx) {

    @Transient
    val vao: ZVertexArray = ZVertexArray()

    actual override fun initialize() {
        vao.initialize(ctx)

        data.buffers.values.filter {buff ->
            buff.enabled
        }.forEach { buff ->
            buff.initialize(ctx)
            buff.bind()
        }
    }

    actual override fun render() {
        ctx as ZGLRenderingContext

        val drawMode = convertDrawMode(data.drawMode)
        vao.bind()
        if (data.hasIndexBuffer) {
            logger.debugOnce("Using indexed buffer rendering")
            val indexBuffer = data.indexBuffer!!
            val count = indexBuffer.count
            ctx.drawElements(drawMode.value, count, toOglBaseType(indexBuffer.dataType), 0)
        } else {
            logger.debugOnce("Using vertices list rendering")
            // TODO: Fix this
            val count = data.buffers["position"]?.count!!
            ctx.drawArrays(drawMode.value, 0, count)
        }
        vao.unbind()
    }

}

fun convertDrawMode(drawMode: ZDrawMode): DrawModes = when (drawMode) {
        ZDrawMode.LINES -> DrawModes.LINES
        ZDrawMode.TRIANGLES -> DrawModes.TRIANGLES
        else -> DrawModes.TRIANGLES
    }
