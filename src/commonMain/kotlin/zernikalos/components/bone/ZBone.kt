package zernikalos.components.bone

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBaseComponent
import zernikalos.components.ZBaseComponentSerializer
import zernikalos.components.ZComponentData
import zernikalos.math.ZTransform

@Serializable(with = ZBoneSerializer::class)
class ZBone internal constructor(data: ZBoneData): ZBaseComponent<ZBoneData>(data) {

    constructor(): this(ZBoneData())

}

@Serializable
data class ZBoneData(
    @ProtoNumber(1)
    val id: String = "",
    @ProtoNumber(2)
    val name: String = "",
    @ProtoNumber(3)
    val transform: ZTransform = ZTransform(),
    @ProtoNumber(5)
    val children: Array<ZBone> = emptyArray()
): ZComponentData()

class ZBoneSerializer: ZBaseComponentSerializer<ZBone, ZBoneData>() {
    override val deserializationStrategy: DeserializationStrategy<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}