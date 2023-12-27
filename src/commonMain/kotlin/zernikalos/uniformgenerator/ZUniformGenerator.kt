package zernikalos.uniformgenerator

import zernikalos.objects.ZObject
import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import kotlin.js.JsExport

@JsExport
interface ZUniformGenerator {

    fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject

}
