/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.*
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
abstract class ZBaseAttribute: ZComponent2 {

    override val isRenderizable: Boolean = true

    @JsName("init")
    constructor(): this(-1, "")

    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String) {
        this.id = id
        this.attributeName = attributeName
    }

    @JsName("initWithAttrId")
    constructor(attrId: ZAttributeId): this(attrId.id, attrId.attrName)

    var id: Int

    var attributeName: String

    val attrId: ZAttributeId
        get() = ZAttributeId.entries.find { it.id == id } ?: ZAttributeId.CUSTOM


    override fun equals(other: Any?): Boolean {
        if (other !is ZBaseAttribute) {
            return false
        }
        return id == other.id && attributeName == other.attributeName
    }
}

expect open class ZAttributeRender: ZBaseAttribute {
    constructor()
    constructor(id: Int, attributeName: String)
    constructor(attrId: ZAttributeId)
}

@JsExport
class ZAttribute: ZAttributeRender {
    @JsName("init")
    constructor(): super()
    @JsName("initWithArgs")
    constructor(id: Int, attributeName: String): super(id, attributeName)
    @JsName("initWithAttrId")
    constructor(attrId: ZAttributeId): super(attrId)

}

