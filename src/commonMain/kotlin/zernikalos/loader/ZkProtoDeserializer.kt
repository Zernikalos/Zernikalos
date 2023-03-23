package zernikalos.loader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.ZkGroup
import zernikalos.objects.ZkModel
import zernikalos.objects.ZkObject

@Serializable
data class ProtoZkObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val group: ZkGroup?,
    @ProtoNumber(3) val model: ZkModel?,
    @ProtoNumber(100) val children: Array<ProtoZkObject>? = emptyArray()
)

object ZkProtoDeserializer: KSerializer<ZkObject> {
    override val descriptor: SerialDescriptor = ProtoZkObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZkObject {
        val decodedProtoObj = decoder.decodeSerializableValue(ProtoZkObject.serializer())
        return transformTree(decodedProtoObj)
    }

    override fun serialize(encoder: Encoder, value: ZkObject) {
        TODO("Not yet implemented")
    }

    private fun transformTree(decodedProtoObj: ProtoZkObject): ZkObject {
        val mrObject = getMrObject(decodedProtoObj)
        decodedProtoObj.children?.forEach { child ->
            val childObj = transformTree(child)
            mrObject.addChild(childObj)
        }
        return mrObject
    }

    private fun getMrObject(decodedProtoObj: ProtoZkObject): ZkObject {
        when (decodedProtoObj.type) {
            "Group" -> return decodedProtoObj.group!!
            "Model" -> return decodedProtoObj.model!!
        }
        throw Error("Type has not been found on object")
    }
}