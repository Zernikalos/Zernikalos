package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject

class ZInverseBindMatrixGenerator: ZUniformGenerator {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject {
        val bindMatrix = sceneContext.getUniform("BindMatrix")?.compute(sceneContext, obj) as ZMatrix4
        return bindMatrix.inverted()
    }
}