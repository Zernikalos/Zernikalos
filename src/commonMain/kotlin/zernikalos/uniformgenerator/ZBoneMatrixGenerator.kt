package zernikalos.uniformgenerator

import zernikalos.ZTypes
import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObjectCollection
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject

class ZBoneMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZUniformValue {
        obj as ZModel
        if (obj.skeleton == null) {
            throw Error("Unable to compute bone matrices without an skeleton attached to object ${obj.name}")
        }
        val skeleton = obj.skeleton!!
        val bones = skeleton.bones

        val boneMatrices = bones.map {
            it.transform.matrix
        }

        val boneCollection = ZAlgebraObjectCollection(ZTypes.MAT4F, bones.size)
        boneCollection.addAll(0, boneMatrices)

        return ZUniformValue(ZTypes.MAT4F, boneCollection)
    }
}