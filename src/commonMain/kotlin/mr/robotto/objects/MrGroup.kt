package mr.robotto.objects

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext

@Serializable(with = MrGroupSerializer::class)
@SerialName("Group")
class MrGroup: MrObject() {
    override fun internalInitialize(ctx: MrRenderingContext) {
    }
}

@Serializable
class MrGroupData: MrObjectData()

class MrGroupSerializer: MrNodeSerializer<MrGroup, MrGroupData>() {
    override val dataDeserializationStrategy: DeserializationStrategy<MrGroupData> = MrGroupData.serializer()

    override fun createDeserializationInstance(): MrGroup {
        return MrGroup()
    }

    override fun assignMembers(data: MrGroup, surrogate: MrGroupData) {
    }

}