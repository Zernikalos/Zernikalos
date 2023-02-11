package mr.robotto.uniformgenerator

import mr.robotto.MrSceneContext
import mr.robotto.math.MrMatrix4f
import mr.robotto.objects.MrObject
import upToRoot

class MrModelMatrixGenerator: MrUniformGeneratorMat4f {
    override fun compute(sceneContext: MrSceneContext, obj: MrObject): MrMatrix4f {
        var m = MrMatrix4f.Identity
        for (parentObj in upToRoot(obj)) {
            val parentMatrix = parentObj.transform.matrix
            m = parentMatrix * m
        }
        return m
    }
}