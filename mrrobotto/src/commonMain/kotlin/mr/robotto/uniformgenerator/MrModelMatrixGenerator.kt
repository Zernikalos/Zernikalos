package mr.robotto.uniformgenerator

import mr.robotto.MrSceneContext
import mr.robotto.math.MrMatrix4f
import mr.robotto.objects.MrObject

class MrModelMatrixGenerator: MrUniformGeneratorMat4f {
    override fun compute(sceneContext: MrSceneContext, obj: MrObject): MrMatrix4f {
        return obj.transform.matrix
    }
}