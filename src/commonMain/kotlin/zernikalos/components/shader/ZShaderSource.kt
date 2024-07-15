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
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZComponentTemplate
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZShaderSourceSerializer::class)
class ZShaderSource internal constructor(data: ZShaderSourceData): ZComponentTemplate<ZShaderSourceData>(data) {

    @JsName("init")
    constructor(): this(ZShaderSourceData())

    private var glslShaderSource: ZGlSLShaderSource by data::glsl

    private var _metalShaderSource: ZMetalShaderSource by data::metal

    var glslVertexShaderSource: String by glslShaderSource::vertexShaderSource

    var glslFragmentShaderSource: String by glslShaderSource::fragmentShaderSource

    var metalShaderSource: String by _metalShaderSource::shaderSource

}

@JsExport
@Serializable
data class ZGlSLShaderSource(
    var vertexShaderSource: String = "",
    var fragmentShaderSource: String = ""
)

@JsExport
@Serializable
data class ZMetalShaderSource(
    var shaderSource: String = ""
)

@JsExport
@Serializable
data class ZShaderSourceData(
    var glsl: ZGlSLShaderSource = ZGlSLShaderSource(),
    var metal: ZMetalShaderSource = ZMetalShaderSource()
): ZComponentData()

class ZShaderSourceSerializer: ZComponentSerializer<ZShaderSource, ZShaderSourceData>() {
    override val kSerializer: KSerializer<ZShaderSourceData>
        get() = ZShaderSourceData.serializer()

    override fun createComponentInstance(data: ZShaderSourceData): ZShaderSource {
        return ZShaderSource(data)
    }

}