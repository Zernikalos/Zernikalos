/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

import zernikalos.components.shader.ZShaderSource
import zernikalos.generators.shadergenerator.libs.defaultFragmentShaderSource
import zernikalos.generators.shadergenerator.libs.defaultVertexShaderSource

internal class ZDefaultShaderGenerator: ZShaderGenerator() {
    override fun buildShaderSource(enabler: ZShaderProgramParameters, source: ZShaderSource) {
        source.glslVertexShaderSource = buildVertexShaderSource(enabler)
        source.glslFragmentShaderSource = buildFragmentShaderSource(enabler)
    }

    private fun buildVertexShaderSource(enabler: ZShaderProgramParameters): String {
        return buildCommonShaderSource(defaultVertexShaderSource, enabler)
    }

    private fun buildFragmentShaderSource(enabler: ZShaderProgramParameters): String {
        return buildCommonShaderSource(defaultFragmentShaderSource, enabler)
    }

    private fun buildCommonShaderSource(initialSource: String, enabler: ZShaderProgramParameters): String {
        var shaderSource = initialSource
        if (enabler.useNormals) shaderSource = "#define USE_NORMALS\n$shaderSource"
        if (enabler.useColors) shaderSource = "#define USE_COLORS\n$shaderSource"
        if (enabler.useTextures) shaderSource = "#define USE_TEXTURES\n$shaderSource"
        if (enabler.flipTextureY) shaderSource = "#define FLIP_TEXTURE_Y\n$shaderSource"
        // if (enabler.useSkinning) shaderSource = "#define USE_SKINNING\n$shaderSource"

        shaderSource = "#version 300 es\n$shaderSource"

        return shaderSource
    }

}