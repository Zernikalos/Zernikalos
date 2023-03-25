package zernikalos.loader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.ZGroup
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject
import zernikalos.objects.ZScene

@Serializable
data class ProtoZkObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val scene: ZScene?,
    @ProtoNumber(3) val group: ZGroup?,
    @ProtoNumber(4) val model: ZModel?,
    @ProtoNumber(100) val children: Array<ProtoZkObject>? = emptyArray()
)

object ZkProtoDeserializer: KSerializer<ZObject> {
    override val descriptor: SerialDescriptor = ProtoZkObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZObject {
        val decodedProtoObj = decoder.decodeSerializableValue(ProtoZkObject.serializer())
        return transformTree(decodedProtoObj)
    }

    override fun serialize(encoder: Encoder, value: ZObject) {
        TODO("Not yet implemented")
    }

    private fun transformTree(decodedProtoObj: ProtoZkObject): ZObject {
        val mrObject = getMrObject(decodedProtoObj)
        decodedProtoObj.children?.forEach { child ->
            val childObj = transformTree(child)
            mrObject.addChild(childObj)
        }
        return mrObject
    }

    private fun getMrObject(decodedProtoObj: ProtoZkObject): ZObject {
        when (decodedProtoObj.type) {
            "Scene" -> return decodedProtoObj.scene!!
            "Group" -> return decodedProtoObj.group!!
            "Model" -> return decodedProtoObj.model!!
        }
        throw Error("Type has not been found on object")
    }
}