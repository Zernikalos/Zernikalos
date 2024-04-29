package zernikalos.shadergenerator

import zernikalos.components.shader.ZShaderSource
import zernikalos.logger.ZLoggable
import zernikalos.shadergenerator.libs.*

actual class ZShaderSourceGenerator : ZLoggable {

    private fun buildMacrosFromEnabler(enabler: ZAttributesEnabler): String {
        var source = ""

        if (enabler.usePosition) source = "#define USE_POSITION\n$source"
        if (enabler.useNormals) source = "#define USE_NORMAL\n$source"
        if (enabler.useColors) source = "#define USE_COLOR\n$source"
        if (enabler.useTextures) source = "#define USE_TEXTURE\n$source"

        return source
    }

    actual fun buildShaderSource(
        enabler: ZAttributesEnabler,
        source: ZShaderSource
    ) {
        val shaderSourceStr = """
            $shaderCommonHeaders
            ${buildMacrosFromEnabler(enabler)}
            $shaderUniforms
            $shaderVertexDefinitions
            $shaderFragmentDefinitions
            $shaderVertexMain
            $shaderFragmentMain
        """.trimIndent()
        source.metalShaderSource = shaderSourceStr
    }

}