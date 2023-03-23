package zernikalos.uniformgenerator

import zernikalos.ZkSceneContext
import zernikalos.math.ZkMatrix4F
import zernikalos.objects.ZkObject
import zernikalos.upToRoot

class ZkModelViewProjectionGenerator: ZkUniformGeneratorMat4F {
    override fun compute(sceneContext: ZkSceneContext, obj: ZkObject): ZkMatrix4F {
        var m = ZkMatrix4F.Identity
        for (parentObj in upToRoot(obj)) {
            val parentMatrix = parentObj.transform.matrix
            m = parentMatrix * m
        }
        return m
    }
}