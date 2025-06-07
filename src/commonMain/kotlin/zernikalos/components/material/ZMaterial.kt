/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.Contextual
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZMaterial
internal constructor(data: ZMaterialData):
    ZRenderizableComponent<ZMaterialData, ZMaterialRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData())

    var texture: ZTexture? by data::texture

    override fun createRenderer(ctx: ZRenderingContext): ZMaterialRenderer {
        return ZMaterialRenderer(ctx, data)
    }
}

@Serializable
data class ZMaterialDataWrapper(
    @ProtoNumber(1)
    override var refId: String = "",
    @ProtoNumber(2)
    override var isReference: Boolean = false,
    @ProtoNumber(100)
    override var data: ZMaterialData? = null
): ZRefComponentWrapper<ZMaterialData>

@Serializable
@JsExport
data class ZMaterialData(
    @Contextual @ProtoNumber(1)
    var texture: ZTexture? = null
): ZComponentData()

class ZMaterialRenderer(ctx: ZRenderingContext, private val data: ZMaterialData): ZRenderer(ctx) {
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

class ZMaterialSerializer(loaderContext: ZLoaderContext): ZRefComponentSerializer<ZMaterial, ZMaterialData, ZMaterialDataWrapper>(loaderContext) {
    override val deserializationStrategy: DeserializationStrategy<ZMaterialDataWrapper>
        get() = ZMaterialDataWrapper.serializer()

    override fun createComponentInstance(data: ZMaterialData): ZMaterial {
        return ZMaterial(data)
    }

}