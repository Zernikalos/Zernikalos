package zernikalos.uniformgenerator

import zernikalos.ZSceneContext
import zernikalos.math.ZMatrix4F
import zernikalos.objects.ZObject
import zernikalos.upToRoot

class ZModelViewProjectionGenerator: ZUniformGeneratorMat4F {
    override fun compute(sceneContext: ZSceneContext, obj: ZObject): ZMatrix4F {
        var m = ZMatrix4F.Identity
        for (parentObj in upToRoot(obj)) {
            val parentMatrix = parentObj.transform.matrix
            m = parentMatrix * m
        }
        return m
    }
}