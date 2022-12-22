package mr.robotto.components.mesh

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer
import mr.robotto.components.buffer.MrVertexBuffer
import mr.robotto.components.buffer.MrVertexArray

@Serializable(with = MrMeshSerializer::class)
class MrMesh : MrComponent<MrMeshData, MrMeshRender>() {
    override lateinit var data: MrMeshData
    override var renderer: MrMeshRender = MrMeshRender()
}

@Serializable
class MrMeshData(val indices: Array<Int>, val attributes: Map<String, MrAttribute>): MrComponentData() {
    @Transient
    val vao: MrVertexArray = MrVertexArray()
}

class MrMeshRender: MrComponentRender<MrMeshData>() {
    override fun internalInitialize() {
        data.vao.initialize(context)
        data.attributes.values.forEach { attr -> attr.initialize(context) }
    }

    override fun render() {
        data.vao.render()
    }

}

class MrMeshSerializer: MrComponentSerializer<MrMesh, MrMeshData>() {

    override val deserializationStrategy: DeserializationStrategy<MrMeshData> = MrMeshData.serializer()

    override fun createDeserializationInstance(): MrMesh {
        return MrMesh()
    }

}