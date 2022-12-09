package mr.robotto.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import mr.robotto.objects.*

/* val module = SerializersModule {
    polymorphic(MrNode::class) {
        defaultDeserializer { className: String? ->
            when(className) {
                "Model" -> return@defaultDeserializer MrModelSerializer()
                "Group" -> return@defaultDeserializer MrGroupSerializer()

                else -> {return@defaultDeserializer MrModelSerializer()}
            }
        }
        // subclass(MrModel::class)
    }
} */

@OptIn(ExperimentalSerializationApi::class)
val module2 = SerializersModule {
    polymorphic(MrObject::class) {
        defaultDeserializer { className: String? ->
            when(className) {
                "Model" -> return@defaultDeserializer MrModel.serializer()
                "Group" -> return@defaultDeserializer MrGroup.serializer()

                else -> {return@defaultDeserializer MrModel.serializer()}
            }
        }
        // subclass(MrModel::class)
    }

}

@OptIn(ExperimentalSerializationApi::class)
val format = Cbor {
    // serializersModule = module
    serializersModule = module2
    ignoreUnknownKeys = true
    //encodeDefaults = true
}

class MrLoader {

    @OptIn(ExperimentalSerializationApi::class)
    fun load(hexString: String): MrObject {
        return format.decodeFromHexString(hexString)
    }
}