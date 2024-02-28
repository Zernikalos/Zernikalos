package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.loader.ZLoaderContext
import zernikalos.math.ZTransform
import zernikalos.utils.findInTree
import zernikalos.utils.treeTraverse
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZSkeleton internal constructor(data: ZSkeletonData): ZRefComponent<ZSkeletonData, ZComponentRender<ZSkeletonData>>(data) {

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
data class ZSkeletonDataWrapper(
    @ProtoNumber(1)
    override var refId: Int,
    @ProtoNumber(2)
    override var isReference: Boolean,
    @ProtoNumber(100)
    override var data: ZSkeletonData? = null
): ZRefComponentData<ZSkeletonData>

@Serializable
data class ZSkeletonData(
    @ProtoNumber(104)
    var root: ZBone = ZBone()
): ZComponentData()

class ZSkeletonSerializer(loaderContext: ZLoaderContext): ZRefComponentSerializer<ZSkeleton, ZSkeletonData, ZSkeletonDataWrapper>(loaderContext) {
    override val deserializationStrategy: DeserializationStrategy<ZSkeletonDataWrapper>
        get() = ZSkeletonDataWrapper.serializer()

    override fun createComponentInstance(data: ZSkeletonData): ZSkeleton {
        return ZSkeleton(data)
    }

}