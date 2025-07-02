/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZMaterialSerializer::class)
class ZMaterial
internal constructor(private val data: ZMaterialData):
    ZRenderizableComponent<ZMaterialRenderer>(), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData())

    var texture: ZTexture? by data::texture

    override fun createRenderer(ctx: ZRenderingContext): ZMaterialRenderer {
        return ZMaterialRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()
    override fun unbind() = renderer.unbind()
}

@Serializable
@JsExport
data class ZMaterialData(
    @Contextual @ProtoNumber(10)
    var texture: ZTexture? = null
): ZComponentData()

class ZMaterialRenderer(ctx: ZRenderingContext, private val data: ZMaterialData): ZComponentRenderer(ctx) {
    override fun initialize() {
        data.texture?.initialize(ctx)
    }

    override fun bind() {
        data.texture?.bind()
    }

    override fun unbind() {
        data.texture?.unbind()
    }

}

class ZMaterialSerializer: ZComponentSerializer<ZMaterial, ZMaterialData>() {
    override val kSerializer: KSerializer<ZMaterialData> = ZMaterialData.serializer()

    override fun createComponentInstance(data: ZMaterialData): ZMaterial {
        return ZMaterial(data)
    }
}
