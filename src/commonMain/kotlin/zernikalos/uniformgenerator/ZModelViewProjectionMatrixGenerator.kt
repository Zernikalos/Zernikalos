package zernikalos.uniformgenerator

import zernikalos.ZSceneContext
import zernikalos.math.ZMatrix4F
import zernikalos.objects.ZObject

class ZModelViewProjectionMatrixGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4F {
        val modelMatrixGenerator = sceneContext.getUniform("ModelMatrix")
        val viewProjectionMatrix = sceneContext.activeCamera?.viewProjectionMatrix
        if (viewProjectionMatrix == null) {
            return ZMatrix4F.Identity
        }
        if (modelMatrixGenerator == null) {
            return viewProjectionMatrix
        }
        val modelMatrix = modelMatrixGenerator.compute(sceneContext, obj)
        return viewProjectionMatrix * modelMatrix
    }
}