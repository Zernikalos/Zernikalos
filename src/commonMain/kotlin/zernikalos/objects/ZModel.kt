package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZShaderProgram
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Model")
class ZModel: ZObject() {
    @ProtoNumber(4)
    private lateinit var shaderProgram: ZShaderProgram
    @ProtoNumber(5)
    private lateinit var mesh: ZMesh

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        shaderProgram.render(ctx)

        shaderProgram.uniforms.forEach {
            val uniformGenerator = sceneContext.getUniform(it.key)
            if (uniformGenerator != null) {
                val uniformValue: ZMatrix4 = uniformGenerator.compute(sceneContext, this)
                it.value.bindValue(ctx, uniformValue.values)
            }
        }

        mesh.render(ctx)
    }
}
