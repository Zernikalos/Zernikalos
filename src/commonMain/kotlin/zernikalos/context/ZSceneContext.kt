package zernikalos.context

import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.uniformgenerator.*
import kotlin.js.JsExport

@JsExport
open class ZSceneContext {

    var scene: ZScene? = null

    var activeCamera: ZCamera? = null

    private val uniformsGeneratorMap = HashMap<String, ZUniformGenerator>()

    fun getUniform(key: String): ZUniformGenerator? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

    fun addUniformGenerator(key: String, generator: ZUniformGenerator) {
        uniformsGeneratorMap[key] = generator
    }

}

class ZSceneContextDefault(): ZSceneContext() {
    init {
        addUniformGenerator("ModelViewProjectionMatrix", ZModelViewProjectionMatrixGenerator())
        addUniformGenerator("ViewMatrix", ZViewMatrixGenerator())
        addUniformGenerator("ProjectionMatrix", ZProjectionMatrixGenerator())
        addUniformGenerator("ModelMatrix", ZModelMatrixGenerator())
        addUniformGenerator("Bones", ZBoneMatrixGenerator())
        addUniformGenerator("BindMatrix", ZBindMatrixGenerator())
        addUniformGenerator("InverseBindMatrix", ZInverseBindMatrixGenerator())
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
