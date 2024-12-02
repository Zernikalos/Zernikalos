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

    val value: ZAlgebraObjectCollection by data::value

    override fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender? {
        return ZUniformBufferRenderer(ctx, data)
    }

    fun addUniform(uniform: ZUniform) {
        data.addUniform(uniform)
    }

}

data class ZUniformBufferData(
    val uniformBlockName: String,
    val uniforms: ArrayList<ZUniform>
): ZComponentData() {

    val count: Int = uniforms.size

    private var _value = ZAlgebraObjectCollection(requiredFloatSpace)

    private val requiredFloatSpace: Int
        get() = uniforms.sumOf { it.value.size }

    val value: ZAlgebraObjectCollection
        get() {
            _value.copyAll(uniforms.sortedBy { it.id }.map { it.value })
            return _value
        }

    init {
        uniforms.sortBy { it.id }
    }

    fun addUniform(uniform: ZUniform) {
        uniforms.add(uniform)
        uniforms.sortBy { it.id }
        _value = ZAlgebraObjectCollection(requiredFloatSpace)
    }
}

expect class ZUniformBufferRenderer(ctx: ZRenderingContext, data: ZUniformBufferData): ZComponentRender<ZUniformBufferData> {

    override fun initialize()

    override fun bind()

    override fun unbind()

    override fun render()

}