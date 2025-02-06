/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport
import kotlin.js.JsName

// TODO: These guys are incorrect, they need to provide a new uniform each time
val ZUniformProjectionMatrix: ZUniform
    get() = ZUniform("u_projMatrix", 1, ZTypes.MAT4F)
val ZUniformViewMatrix: ZUniform
    get() = ZUniform("u_viewMatrix", 1, ZTypes.MAT4F)
val ZUniformModelViewProjectionMatrix: ZUniform
    get() = ZUniform("u_mvpMatrix", 1, ZTypes.MAT4F)

fun ZBonesMatrixArray(count: Int): ZUniform {
    return ZUniform("u_bones", count, ZTypes.MAT4F)
}

fun ZInverseBindMatrixArray(count: Int): ZUniform {
    return ZUniform("u_invBindMatrix", count, ZTypes.MAT4F)
}

@Serializable(with = ZUniformSerializer::class)
@JsExport
class ZUniform internal constructor(data: ZUniformData): ZRenderizableComponent<ZUniformData, ZUniformRenderer>(data) {

    @JsName("initWithArgs")
    constructor(uniformName: String, count: Int , dataType: ZDataType): this(ZUniformData(uniformName, count, dataType))
    @JsName("init")
    constructor(): this(ZUniformData())

    /**
     * This is the name within the shader source code
     */
    var uniformName: String by data::uniformName

    /**
     * How many elements of this will be used
     */
    var count: Int by data::count

    /**
     * The datatype of all individual elements used by this uniform
     */
    var dataType: ZDataType by data::dataType

    var idx: Int by data::idx

    var value: ZAlgebraObject? by data::value

    override fun createRenderer(ctx: ZRenderingContext): ZUniformRenderer {
        return ZUniformRenderer(ctx, data)
    }

    fun bindValue(value: ZAlgebraObject) {
        this.value = value
    }

    override fun toString(): String {
        return data.toString()
    }

}

@Serializable
data class ZUniformData(
    @ProtoNumber(1)
    var uniformName: String = "",
    @ProtoNumber(2)
    var count: Int = -1,
    @ProtoNumber(3)
    var dataType: ZDataType = ZTypes.NONE,
    @ProtoNumber(4)
    var idx: Int = -1
): ZComponentData() {

    @Transient
    var value: ZAlgebraObject? = null
}

expect class ZUniformRenderer(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData> {

    override fun initialize()
}

class ZUniformSerializer: ZComponentSerializer<ZUniform, ZUniformData>() {
    override val kSerializer: KSerializer<ZUniformData>
        get() = ZUniformData.serializer()

    override fun createComponentInstance(data: ZUniformData): ZUniform {
        return ZUniform(data)
    }

}