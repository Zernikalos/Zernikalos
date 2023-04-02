package zernikalos

import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.uniformgenerator.*
import kotlin.js.JsExport

open class ZSceneContext(val scene: ZScene) {

    private var _activeCamera: ZCamera? = null

    var activeCamera: ZCamera?
        get() = _activeCamera
        set(value) {
            _activeCamera = value
        }

    private val uniformsGeneratorMap = HashMap<String, ZUniformGeneratorMat4F>()

    fun getUniform(key: String): ZUniformGeneratorMat4F? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

    fun addUniformGenerator(key: String, generator: ZUniformGeneratorMat4F) {
        uniformsGeneratorMap[key] = generator
    }

}

class ZSceneContextDefault(scene: ZScene): ZSceneContext(scene) {
    init {
        addUniformGenerator("ModelViewProjectionMatrix", ZModelViewProjectionMatrixGenerator())
        addUniformGenerator("ProjectionMatrix", ZProjectionMatrixGenerator())
        addUniformGenerator("ModelMatrix", ZModelMatrixGenerator())
    }
}

@JsExport
fun createSceneContext(scene: ZScene): ZSceneContext {
    return ZSceneContext(scene)
}

@JsExport
fun createDefaultSceneContext(scene: ZScene): ZSceneContext {
    return ZSceneContextDefault(scene)
}
