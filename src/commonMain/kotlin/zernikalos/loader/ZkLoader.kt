package zernikalos.loader

import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import zernikalos.objects.ZkGroup
import zernikalos.objects.ZkModel
import zernikalos.objects.ZkObject
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
val zkObjectModule = SerializersModule {
    polymorphic(ZkObject::class) {
        subclass(ZkModel::class)
        subclass(ZkGroup::class)
        defaultDeserializer { ZkGroup.serializer() }
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
    serializersModule = zkObjectModule
    ignoreUnknownKeys = true
}

val protoFormat = ProtoBuf {
    serializersModule = zkObjectModule
    encodeDefaults = true
}

val jsonFormat = Json {
    serializersModule = zkObjectModule
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromCborString(hexString: String): ZkObject {
    return cborFormat.decodeFromHexString(hexString)
}

@JsExport
fun loadFromProtoString(hexString: String): ZkObject {
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
fun loadFromJsonString(jsonString: String): ZkObject {
    return jsonFormat.decodeFromString(jsonString)
}