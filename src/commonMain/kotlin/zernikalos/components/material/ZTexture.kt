/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
abstract class ZBaseTexture: ZComponent2, ZBindeable2 {

    @ProtoNumber(1)
    var id: String = ""

    @ProtoNumber(2)
    var width: Int = 0

    @ProtoNumber(3)
    var height: Int = 0

    @ProtoNumber(4)
    var flipX: Boolean = false

    @ProtoNumber(5)
    var flipY: Boolean = false

    @ProtoNumber(10)
    var dataArray: ByteArray = byteArrayOf()

    override val isRenderizable: Boolean = true

    @JsName("init")
    constructor() {

    }

    @JsName("initWithArgs")
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray) {
        this.id = id
        this.dataArray = dataArray
        this.width = width
        this.height = height
        this.flipX = flipX
        this.flipY = flipY
    }


}

@Serializable
expect open class ZTextureRender: ZBaseTexture {
    constructor()
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray)

    override fun bind(ctx: ZRenderingContext)
    override fun unbind(ctx: ZRenderingContext)
}

@Serializable
@JsExport
class ZTexture: ZTextureRender {
    @JsName("init")
    constructor(): super()
    @JsName("initWithArgs")
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray): super(id, width, height, flipX, flipY, dataArray)
}


/**
 * @suppress
 */
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
): ZComponentData()


/**
 * @suppress
 */
internal class ZTextureSerializer(private val loaderContext: ZLoaderContext): ZComponentSerializer<ZTexture, ZTextureData>() {

    override val kSerializer: KSerializer<ZTextureData> = ZTextureData.serializer()

    override fun createComponentInstance(data: ZTextureData): ZTexture {
        if (loaderContext.hasComponent(data.id)) {
            return loaderContext.getComponent(data.id) as ZTexture
        }
        val texture = ZTexture(data.id, data.width, data.height, data.flipX, data.flipY, data.dataArray)
        loaderContext.addComponent(texture.id, texture)
        return texture
    }

}