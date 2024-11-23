/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.*
import zernikalos.components.skeleton.ZSkeleton
import zernikalos.context.ZContext
import zernikalos.context.ZRenderingContext
import zernikalos.generators.shadergenerator.ZAttributesEnabler
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
        get() = material?.texture != null && mesh.hasBufferById(ZAttributeId.UV)

    val hasSkeleton: Boolean
        get() = skeleton != null

    @Transient
    private lateinit var renderer: ZModelRenderer

    override fun internalInitialize(ctx: ZContext) {
        renderer = ZModelRenderer(ctx.renderingContext, this)

        val enabler = buildAttributeEnabler()

        if (hasSkeleton) {
            skeleton?.initialize(ctx.renderingContext)
        }

        val shaderSourceGenerator = createShaderGenerator(ZShaderGeneratorType.DEFAULT)
        shaderSourceGenerator.generate(enabler, shaderProgram)

        enableRequiredBuffers(enabler)

        logger.debug("[$name] Enabled buffers:\n${
            mesh.buffers.values.filter { it.enabled }.joinToString(separator = ",\n") { it.toString() }
        }")

        renderer.initialize()
    }

    private fun enableRequiredBuffers(enabler: ZAttributesEnabler) {
        mesh.indexBuffer?.enabled = true
        mesh.getBufferById(ZAttributeId.POSITION)?.enabled = enabler.usePosition
        mesh.getBufferById(ZAttributeId.NORMAL)?.enabled = enabler.useNormals
        mesh.getBufferById(ZAttributeId.UV)?.enabled = enabler.useTextures
        mesh.getBufferById(ZAttributeId.COLOR)?.enabled = enabler.useColors

        mesh.getBufferById(ZAttributeId.BONE_WEIGHT)?.enabled = enabler.useSkinning
        mesh.getBufferById(ZAttributeId.BONE_INDEX)?.enabled = enabler.useSkinning
    }

    private fun buildAttributeEnabler(): ZAttributesEnabler {
        val enabler = ZAttributesEnabler()
        enabler.usePosition = mesh.hasBufferById(ZAttributeId.POSITION)
        enabler.useColors = mesh.hasBufferById(ZAttributeId.COLOR)
        enabler.useNormals = mesh.hasBufferById(ZAttributeId.NORMAL)
        if (hasTextures) {
            enabler.useTextures = hasTextures
            if (material?.texture?.flipY == true) {
                enabler.flipTextureY = true
            }
        }
        if (hasSkeleton) {
            enabler.useSkinning = true
            enabler.maxBones = skeleton!!.bones.size
        }
        return enabler
    }

    override fun internalRender(ctx: ZContext) {
        shaderProgram.uniforms.forEach { (name, uniform) ->
            val uniformGenerator = ctx.sceneContext.getUniform(name)
            if (uniformGenerator != null) {
                val uniformValue = uniformGenerator.compute(ctx.sceneContext, this)
                uniform.bindValue(uniformValue)
            }
        }

        renderer.render()
    }
}

expect class ZModelRenderer(ctx: ZRenderingContext, model: ZModel) {

    fun initialize()

    fun render()
}
