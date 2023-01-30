package mr.robotto.uniformgenerator

import mr.robotto.math.MrMatrix4f
import mr.robotto.objects.MrObject
import mr.robotto.objects.MrSceneContext

interface MrUniformGenerator<T> {

    fun compute(sceneContext: MrSceneContext, obj: MrObject): T

}

interface MrUniformGeneratorMat4f: MrUniformGenerator<MrMatrix4f>