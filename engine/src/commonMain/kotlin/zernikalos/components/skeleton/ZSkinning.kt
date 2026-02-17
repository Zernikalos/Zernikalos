package zernikalos.components.skeleton

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZSerializableComponent
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZSerializableComponent<ZSkinningData>(data) {

    @JsName("init")
    constructor(): this(ZSkinningData())

    val boneIds: Array<String>
        get() = data.boneIds.toTypedArray()

    val inverseBindMatrices: Array<ZMatrix4>
        get() = data.inverseBindMatrices.toTypedArray()

    fun addBoneId(boneId: String) {
        data.boneIds.add(boneId)
    }

    fun removeBoneId(boneId: String) {
        data.boneIds.remove(boneId)
    }

    fun addInverseBindMatrices(matrices: List<ZMatrix4>) {
        data.inverseBindMatrices.addAll(matrices)
    }

    fun addInverseBindMatrix(matrix: ZMatrix4) {
        data.inverseBindMatrices.add(matrix)
    }

    @JsName("addInverseBindMatrixAt")
    fun addInverseBindMatrix(idx: Int, matrix: ZMatrix4) {
        data.inverseBindMatrices.add(idx, matrix)
    }

}

@Serializable
data class ZSkinningData(
    @ProtoNumber(10)
    val boneIds: ArrayList<String> = ArrayList(),
    @ProtoNumber(11)
    val inverseBindMatrices: ArrayList<ZMatrix4> = ArrayList()
): ZComponentData()

class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val kSerializer: KSerializer<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }
}
