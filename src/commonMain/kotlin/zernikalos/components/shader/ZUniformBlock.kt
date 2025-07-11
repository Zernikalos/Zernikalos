/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZAlgebraObjectCollection
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZUniformBlock internal constructor(private val data: ZUniformBlockData):
    ZRenderizableComponent<ZUniformBlockRenderer>(), ZBindeable, ZBaseUniform {

    @JsExport.Ignore
    constructor(id: Int, uniformBlockName: String, uniforms: LinkedHashMap<String, ZUniformData>): this(
        ZUniformBlockData(
            id,
            uniformBlockName,
            uniforms
        )
    )

    @JsName("initWithArgs")
    constructor(id: Int, uniformBlockName: String, uniforms: List<Pair<String, ZUniformData>>): this(
        ZUniformBlockData(
            id,
            uniformBlockName,
            LinkedHashMap(uniforms.toMap())
        )
    )

    val uniforms: MutableMap<String, ZUniformData> by data::uniforms

    override val id: Int by data::id

    override val uniformName: String by data::uniformBlockName

    override var value: ZAlgebraObject by data::value

    override fun createRenderer(ctx: ZRenderingContext): ZUniformBlockRenderer {
        return ZUniformBlockRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

}

data class ZUniformBlockData(
    val id: Int = -1,
    val uniformBlockName: String = "",
    val uniforms: LinkedHashMap<String, ZUniformData>
): ZComponentData() {

    val count: Int = uniforms.size

    val byteSize: Int
        get() = uniforms.values.sumOf { it.byteSize }

    private var _value = ZAlgebraObjectCollection(byteSize)

    var value: ZAlgebraObject
        get() {
            var offset = 0
            uniforms.values.forEach { uniform ->
                _value.copyInto(offset, uniform.value)
                offset += uniform.byteSize
            }
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

