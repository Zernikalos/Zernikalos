package zernikalos.components.skeleton

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.KSerializer
import zernikalos.components.ZComponentData
import zernikalos.components.ZSerializableComponent
import zernikalos.components.ZComponentSerializer
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZSerializableComponent<ZSkinningData>(data) {

    @JsName("init")
    constructor(): this(ZSkinningData())

    val boneIds: Array<String>
        get() = data.boneIds.toTypedArray()

    fun addBoneId(boneId: String) {
        data.boneIds.add(boneId)
    }

    fun removeBoneId(boneId: String) {
        data.boneIds.remove(boneId)
    }
}

@Serializable
data class ZSkinningData(
    @ProtoNumber(10)
    val boneIds: ArrayList<String> = ArrayList()
): ZComponentData()

class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val kSerializer: KSerializer<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }
}
