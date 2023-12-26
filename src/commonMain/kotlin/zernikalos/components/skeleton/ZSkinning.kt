package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBaseComponent
import zernikalos.components.ZBaseComponentSerializer
import zernikalos.components.ZComponentData
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZBaseComponent<ZSkinningData>(data) {

    val boneIndices: Array<Int> by data::boneIndices

}

@Serializable
data class ZSkinningData(
    @ProtoNumber(1)
    var boneIndices: Array<Int> = emptyArray()
): ZComponentData()

class ZSkinningSerializer: ZBaseComponentSerializer<ZSkinning, ZSkinningData>() {
    override val deserializationStrategy: DeserializationStrategy<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }

}