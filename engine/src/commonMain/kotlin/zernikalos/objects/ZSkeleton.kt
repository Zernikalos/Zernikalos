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
import zernikalos.action.ZKeyFrame
import zernikalos.components.skeleton.ZBone
import zernikalos.context.ZContext
import zernikalos.loader.ZLoaderContext
import zernikalos.math.ZMatrix4
import zernikalos.math.ZTransform
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

    /**
     * Writes [keyFrame] into this skeleton’s [root] hierarchy as world [ZBone.poseMatrix] values.
     *
     * **Merge rule (per bone, TRS channels):** each bone starts from its rest [ZBone.transform].
     * For every channel (position, rotation, scale), if [keyFrame] carries a value for that bone,
     * the sampled value **replaces** the rest value for that channel only; omitted channels keep
     * the rest pose. This is not additive delta blending (future multi-clip blending would define
     * its own rules on top of this baseline).
     *
     * @param keyFrame Sampled local overrides per bone id (typically from [zernikalos.action.ZSkeletalAction.sampleAt]).
     * @param parentWorldPose Parent world matrix for [root]; use identity when [root] is the scene root.
     */
    fun applyKeyFrame(keyFrame: ZKeyFrame, parentWorldPose: ZMatrix4 = ZMatrix4.Identity) {
        applyKeyFrameToBone(root, keyFrame, parentWorldPose)
    }

    private fun applyKeyFrameToBone(bone: ZBone, keyFrame: ZKeyFrame, parentWorldPose: ZMatrix4) {
        val boneTransform = keyFrame.getBoneTransform(bone.id)
        // Merge rest transform with any animated components from the sample (per-channel replace).
        val merged = ZTransform(bone.transform.position, bone.transform.rotation, bone.transform.scale)
        if (boneTransform != null) {
            boneTransform.position?.let { merged.position = it }
            boneTransform.rotation?.let { merged.rotation = it }
            boneTransform.scale?.let { merged.scale = it }
        }
        val localPoseMat = merged.matrix
        ZMatrix4.mult(bone.poseMatrix, parentWorldPose, localPoseMat)
        for (child in bone.children) {
            applyKeyFrameToBone(child, keyFrame, bone.poseMatrix)
        }
    }

    override fun internalInitialize(ctx: ZContext) {
        root.initialize(ctx.renderingContext)
    }

    override fun internalRender(ctx: ZContext) {
    }

    override fun internalDispose(ctx: ZContext) {
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
