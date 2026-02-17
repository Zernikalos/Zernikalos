/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.BufferTargetType
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger
import zernikalos.toOglBaseType

actual class ZBufferKeyRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferKeyData) : ZComponentRenderer(ctx) {

    private val bufferTargetType: BufferTargetType
        get() = if (data.isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        if (data.isIndexBuffer) {
            return
        }

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

        logger.debug("Initializing BufferKey ${data.name}=[@${data.id}]")
    }

    actual override fun bind() {
        // Binding is handled by ZBufferContentRenderer
    }

    actual override fun unbind() {
    }

}
