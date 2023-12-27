package zernikalos.uniformgenerator

import zernikalos.ZDataType
import zernikalos.math.ZMatrix4
import zernikalos.objects.ZObject
import zernikalos.context.ZSceneContext
import zernikalos.ZTypes
import kotlin.js.JsExport

@JsExport
interface ZUniformGenerator {

    fun compute(sceneContext: ZSceneContext, obj: ZObject): ZUniformValue

}
