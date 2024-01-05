package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBaseComponent
import zernikalos.components.ZBaseComponentSerializer
import zernikalos.components.ZComponentData
import zernikalos.math.ZTransform
import zernikalos.utils.findInTree
import zernikalos.utils.treeTraverse
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZSkeletonSerializer::class)
class ZSkeleton internal constructor(data: ZSkeletonData): ZBaseComponent<ZSkeletonData>(data) {

    @JsName("init")
    constructor(): this(ZSkeletonData())

    var root: ZBone by data::root

    val bones: Array<ZBone>
        get() = treeTraverse(root).asSequence().toList().toTypedArray()

    val transform: ZTransform by root::transform

    fun findBoneByName(name: String): ZBone? {
        return findInTree(root) { bone: ZBone -> bone.name == name }
    }

}

@Serializable
data class ZSkeletonData(
    @ProtoNumber(4)
    var root: ZBone = ZBone()
): ZComponentData()

class ZSkeletonSerializer: ZBaseComponentSerializer<ZSkeleton, ZSkeletonData>() {
    override val deserializationStrategy: DeserializationStrategy<ZSkeletonData>
        get() = ZSkeletonData.serializer()

    override fun createComponentInstance(data: ZSkeletonData): ZSkeleton {
        return ZSkeleton(data)
    }

}