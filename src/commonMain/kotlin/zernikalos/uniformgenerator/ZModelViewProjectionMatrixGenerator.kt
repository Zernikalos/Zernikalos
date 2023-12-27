package zernikalos.uniformgenerator

import zernikalos.ZTypes
import zernikalos.context.ZSceneContext
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZModelViewProjectionMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZUniformValue {
        val modelMatrixGenerator = sceneContext.getUniform("ModelMatrix")
        val viewProjectionMatrix = sceneContext.activeCamera?.viewProjectionMatrix
        if (viewProjectionMatrix == null) {
            return ZUniformValue(
                ZTypes.MAT4F,
                ZMatrix4.Identity
            )
        }
        if (modelMatrixGenerator == null) {
            return ZUniformValue(ZTypes.MAT4F, viewProjectionMatrix)
        }
        val modelMatrix = modelMatrixGenerator.compute(sceneContext, obj).value as ZMatrix4
        return ZUniformValue(
            ZTypes.MAT4F,
            viewProjectionMatrix * modelMatrix)
    }
}