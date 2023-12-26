package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZSceneContext
import zernikalos.context.ZRenderingContext
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZMesh
import zernikalos.components.skeleton.ZSkeleton
import zernikalos.components.shader.ZShaderProgram
import zernikalos.components.skeleton.ZSkinning
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Model")
class ZModel: ZObject() {
    @ProtoNumber(4)
    lateinit var shaderProgram: ZShaderProgram
    @ProtoNumber(5)
    lateinit var mesh: ZMesh
    @ProtoNumber(6)
    var material: ZMaterial? = null
    @ProtoNumber(7)
    var skinning: ZSkinning? = null
    @ProtoNumber(8)
    var skeleton: ZSkeleton? = null

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
                val uniformValue: ZMatrix4 = uniformGenerator.compute(sceneContext, this)
                uniform.bindValue(shaderProgram, uniformValue.values)
            }
        }

        mesh.bind()
        mesh.render()
        mesh.unbind()

        material?.unbind()
        shaderProgram.unbind()
    }
}
