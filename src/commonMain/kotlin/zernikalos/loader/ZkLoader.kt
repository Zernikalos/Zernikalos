package zernikalos.loader

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import zernikalos.objects.ZGroup
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
val zObjectModule = SerializersModule {
    polymorphic(ZObject::class) {
        subclass(ZModel::class)
        subclass(ZGroup::class)
        defaultDeserializer { ZGroup.serializer() }
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

val protoFormat = ProtoBuf {
    serializersModule = zObjectModule
    encodeDefaults = true
}

val jsonFormat = Json {
    serializersModule = zObjectModule
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@JsExport
fun loadFromProtoString(hexString: String): ZObject {
    return protoFormat.decodeFromHexString(ZkProtoDeserializer, hexString)
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
fun loadFromJsonString(jsonString: String): ZObject {
    return jsonFormat.decodeFromString(jsonString)
}