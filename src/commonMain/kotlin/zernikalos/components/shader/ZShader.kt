/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZRenderer
import zernikalos.components.ZComponentData
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
enum class ZShaderType {
    VERTEX_SHADER,
    FRAGMENT_SHADER
}

@JsExport
class ZShader
internal constructor (data: ZShaderData): ZRenderizableComponent<ZShaderData, ZShaderRenderer>(data) {

    @JsName("init")
    constructor(): this(ZShaderData())

    @JsName("initWithType")
    constructor(type: ZShaderType): this(ZShaderData(type))

    val type: ZShaderType by data::type

    @JsName("initializeWithSources")
    fun initialize(source: ZShaderSource) {
        renderer.initialize(source)
    }

    override fun createRenderer(ctx: ZRenderingContext): ZShaderRenderer {
        return ZShaderRenderer(ctx, data)
    }

}

data class ZShaderData(
    var type: ZShaderType = ZShaderType.VERTEX_SHADER
): ZComponentData()

expect class ZShaderRenderer(ctx: ZRenderingContext, data: ZShaderData): ZRenderer {

    override fun initialize()

    fun initialize(source: ZShaderSource)

}
