package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.context.ZRenderingContext
import zernikalos.ZTypes
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName


@Serializable(with = ZUniformSerializer::class)
@JsExport
class ZUniform internal constructor(data: ZUniformData): ZComponent<ZUniformData, ZUniformRenderer>(data) {

    @JsName("init")
    constructor(): this(ZUniformData())

    var uniformName: String by data::uniformName

    var count: Int by data::count

    var dataType: ZDataType by data::dataType

    var idx: Int by data::idx

    override fun internalInitialize(ctx: ZRenderingContext) {
    }

    override fun createRenderer(ctx: ZRenderingContext): ZUniformRenderer {
        return ZUniformRenderer(ctx, data)
    }

    fun bindLocation(program: ZProgram) {
        renderer.bindLocation(program)
    }

    fun bindValue(shaderProgram: ZShaderProgram, values: FloatArray) {
        renderer.bindValue(shaderProgram, values)
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

    fun bindLocation(program: ZProgram)

    fun bindValue(shaderProgram: ZShaderProgram, values: FloatArray)
}

class ZUniformSerializer: ZComponentSerializer<ZUniform, ZUniformData, ZUniformRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZUniformData>
        get() = ZUniformData.serializer()

    override fun createComponentInstance(data: ZUniformData): ZUniform {
        return ZUniform(data)
    }

}