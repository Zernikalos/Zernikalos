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

/**
 * The ZShaderGenerator class is responsible for generating the attributes, uniforms and source required
 * for a shader program based on the provided ZAttributesEnabler configuration.
 * This class serves as a base class and requires concrete implementations to define the shader source
 * code building process.
 */
internal abstract class ZShaderGenerator(): ZLoggable {

    /**
     * Generates and configures shader attributes and uniforms based on the specified enabler's settings.
     * The shader source is also built within this method.
     *
     * @param enabler An instance of ZAttributesEnabler that specifies which attributes and uniforms
     *                should be enabled in the shader program.
     * @param shaderProgram An instance of ZShaderProgram that will be configured with the necessary
     *                      attributes and uniforms as defined by the enabler.
     */
    fun generate(enabler: ZShaderProgramParameters, shaderProgram: ZShaderProgramRender) {
        shaderProgram.clearAttributes()
        addRequiredAttributes(enabler, shaderProgram)

        shaderProgram.clearUniforms()
        addRequiredUniforms(enabler, shaderProgram)

        buildShaderSource(enabler, shaderProgram.shaderSource)
    }

    private fun addRequiredAttributes(params: ZShaderProgramParameters, shaderProgram: ZShaderProgramRender) {
        shaderProgram.addAttribute(ZAttrIndices)
        if (params.usePosition) shaderProgram.addAttribute(ZAttrPosition)
        if (params.useNormals) shaderProgram.addAttribute(ZAttrNormal)
        if (params.useTextures) shaderProgram.addAttribute(ZAttrUv)
        if (params.useColors) shaderProgram.addAttribute(ZAttrColor)
        if (params.useSkinning) {
            shaderProgram.addAttribute(ZAttrBoneIndices)
            shaderProgram.addAttribute(ZAttrBoneWeight)
        }
    }

    private fun addRequiredUniforms(params: ZShaderProgramParameters, shaderProgram: ZShaderProgramRender) {
        shaderProgram.addUniform("ModelViewProjectionMatrix", ZUniformModelViewProjectionMatrix)
        shaderProgram.addUniform("ViewMatrix", ZUniformViewMatrix)
        shaderProgram.addUniform("ProjectionMatrix", ZUniformProjectionMatrix)
        if (params.useSkinning) {
            shaderProgram.addUniform("Bones", ZBonesMatrixArray(params.maxBones))
            shaderProgram.addUniform("InverseBindMatrix", ZInverseBindMatrixArray(params.maxBones))
        }
    }

    /**
     * Constructs and sets the shader source code based on the specified attributes enabled in the enabler.
     *
     * @param enabler A ZAttributesEnabler instance that determines which attributes should be included in the shader source.
     * @param source A ZShaderSource instance that will be populated with the constructed shader code.
     */
    protected abstract fun buildShaderSource(enabler: ZShaderProgramParameters, source: ZShaderSource)

}
