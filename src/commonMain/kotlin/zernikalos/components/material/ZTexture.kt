/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.loader.ZLoaderContext
import zernikalos.logger.logger
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Texture filter mode for minification and magnification filtering.
 */
@JsExport
@Serializable
enum class ZTextureFilterMode {
    @SerialName("nearest")
    NEAREST,
    @SerialName("linear")
    LINEAR
}

/**
 * Texture wrap mode for texture coordinate addressing.
 */
@JsExport
@Serializable
enum class ZTextureWrapMode {
    @SerialName("repeat")
    REPEAT,
    @SerialName("clamp-to-edge")
    CLAMP_TO_EDGE,
    @SerialName("mirror-repeat")
    MIRROR_REPEAT
}

/**
 * Texture format specification.
 */
@JsExport
@Serializable
enum class ZTextureFormat {
    @SerialName("rgba8unorm")
    RGBA8UNORM,
    @SerialName("rgba8unorm-srgb")
    RGBA8UNORM_SRGB,
    @SerialName("bgra8unorm")
    BGRA8UNORM,
    @SerialName("bgra8unorm-srgb")
    BGRA8UNORM_SRGB,
    @SerialName("r8unorm")
    R8UNORM,
    @SerialName("rg8unorm")
    RG8UNORM,
    @SerialName("rgb8unorm")
    RGB8UNORM
}

/**
 * Represents a texture component in the Zernikalos framework.
 *
 */
@JsExport
class ZTexture internal constructor(private val data: ZTextureData): ZRenderizableComponent<ZTextureRenderer>(), ZBindeable {

    @JsName("init")
    constructor(): this(ZTextureData())

    @JsName("initWithArgs")
    constructor(id: String, width: Int, height: Int, flipX: Boolean, flipY: Boolean, dataArray: ByteArray): this(
        ZTextureData(
            id = id,
            width = width,
            height = height,
            flipX = flipX,
            flipY = flipY,
            dataArray = dataArray
        )
    )

    var id: String by data::id

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

    /**
     * Represents the minification filter mode for the texture.
     */
    var minFilter: ZTextureFilterMode by data::minFilter

    /**
     * Represents the magnification filter mode for the texture.
     */
    var magFilter: ZTextureFilterMode by data::magFilter

    /**
     * Represents the wrap mode for the U (horizontal) texture coordinate.
     */
    var wrapModeU: ZTextureWrapMode by data::wrapModeU

    /**
     * Represents the wrap mode for the V (vertical) texture coordinate.
     */
    var wrapModeV: ZTextureWrapMode by data::wrapModeV

    /**
     * Represents the texture format.
     */
    var format: ZTextureFormat by data::format

    override fun createRenderer(ctx: ZRenderingContext): ZTextureRenderer {
        return ZTextureRenderer(ctx, data)
    }

    override fun internalInitialize(ctx: ZRenderingContext) {
        logger.debug("Initializing texture $refId")
    }

    override fun bind() = renderer.bind()
    override fun unbind() = renderer.unbind()
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
    @ProtoNumber(6)
    var minFilter: ZTextureFilterMode = ZTextureFilterMode.LINEAR,
    @ProtoNumber(7)
    var magFilter: ZTextureFilterMode = ZTextureFilterMode.LINEAR,
    @ProtoNumber(8)
    var wrapModeU: ZTextureWrapMode = ZTextureWrapMode.CLAMP_TO_EDGE,
    @ProtoNumber(9)
    var wrapModeV: ZTextureWrapMode = ZTextureWrapMode.CLAMP_TO_EDGE,
    @ProtoNumber(10)
    var format: ZTextureFormat = ZTextureFormat.RGBA8UNORM,
    @ProtoNumber(100)
    var dataArray: ByteArray = byteArrayOf(),
): ZComponentData()

/**
 * @suppress
 */
expect class ZTextureRenderer(ctx: ZRenderingContext, data: ZTextureData): ZComponentRenderer {

    override fun initialize()

    override fun render()

}

/**
 * @suppress
 */
internal class ZTextureSerializer(private val loaderContext: ZLoaderContext): ZComponentSerializer<ZTexture, ZTextureData>() {

    override val kSerializer: KSerializer<ZTextureData> = ZTextureData.serializer()

    override fun createComponentInstance(data: ZTextureData): ZTexture {
        if (loaderContext.hasComponent(data.id)) {
            return loaderContext.getComponent(data.id) as ZTexture
        }
        val texture = ZTexture(data)
        loaderContext.addComponent(texture.id, texture)
        return texture
    }

}
