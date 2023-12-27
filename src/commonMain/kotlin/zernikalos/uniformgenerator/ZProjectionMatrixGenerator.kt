package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZProjectionMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        val activeCamera = sceneContext.activeCamera
        if (activeCamera != null) {
            return activeCamera.projectionMatrix
        }
        return ZMatrix4.Identity
    }
}