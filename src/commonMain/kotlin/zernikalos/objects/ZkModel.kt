package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import zernikalos.components.mesh.ZkMesh
import zernikalos.components.shader.ZkShaderProgram
import zernikalos.math.ZkMatrix4F
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Model")
class ZkModel: ZkObject() {
    @ProtoNumber(4)
    private lateinit var shaderProgram: ZkShaderProgram
    @ProtoNumber(5)
    private lateinit var mesh: ZkMesh

    override fun internalInitialize(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZkSceneContext, ctx: ZkRenderingContext) {
        shaderProgram.render(ctx)

        shaderProgram.uniforms.forEach {
            val uniformGenerator = sceneContext.getUniform(it.key)
            if (uniformGenerator != null) {
                val uniformValue: ZkMatrix4F = uniformGenerator.compute(sceneContext, this) as ZkMatrix4F
                it.value.bindValue(ctx, uniformValue.values)
            }
        }

        mesh.render(ctx)
    }
}
