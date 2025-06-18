/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ExpectShaderType
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext

enum class ZGlShaderType(val value: Int) {
    VERTEX_SHADER(ExpectShaderType.VERTEX_SHADER),
    FRAGMENT_SHADER(ExpectShaderType.FRAGMENT_SHADER)
}

actual class ZShaderProgramRenderer actual constructor(ctx: ZRenderingContext, private val data: ZShaderProgramData): ZComponentRenderer(ctx) {

    val program: ZProgram = ZProgram()

    actual override fun initialize() {
        ctx as ZGLRenderingContext

        program.initialize(ctx)

        data.vertexShader.initialize(ctx)
        data.vertexShader.initialize(data.shaderSource)
        attachShader(data.vertexShader)

        data.fragmentShader.initialize(ctx)
        data.fragmentShader.initialize(data.shaderSource)
        attachShader(data.fragmentShader)

        data.attributes.values.forEach { attr ->
            attr.initialize(ctx)
            attr.renderer.bindLocation(program.renderer.programId)
        }

        program.link()
        data.uniforms.singles.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.renderer.uniformId = ctx.getUniformLocation(
                program.renderer.programId,
                uniform.uniformName
            )
        }
        data.uniforms.blocks.forEach { uniform ->
            uniform.initialize(ctx)
            uniform.renderer.bindLocation(program.renderer.programId)
        }
    }

    actual override fun bind() {
        program.bind()
        data.uniforms.singles.forEach { uniform ->
            uniform.bind()
        }
        data.uniforms.blocks.forEach { uniform ->
            uniform.bind()
        }
    }

    actual override fun unbind() {
        program.unbind()
    }

    private fun attachShader(shader: ZShader) {
        ctx as ZGLRenderingContext

        ctx.attachShader(program.renderer.programId, shader.renderer.shader)
    }

}
