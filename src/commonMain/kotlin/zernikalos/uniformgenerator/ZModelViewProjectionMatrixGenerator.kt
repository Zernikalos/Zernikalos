package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZModelViewProjectionMatrixGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4 {
        val modelMatrixGenerator = sceneContext.getUniform("ModelMatrix")
        val viewProjectionMatrix = sceneContext.activeCamera?.viewProjectionMatrix
        if (viewProjectionMatrix == null) {
            return ZMatrix4.Identity
        }
        if (modelMatrixGenerator == null) {
            return viewProjectionMatrix
        }
        val modelMatrix = modelMatrixGenerator.compute(sceneContext, obj)
        return viewProjectionMatrix * modelMatrix
    }
}