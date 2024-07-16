/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderProgramSerializer::class)
class ZShaderProgram internal constructor(data: ZShaderProgramData): ZRenderizableComponentTemplate<ZShaderProgramData, ZShaderProgramRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZShaderProgramData())

    val shaderSource: ZShaderSource by data::shaderSource

    val attributes: Map<String, ZAttribute> by data::attributes

    val uniforms: Map<String, ZUniform> by data::uniforms

    val uniformsNames: Set<String>
        get() = uniforms.keys

    fun addUniform(name: String, uniform: ZUniform) {
        data.uniforms[name] = uniform
    }

    fun getUniform(name: String): ZUniform? {
        return data.uniforms[name]
    }

    fun clearUniforms() {
        data.uniforms.clear()
    }

    @JsName("addAttributeByName")
    fun addAttribute(name: String, attribute: ZAttribute) {
        data.attributes[name] = attribute
    }

    fun addAttribute(attribute: ZAttribute) {
        data.attributes[attribute.attributeName] = attribute
    }

    fun clearAttributes() {
        data.attributes.clear()
    }

    override fun createRenderer(ctx: ZRenderingContext): ZShaderProgramRenderer {
        return ZShaderProgramRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }

}

@Serializable
data class ZShaderProgramData(
    @Transient
    var vertexShader: ZShader = ZShader(ZShaderType.VERTEX_SHADER),
    @Transient
    var fragmentShader: ZShader = ZShader(ZShaderType.FRAGMENT_SHADER),
    @Transient
    var shaderSource: ZShaderSource = ZShaderSource(),
    @ProtoNumber(3)
    val attributes: LinkedHashMap<String, ZAttribute> = LinkedHashMap(),
    @ProtoNumber(4)
    var uniforms: LinkedHashMap<String, ZUniform> = LinkedHashMap()
): ZComponentData()

expect class ZShaderProgramRenderer(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRender<ZShaderProgramData> {

    override fun initialize()
    override fun bind()
    override fun unbind()

}

class ZShaderProgramSerializer: ZComponentSerializer<ZShaderProgram, ZShaderProgramData>() {
    override val kSerializer: KSerializer<ZShaderProgramData>
        get() = ZShaderProgramData.serializer()

    override fun createComponentInstance(data: ZShaderProgramData): ZShaderProgram {
        return ZShaderProgram(data)
    }

}
