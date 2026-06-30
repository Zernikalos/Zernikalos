/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

import zernikalos.components.shader.ZAttributeId
import zernikalos.components.shader.ZShaderProgram
import zernikalos.logger.ZLoggable
import zernikalos.logger.logger
import zernikalos.objects.ZModel

private object PipelineCapabilitiesBuilderLogger: ZLoggable

internal fun pipelineCapabilitiesBuilder(model: ZModel, shaderProgram: ZShaderProgram): ZShaderProgramParameters {
    val shaderParameters = ZShaderProgramParameters(
        model.mesh.attributeIds.intersect(shaderProgram.attributeIds)
    )

    shaderParameters.usePosition = ZAttributeId.POSITION in model.mesh
    shaderParameters.useColors = ZAttributeId.COLOR in model.mesh
    shaderParameters.useNormals = ZAttributeId.NORMAL in model.mesh

    if (model.hasTextures) {
        shaderParameters.useTextures = true
        if (model.material?.texture?.flipY == true) {
            shaderParameters.flipTextureY = true
        }
    }

    if (model.hasSkeleton) {
        shaderParameters.useSkinning = true
        shaderParameters.maxBones = model.skeleton!!.bones.size
    }

    if (model.material?.usesPbr == true) {
        shaderParameters.usePbrMaterial = true
    }

    if (model.material?.usesPhong == true) {
        shaderParameters.usePhongMaterial = true
    }

    if (shaderParameters.useNormals && (shaderParameters.usePbrMaterial || shaderParameters.usePhongMaterial)) {
        shaderParameters.useLighting = true
    }

    enableRequiredBuffers(model, shaderParameters)

    PipelineCapabilitiesBuilderLogger.logger.debug("[${model.name}] Enabled buffers:\n${
        model.mesh.buffers.values.filter { it.enabled }.joinToString(separator = ",\n") { it.toString() }
    }")

    return shaderParameters
}

private fun enableRequiredBuffers(model: ZModel, shaderParameters: ZShaderProgramParameters) {
    // TODO: This might fail when the mesh is shared
    model.mesh.indexBuffer?.enabled = true
    model.mesh.position?.enabled = shaderParameters.usePosition
    model.mesh.normal?.enabled = shaderParameters.useNormals
    model.mesh.uv?.enabled = shaderParameters.useTextures
    model.mesh.color?.enabled = shaderParameters.useColors

    model.mesh.boneWeight?.enabled = shaderParameters.useSkinning
    model.mesh.boneIndex?.enabled = shaderParameters.useSkinning
}
