package mr.robotto.uniformgenerator

import mr.robotto.math.MrMatrix4f
import mr.robotto.objects.MrObject
import mr.robotto.MrSceneContext
import mr.robotto.MrUniformType

interface MrUniformGenerator<T> {

    val type: MrUniformType

    fun compute(sceneContext: MrSceneContext, obj: MrObject): T

}

interface MrUniformGeneratorMat4f: MrUniformGenerator<MrMatrix4f> {
    override val type: MrUniformType
        get() = MrUniformType.MAT4
}