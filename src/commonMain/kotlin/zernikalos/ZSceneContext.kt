package zernikalos

import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.uniformgenerator.ZModelMatrixGenerator
import zernikalos.uniformgenerator.ZModelViewProjectionGenerator
import zernikalos.uniformgenerator.ZUniformGeneratorMat4F
import kotlin.js.JsExport

open class ZSceneContext(val scene: ZScene) {

    private var _activeCamera: ZCamera? = null

    val activeCamera: ZCamera?
        get() = _activeCamera

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
        addUniformGenerator("modelMatrix", ZModelMatrixGenerator())
        addUniformGenerator("ModelViewProjection", ZModelViewProjectionGenerator())
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
