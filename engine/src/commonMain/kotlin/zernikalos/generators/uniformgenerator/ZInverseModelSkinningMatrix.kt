package zernikalos.generators.uniformgenerator

import zernikalos.objects.ZModel

val ZInverseModelSkinningMatrixGenerator: ZUniformGenerator = { sceneContext, obj ->
    obj as ZModel
    if (!obj.hasSkeleton) {
        throw Error("Unable to compute inverse model skinning matrix without an skeleton attached to object ${obj.name}")
    }
    val skeleton = obj.skeleton!!
    val bones = skeleton.bones
    val skinning = obj.skinning!!
    val inverseModelSkinningMatrix = skinning.inverseModelSkinBindMatrix
    inverseModelSkinningMatrix
}
