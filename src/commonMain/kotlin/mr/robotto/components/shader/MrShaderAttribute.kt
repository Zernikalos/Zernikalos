package mr.robotto.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer

@Serializable(with = MrShaderAttributeSerializer::class)
class MrShaderAttribute: MrComponent<MrShaderAttributeData, MrShaderAttributeRender>() {
    override lateinit var data: MrShaderAttributeData
    override var renderer: MrShaderAttributeRender = MrShaderAttributeRender()

}

@Serializable
data class MrShaderAttributeData(val index: Int, val attributeName: String): MrComponentData()

class MrShaderAttributeRender: MrComponentRender<MrShaderAttributeData>() {
    override fun internalInitialize() {
    }

    override fun render() {
    }

}

class MrShaderAttributeSerializer: MrComponentSerializer<MrShaderAttribute, MrShaderAttributeData>() {
    override val deserializationStrategy: DeserializationStrategy<MrShaderAttributeData> = MrShaderAttributeData.serializer()

    override fun createDeserializationInstance(): MrShaderAttribute {
        return MrShaderAttribute()
    }

}