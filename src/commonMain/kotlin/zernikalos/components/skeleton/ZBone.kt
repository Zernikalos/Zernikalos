package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZTreeNode
import zernikalos.components.*
import zernikalos.math.ZTransform
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBoneSerializer::class)
class ZBone internal constructor(data: ZBoneData): ZComponent<ZBoneData, ZComponentRender<ZBoneData>>(data), ZTreeNode<ZBone> {

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
    override val deserializationStrategy: DeserializationStrategy<ZBoneData>
        get() = ZBoneData.serializer()

    override fun createComponentInstance(data: ZBoneData): ZBone {
        return ZBone(data)
    }

}