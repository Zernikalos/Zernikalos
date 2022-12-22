package mr.robotto.components.mesh

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.ByteString
import mr.robotto.components.*
import mr.robotto.components.buffer.MrVertexBuffer

@Serializable
class MrAttribute(val name: String, val size: Int, val count: Int, @ByteString val data: ByteArray): MrComponent() {

    @Transient
    val vertexBuffer: MrVertexBuffer = MrVertexBuffer()

    override fun renderInitialize() {
        // TODO Create an initializer in base classes
        vertexBuffer.dataArray = data
        vertexBuffer.initialize(context)
    }

    override fun render() {
    }

}
