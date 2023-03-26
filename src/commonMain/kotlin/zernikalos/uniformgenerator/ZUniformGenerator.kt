package zernikalos.uniformgenerator

import zernikalos.math.ZMatrix4F
import zernikalos.objects.ZObject
import zernikalos.ZSceneContext
import zernikalos.components.shader.ZUniformType

interface ZUniformGenerator<T> {

    val type: ZUniformType

    fun compute(sceneContext: ZSceneContext, obj: ZObject): T

}

interface ZUniformGeneratorMat4F: ZUniformGenerator<ZMatrix4F> {
    override val type: ZUniformType
        get() = ZUniformType.MAT4
}