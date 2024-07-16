/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZTexture internal constructor(data: ZTextureData): ZRenderizableComponentTemplate<ZTextureData, ZTextureRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZTextureData())

    @JsName("initWithArgs")
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray): this(ZTextureData(id, width, height, flipX, flipY, dataArray))

    var id: String by data::id

    var width: Int by data::width

    var height: Int by data::height

    var flipX: Boolean by data::flipX

    var flipY: Boolean by data::flipY

    var dataArray: ByteArray by data::dataArray

    override fun createRenderer(ctx: ZRenderingContext): ZTextureRenderer {
        return ZTextureRenderer(ctx, data)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
        renderer.unbind()
    }
}

@Serializable
data class ZTextureDataWrapper(
    @ProtoNumber(1)
    override var refId: Int = 0,
    @ProtoNumber(2)
    override var isReference: Boolean = false,
    @ProtoNumber(100)
    override var data: ZTextureData? = null
): ZRefComponentWrapper<ZTextureData>

@Serializable
data class ZTextureData(
    @ProtoNumber(1)
    var id: String = "",
    @ProtoNumber(2)
    var width: Int = 0,
    @ProtoNumber(3)
    var height: Int = 0,
    @ProtoNumber(4)
    var flipX: Boolean = false,
    @ProtoNumber(5)
    var flipY: Boolean = false,
    @ProtoNumber(10)
    var dataArray: ByteArray = byteArrayOf(),
): ZComponentData() {

//    @ProtoNumber(1)
//    override var refId: Int? = null
//    @ProtoNumber(2)
//    override var isReference: Boolean = false

//    @Transient
//    var bitmap: ZBitmap = ZBitmap(dataArray)

}

expect class ZTextureRenderer(ctx: ZRenderingContext, data: ZTextureData): ZComponentRender<ZTextureData> {

    override fun initialize()

    override fun render()

}

class ZTextureSerializer(loaderContext: ZLoaderContext): ZRefComponentSerializer<ZTexture, ZTextureData, ZTextureDataWrapper>(loaderContext) {

    override val deserializationStrategy: DeserializationStrategy<ZTextureDataWrapper> = ZTextureDataWrapper.serializer()

    override fun createComponentInstance(data: ZTextureData): ZTexture {
        return ZTexture(data)
    }

}