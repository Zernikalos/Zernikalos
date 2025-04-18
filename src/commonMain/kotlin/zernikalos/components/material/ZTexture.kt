/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import zernikalos.logger.logger
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a texture component in the Zernikalos framework.
 *
 */
@JsExport
class ZTexture internal constructor(data: ZTextureData): ZRenderizableComponent<ZTextureData, ZTextureRenderer>(data), ZBindeable {

    @JsName("init")
    constructor(): this(ZTextureData())

    @JsName("initWithArgs")
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray): this(ZTextureData(id, width, height, flipX, flipY, dataArray))

    /**
     * Represents the width of the texture image
     */
    var width: Int by data::width

    /**
     * Represents the height of the texture image
     */
    var height: Int by data::height

    /**
     * Represents whether the texture should be flipped horizontally.
     */
    var flipX: Boolean by data::flipX

    /**
     * Represents whether the texture should be flipped vertically.
     */
    var flipY: Boolean by data::flipY

    /**
     * Represents an array of bytes used for storing the image data.
     *
     * @property dataArray The byte array containing the data.
     */
    var dataArray: ByteArray by data::dataArray

    override fun createRenderer(ctx: ZRenderingContext): ZTextureRenderer {
        return ZTextureRenderer(ctx, data)
    }

    override fun internalInitialize(ctx: ZRenderingContext) {
        logger.debug("Initializing texture $refId")
    }
}

/**
 * @suppress
 */
@Serializable
internal data class ZTextureDataWrapper(
    @ProtoNumber(1)
    override var refId: String = "",
    @ProtoNumber(2)
    override var isReference: Boolean = false,
    @ProtoNumber(100)
    override var data: ZTextureData? = null
): ZRefComponentWrapper<ZTextureData>

/**
 * @suppress
 */
@Serializable
data class ZTextureData(
    @ProtoNumber(1)
    override var refId: String = "",
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
expect class ZTextureRenderer(ctx: ZRenderingContext, data: ZTextureData): ZComponentRender<ZTextureData> {

    override fun initialize()

    override fun render()

}

/**
 * @suppress
 */
internal class ZTextureSerializer(private val loaderContext: ZLoaderContext): ZComponentSerializer<ZTexture, ZTextureData>() {

    override val kSerializer: KSerializer<ZTextureData> = ZTextureData.serializer()

    override fun createComponentInstance(data: ZTextureData): ZTexture {
        if (loaderContext.hasComponent(data.refId)) {
            return loaderContext.getComponent(data.refId) as ZTexture
        }
        val texture = ZTexture(data)
        loaderContext.addComponent(texture.refId, texture)
        return texture
    }

}