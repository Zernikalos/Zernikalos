package mr.robotto.components.shader

import kotlinx.serialization.Serializable
import mr.robotto.components.*

@Serializable
class MrShaderAttribute(val index: Int, val attributeName: String): MrComponent() {
    override fun renderInitialize() {
    }

    override fun render() {
    }

}
