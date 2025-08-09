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
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZComponentData
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector2
import zernikalos.math.ZVoidAlgebraObject
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZUniform internal constructor(private val data: ZUniformData):
    ZRenderizableComponent<ZUniformRenderer>(), ZBindeable, ZBaseUniform {

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

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun toString(): String {
        return data.toString()
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

expect class ZUniformRenderer(ctx: ZRenderingContext, data: ZUniformData): ZComponentRenderer {

    override fun initialize()
}

// TODO: This need to be reimplemented somehow
private fun algebraObjectByType(type: ZDataType): ZAlgebraObject {
    return when (type) {
        ZTypes.MAT4F -> return ZMatrix4()
        ZTypes.VEC2F -> return ZVector2()
        else -> ZVoidAlgebraObject()
    }
}
