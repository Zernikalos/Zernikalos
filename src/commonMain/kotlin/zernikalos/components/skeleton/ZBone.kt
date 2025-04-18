/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.skeleton

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.action.ZKeyFrame
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZSerializableComponent
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZMatrix4
import zernikalos.math.ZTransform
import zernikalos.search.ZTreeNode
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBoneSerializer::class)
class ZBone internal constructor(data: ZBoneData): ZSerializableComponent<ZBoneData>(data), ZTreeNode<ZBone> {

    @JsName("init")
    constructor(): this(ZBoneData())

    /**
     * Returns the unique identifier of this bone.
     */
    var id: String by data::id

    /**
     * Returns the name of this bone.
     */
    var name: String by data::name

    /**
     * Returns the transform of this bone.
     */
    var transform: ZTransform by data::transform

    /**
     * Returns the inverse bind transform of this bone.
     */
    /**
     * Returns the bind matrix of this bone.
     */
    var bindMatrix: ZMatrix4 = ZMatrix4.Identity

    /**
     * Returns the inverse bind matrix of this bone.
     */
    var inverseBindMatrix: ZMatrix4 = ZMatrix4.Identity

    /**
     * Returns the pose matrix of this bone.
     */
    var poseMatrix: ZMatrix4 = ZMatrix4.Identity

    @Transient
    internal var _parent: ZBone? = null
    override val parent: ZBone?
        get() = _parent

    /**
     * Returns true if this bone has a parent, false otherwise.
     */
    override val hasParent: Boolean
        get() = parent != null

    /**
     * Returns the children of this bone.
     */
    override val children: Array<ZBone>
        get() = data.children.toTypedArray()

    init {
        children.forEach { b ->
            b._parent = this
        }
    }

    /**
     * Add a child bone to this bone.
     * @param bone the bone to add
     */
    fun addChild(bone: ZBone) {
        data.children.add(bone)
        bone._parent = this
    }

    override fun internalInitialize(ctx: ZRenderingContext) {
        if (isRoot) {
            computeInverseBindMatrix(ZMatrix4.Identity)
        }
    }

    /**
     * Compute the pose matrix for this bone and its children from the given keyframe.
     * @param keyFrame the keyframe to compute the pose from
     * @param parentPoseMatrix the pose matrix of the parent bone
     */
    fun computePoseFromKeyFrame(keyFrame: ZKeyFrame, parentPoseMatrix: ZMatrix4) {
        val boneTransform = keyFrame.getBoneTransform(name)

        val localPoseMat = if (boneTransform != null) {
            val animationTransform = boneTransform.toTransform()
            animationTransform.matrix
        } else {
            bindMatrix
        }

        ZMatrix4.mult(poseMatrix, parentPoseMatrix, localPoseMat)

        for (child in children) {
            child.computePoseFromKeyFrame(keyFrame, poseMatrix)
        }
    }

    /**
     * Compute the inverse bind matrix for this bone and its children.
     * Should be called after all the bones have been added to the skeleton.
     * @param parentBindMatrix the bind matrix of the parent bone
     */
    private fun computeInverseBindMatrix(parentBindMatrix: ZMatrix4) {
        bindMatrix = ZMatrix4()
        ZMatrix4.mult(bindMatrix, parentBindMatrix, transform.matrix)
        ZMatrix4.invert(inverseBindMatrix, bindMatrix)
        for (child in children) {
            child.computeInverseBindMatrix(bindMatrix)
        }
    }
}

@Serializable
data class ZBoneData(
    @ProtoNumber(1)
    var id: String = "",
    @ProtoNumber(2)
    var name: String = "",
    @ProtoNumber(4)
    var transform: ZTransform = ZTransform(),
    @ProtoNumber(5)
    val children: ArrayList<ZBone> = arrayListOf()
): ZComponentData()


class ZBoneSerializer: ZComponentSerializer<ZBone, ZBoneData>() {
    override val kSerializer: KSerializer<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}