package mr.robotto.loader

import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import mr.robotto.objects.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
val mrObjectModule = SerializersModule {
    polymorphic(MrObject::class) {
        subclass(MrModel::class)
        subclass(MrGroup::class)
        defaultDeserializer { MrGroup.serializer() }
    }
//    polymorphic(MrObject::class) {
//        defaultDeserializer { className: String? ->
//            when(className) {
//                "Model" -> return@defaultDeserializer MrModel.serializer()
//                "Group" -> return@defaultDeserializer MrGroup.serializer()
//
//                else -> return@defaultDeserializer MrGroup.serializer()
//            }
//        }
//    }


}

@OptIn(ExperimentalSerializationApi::class)
val cborFormat = Cbor {
    serializersModule = mrObjectModule
    ignoreUnknownKeys = true
}

val protoFormat = ProtoBuf {
    serializersModule = mrObjectModule
    encodeDefaults = true
}

val jsonFormat = Json {
    serializersModule = mrObjectModule
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromCborString(hexString: String): MrObject {
    return cborFormat.decodeFromHexString(hexString)
}

@JsExport
fun loadFromProtoString(hexString: String): MrObject {
    return protoFormat.decodeFromHexString(MrProtoDeserializer, hexString)
}

//@Serializable
//data class Grupito(val type: String, val name: String, val data: ByteArray)
//
//@Serializable
//data class ProtoObject(val type: String, val group: Grupito?, val children: Array<ProtoObject>? = emptyArray())
//
//
//@JsExport
//fun sample(st: String): ProtoObject {
//    return protoFormat.decodeFromHexString<ProtoObject>(st)
//}

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromJsonString(jsonString: String): MrObject {
    return jsonFormat.decodeFromString(jsonString)
}