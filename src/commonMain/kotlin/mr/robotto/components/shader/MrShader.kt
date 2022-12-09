package mr.robotto.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mr.robotto.GLWrap
import mr.robotto.ShaderType
import mr.robotto.components.MrComponent
import mr.robotto.components.MrComponentData
import mr.robotto.components.MrComponentRender
import mr.robotto.components.MrComponentSerializer

@Serializable(with = MrShaderSerializer::class)
class MrShader: MrComponent<MrShaderData, MrShaderRender>() {

    override lateinit var data: MrShaderData
    override var renderer: MrShaderRender = MrShaderRender()

    val shader: GLWrap
        get() = data.shader
}

@Serializable
class MrShaderData(val type: String, val source: String, @Transient var shader: GLWrap = GLWrap()): MrComponentData()

class MrShaderRender: MrComponentRender<MrShaderData>() {

    override fun internalInitialize() {
        val type = if (data.type == "vertex") ShaderType.VERTEX_SHADER else ShaderType.FRAGMENT_SHADER
        val shader = createShader(type)
        // if (shaderId <= 0) {
        //     throw Error("Error creating shader")
        // }

        compileShader(shader, data.source)
        checkShader(shader)

        data.shader = shader
    }

    override fun render() {
    }

    private fun createShader(shaderType: Int): GLWrap {
        return context.createShader(shaderType)
    }

    private fun compileShader(shader: GLWrap, source: String) {
        context.shaderSource(shader, source)
        context.compileShader(shader)
    }

    private fun checkShader(shader: GLWrap) {
        val compilerStatus = context.getShaderInfoLog(shader)
        val compilerError = context.getError()
        if (compilerStatus != "" || compilerError > 0) {
            context.deleteShader(shader)
            throw Error("Error compiling shader")
        }
    }

}

class MrShaderSerializer: MrComponentSerializer<MrShader, MrShaderData>() {
    override val deserializationStrategy: DeserializationStrategy<MrShaderData> = MrShaderData.serializer()

    override fun createDeserializationInstance(): MrShader {
        return MrShader()
    }

}