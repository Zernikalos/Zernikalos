package zernikalos

import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.ui.ZSurfaceView
import zernikalos.uniformgenerator.*
import kotlin.js.JsExport

@JsExport
open class ZSceneContext {

    var scene: ZScene? = null

    var activeCamera: ZCamera? = null

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

class ZSceneContextDefault(): ZSceneContext() {
    init {
        addUniformGenerator("ModelViewProjectionMatrix", ZModelViewProjectionMatrixGenerator())
        addUniformGenerator("ProjectionMatrix", ZProjectionMatrixGenerator())
        addUniformGenerator("ModelMatrix", ZModelMatrixGenerator())
    }
}

@JsExport
fun createSceneContext(): ZSceneContext {
    return ZSceneContext()
}

@JsExport
fun createDefaultSceneContext(): ZSceneContext {
    return ZSceneContextDefault()
}
