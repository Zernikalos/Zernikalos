/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponent2
import zernikalos.components.ZComponentData
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZShaderSource internal constructor(data: ZShaderSourceData): ZComponent2() {

    @JsName("init")
    constructor(): this(ZShaderSourceData())

    override val isRenderizable: Boolean = false

    private var glslShaderSource: ZGlSLShaderSource by data::glsl

    private var _metalShaderSource: ZMetalShaderSource by data::metal

    var glslVertexShaderSource: String by glslShaderSource::vertexShaderSource

    var glslFragmentShaderSource: String by glslShaderSource::fragmentShaderSource

    var metalShaderSource: String by _metalShaderSource::shaderSource
}

@JsExport
data class ZGlSLShaderSource(
    var vertexShaderSource: String = "",
    var fragmentShaderSource: String = ""
)

@JsExport
data class ZMetalShaderSource(
    var shaderSource: String = ""
)

@JsExport
data class ZShaderSourceData(
    var glsl: ZGlSLShaderSource = ZGlSLShaderSource(),
    var metal: ZMetalShaderSource = ZMetalShaderSource()
): ZComponentData()
