package mr.robotto.components

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mr.robotto.MrRenderingContext

abstract class MrComponent {

    protected lateinit var context: MrRenderingContext

    fun initialize(ctx: MrRenderingContext) {
        context = ctx
        renderInitialize()
    }

    protected abstract fun renderInitialize()

    abstract fun render()
}
