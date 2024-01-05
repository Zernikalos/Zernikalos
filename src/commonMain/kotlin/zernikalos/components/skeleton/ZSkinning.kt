package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import kotlin.js.JsExport

@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZComponent<ZSkinningData, ZComponentRender<ZSkinningData>>(data) {

    val boneIndices: Array<Int> by data::boneIndices

}

@Serializable
data class ZSkinningData(
    @ProtoNumber(1)
    var boneIndices: Array<Int> = emptyArray()
): ZComponentData()

class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val deserializationStrategy: DeserializationStrategy<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }

}