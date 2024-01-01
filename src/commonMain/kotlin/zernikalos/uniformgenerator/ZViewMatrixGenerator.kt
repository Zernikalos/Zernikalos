package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZViewMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        val viewMatrix = sceneContext.activeCamera?.viewMatrix
        if (viewMatrix == null) {
            return ZMatrix4.Identity
        }
        return viewMatrix
    }
}