/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZDrawMode
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.*
import zernikalos.context.ZContext
import zernikalos.context.ZRenderingContext
import zernikalos.generators.shadergenerator.ZShaderProgramParameters
import zernikalos.generators.shadergenerator.ZShaderGeneratorType
import zernikalos.generators.shadergenerator.createShaderGenerator
import zernikalos.logger.logger
import kotlin.js.JsExport

@JsExport
@Serializable
class ZModel: ZObject() {

    override val type = ZObjectType.MODEL

    @ProtoNumber(4)
    lateinit var mesh: ZMesh
    @ProtoNumber(5)
    lateinit var shaderProgram: ZShaderProgram
    @Contextual @ProtoNumber(6)
    var material: ZMaterial? = null
    @Contextual @ProtoNumber(7)
    var skeleton: ZSkeleton? = null

    val hasTextures: Boolean
        get() = material?.texture != null && mesh.hasBuffer(ZAttributeId.UV)

    val hasSkeleton: Boolean
        get() = skeleton != null

    var drawMode: ZDrawMode
        get() = mesh.drawMode
        set(value) {
            mesh.drawMode = value
        }

    @Transient
    private lateinit var renderer: ZModelRenderer

    override fun internalInitialize(ctx: ZContext) {
        renderer = ZModelRenderer(ctx.renderingContext, this)

        val shaderProgramParams = buildShaderParameters()

        val shaderSourceGenerator = createShaderGenerator(ZShaderGeneratorType.DEFAULT)
        shaderSourceGenerator.generate(shaderProgramParams, shaderProgram)

        enableRequiredBuffers(shaderProgramParams)

        logger.debug("[$name] Enabled buffers:\n${
            mesh.buffers.values.filter { it.enabled }.joinToString(separator = ",\n") { it.toString() }
        }")

        renderer.initialize()
    }

    private fun enableRequiredBuffers(shaderParameters: ZShaderProgramParameters) {
        mesh.indexBuffer?.enabled = true
        mesh.position?.enabled = shaderParameters.usePosition
        mesh.normal?.enabled = shaderParameters.useNormals
        mesh.uv?.enabled = shaderParameters.useTextures
        mesh.color?.enabled = shaderParameters.useColors

        mesh.boneWeight?.enabled = shaderParameters.useSkinning
        mesh.boneIndex?.enabled = shaderParameters.useSkinning
    }

    private fun buildShaderParameters(): ZShaderProgramParameters {
        val shaderParameters = ZShaderProgramParameters(
            mesh.attributeIds.intersect(shaderProgram.attributeIds)
        )
        shaderParameters.usePosition = ZAttributeId.POSITION in mesh
        shaderParameters.useColors = ZAttributeId.COLOR in mesh
        shaderParameters.useNormals = ZAttributeId.NORMAL in mesh
        if (hasTextures) {
            shaderParameters.useTextures = hasTextures
            if (material?.texture?.flipY == true) {
                shaderParameters.flipTextureY = true
            }
        }
        if (hasSkeleton) {
            shaderParameters.useSkinning = true
            shaderParameters.maxBones = skeleton!!.bones.size
        }
        return shaderParameters
    }

    override fun internalRender(ctx: ZContext) {
        shaderProgram.uniforms.forEach { (name, uniform) ->
            val uniformGenerator = ctx.sceneContext.getUniform(name)
            if (uniformGenerator != null) {
                uniform.value = uniformGenerator.compute(ctx.sceneContext, this)
            }
        }

        renderer.render()
    }
}

expect class ZModelRenderer(ctx: ZRenderingContext, model: ZModel) {

    fun initialize()

    fun render()
}
