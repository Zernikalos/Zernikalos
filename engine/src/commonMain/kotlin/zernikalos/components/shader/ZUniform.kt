/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import zernikalos.generators.uniformgenerator.ZUniformGenerator
import zernikalos.logger.logger
import zernikalos.math.*
import zernikalos.objects.ZObject
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZUniform internal constructor(private val data: ZUniformBlockData):
    ZRenderizableComponent<ZUniformRenderer>(), ZBindeable, ZBaseUniform {

    @JsExport.Ignore
    constructor(id: Int, uniformName: String, uniforms: LinkedHashMap<String, ZUniformData>): this(
        ZUniformBlockData(
            id,
            uniformName,
            uniforms
        )
    )

    @JsName("initWithUniforms")
    constructor(id: Int, uniformName: String, uniforms: List<Pair<String, ZUniformData>>): this(
        ZUniformBlockData(
            id,
            uniformName,
            LinkedHashMap(uniforms.toMap())
        )
    )

    @JsName("initSingle")
    constructor(id: Int, uniformName: String, count: Int, dataType: ZDataType): this(
        ZUniformBlockData(
            id = id,
            uniformBlockName = uniformName,
            uniforms = linkedMapOf(
                uniformName to ZUniformData(id, uniformName, count, dataType)
            )
        )
    )

    @JsName("init")
    constructor(): this(ZUniformBlockData())

    val generators: HashMap<String, ZUniformGenerator> = hashMapOf()

    val uniforms: MutableMap<String, ZUniformData> by data::uniforms

    override val id: Int by data::id

    override val uniformName: String by data::uniformBlockName

    override var value: ZAlgebraObject by data::value

    val isSingleUniform: Boolean
        get() = uniforms.size == 1

    val singleElement: ZUniformData?
        get() = if (isSingleUniform) uniforms.values.firstOrNull() else null


    fun addGenerators(gens: Map<String, ZUniformGenerator>) {
        gens.forEach { (name, generator) ->
            generators[name] = generator
        }
    }

    override fun createRenderer(ctx: ZRenderingContext): ZUniformRenderer {
        return ZUniformRenderer(ctx, data)
    }

    override fun initialize(ctx: ZRenderingContext) {
        super.initialize(ctx)
        logger.debug("Initialized uniform: ${data.uniformBlockName} (${data.byteSize} bytes)")
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun toString(): String {
        return data.toString()
    }

    fun computeValue(context: ZSceneContext, obj: ZObject) {
        val gen = generators
        if (gen.isEmpty()) return
        for ((name, member) in uniforms) {
            val generator = gen[name] ?: continue
            val v = generator(context, obj)
            member.value = v
        }
    }

}

data class ZUniformData(
    override var id: Int = -1,
    override var uniformName: String = "",
    var count: Int = -1,
    var dataType: ZDataType = ZTypes.NONE,
): ZComponentData(), ZBaseUniform {
    val byteSize: Int
        get() = dataType.byteSize * count

    override var value: ZAlgebraObject = algebraObjectByType(dataType)
}

data class ZUniformBlockData(
    val id: Int = -1,
    val uniformBlockName: String = "",
    val uniforms: LinkedHashMap<String, ZUniformData> = LinkedHashMap(),
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

expect class ZUniformRenderer(ctx: ZRenderingContext, data: ZUniformBlockData): ZComponentRenderer {

    override fun initialize()
}

// TODO: This need to be reimplemented somehow
private fun algebraObjectByType(type: ZDataType): ZAlgebraObject {
    return when (type) {
        ZTypes.MAT4F -> ZMatrix4()
        ZTypes.VEC2F -> ZVector2()
        else -> ZVoidAlgebraObject()
    }
}
