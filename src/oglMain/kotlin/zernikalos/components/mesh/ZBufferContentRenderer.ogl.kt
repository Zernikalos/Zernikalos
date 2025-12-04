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
import zernikalos.context.*
import zernikalos.logger.logger

actual class ZBufferContentRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferContentData) : ZComponentRenderer(ctx) {

    @Transient
    lateinit var buffer: GLWrap

    private var isIndexBuffer: Boolean = false

    private val bufferTargetType: BufferTargetType
        get() = if (isIndexBuffer) BufferTargetType.ELEMENT_ARRAY_BUFFER else BufferTargetType.ARRAY_BUFFER

    fun initializeAs(isIndexBuffer: Boolean) {
        ctx as ZGLRenderingContext

        this.isIndexBuffer = isIndexBuffer

        if (data.dataArray.isEmpty()) {
            return
        }

        buffer = ctx.createBuffer()
        ctx.bindBuffer(bufferTargetType, buffer)
        ctx.bufferData(bufferTargetType, data.dataArray, BufferUsageType.STATIC_DRAW)

        logger.debug("Initializing BufferContent ${data.id}=[@${data.id}-${bufferTargetType.name}]")
    }

    actual override fun initialize() {

    }

    actual override fun bind() {
        ctx as ZGLRenderingContext
        ctx.bindBuffer(bufferTargetType, buffer)
    }

    actual override fun unbind() {
    }

}
