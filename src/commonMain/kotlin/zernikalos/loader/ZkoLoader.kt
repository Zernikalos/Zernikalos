/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import zernikalos.components.material.ZMaterial
import zernikalos.components.material.ZMaterialSerializer
import zernikalos.components.material.ZTexture
import zernikalos.components.material.ZTextureSerializer
import zernikalos.components.skeleton.ZSkeleton
import zernikalos.components.skeleton.ZSkeletonSerializer
import zernikalos.objects.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private fun createSerializersModule(): ProtoBuf {
    val loaderContext = ZLoaderContext()

    @OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
    val zObjectModule = SerializersModule {
        polymorphic(ZObject::class) {
            subclass(ZModel::class)
            subclass(ZGroup::class)
            subclass(ZScene::class)
            subclass(ZCamera::class)
            defaultDeserializer { ZGroup.serializer() }
        }

        contextual(ZModel::class) { _ -> ZModelSerializer(loaderContext) }
        contextual(ZMaterial::class) { _ -> ZMaterialSerializer(loaderContext)}
        contextual(ZTexture::class) { _ -> ZTextureSerializer(loaderContext)}
        contextual(ZSkeleton::class) { _ -> ZSkeletonSerializer(loaderContext)}

    }

    val protoFormat = ProtoBuf {
        serializersModule = zObjectModule
        encodeDefaults = true
    }

    return protoFormat
}



@JsExport
fun loadFromProtoString(hexString: String): ZObject {
    val protoFormat = createSerializersModule()
    return protoFormat.decodeFromHexString(ZkoFormat.serializer(), hexString).data.zObject
}

// TODO: These methods might not return ZObject but ZObject?
@JsExport
fun loadFromProto(byteArray: ByteArray): ZObject {
    val protoFormat = createSerializersModule()
    val zkoFormat = protoFormat.decodeFromByteArray(ZkoFormat.serializer(), byteArray)
    return zkoFormat.data.zObject
}

