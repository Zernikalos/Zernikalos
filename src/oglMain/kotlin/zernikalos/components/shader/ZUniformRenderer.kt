/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZTypes
import zernikalos.components.ZComponentRender
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import zernikalos.logger.logger

actual class ZUniformRenderer actual constructor(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData>(ctx, data) {

    lateinit var uniformId: GLWrap

    actual override fun initialize() {
    }

    fun bindLocation(programId: GLWrap) {
        ctx as ZGLRenderingContext
        uniformId = ctx.getUniformLocation(programId, data.uniformName)
        logger.debug("Binding ${data.uniformName} to uniformId ${uniformId}")
    }

    actual fun bindValue(shaderProgram: ZShaderProgram, values: FloatArray) {
        ctx as ZGLRenderingContext
        when (data.dataType) {
            ZTypes.MAT4F -> ctx.uniformMatrix4fv(uniformId, data.count, false, values)
            else -> return
        }
    }
}
