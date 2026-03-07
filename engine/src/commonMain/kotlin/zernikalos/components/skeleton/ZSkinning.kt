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

/**
 * Skin binding between a mesh and a skeleton for skeletal animation.
 *
 * Holds the list of bone IDs that influence the mesh, their per-bone inverse bind matrices,
 * and an optional skin-level bind matrix pair used when the mesh node and skeleton root
 * do not share the same transform (e.g. in glTF when skin and mesh are on different nodes).
 *
 * Typically created by loaders/exporters and attached to a [ZModel] together with a [ZSkeleton].
 */
@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZSerializableComponent<ZSkinningData>(data) {

    @JsName("init")
    constructor(): this(ZSkinningData())

    /**
     * Transform from mesh local space to skeleton (bind) space at bind time.
     * Use identity when the mesh and skeleton share the same node; set from asset data
     * when they differ (e.g. glTF skin on a different node than the mesh).
     */
    var modelSkinBindMatrix: ZMatrix4 by data::modelSkinBindMatrix

    /**
     * Inverse of [modelSkinBindMatrix]. Transforms from skeleton space back to mesh local space.
     */
    val inverseModelSkinBindMatrix: ZMatrix4 by data::inverseModelSkinBindMatrix

    /**
     * Ordered list of bone IDs that influence this skin. Order must match [inverseBindMatrices].
     */
    val boneIds: Array<String>
        get() = data.boneIds.toTypedArray()

    /**
     * Per-bone inverse bind matrices (bind pose bone world → bone local). One per entry in [boneIds].
     */
    val inverseBindMatrices: Array<ZMatrix4>
        get() = data.inverseBindMatrices.toTypedArray()

    /** Appends a bone ID to the skin. */
    fun addBoneId(boneId: String) {
        data.boneIds.add(boneId)
    }

    /** Removes the first occurrence of [boneId] from the skin. */
    fun removeBoneId(boneId: String) {
        data.boneIds.remove(boneId)
    }

    /** Appends all [matrices] to the inverse bind matrix list. Order must match [boneIds]. */
    fun addInverseBindMatrices(matrices: List<ZMatrix4>) {
        data.inverseBindMatrices.addAll(matrices)
    }

    /** Appends a single inverse bind matrix. Order must match [boneIds]. */
    fun addInverseBindMatrix(matrix: ZMatrix4) {
        data.inverseBindMatrices.add(matrix)
    }

    /** Inserts [matrix] at [idx] in the inverse bind matrix list. */
    @JsName("addInverseBindMatrixAt")
    fun addInverseBindMatrix(idx: Int, matrix: ZMatrix4) {
        data.inverseBindMatrices.add(idx, matrix)
    }

    override fun internalDispose() {
        // Large CPU arrays (boneIds, inverseBindMatrices) live in data; no explicit clear API yet.
    }

}

/**
 * @suppress
 */
@Serializable
data class ZSkinningData(
    @ProtoNumber(1)
    var modelSkinBindMatrix: ZMatrix4 = ZMatrix4(),
    @ProtoNumber(10)
    val boneIds: ArrayList<String> = ArrayList(),
    @ProtoNumber(11)
    val inverseBindMatrices: ArrayList<ZMatrix4> = ArrayList()
): ZComponentData() {

    /** Inverse of [modelSkinBindMatrix]; derived for shader use. */
    val inverseModelSkinBindMatrix: ZMatrix4
        get() = modelSkinBindMatrix.inverted()
}

/**
 * @suppress
 */
class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val kSerializer: KSerializer<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }
}
