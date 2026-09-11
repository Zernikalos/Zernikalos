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
import zernikalos.components.light.*
import zernikalos.objects.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private fun createZerializerModule(): SerializersModule {
    @OptIn(ExperimentalSerializationApi::class, ExperimentalJsExport::class)
    return SerializersModule {
        polymorphic(ZObject::class) {
            subclass(ZModel::class)
            subclass(ZGroup::class)
            subclass(ZScene::class)
            subclass(ZCamera::class)
            subclass(ZSkeleton::class)
            defaultDeserializer { ZGroup.serializer() }
        }

        polymorphic(ZLamp::class) {
            subclass(ZDirectionalLamp::class)
            subclass(ZPointLamp::class)
            subclass(ZSpotLamp::class)
            subclass(ZAmbientLamp::class)
        }

        contextual(ZSkeleton::class) { _ -> ZSkeletonSerializer }
    }
}

private fun createProtoSerializersModule(): ProtoBuf {
    val zObjectModule = createZerializerModule()

    return ProtoBuf {
        serializersModule = zObjectModule
        encodeDefaults = true
    }
}

/**
 * Loads ZKO protobuf payloads into runtime [ZKo] / [ZObject] graphs.
 *
 * @property loaderContext Component registry used for reference deduplication during decode.
 */
@JsExport
class ZkoLoader(
    val loaderContext: ZLoaderContext = ZLoaderContext()
) {

    /**
     * Decodes a byte array into a [ZKo] instance.
     */
    fun load(byteArray: ByteArray): ZKo {
        return loaderSession.withContext(loaderContext) {
            val protoFormat = createProtoSerializersModule()
            val zkoFormat = protoFormat.decodeFromByteArray(ZkoFormat.serializer(), byteArray)
            val root = ZkoHierarchyNode.transformHierarchy(zkoFormat.hierarchy, zkoFormat.objects)
            ZKo(zkoFormat.header, root, zkoFormat.actions)
        }
    }

    internal companion object {
        private val loaderSession = LoaderContextStack()

        internal fun requireLoaderContext(): ZLoaderContext = loaderSession.require()
    }
}

/**
 * Represents the ZKo data structure exposed externally after loading a Zko file.
 *
 * @property header The metadata header associated with the ZKo object. This contains the version information and ensures compatibility.
 * @property root The root [ZObject] of the ZKo structure. This represents the starting point of the object hierarchy.
 * @property actions An optional list of actions ([ZSkeletalAction]) that can be executed or associated with this ZKo object.
 */
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
fun loadFromProto(byteArray: ByteArray): ZKo = ZkoLoader().load(byteArray)
