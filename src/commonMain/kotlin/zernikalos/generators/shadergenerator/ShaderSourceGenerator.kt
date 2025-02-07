/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

import zernikalos.components.shader.*
import zernikalos.logger.ZLoggable

abstract class ZShaderGenerator(): ZLoggable {

    fun generate(enabler: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
        shaderProgram.clearAttributes()
        addRequiredAttributes(enabler, shaderProgram)

        shaderProgram.clearUniforms()
        addRequiredUniforms(enabler, shaderProgram)

        buildShaderSource(enabler, shaderProgram.shaderSource)
    }

    private fun addRequiredAttributes(enabler: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
        shaderProgram.addAttribute(ZAttrIndices)
        if (enabler.usePosition) shaderProgram.addAttribute(ZAttrPosition)
        if (enabler.useNormals) shaderProgram.addAttribute(ZAttrNormal)
        if (enabler.useTextures) shaderProgram.addAttribute(ZAttrUv)
        if (enabler.useColors) shaderProgram.addAttribute(ZAttrColor)
        if (enabler.useSkinning) {
            shaderProgram.addAttribute(ZAttrBoneIndices)
            shaderProgram.addAttribute(ZAttrBoneWeight)
        }
    }

    private fun addRequiredUniforms(enabler: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
        shaderProgram.addUniform("ModelViewProjectionMatrix", ZUniformModelViewProjectionMatrix)
        shaderProgram.addUniform("ViewMatrix", ZUniformViewMatrix)
        shaderProgram.addUniform("ProjectionMatrix", ZUniformProjectionMatrix)
        if (enabler.useSkinning) {
            shaderProgram.addUniform("Bones", ZBonesMatrixArray(enabler.maxBones))
            shaderProgram.addUniform("InverseBindMatrix", ZInverseBindMatrixArray(enabler.maxBones))
        }
    }

    protected abstract fun buildShaderSource(enabler: ZShaderProgramParameters, source: ZShaderSource)

}

enum class ZShaderGeneratorType {
    DEFAULT
}

internal expect fun createShaderGenerator(type: ZShaderGeneratorType): ZShaderGenerator