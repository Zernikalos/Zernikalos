/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBindeable
import zernikalos.components.ZComponentRenderer
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZBufferContentSerializer::class)
class ZBufferContent internal constructor(private val data: ZBufferContentData): ZRenderizableComponent<ZBufferContentRenderer>(), ZBindeable {

    /**
     * Initializes a new instance of `ZBufferContent` class.
     * It is used to instantiate a `ZBufferContent` object with default data values.
     */
    @JsName("init")
    constructor(): this(ZBufferContentData())

    /**
     * Initializes a ZBufferContent object with the given arguments.
     *
     * @param id The ID of the buffer content.
     * @param dataArray The array of byte data for the buffer.
     */
    @JsName("initWithArgs")
    constructor(
        id: Int,
        dataArray: ByteArray
    ): this(ZBufferContentData(id, dataArray))

    /**
     * Represents the ID of the buffer content.
     */
    var id: Int by data::id

    /**
     * Represents an array of bytes for ZBufferContent data.
     */
    var dataArray: ByteArray by data::dataArray

    /**
     * Indicates whether the [data] has any data.
     */
    val hasData: Boolean
        get() = data.dataArray.isNotEmpty()

    override fun createRenderer(ctx: ZRenderingContext): ZBufferContentRenderer {
        return ZBufferContentRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()

    override fun unbind() = renderer.unbind()

    override fun toString(): String {
        return "ZBufferContent(id=${data.id}, size=${data.dataArray.size})"
    }

}

/**
 * @suppress
 */
@Serializable
@JsExport
data class ZBufferContentData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var dataArray: ByteArray = byteArrayOf()
)

expect class ZBufferContentRenderer(ctx: ZRenderingContext, data: ZBufferContentData): ZComponentRenderer {
    override fun initialize()

    override fun bind()

    override fun unbind()
}

class ZBufferContentSerializer: ZComponentSerializer<ZBufferContent, ZBufferContentData>() {
    override val kSerializer: KSerializer<ZBufferContentData> = ZBufferContentData.serializer()

    override fun createComponentInstance(data: ZBufferContentData): ZBufferContent {
        return ZBufferContent(data)
    }
}

