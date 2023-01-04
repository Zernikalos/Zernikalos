package mr.robotto.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import mr.robotto.objects.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
val mrObjectModule = SerializersModule {
    polymorphic(MrObject::class) {
        defaultDeserializer { className: String? ->
            when(className) {
                "Model" -> return@defaultDeserializer MrModel.serializer()
                "Group" -> return@defaultDeserializer MrGroup.serializer()

                else -> {return@defaultDeserializer MrModel.serializer()}
            }
        }
    }

}

@OptIn(ExperimentalSerializationApi::class)
val cborFormat = Cbor {
    serializersModule = mrObjectModule
    ignoreUnknownKeys = true
}

val jsonFormat = Json {
    serializersModule = mrObjectModule
    ignoreUnknownKeys = true
}

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromCborString(hexString: String): MrObject {
    return cborFormat.decodeFromHexString(hexString)
}

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromJsonString(jsonString: String): MrObject {
    return jsonFormat.decodeFromString(jsonString)
}