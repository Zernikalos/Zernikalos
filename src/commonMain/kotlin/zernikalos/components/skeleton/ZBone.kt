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

    var id: String by data::id

    var name: String by data::name

    var idx: Int by data::idx

    var transform: ZTransform by data::transform

    var inverseBindTransform: ZTransform? by data::inverseBindTransform

    var bindMatrix: ZMatrix4 = ZMatrix4.Identity
    var inverseBindMatrix: ZMatrix4 = ZMatrix4.Identity
    var poseMatrix: ZMatrix4 = ZMatrix4.Identity

    @Transient
    internal var _parent: ZBone? = null
    override val parent: ZBone?
        get() = _parent

    override val hasParent: Boolean
        get() = parent != null

    override val children: Array<ZBone>
        get() = data.children.toTypedArray()

    init {
        children.forEach { b ->
            b._parent = this
        }
    }

    fun addChild(bone: ZBone) {
        data.children.add(bone)
        bone._parent = this
    }

    override fun internalInitialize(ctx: ZRenderingContext) {
        if (isRoot) {
            computeInverseBindMatrix(ZMatrix4.Identity)
            computePose(ZMatrix4.Identity)
        }
    }

    fun computePose(parentPoseMatrix: ZMatrix4) {
        val currentLocalPoseMatrix = ZMatrix4()
        val currentPoseMatrix = ZMatrix4()
        ZMatrix4.mult(currentPoseMatrix, parentPoseMatrix, currentLocalPoseMatrix)
        for (child in children) {
            child.computePose(currentPoseMatrix)
        }
        val globalPoseMatrix =  ZMatrix4()
        ZMatrix4.mult(globalPoseMatrix, inverseBindMatrix, currentPoseMatrix)
        poseMatrix = globalPoseMatrix
    }

    fun computePoseFromKeyFrame(keyFrame: ZKeyFrame, parentPoseMatrix: ZMatrix4) {
        val poseMat = keyFrame.getBoneTransform(name)!!.toTransform().matrix
        ZMatrix4.mult(poseMatrix, parentPoseMatrix, poseMat)
        for (child in children) {
            child.computePoseFromKeyFrame(keyFrame, poseMatrix)
        }
    }

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
    @ProtoNumber(3)
    var idx: Int = -1,
    @ProtoNumber(4)
    var transform: ZTransform = ZTransform(),
    @ProtoNumber(5)
    val children: ArrayList<ZBone> = arrayListOf(),

    @ProtoNumber(6)
    var inverseBindTransform: ZTransform? = ZTransform(),
): ZComponentData()


class ZBoneSerializer: ZComponentSerializer<ZBone, ZBoneData>() {
    override val kSerializer: KSerializer<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}