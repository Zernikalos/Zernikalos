package zernikalos.uniformgenerator

import zernikalos.math.ZkMatrix4F
import zernikalos.objects.ZkObject
import zernikalos.ZkSceneContext
import zernikalos.ZkUniformType

interface ZkUniformGenerator<T> {

    val type: ZkUniformType

    fun compute(sceneContext: ZkSceneContext, obj: ZkObject): T

}

interface ZkUniformGeneratorMat4F: ZkUniformGenerator<ZkMatrix4F> {
    override val type: ZkUniformType
        get() = ZkUniformType.MAT4
}