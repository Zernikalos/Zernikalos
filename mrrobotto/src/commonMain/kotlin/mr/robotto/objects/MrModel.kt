package mr.robotto.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext
import mr.robotto.components.mesh.MrMesh
import mr.robotto.components.shader.MrShaderProgram
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Model")
class MrModel: MrObject() {
    private lateinit var mesh: MrMesh
    private lateinit var shaderProgram: MrShaderProgram

    override fun internalInitialize(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        shaderProgram.initialize(ctx)
        mesh.initialize(ctx)
    }

    override fun internalRender(sceneContext: MrSceneContext, ctx: MrRenderingContext) {
        shaderProgram.render(ctx)
        mesh.render(ctx)
    }
}
