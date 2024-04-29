package zernikalos.shadergenerator

import zernikalos.components.shader.ZShaderSource
import zernikalos.shadergenerator.libs.defaultFragmentShaderSource
import zernikalos.shadergenerator.libs.defaultVertexShaderSource
import zernikalos.logger.ZLoggable

actual class ZShaderSourceGenerator : ZLoggable {
    actual fun buildShaderSource(enabler: ZAttributesEnabler, source: ZShaderSource) {
        source.glslVertexShaderSource = buildVertexShaderSource(enabler)
        source.glslFragmentShaderSource = buildFragmentShaderSource(enabler)
    }

    private fun buildVertexShaderSource(enabler: ZAttributesEnabler): String {
        return buildCommonShaderSource(defaultVertexShaderSource, enabler)
    }

    private fun buildFragmentShaderSource(enabler: ZAttributesEnabler): String {
        return buildCommonShaderSource(defaultFragmentShaderSource, enabler)
    }

    private fun buildCommonShaderSource(initialSource: String, enabler: ZAttributesEnabler): String {
        var shaderSource = initialSource
        if (enabler.useNormals) shaderSource = "#define USE_NORMALS\n$shaderSource"
        if (enabler.useColors) shaderSource = "#define USE_COLORS\n$shaderSource"
        if (enabler.useTextures) shaderSource = "#define USE_TEXTURES\n$shaderSource"

        shaderSource = "#version 300 es\n$shaderSource"

        return shaderSource
    }

}