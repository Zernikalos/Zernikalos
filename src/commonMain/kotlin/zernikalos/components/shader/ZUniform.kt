package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZRenderingContext
import zernikalos.ZTypes
import zernikalos.components.*
import kotlin.js.JsExport
import kotlin.js.JsName


@Serializable(with = ZUniformSerializer::class)
@JsExport
class ZUniform internal constructor(data: ZUniformData, renderer: ZUniformRenderer): ZComponent<ZUniformData, ZUniformRenderer>(data, renderer) {

    @JsName("init")
    constructor(): this(ZUniformData(), ZUniformRenderer())

    var uniformName: String
        get() = data.uniformName
        set(value) {
            data.uniformName = value
        }

    var count: Int
        get() = data.count
        set(value) {
            data.count = value
        }

    var dataType: ZDataType
        get() = data.dataType
        set(value) {
            data.dataType = value
        }

    override fun initialize(ctx: ZRenderingContext) {
    }

    fun bindLocation(ctx: ZRenderingContext, program: ZProgram) {
        renderer.bindLocation(ctx, data, program)
    }

    fun bindValue(ctx: ZRenderingContext, values: FloatArray) {
        renderer.bindValue(ctx, data, values)
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
    var dataType: ZDataType = ZTypes.NONE
): ZComponentData()

expect class ZUniformRenderer(): ZComponentRender<ZUniformData> {

    override fun initialize(ctx: ZRenderingContext, data: ZUniformData)

    fun bindLocation(ctx: ZRenderingContext, data: ZUniformData, program: ZProgram)

    fun bindValue(ctx: ZRenderingContext, data: ZUniformData, values: FloatArray)
}

class ZUniformSerializer: ZComponentSerializer<ZUniform, ZUniformData, ZUniformRenderer>() {
    override val deserializationStrategy: DeserializationStrategy<ZUniformData>
        get() = ZUniformData.serializer()

    override fun createRendererComponent(): ZUniformRenderer {
        return ZUniformRenderer()
    }

    override fun createComponentInstance(data: ZUniformData, renderer: ZUniformRenderer): ZUniform {
        return ZUniform()
    }

}