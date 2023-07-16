package zernikalos.uniformgenerator

import zernikalos.ZDataType
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject
import zernikalos.ZSceneContext

interface ZUniformGenerator<T> {

    val type: ZDataType

    fun compute(sceneContext: ZSceneContext, obj: ZObject): T

}

interface ZUniformGeneratorMat4F: ZUniformGenerator<ZMatrix4> {
    override val type: ZDataType
        get() = ZDataType.MAT4F
}