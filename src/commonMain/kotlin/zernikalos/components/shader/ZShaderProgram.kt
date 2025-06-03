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
abstract class ZBaseShaderProgram: ZComponent2, ZBindeable2 {

    protected var vertexShader: ZShader = ZShader(ZShaderType.VERTEX_SHADER)
    protected var fragmentShader: ZShader = ZShader(ZShaderType.FRAGMENT_SHADER)

    val shaderSource: ZShaderSource = ZShaderSource()

    private val _attributes: HashMap<String, ZAttribute> = hashMapOf()
    val attributes: Map<String, ZAttribute>
        get() = _attributes.toMap()

    val uniforms: ZUniformCollection = ZUniformCollection()

    val uniformsNames: Set<String>
        get() = uniforms.keys

    val attributeIds: Set<ZAttributeId>
        get() = attributes.values.map { it.attrId }.toSet()

    override val isRenderizable: Boolean = true

    @JsName("init")
    constructor() {

    }

    fun hasAttribute(name: String): Boolean {
        return attributes.containsKey(name)
    }

    fun hasAttributeById(attrId: ZAttributeId): Boolean {
        return attributes.values.any { it.id == attrId.id }
    }

    fun addUniform(name: String, uniform: ZUniform) {
        uniforms[name] = uniform
    }

    fun getUniform(name: String): ZBaseUniform? {
        return uniforms[name]
    }

    fun clearUniforms() {
        uniforms.clear()
    }

    @JsName("addAttributeByName")
    fun addAttribute(name: String, attribute: ZAttribute) {
        _attributes[name] = attribute
    }

    fun addAttribute(attribute: ZAttribute) {
        _attributes[attribute.attributeName] = attribute
    }

    fun clearAttributes() {
        _attributes.clear()
    }

}

expect open class ZShaderProgramRender: ZBaseShaderProgram {

    constructor()

    override fun bind(ctx: ZRenderingContext)
    override fun unbind(ctx: ZRenderingContext)
}

@JsExport
class ZShaderProgram: ZShaderProgramRender {

    @JsName("init")
    constructor(): super()

}
