package zernikalos.objects

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZSceneContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZMesh
import zernikalos.components.skeleton.ZSkeleton
import zernikalos.components.shader.ZShaderProgram
import zernikalos.components.skeleton.ZSkinning
import zernikalos.loader.ZLoaderContext
import zernikalos.logger.ZLoggable
import kotlin.js.JsExport

@JsExport
@Serializable
class ZModel: ZObject() {

    override val type = ZObjectType.MODEL

    @ProtoNumber(4)
    lateinit var shaderProgram: ZShaderProgram
    @ProtoNumber(5)
    lateinit var mesh: ZMesh
    @Contextual @ProtoNumber(6)
    var material: ZMaterial? = null
    @ProtoNumber(7)
    var skinning: ZSkinning? = null
    @Contextual @ProtoNumber(8)
    var skeleton: ZSkeleton? = null

    val hasTextures: Boolean
        get() = material?.texture != null

    val hasSkeleton: Boolean
        get() = skeleton != null

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
        material?.initialize(ctx)
        //skeleton?.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.bind()
        material?.bind()

        shaderProgram.uniforms.forEach { (name, uniform) ->
            val uniformGenerator = sceneContext.getUniform(name)
            if (uniformGenerator != null) {
                val uniformValue = uniformGenerator.compute(sceneContext, this)
                uniform.bindValue(shaderProgram, uniformValue)
            }
        }

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
