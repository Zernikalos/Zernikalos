/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

/**
 * Preprocesses WGSL shader source code by evaluating conditional directives.
 *
 * Supports the following directives:
 * - `//#ifdef MACRO_NAME` - includes following code if macro is defined
 * - `//#ifndef MACRO_NAME` - includes following code if macro is NOT defined
 * - `//#else` - alternative branch
 * - `//#endif` - ends conditional block
 *
 * Example usage in shader:
 * ```
 * //#ifdef USE_NORMALS
 *     @location(1) normal: vec3<f32>,
 * //#endif
 * ```
 */
internal fun shaderPreprocessor(source: String, enabler: ZShaderProgramParameters): String {
    val defines = buildDefinesSet(enabler)
    return processDirectives(source, defines)
}

private fun buildDefinesSet(enabler: ZShaderProgramParameters): Set<String> {
    return buildSet {
        if (enabler.usePosition) add("USE_POSITION")
        if (enabler.useNormals) add("USE_NORMALS")
        if (enabler.useColors) add("USE_COLORS")
        if (enabler.useTextures) add("USE_TEXTURES")
        if (enabler.useSkinning) add("USE_SKINNING")
        if (enabler.usePbrMaterial) add("USE_PBR_MATERIAL")
        if (enabler.usePhongMaterial) add("USE_PHONG_MATERIAL")
        if (enabler.flipTextureY) add("FLIP_TEXTURE_Y")
    }
}

private fun processDirectives(source: String, defines: Set<String>): String {
    val lines = source.lines()
    val result = StringBuilder()
    val stateStack = ArrayDeque<ConditionalState>()

    for (line in lines) {
        val trimmed = line.trim()

        when {
            trimmed.startsWith("//#ifdef ") -> {
                val macro = trimmed.removePrefix("//#ifdef ").trim()
                val isActive = macro in defines
                val parentActive = stateStack.lastOrNull()?.isOutputting ?: true
                stateStack.addLast(ConditionalState(
                    isConditionMet = isActive,
                    isOutputting = parentActive && isActive,
                    hasElse = false
                ))
            }

            trimmed.startsWith("//#ifndef ") -> {
                val macro = trimmed.removePrefix("//#ifndef ").trim()
                val isActive = macro !in defines
                val parentActive = stateStack.lastOrNull()?.isOutputting ?: true
                stateStack.addLast(ConditionalState(
                    isConditionMet = isActive,
                    isOutputting = parentActive && isActive,
                    hasElse = false
                ))
            }

            trimmed == "//#else" -> {
                if (stateStack.isEmpty()) {
                    error("Unexpected //#else without matching //#ifdef or //#ifndef")
                }
                val current = stateStack.removeLast()
                if (current.hasElse) {
                    error("Duplicate //#else directive")
                }
                val parentActive = stateStack.lastOrNull()?.isOutputting ?: true
                stateStack.addLast(ConditionalState(
                    isConditionMet = !current.isConditionMet,
                    isOutputting = parentActive && !current.isConditionMet,
                    hasElse = true
                ))
            }

            trimmed == "//#endif" -> {
                if (stateStack.isEmpty()) {
                    error("Unexpected //#endif without matching //#ifdef or //#ifndef")
                }
                stateStack.removeLast()
            }

            else -> {
                val shouldOutput = stateStack.lastOrNull()?.isOutputting ?: true
                if (shouldOutput) {
                    result.appendLine(line)
                }
            }
        }
    }

    if (stateStack.isNotEmpty()) {
        error("Unclosed //#ifdef or //#ifndef directive")
    }

    return result.toString().trimEnd()
}

private data class ConditionalState(
    val isConditionMet: Boolean,
    val isOutputting: Boolean,
    val hasElse: Boolean
)
