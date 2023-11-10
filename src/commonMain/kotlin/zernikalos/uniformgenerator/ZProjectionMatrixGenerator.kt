package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZProjectionMatrixGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4 {
        val activeCamera = sceneContext.activeCamera
        if (activeCamera != null) {
            return activeCamera.projectionMatrix
        }
        return ZMatrix4.Identity
    }
}