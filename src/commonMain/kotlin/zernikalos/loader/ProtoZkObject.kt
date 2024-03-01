package zernikalos.loader

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.*

@Serializable
data class ProtoZkObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val scene: ZScene?,
    @ProtoNumber(3) val group: ZGroup?,
    @Contextual @ProtoNumber(4) val model: ZModel?,
    @ProtoNumber(5) val camera: ZCamera?,
    @ProtoNumber(6) val skeleton: ZSkeleton?,
    @ProtoNumber(100) val children: Array<ProtoZkObject>? = emptyArray()
) {
    val zObject: ZObject
        get() {
            val obj = detectZObject()
            fillChildren(obj)
            return obj
        }

    private fun fillChildren(obj: ZObject) {
        children?.forEach { child ->
            obj.addChild(child.zObject)
        }
    }
    private fun detectZObject(): ZObject {
        when (type) {
            ZObjectType.SCENE.name -> return scene!!
            ZObjectType.GROUP.name -> return group!!
            ZObjectType.MODEL.name -> return model!!
            ZObjectType.CAMERA.name -> return camera!!
            ZObjectType.SKELETON.name -> return skeleton!!
        }
        throw Error("Type has not been found on object")
    }
}