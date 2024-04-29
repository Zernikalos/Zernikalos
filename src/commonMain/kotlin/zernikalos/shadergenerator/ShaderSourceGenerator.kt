package zernikalos.shadergenerator

import zernikalos.components.shader.ZShaderSource
import zernikalos.logger.ZLoggable

class ZAttributesEnabler {
    var usePosition: Boolean = true
    var useNormals: Boolean = false
    var useColors: Boolean = false
    var useTextures: Boolean = false
    var useSkinning: Boolean = false
}

expect class ZShaderSourceGenerator(): ZLoggable {

    fun buildShaderSource(enabler: ZAttributesEnabler, source: ZShaderSource)

}
