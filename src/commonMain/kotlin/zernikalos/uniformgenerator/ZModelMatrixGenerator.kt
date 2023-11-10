package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject
import zernikalos.upToRoot

class ZModelMatrixGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4 {
        var m = ZMatrix4.Identity
        for (parentObj in upToRoot(obj)) {
            val parentMatrix = parentObj.transform.matrix
            m = parentMatrix * m
        }
        return m
    }
}