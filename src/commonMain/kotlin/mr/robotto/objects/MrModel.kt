package mr.robotto.objects

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.components.mesh.MrMesh
import mr.robotto.components.shader.MrShaderProgram
import kotlin.js.JsExport

@JsExport
@Serializable(with = MrModelSerializer::class)
@SerialName("Model")
class MrModel: MrObject() {
    lateinit var mesh: MrMesh
    lateinit var shaderProgram: MrShaderProgram

    override fun internalInitialize(ctx: MrRenderingContext) {
        println("Model initialization")
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
    }
}

@Serializable
class MrModelData(val mesh: MrMesh, val shaderProgram: MrShaderProgram) : MrObjectData()

class MrModelSerializer: MrNodeSerializer<MrModel, MrModelData>() {
    override val dataDeserializationStrategy: DeserializationStrategy<MrModelData> = MrModelData.serializer()

    override fun createDeserializationInstance(): MrModel {
        return MrModel()
    }

    override fun assignMembers(data: MrModel, surrogate: MrModelData) {
        data.mesh = surrogate.mesh
        data.shaderProgram = surrogate.shaderProgram
    }

}