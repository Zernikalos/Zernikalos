package zernikalos.components.skeleton

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZSerializableComponent

@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZSerializableComponent<ZSkinningData>(data) {
}

@Serializable
data class ZSkinningData(
    val boneNames: List<String>
): ZComponentData()

class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val kSerializer: KSerializer<ZSkinningData> = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }

}