package zernikalos.generators.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZVoidAlgebraObject
import zernikalos.objects.ZObject

class ZVoidGenerator: ZUniformGenerator {
    override fun compute(
        sceneContext: ZSceneContext,
        obj: ZObject
    ): ZAlgebraObject {
        return ZVoidAlgebraObject()
    }
}
