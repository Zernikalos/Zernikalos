package mr.robotto.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer

@Serializable(with = MrMeshSerializer::class)
class MrMesh : MrComponent<MrMeshData, MrMeshRender>() {
    override lateinit var data: MrMeshData
    override var renderer: MrMeshRender = MrMeshRender()
}

@Serializable
class MrMeshData(val indices: Array<Int>, val attributes: Map<String, MrAttribute>): MrComponentData()

class MrMeshRender: MrComponentRender<MrMeshData>() {
    override fun internalInitialize() {
    }

    override fun render() {
    }

}

class MrMeshSerializer: MrComponentSerializer<MrMesh, MrMeshData>() {

    override val deserializationStrategy: DeserializationStrategy<MrMeshData> = MrMeshData.serializer()

    override fun createDeserializationInstance(): MrMesh {
        return MrMesh()
    }

}