package zernikalos.loader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.*

@Serializable
data class ProtoZkObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val scene: ZScene?,
    @ProtoNumber(3) val group: ZGroup?,
    @ProtoNumber(4) val model: ZModel?,
    @ProtoNumber(5) val camera: ZCamera?,
    @ProtoNumber(6) val skeleton: ZSkeleton?,
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
        val zObject = getZObject(decodedProtoObj)
        decodedProtoObj.children?.forEach { child ->
            val childObj = transformTree(child)
            zObject.addChild(childObj)
        }
        return zObject
    }

    private fun getZObject(decodedProtoObj: ProtoZkObject): ZObject {
        when (decodedProtoObj.type) {
            ZObjectType.SCENE.name -> return decodedProtoObj.scene!!
            ZObjectType.GROUP.name -> return decodedProtoObj.group!!
            ZObjectType.MODEL.name -> return decodedProtoObj.model!!
            ZObjectType.CAMERA.name -> return decodedProtoObj.camera!!
            ZObjectType.SKELETON.name -> return decodedProtoObj.skeleton!!
        }
        throw Error("Type has not been found on object")
    }
}