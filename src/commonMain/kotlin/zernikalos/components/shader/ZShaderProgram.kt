/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZShaderProgram internal constructor(data: ZShaderProgramData): ZRenderizableComponent<ZShaderProgramData, ZShaderProgramRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZShaderProgramData())

    val shaderSource: ZShaderSource by data::shaderSource

    val attributes: Map<String, ZAttribute> by data::attributes

    val uniforms: ZUniformCollection by data::uniforms

    val uniformsNames: Set<String>
        get() = uniforms.keys

    val attributeIds: Set<ZAttributeId>
        get() = attributes.values.map { it.attrId }.toSet()

    fun hasAttribute(name: String): Boolean {
        return attributes.containsKey(name)
    }

    fun hasAttributeById(attrId: ZAttributeId): Boolean {
        return attributes.values.any { it.id == attrId.id }
    }

    fun addUniform(name: String, uniform: ZUniform) {
        data.uniforms[name] = uniform
    }

    fun getUniform(name: String): ZBaseUniform? {
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

}

data class ZShaderProgramData(
    var vertexShader: ZShader = ZShader(ZShaderType.VERTEX_SHADER),
    var fragmentShader: ZShader = ZShader(ZShaderType.FRAGMENT_SHADER),
    var shaderSource: ZShaderSource = ZShaderSource(),
    val attributes: LinkedHashMap<String, ZAttribute> = LinkedHashMap(),
    var uniforms: ZUniformCollection = ZUniformCollection()
): ZComponentData()

expect class ZShaderProgramRenderer(ctx: ZRenderingContext, data: ZShaderProgramData): ZComponentRenderer {
    override fun initialize()
    override fun bind()
    override fun unbind()
}
