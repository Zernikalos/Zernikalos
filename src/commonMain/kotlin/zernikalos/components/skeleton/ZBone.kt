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
import zernikalos.search.ZTreeNode
import zernikalos.components.ZBasicComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import zernikalos.math.ZTransform
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBoneSerializer::class)
class ZBone internal constructor(data: ZBoneData): ZBasicComponent<ZBoneData>(data), ZTreeNode<ZBone> {

    @JsName("init")
    constructor(): this(ZBoneData())

    var id: String by data::id

    var name: String by data::name

    var idx: Int by data::idx

    var transform: ZTransform by data::transform

    override var parent: ZBone? = null
        get() = data._parent

    override val hasParent: Boolean
        get() = parent != null

    override val children: Array<ZBone>
        get() = data.children.toTypedArray()

    fun addChild(bone: ZBone) {
        data.children.add(bone)
        bone.data._parent = this
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
    val children: ArrayList<ZBone> = arrayListOf()
): ZComponentData() {

    @Transient
    internal var _parent: ZBone? = null

}

class ZBoneSerializer: ZComponentSerializer<ZBone, ZBoneData>() {
    override val kSerializer: KSerializer<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}