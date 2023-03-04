package mr.robotto.loader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import mr.robotto.objects.MrGroup
import mr.robotto.objects.MrModel
import mr.robotto.objects.MrObject

@Serializable
data class ProtoMrObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val group: MrGroup?,
    @ProtoNumber(3) val model: MrModel?,
    @ProtoNumber(100) val children: Array<ProtoMrObject>? = emptyArray()
)

object MrProtoDeserializer: KSerializer<MrObject> {
    override val descriptor: SerialDescriptor = ProtoMrObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): MrObject {
        val decodedProtoObj = decoder.decodeSerializableValue(ProtoMrObject.serializer())
        return transformTree(decodedProtoObj)
    }

    override fun serialize(encoder: Encoder, value: MrObject) {
        TODO("Not yet implemented")
    }

    private fun transformTree(decodedProtoObj: ProtoMrObject): MrObject {
        val mrObject = getMrObject(decodedProtoObj)
        decodedProtoObj.children?.forEach { child ->
            val childObj = transformTree(child)
            mrObject.addChild(childObj)
        }
        return mrObject
    }

    private fun getMrObject(decodedProtoObj: ProtoMrObject): MrObject {
        when (decodedProtoObj.type) {
            "Group" -> return decodedProtoObj.group!!
            "Model" -> return decodedProtoObj.model!!
        }
        throw Error("Type has not been found on object")
    }
}