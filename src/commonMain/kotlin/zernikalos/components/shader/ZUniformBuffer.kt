/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZBaseComponentRender
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.ZTemplateComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObjectCollection
import kotlin.js.JsExport

@JsExport
class ZUniformBuffer internal constructor(data: ZUniformBufferData): ZTemplateComponent<ZUniformBufferData, ZUniformBufferRenderer>(data) {

    val uniforms: List<ZUniform> by data::uniforms

    override fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender? {
        return ZUniformBufferRenderer(ctx, data)
    }

}

data class ZUniformBufferData(
    val uniformBlockName: String,
    val uniforms: List<ZUniform>
): ZComponentData() {

    val count: Int = uniforms.size

    val value: ZAlgebraObjectCollection
        get() {
            val requiredSpace = uniforms.sumOf { it.value.size }
            val collection = ZAlgebraObjectCollection(requiredSpace)
            collection.addAll(uniforms.map { it.value })
            return collection
        }
}

expect class ZUniformBufferRenderer(ctx: ZRenderingContext, data: ZUniformBufferData): ZComponentRender<ZUniformBufferData> {

    override fun initialize()

    override fun bind()

    override fun unbind()

    override fun render()

}