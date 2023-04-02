package zernikalos.loader

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import zernikalos.components.camera.ZLens
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.objects.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
val zObjectModule = SerializersModule {
    polymorphic(ZObject::class) {
        subclass(ZModel::class)
        subclass(ZGroup::class)
        subclass(ZScene::class)
        subclass(ZCamera::class)
        defaultDeserializer { ZGroup.serializer() }
    }

    polymorphic(ZLens::class) {
        subclass(ZPerspectiveLens::class)
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

@JsExport
@OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
fun loadFromJsonString(jsonString: String): ZObject {
    return jsonFormat.decodeFromString(jsonString)
}