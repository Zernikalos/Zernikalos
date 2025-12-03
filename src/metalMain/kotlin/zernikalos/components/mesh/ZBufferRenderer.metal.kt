/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLVertexAttributeDescriptor
import platform.Metal.MTLVertexBufferLayoutDescriptor
import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {

    val attributeDescriptor: MTLVertexAttributeDescriptor
        get() = data.key.renderer.attributeDescriptor

    val layoutDescriptor: MTLVertexBufferLayoutDescriptor
        get() = data.key.renderer.layoutDescriptor

    val buffer: MTLBufferProtocol?
        get() = data.content.renderer.buffer

    actual override fun initialize() {
        if (data.isIndexBuffer) {
            initialzeBuffer(ctx, data)
        } else {
            initializeBufferKey(data)
            initialzeBuffer(ctx, data)
        }
    }

    actual override fun bind() {
        data.content.bind()
    }

    actual override fun unbind() {
    }

    private fun initialzeBuffer(ctx: ZRenderingContext, data: ZBufferData) {
        if (data.content.isInitialized) {
            return
        }
        data.content.initialize(ctx)
    }

    private fun initializeBufferKey(data: ZBufferData) {
        data.key.initialize(ctx)
    }

}
