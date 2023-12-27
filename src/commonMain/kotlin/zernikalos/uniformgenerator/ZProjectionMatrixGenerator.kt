package zernikalos.uniformgenerator

import zernikalos.ZTypes
import zernikalos.context.ZSceneContext
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZProjectionMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZUniformValue {
        val activeCamera = sceneContext.activeCamera
        if (activeCamera != null) {
            return ZUniformValue(ZTypes.MAT4F, activeCamera.projectionMatrix)
        }
        return ZUniformValue(ZTypes.MAT4F, ZMatrix4.Identity)
    }
}