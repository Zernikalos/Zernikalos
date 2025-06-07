/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZBaseComponentRender
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZAttributeRenderer actual constructor(ctx: ZRenderingContext, private val data: ZAttributeData): ZBaseComponentRender(ctx) {
    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext

        logger.debug("Binding shader attribute ${data.attributeName} to layout ${data.id} to program ${programId.id}")
        ctx.bindAttribLocation(programId, data.id, data.attributeName)
    }

}