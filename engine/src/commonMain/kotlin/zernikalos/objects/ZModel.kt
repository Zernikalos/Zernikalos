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
import zernikalos.action.ZKeyFrame
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZDrawMode
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZAttributeId
import zernikalos.components.shader.ZShaderProgram
import zernikalos.components.skeleton.ZSkinning
import zernikalos.context.ZContext
import zernikalos.context.ZRenderingContext
import zernikalos.generators.shadergenerator.ZShaderGeneratorType
import zernikalos.generators.shadergenerator.createShaderGenerator
import zernikalos.generators.shadergenerator.pipelineCapabilitiesBuilder
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport

@JsExport
@Serializable
open class ZModel: ZObject() {

    @Transient
    override val type = ZObjectType.MODEL

    @Contextual @ProtoNumber(4)
    var mesh: ZMesh = ZMesh()
    @Transient
    var shaderProgram: ZShaderProgram = ZShaderProgram()
    @Contextual @ProtoNumber(6)
    var material: ZMaterial? = null
    @Contextual @ProtoNumber(7)
    var skeleton: ZSkeleton? = null
    @ProtoNumber(8)
    var skinning: ZSkinning? = null

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
    lateinit var renderer: ZModelRenderer

    override fun internalInitialize(ctx: ZContext) {
        renderer = ZModelRenderer(ctx.renderingContext, this)

        val shaderProgramParams = pipelineCapabilitiesBuilder(this, shaderProgram)

        if (hasSkeleton) {
            skeleton?.initialize(ctx)
            // Commit rest pose into poseMatrix so the joint palette (e.g. ZBoneMatrixGenerator)
            // always reads consistent world matrices, not a mix of bind vs pose.
            skeleton?.applyKeyFrame(ZKeyFrame(0f), ZMatrix4.Identity)
        }

        val shaderSourceGenerator = createShaderGenerator(ZShaderGeneratorType.DEFAULT)
        shaderSourceGenerator.generate(shaderProgramParams, shaderProgram)

        shaderProgram.initialize(ctx.renderingContext)
        mesh.initialize(ctx.renderingContext)
        material?.initialize(ctx.renderingContext)

        renderer.initialize()
    }

    override fun internalRender(ctx: ZContext) {

        shaderProgram.uniforms.blocks.forEach { (name, uniform) ->
            uniform.computeValue(ctx.sceneContext, this)
        }

        renderer.render()
    }

    override fun internalDispose(ctx: ZContext) {
        skeleton?.dispose(ctx)

        shaderProgram.dispose()
        mesh.dispose()
        material?.dispose()
        skinning?.dispose()
        renderer.dispose()
    }
}

expect class ZModelRenderer(ctx: ZRenderingContext, model: ZModel) {

    fun initialize()

    fun render()

    fun dispose()
}
