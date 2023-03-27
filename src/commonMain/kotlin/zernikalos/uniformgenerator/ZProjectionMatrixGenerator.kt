package zernikalos.uniformgenerator

import zernikalos.ZSceneContext
import zernikalos.math.ZMatrix4F
import zernikalos.objects.ZObject

class ZProjectionMatrixGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4F {
        val activeCamera = sceneContext.activeCamera
        if (activeCamera != null) {
            return activeCamera.projectionMatrix
        }
        return ZMatrix4F.Identity
    }
}