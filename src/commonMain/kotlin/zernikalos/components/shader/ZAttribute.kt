/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.ZRenderer
import zernikalos.components.ZComponentData
import zernikalos.components.ZRenderizableComponent
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
enum class ZAttributeId(val id: Int, val attrName: String) {
    INDICES(0, "indices"),
    POSITION(1, "a_position"),
    NORMAL(2, "a_normal"),
    COLOR(3, "a_color"),
    UV(4, "a_uv"),
    BONE_WEIGHT(5, "a_boneWeight"),
    BONE_INDEX(6, "a_boneIndices"),
    CUSTOM(-1, "")
}


val ZAttrIndices: ZAttribute
    get() = ZAttribute(ZAttributeId.INDICES)
val ZAttrPosition: ZAttribute
    get() = ZAttribute(ZAttributeId.POSITION)
val ZAttrNormal: ZAttribute
    get() = ZAttribute(ZAttributeId.NORMAL)
val ZAttrColor: ZAttribute
    get() = ZAttribute(ZAttributeId.COLOR)
val ZAttrUv: ZAttribute
    get() = ZAttribute(ZAttributeId.UV)
val ZAttrBoneWeight: ZAttribute
    get() = ZAttribute(ZAttributeId.BONE_WEIGHT)
val ZAttrBoneIndices: ZAttribute
    get() = ZAttribute(ZAttributeId.BONE_INDEX)

@JsExport
class ZAttribute internal constructor(data: ZAttributeData): ZRenderizableComponent<ZAttributeData, ZAttributeRenderer>(data) {

    @JsName("init")
    constructor(): this(ZAttributeData())

    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String): this(ZAttributeData(id, attributeName))

    @JsName("initWithAttrId")
    constructor(attrId: ZAttributeId): this(ZAttributeData(attrId.id, attrId.attrName))

    var id: Int by data::id

    var attributeName: String by data::attributeName

    val attrId: ZAttributeId
        get() = ZAttributeId.entries.find { it.id == id } ?: ZAttributeId.CUSTOM

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

data class ZAttributeData(
    var id: Int = -1,
    var attributeName: String = ""
): ZComponentData()

expect class ZAttributeRenderer(ctx: ZRenderingContext, data: ZAttributeData): ZRenderer {
    override fun initialize()
}
