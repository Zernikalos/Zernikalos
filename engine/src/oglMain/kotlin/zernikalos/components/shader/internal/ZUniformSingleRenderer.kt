/*
 * Copyright (c) 2024-2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader.internal

import zernikalos.ZTypes
import zernikalos.components.shader.ZUniformBlockData
import zernikalos.components.shader.ZUniformData
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

internal class ZUniformSingleRenderer(
    private val ctx: ZRenderingContext,
    private val data: ZUniformBlockData
): ZUniformInternalRenderer {

    private lateinit var _uniformId: GLWrap
    private val singleData: ZUniformData = data.uniforms.values.first()

    fun setUniformId(value: GLWrap) {
        _uniformId = value
    }

    override fun initialize() {
    }

    override fun bind() {
        ctx as ZGLRenderingContext
        if (!_uniformId.isValid) {
            return
        }
        when (singleData.dataType) {
            ZTypes.MAT4F -> ctx.uniformMatrix4fv(_uniformId, singleData.count, false, singleData.value.floatArray)
            else -> return
        }
    }

    override fun unbind() {
    }
}
