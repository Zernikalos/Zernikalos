/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZComponentData
import zernikalos.components.ZRenderizableComponent
import zernikalos.components.ZSerializableComponent
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZShaderSource internal constructor(data: ZShaderSourceData): ZSerializableComponent<ZShaderSourceData>(data) {

    @JsName("init")
    constructor(): this(ZShaderSourceData())

    private var glslShaderSource: ZGlSLShaderSource by data::glsl

    private var _metalShaderSource: ZMetalShaderSource by data::metal

    private var _wgpuShaderSource: ZWebGpuShaderSource by data::wgpu

    var glslVertexShaderSource: String by glslShaderSource::vertexShaderSource

    var glslFragmentShaderSource: String by glslShaderSource::fragmentShaderSource

    var metalShaderSource: String by _metalShaderSource::shaderSource

    var wgpuShaderSource: String by _wgpuShaderSource::shaderSource

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
data class ZWebGpuShaderSource(
    var shaderSource: String = ""
)

@JsExport
data class ZShaderSourceData(
    var glsl: ZGlSLShaderSource = ZGlSLShaderSource(),
    var metal: ZMetalShaderSource = ZMetalShaderSource(),
    var wgpu: ZWebGpuShaderSource = ZWebGpuShaderSource()
): ZComponentData()
