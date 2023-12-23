package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBaseComponent
import zernikalos.components.ZBaseComponentSerializer
import zernikalos.components.ZComponentData
import zernikalos.math.ZTransform
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBoneSerializer::class)
class ZBone internal constructor(data: ZBoneData): ZBaseComponent<ZBoneData>(data) {

    @JsName("init")
    constructor(): this(ZBoneData())

    var id: String by data::id

    var name: String by data::name

    var transform: ZTransform by data::transform

    val children: Array<ZBone>
        get() = data.children.toTypedArray()

    fun addChildren(bone: ZBone) {
        data.children.add(bone)
    }

}

@Serializable
data class ZBoneData(
    @ProtoNumber(1)
    var id: String = "",
    @ProtoNumber(2)
    var name: String = "",
    @ProtoNumber(3)
    var transform: ZTransform = ZTransform(),
    @ProtoNumber(5)
    val children: ArrayList<ZBone> = arrayListOf()
): ZComponentData()

class ZBoneSerializer: ZBaseComponentSerializer<ZBone, ZBoneData>() {
    override val deserializationStrategy: DeserializationStrategy<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}