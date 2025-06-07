/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZBaseComponentRender
import zernikalos.context.*
import zernikalos.math.ZAlgebraObjectCollection

actual class ZUniformBlockRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData
) : ZBaseComponentRender(ctx) {

    private var ubo: GLWrap? = null

    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext

        val uniformBlockIndex = ctx.getUniformBlockIndex(programId, data.uniformBlockName)

        ubo = ctx.createBuffer()

        bind()
        ctx.bindBuffer(BufferTargetType.UNIFORM_BUFFER, ubo!!)

        val algObj = data.value as ZAlgebraObjectCollection
        ctx.bufferData(BufferTargetType.UNIFORM_BUFFER, algObj.byteSize, BufferUsageType.DYNAMIC_DRAW)
        ctx.bindBufferBase(BufferTargetType.UNIFORM_BUFFER, uniformBlockIndex, ubo!!)
    }

    actual override fun bind() {
        ctx as ZGLRenderingContext

        val algObj = data.value as ZAlgebraObjectCollection
        ctx.bindBuffer(BufferTargetType.UNIFORM_BUFFER, ubo!!)
        ctx.bufferData(BufferTargetType.UNIFORM_BUFFER, algObj.byteArray, BufferUsageType.DYNAMIC_DRAW)
    }

    actual override fun unbind() {
    }

    actual override fun render() {
    }

}