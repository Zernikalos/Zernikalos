package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.context.ZRenderingContext
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport
import kotlin.js.JsName

// TODO: These guys are incorrect, they need to provide a new uniform each time
val ZUniformProjectionMatrix = ZUniform("u_projMatrix", 1, ZTypes.MAT4F)
val ZUniformViewMatrix = ZUniform("u_viewMatrix", 1, ZTypes.MAT4F)
val ZUniformModelViewProjectionMatrix = { ZUniform("u_mvpMatrix", 1, ZTypes.MAT4F) }

@Serializable(with = ZUniformSerializer::class)
@JsExport
class ZUniform internal constructor(data: ZUniformData): ZComponent<ZUniformData, ZUniformRenderer>(data) {

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

    override fun createRenderer(ctx: ZRenderingContext): ZUniformRenderer {
        return ZUniformRenderer(ctx, data)
    }

    fun bindValue4fv(shaderProgram: ZShaderProgram, values: FloatArray) {
        renderer.bindValue(shaderProgram, values)
    }

    fun bindValue(shaderProgram: ZShaderProgram, uniform: ZAlgebraObject) {
        when (uniform.dataType) {
            ZTypes.MAT4F -> bindValue4fv(shaderProgram, uniform.values)
        }
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
): ZComponentData()

expect class ZUniformRenderer(ctx: ZRenderingContext, data: ZUniformData): ZComponentRender<ZUniformData> {

    override fun initialize()

    fun bindValue(shaderProgram: ZShaderProgram, values: FloatArray)
}

class ZUniformSerializer: ZComponentSerializer<ZUniform, ZUniformData>() {
    override val deserializationStrategy: DeserializationStrategy<ZUniformData>
        get() = ZUniformData.serializer()

    override fun createComponentInstance(data: ZUniformData): ZUniform {
        return ZUniform(data)
    }

}