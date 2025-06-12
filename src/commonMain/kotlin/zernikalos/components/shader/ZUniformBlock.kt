/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZComponentData
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZAlgebraObjectCollection
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZUniformBlock internal constructor(private val data: ZUniformBlockData):
    ZRenderizableComponent<ZUniformBlockRenderer>(), ZBindeable, ZBaseUniform {

    @JsName("initWithName")
    constructor(uniformBlockName: String): this(ZUniformBlockData(uniformBlockName))

    val uniforms: MutableMap<String, ZUniformData> by data::uniforms

    override val id: Int = 0

    override val uniformName: String by data::uniformBlockName

    override var value: ZAlgebraObject by data::value

    fun addUniform(uniformName: String, uniform: ZUniformData) {
        this[uniformName] = uniform
    }

    operator fun set(uniformName: String, value: ZUniformData) {
        data.uniforms[uniformName] = value
    }

    @JsName("bindValue")
    operator fun set(uniformName: String, value: ZAlgebraObject) {
        data.uniforms[uniformName]?.value = value
    }

    override fun createRenderer(ctx: ZRenderingContext): ZUniformBlockRenderer {
        return ZUniformBlockRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

}

data class ZUniformBlockData(
    val uniformBlockName: String = "",
    val uniforms: LinkedHashMap<String, ZUniformData> = LinkedHashMap()
): ZComponentData() {

    val count: Int = uniforms.size

    private val requiredDataSize: Int
        get() = uniforms.values.sumOf { it.value?.byteSize ?: 0 }

    private var _value = ZAlgebraObjectCollection(requiredDataSize)

    var value: ZAlgebraObject
        get() {
            if (_value.byteSize != requiredDataSize) {
                _value = ZAlgebraObjectCollection(requiredDataSize)
            }
            _value.copyAll(uniforms.values.map { it.value!! })
            return _value
        }
        set(value) {
            _value = value as ZAlgebraObjectCollection
        }
}

expect class ZUniformBlockRenderer(ctx: ZRenderingContext, data: ZUniformBlockData): ZComponentRenderer {

    override fun initialize()

    override fun bind()

    override fun unbind()

}
