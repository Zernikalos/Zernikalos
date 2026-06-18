/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZVulkanRenderingContext

actual class ZBufferRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZBufferData,
): ZComponentRenderer(ctx) {
    var nativeBufferHandle: Long = 0L
        private set

    private val vulkanContext: ZVulkanRenderingContext?
        get() = ctx as? ZVulkanRenderingContext

    actual override fun initialize() {
        data.key.initialize(ctx)
        data.content.initialize(ctx)
        if (data.hasData) {
            nativeBufferHandle = vulkanContext?.createNativeBuffer(data.dataArray, data.isIndexBuffer) ?: 0L
        }
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

    actual override fun dispose() {
        if (nativeBufferHandle != 0L) {
            vulkanContext?.disposeNativeBuffer(nativeBufferHandle)
            nativeBufferHandle = 0L
        }
        data.key.dispose()
        data.content.dispose()
    }
}
