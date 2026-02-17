/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.skeleton.ZBone
import zernikalos.context.ZContext
import zernikalos.loader.ZLoaderContext
import zernikalos.search.findInTree
import zernikalos.search.treeAsList
import kotlin.js.JsExport

@JsExport
@Serializable
class ZSkeleton: ZObject() {

    override val type = ZObjectType.SKELETON

    @ProtoNumber(101)
    var root: ZBone = ZBone()

    val bones: Array<ZBone>
        get() = treeAsList(root).toTypedArray()

    fun findBoneByName(name: String): ZBone? {
        return findInTree(root) { bone: ZBone -> bone.name == name }
    }

    override fun internalInitialize(ctx: ZContext) {
        root.initialize(ctx.renderingContext)
    }

    override fun internalRender(ctx: ZContext) {
    }
}

@Serializable
data class ZSkeletonProtoRef(
    @ProtoNumber(1)
    val type: String,
    @ProtoNumber(2)
    val refId: String = "",
    @ProtoNumber(3)
    val isReference: Boolean = false,
    @ProtoNumber(100)
    val data: ZSkeleton? = null
)

class ZSkeletonSerializer(private val loaderContext: ZLoaderContext): KSerializer<ZSkeleton> {
    override val descriptor: SerialDescriptor
        get() = ZSkeletonProtoRef.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZSkeleton) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): ZSkeleton {
        val data = decoder.decodeSerializableValue(ZSkeletonProtoRef.serializer())
        return if (data.isReference) {
            loaderContext.getComponent(data.refId) as ZSkeleton
        } else {
            val skeleton = data.data!!
            loaderContext.addComponent(data.refId, skeleton)
            skeleton
        }
    }

}