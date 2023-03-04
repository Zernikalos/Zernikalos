package mr.robotto.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext
import mr.robotto.components.mesh.MrMesh
import mr.robotto.components.shader.MrShaderProgram
import mr.robotto.math.MrMatrix4f
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Model")
class MrModel: MrObject() {
    @ProtoNumber(4)
    private lateinit var shaderProgram: MrShaderProgram
    @ProtoNumber(5)
    private lateinit var mesh: MrMesh

    override fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
    }

    override fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        shaderProgram.render(ctx)

        shaderProgram.uniforms.forEach {
            val uniformGenerator = sceneContext.getUniform(it.key)
            if (uniformGenerator != null) {
                val uniformValue: MrMatrix4f = uniformGenerator.compute(sceneContext, this) as MrMatrix4f
                it.value.bindValue(ctx, uniformValue.values)
            }
        }

        mesh.render(ctx)
    }
}
