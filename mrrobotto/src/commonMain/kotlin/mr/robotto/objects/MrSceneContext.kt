package mr.robotto.objects

import mr.robotto.uniformgenerator.MrUniformGenerator
import kotlin.js.JsExport

@JsExport
class MrSceneContext(val scene: MrScene) {

    private var _activeCamera: MrCamera? = null

    val activeCamera: MrCamera?
        get() = _activeCamera

    private val uniformsGeneratorMap = HashMap<String, MrUniformGenerator<*>>()

    fun getUniform(key: String): MrUniformGenerator<*>? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

}

@JsExport
fun createSceneContext(scene: MrScene): MrSceneContext {
    return MrSceneContext(scene)
}
