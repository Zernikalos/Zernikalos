/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZVulkanRenderingContext

actual class ZUniformRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZUniformBlockData,
): ZComponentRenderer(ctx) {
    var nativeUniformBufferHandle: Long = 0L
        private set

    private val alignedByteSize: Int
        get() {
            val alignment = 16
            return ((data.byteSize + alignment - 1) / alignment) * alignment
        }

    private val alignedData: ByteArray
        get() = data.value.byteArray.let { bytes ->
            if (bytes.size % 16 == 0) bytes else bytes.copyOf(alignedByteSize)
        }

    private val vulkanContext: ZVulkanRenderingContext?
        get() = ctx as? ZVulkanRenderingContext

    actual override fun initialize() {
        nativeUniformBufferHandle = vulkanContext?.createNativeUniformBuffer(alignedByteSize) ?: 0L
    }

    override fun bind() {
        update()
    }

    fun update() {
        val handle = nativeUniformBufferHandle
        if (handle == 0L) return

        vulkanContext?.updateNativeUniformBuffer(handle, alignedData)
    }

    override fun unbind() {
    }

    actual override fun dispose() {
        if (nativeUniformBufferHandle != 0L) {
            vulkanContext?.disposeNativeUniformBuffer(nativeUniformBufferHandle)
            nativeUniformBufferHandle = 0L
        }
    }
}
