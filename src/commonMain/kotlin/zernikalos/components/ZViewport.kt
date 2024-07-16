/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZVector4
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZViewportSerializer::class)
class ZViewport
internal constructor(data: ZViewportData):
    ZRenderizableComponentTemplate<ZViewportData, ZViewportRenderer>(data),
    ZRenderizable {

    @JsName("init")
    constructor(): this(ZViewportData())

    var clearColor: ZVector4 by data::clearColor

    override fun createRenderer(ctx: ZRenderingContext): ZViewportRenderer {
        return ZViewportRenderer(ctx, data)
    }

    override fun render() {
        renderer.render()
    }

}

@JsExport
@Serializable
data class ZViewportData(
    var clearColor: ZVector4 = ZVector4(.2f, .2f, .2f, 1.0f),
//    var clearMask: Int = BufferBit.COLOR_BUFFER.value or BufferBit.DEPTH_BUFFER.value
): ZComponentData()

expect class ZViewportRenderer(ctx: ZRenderingContext, data: ZViewportData): ZComponentRender<ZViewportData> {
    override fun initialize()

    override fun render()

}

class ZViewportSerializer: ZComponentSerializer<ZViewport, ZViewportData>() {
    override val kSerializer: KSerializer<ZViewportData>
        get() = ZViewportData.serializer()

    override fun createComponentInstance(data: ZViewportData): ZViewport {
        return ZViewport(data)
    }

}
