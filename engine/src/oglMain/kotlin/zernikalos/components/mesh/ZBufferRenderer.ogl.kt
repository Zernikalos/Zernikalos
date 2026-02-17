/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

actual class ZBufferRenderer actual constructor(ctx: ZRenderingContext, private val data: ZBufferData) : ZComponentRenderer(ctx) {

    actual override fun initialize() {
        initializeBufferContent(ctx)
        initializeBufferKey(ctx)
    }

    actual override fun bind() {
        data.content.bind()
    }

    actual override fun unbind() {
    }


    // TODO: Temporal code for future reference
    private fun initializeBufferContent(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        if (data.content.isInitialized) {
            return
        }
        data.content.initialize(ctx)
        data.content.renderer.initializeAs(data.isIndexBuffer)
    }

    private fun initializeBufferKey(ctx: ZRenderingContext) {
        ctx as ZGLRenderingContext

        bind()
        data.key.initialize(ctx)
    }

}
