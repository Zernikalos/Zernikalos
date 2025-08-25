/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.*
import zernikalos.math.ZAlgebraObjectCollection

actual class ZUniformBlockRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData
) : ZComponentRenderer(ctx) {

    private var ubo: GLWrap? = null

    private var bindingPoint: Int = -1

    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext

        val uniformBlockIndex = ctx.getUniformBlockIndex(programId, data.uniformBlockName)

        if (!uniformBlockIndex.isValid || uniformBlockIndex.id == -1) {
            throw Error("Uniform block index is not valid")
        }
        ubo = ctx.createBuffer()

        ctx.bindBuffer(BufferTargetType.UNIFORM_BUFFER, ubo!!)
        ctx.bufferData(BufferTargetType.UNIFORM_BUFFER, data.byteSize, BufferUsageType.DYNAMIC_DRAW)

        // Problem: OpenGL binding points are global across all shader programs.
        // When multiple shaders use the same binding point, the last binding overwrites
        // previous ones, causing uniform data corruption and rendering artifacts.
        //
        // Solution: Generate unique binding points using XOR hash for good distribution,
        // then resolve any collisions with linear probing to ensure no conflicts.
        val programIdAsInt = programId.id as Int
        bindingPoint = (programIdAsInt xor data.id) % ctx.maxUniformBuffersBinding
        while (ZUniformBlockRenderer.isBindingPointUsed(bindingPoint)) {
            bindingPoint = (bindingPoint + 1) % ctx.maxUniformBuffersBinding
        }
        ZUniformBlockRenderer.addBindingPoint(bindingPoint)

        ctx.uniformBlockBinding(programId, uniformBlockIndex, bindingPoint)
        ctx.bindBufferBase(BufferTargetType.UNIFORM_BUFFER, bindingPoint, ubo!!)
    }

    actual override fun bind() {
        ctx as ZGLRenderingContext

        val algObj = data.value as ZAlgebraObjectCollection
        ctx.bindBuffer(BufferTargetType.UNIFORM_BUFFER, ubo!!)
        ctx.bufferData(BufferTargetType.UNIFORM_BUFFER, algObj.byteArray, BufferUsageType.DYNAMIC_DRAW)
    }

    actual override fun unbind() {
        ctx as ZGLRenderingContext
        //ctx?.bindBufferBase(BufferTargetType.UNIFORM_BUFFER, bindingPoint, 0)
    }

    companion object {
        private val usedBindingPoints = mutableSetOf<Int>()

        fun isBindingPointUsed(bindingPoint: Int): Boolean {
            return usedBindingPoints.contains(bindingPoint)
        }

        fun addBindingPoint(bindingPoint: Int) {
            usedBindingPoints.add(bindingPoint)
        }
    }
}
