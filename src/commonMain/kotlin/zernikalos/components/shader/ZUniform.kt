/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport
import kotlin.js.JsName

val ZUniformProjectionMatrix: ZUniform
    get() = ZUniform(0, "u_projMatrix", 1, ZTypes.MAT4F)
val ZUniformViewMatrix: ZUniform
    get() = ZUniform(1, "u_viewMatrix", 1, ZTypes.MAT4F)
val ZUniformModelViewProjectionMatrix: ZUniform
    get() = ZUniform(2, "u_mvpMatrix", 1, ZTypes.MAT4F)

fun ZBonesMatrixArray(count: Int): ZUniform {
    return ZUniform(5,"u_bones", count, ZTypes.MAT4F)
}

fun ZInverseBindMatrixArray(count: Int): ZUniform {
    return ZUniform(6, "u_invBindMatrix", count, ZTypes.MAT4F)
}

@JsExport
class ZUniform internal constructor(data: ZUniformData):
    ZRenderizableComponent<ZUniformData, ZUniformRenderer>(data), ZBaseUniform {

    @JsName("initWithArgs")
    constructor(id: Int, uniformName: String, count: Int , dataType: ZDataType): this(ZUniformData(id, uniformName, count, dataType))
    @JsName("init")
    constructor(): this(ZUniformData())

    /**
     * Represents the unique identifier for a `ZUniform` instance.
     * This ID is used to differentiate between different uniform components
     */
    override var id: Int by data::id

    /**
     * This is the name within the shader source code
     */
    override var uniformName: String by data::uniformName

    /**
     * How many elements of this will be used
     */
    var count: Int by data::count

    /**
     * The datatype of all individual elements used by this uniform
     */
    var dataType: ZDataType by data::dataType

    override var value: ZAlgebraObject
        get() = data.value!!
        set(value) {
            data.value = value
        }

    override fun createRenderer(ctx: ZRenderingContext): ZUniformRenderer {
        return ZUniformRenderer(ctx, data)
    }

    override fun toString(): String {
        return data.toString()
    }

}

data class ZUniformData(
    var id: Int = -1,
    var uniformName: String = "",
    var count: Int = -1,
    var dataType: ZDataType = ZTypes.NONE,
): ZComponentData() {
    var value: ZAlgebraObject? = null
}

expect class ZUniformRenderer(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData> {

    override fun initialize()
}
