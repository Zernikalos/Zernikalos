package mr.robotto

import mr.robotto.objects.MrCamera
import mr.robotto.objects.MrScene
import mr.robotto.uniformgenerator.MrModelMatrixGenerator
import mr.robotto.uniformgenerator.MrModelViewProjectionGenerator
import mr.robotto.uniformgenerator.MrUniformGenerator
import mr.robotto.uniformgenerator.MrUniformGeneratorMat4f
import kotlin.js.JsExport

open class MrSceneContext(val scene: MrScene) {

    private var _activeCamera: MrCamera? = null

    val activeCamera: MrCamera?
        get() = _activeCamera

    private val uniformsGeneratorMap = HashMap<String, MrUniformGeneratorMat4f>()

    fun getUniform(key: String): MrUniformGeneratorMat4f? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

    fun addUniformGenerator(key: String, generator: MrUniformGeneratorMat4f) {
        uniformsGeneratorMap[key] = generator
    }

}

class MrSceneContextDefault(scene: MrScene): MrSceneContext(scene) {
    init {
        addUniformGenerator("modelMatrix", MrModelMatrixGenerator())
        addUniformGenerator("ModelViewProjection", MrModelViewProjectionGenerator())
    }
}

@JsExport
fun createSceneContext(scene: MrScene): MrSceneContext {
    return MrSceneContext(scene)
}

@JsExport
fun createDefaultSceneContext(scene: MrScene): MrSceneContext {
    return MrSceneContextDefault(scene)
}
