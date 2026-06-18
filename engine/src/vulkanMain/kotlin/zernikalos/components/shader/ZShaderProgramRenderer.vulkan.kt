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

actual class ZShaderProgramRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZShaderProgramData,
): ZComponentRenderer(ctx) {
    actual override fun initialize() {
        data.vertexShader.initialize(ctx)
        data.fragmentShader.initialize(ctx)
        data.attributes.values.forEach { it.initialize(ctx) }
        data.uniforms.all.forEach { it.initialize(ctx) }
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

    actual override fun dispose() {
        data.vertexShader.dispose()
        data.fragmentShader.dispose()
        data.uniforms.all.forEach { it.dispose() }
    }
}
