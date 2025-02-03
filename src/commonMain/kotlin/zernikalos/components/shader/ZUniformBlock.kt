/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZDataType
import zernikalos.components.ZBaseComponentRender
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.ZTemplateComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZAlgebraObjectCollection
import kotlin.js.JsExport

@JsExport
interface IZUniform {
    /**
     * Represents the unique identifier for a `ZUniform` instance.
     * This ID is used to differentiate between different uniform components
     */
    var id: Int

    /**
     * This is the name within the shader source code
     */
    var uniformName: String

    /**
     * How many elements of this will be used
     */
    var count: Int

    /**
     * The datatype of all individual elements used by this uniform
     */
    var dataType: ZDataType

    var value: ZAlgebraObject
}

@JsExport
class ZUniformBlock internal constructor(data: ZUniformBlockData): ZTemplateComponent<ZUniformBlockData, ZUniformBlockRenderer>(data) {

    val uniforms: List<IZUniform> by data::uniforms

    val value: ZAlgebraObjectCollection by data::value

    override fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender? {
        return ZUniformBlockRenderer(ctx, data)
    }

    fun addUniform(uniform: IZUniform) {
        data.addUniform(uniform)
    }

}

data class ZUniformBlockData(
    val uniformBlockName: String,
    val uniforms: ArrayList<IZUniform>
): ZComponentData() {

    val count: Int = uniforms.size

    private val requiredFloatSpace: Int
        get() = uniforms.sumOf { it.value.size }

    private var _value = ZAlgebraObjectCollection(requiredFloatSpace)

    val value: ZAlgebraObjectCollection
        get() {
            _value.copyAll(uniforms.sortedBy { it.id }.map { it.value })
            return _value
        }

    init {
        uniforms.sortBy { it.id }
    }

    fun addUniform(uniform: IZUniform) {
        uniforms.add(uniform)
        uniforms.sortBy { it.id }
        _value = ZAlgebraObjectCollection(requiredFloatSpace)
    }
}

expect class ZUniformBlockRenderer(ctx: ZRenderingContext, data: ZUniformBlockData): ZComponentRender<ZUniformBlockData> {

    override fun initialize()

    override fun bind()

    override fun unbind()

    override fun render()

}