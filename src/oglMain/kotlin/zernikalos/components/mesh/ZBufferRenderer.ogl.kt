/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.*
import zernikalos.logger.logger
import zernikalos.toOglBaseType

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {

    @Transient
    lateinit var buffer: GLWrap

    private val bufferTargetType: BufferTargetType
        get() = if (data.isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    actual override fun initialize() {
        initializeBuffer(ctx, data)
        if (!data.isIndexBuffer) {
            initializeBufferKey(ctx, data)
        }
        logger.debug("Initializing Buffer ${data.name}=[@${data.id}-${bufferTargetType.name}]")
    }

    actual override fun bind() {
        ctx as ZGLRenderingContext

        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind() {
    }

    private fun initializeBufferKey(ctx: ZRenderingContext, data: ZBufferData) {
        ctx as ZGLRenderingContext

        val glDataType = toOglBaseType(data.dataType)

        ctx.enableVertexAttrib(data.id)
        ctx.vertexAttribPointer(
            data.id,
            data.size,
            glDataType,
            data.normalized,
            data.stride,
            data.offset
        )
    }

    private fun initializeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        if (!data.hasData) {
            return
        }
        ctx as ZGLRenderingContext

        buffer = ctx.createBuffer()
        // TODO Check errors
        //        if (!data.buffer) {
        //            throw Error("Unable to create buffer")
        //        }

        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, data.dataArray, BufferUsageType.STATIC_DRAW)
    }

}
