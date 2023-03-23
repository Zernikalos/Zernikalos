package zernikalos

import zernikalos.objects.ZkCamera
import zernikalos.objects.ZkScene
import zernikalos.uniformgenerator.ZkModelMatrixGenerator
import zernikalos.uniformgenerator.ZkModelViewProjectionGenerator
import zernikalos.uniformgenerator.ZkUniformGeneratorMat4F
import kotlin.js.JsExport

open class ZkSceneContext(val scene: ZkScene) {

    private var _activeCamera: ZkCamera? = null

    val activeCamera: ZkCamera?
        get() = _activeCamera

    private val uniformsGeneratorMap = HashMap<String, ZkUniformGeneratorMat4F>()

    fun getUniform(key: String): ZkUniformGeneratorMat4F? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

    fun addUniformGenerator(key: String, generator: ZkUniformGeneratorMat4F) {
        uniformsGeneratorMap[key] = generator
    }

}

class ZkSceneContextDefault(scene: ZkScene): ZkSceneContext(scene) {
    init {
        addUniformGenerator("modelMatrix", ZkModelMatrixGenerator())
        addUniformGenerator("ModelViewProjection", ZkModelViewProjectionGenerator())
    }
}

@JsExport
fun createSceneContext(scene: ZkScene): ZkSceneContext {
    return ZkSceneContext(scene)
}

@JsExport
fun createDefaultSceneContext(scene: ZkScene): ZkSceneContext {
    return ZkSceneContextDefault(scene)
}
