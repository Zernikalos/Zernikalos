/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Contextual
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.*
import zernikalos.components.skeleton.ZSkeleton
import zernikalos.components.skeleton.ZSkinning
import zernikalos.context.ZContext
import zernikalos.loader.ZLoaderContext
import zernikalos.logger.ZLoggable
import zernikalos.generators.shadergenerator.ZAttributesEnabler
import zernikalos.generators.shadergenerator.ZShaderSourceGenerator
import kotlin.js.JsExport

@JsExport
@Serializable
class ZModel: ZObject(), ZLoggable {

    override val type = ZObjectType.MODEL

    @ProtoNumber(4)
    lateinit var mesh: ZMesh
    @ProtoNumber(5)
    lateinit var shaderProgram: ZShaderProgram
    @Contextual @ProtoNumber(6)
    var material: ZMaterial? = null
    @ProtoNumber(7)
    var skinning: ZSkinning? = null
    @Contextual @ProtoNumber(8)
    var skeleton: ZSkeleton? = null

    val hasTextures: Boolean
        get() = material?.texture != null && mesh.hasBufferKey("uv")

    val hasSkeleton: Boolean
        get() = skeleton != null

    override fun internalInitialize(ctx: ZContext) {
        val enabler = buildAttributeEnabler()

        val shaderSourceGenerator = ZShaderSourceGenerator()
        shaderSourceGenerator.buildShaderSource(enabler, shaderProgram.shaderSource)

        // TODO
        shaderProgram.clearAttributes()
        addRequiredAttributes(enabler)

        shaderProgram.clearUniforms()
        addRequiredUniforms(enabler)

        mesh.buildBuffers()
        enableRequiredBuffers(enabler)

        shaderProgram.initialize(ctx.renderingContext)
        mesh.initialize(ctx.renderingContext)
        material?.initialize(ctx.renderingContext)
        //skeleton?.initialize(ctx)
    }

    private fun addRequiredUniforms(enabler: ZAttributesEnabler) {
        shaderProgram.addUniform("ModelViewProjectionMatrix", ZUniformModelViewProjectionMatrix)
        //shaderProgram.addUniform("ViewMatrix", ZUniformViewMatrix)
        //shaderProgram.addUniform("ProjectionMatrix", ZUniformProjectionMatrix)
    }

    private fun addRequiredAttributes(enabler: ZAttributesEnabler) {
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

    private fun enableRequiredBuffers(enabler: ZAttributesEnabler) {
        mesh.indexBuffer?.enabled = true
        if (enabler.usePosition) mesh.getBufferById(ZAttributeId.POSITION)?.enabled = true
        if (enabler.useNormals) mesh.getBufferById(ZAttributeId.NORMAL)?.enabled = true
        if (enabler.useTextures) mesh.getBufferById(ZAttributeId.UV)?.enabled = true
        if (enabler.useColors) mesh.getBufferById(ZAttributeId.COLOR)?.enabled = true
        if (enabler.useSkinning) {
            mesh.getBufferById(ZAttributeId.BONE_WEIGHT)?.enabled = true
            mesh.getBufferById(ZAttributeId.BONE_INDEX)?.enabled = true
        }
    }

    private fun buildAttributeEnabler(): ZAttributesEnabler {
        val enabler = ZAttributesEnabler()
        enabler.usePosition = mesh.hasBufferKey("position")
        enabler.useColors = mesh.hasBufferKey("color")
        //enabler.useNormals = mesh.hasBufferKey("normal")
        enabler.useTextures = hasTextures
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

        shaderProgram.bind()
        material?.bind()

        mesh.bind()
        mesh.render()
        mesh.unbind()

        material?.unbind()
        shaderProgram.unbind()
    }
}

class ZModelSerializer(private val loaderContext: ZLoaderContext): KSerializer<ZModel>, ZLoggable {

    val deserializationStrategy: DeserializationStrategy<ZModel> = ZModel.serializer()

    override val descriptor: SerialDescriptor
        get() = deserializationStrategy.descriptor

    override fun deserialize(decoder: Decoder): ZModel {
        val model = decoder.decodeSerializableValue(deserializationStrategy)
        return model
    }

    override fun serialize(encoder: Encoder, value: ZModel) {
        TODO("Not yet implemented")
    }

}
