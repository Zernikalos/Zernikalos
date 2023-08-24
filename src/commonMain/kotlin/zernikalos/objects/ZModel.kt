package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.material.ZMaterial
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZShaderProgram
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

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
        material?.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.bind(ctx)
        material?.bind(ctx)

        shaderProgram.uniforms.forEach {
            val uniformGenerator = sceneContext.getUniform(it.key)
            if (uniformGenerator != null) {
                val uniformValue: ZMatrix4 = uniformGenerator.compute(sceneContext, this)
                it.value.bindValue(ctx, uniformValue.values)
            }
        }

        mesh.render(ctx)

        material?.unbind(ctx)
        shaderProgram.unbind(ctx)
    }
}
