/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import zernikalos.action.ZSkeletalAction
import zernikalos.components.material.ZMaterial
import zernikalos.components.material.ZMaterialSerializer
import zernikalos.components.material.ZTexture
import zernikalos.components.material.ZTextureSerializer
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
            subclass(ZSkeleton::class)
            defaultDeserializer { ZGroup.serializer() }
        }

        contextual(ZkoObjectProto::class) { _ -> ZkoObjectProtoSerializer(loaderContext)}
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
data class ZKo(
    val header: ZkoHeader,
    val root: ZObject,
    val actions: List<ZSkeletalAction>? = null
)

/**
 * Decodes a ByteArray into a [ZKo] object.
 *
 * This function parses the given byte array,
 * extracts the header, objects, hierarchy, and actions, and reconstructs the
 * root [ZObject] hierarchy.
 *
 * @param byteArray The byte array containing the serialized ZKo data.
 * @return A [ZKo] instance reconstructed from the provided data.
 */
@JsExport
fun loadFromProto(byteArray: ByteArray): ZKo {
    val protoFormat = createSerializersModule()
    val zkoFormat = protoFormat.decodeFromByteArray(ZkoFormat.serializer(), byteArray)
    val header = zkoFormat.header
    val obj = ZkoHierarchyNode.transformHierarchy(zkoFormat.hierarchy, zkoFormat.objects)
    val actions = zkoFormat.actions
    return ZKo(header, obj, actions)
}

