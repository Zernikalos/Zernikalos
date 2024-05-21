/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentRender
import zernikalos.components.ZComponentSerializer
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

enum class ZAttributeId(val id: Int) {
    INDICES(0),
    POSITION(1),
    NORMAL(2),
    COLOR(3),
    UV(4),
    BONE_WEIGHT(5),
    BONE_INDEX(6)
}

// TODO: These guys are incorrect, they need to provide a new attribute each time
val ZAttrIndices = ZAttribute(0, "indices")
val ZAttrPosition = ZAttribute(1, "a_position")
val ZAttrNormal = ZAttribute(2, "a_normal")
val ZAttrColor = ZAttribute(3, "a_color")
val ZAttrUv = ZAttribute(4, "a_uv")
val ZAttrBoneWeight = ZAttribute(5, "a_boneWeight")
val ZAttrBoneIndices = ZAttribute(6, "a_boneIndices")

@JsExport
@Serializable(with = ZAttributeSerializer::class)
class ZAttribute internal constructor(data: ZAttributeData): ZComponent<ZAttributeData, ZAttributeRenderer>(data) {

    @JsName("init")
    constructor(): this(ZAttributeData())

    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String): this(ZAttributeData(id, attributeName))

    var id: Int by data::id

    var attributeName: String by data::attributeName

    override fun createRenderer(ctx: ZRenderingContext): ZAttributeRenderer {
        return ZAttributeRenderer(ctx, data)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ZAttribute) {
            return false
        }
        return data == other.data
    }
}

@Serializable
data class ZAttributeData(
    @ProtoNumber(1)
    var id: Int = -1,
    @ProtoNumber(2)
    var attributeName: String = ""
): ZComponentData()

expect class ZAttributeRenderer(ctx: ZRenderingContext, data: ZAttributeData): ZComponentRender<ZAttributeData> {
    override fun initialize()

}

class ZAttributeSerializer: ZComponentSerializer<ZAttribute, ZAttributeData>() {
    override val deserializationStrategy: DeserializationStrategy<ZAttributeData>
        get() = ZAttributeData.serializer()

    override fun createComponentInstance(data: ZAttributeData): ZAttribute {
        return ZAttribute(data)
    }

}
