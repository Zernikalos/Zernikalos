/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.light.ZDirectionalLamp
import zernikalos.components.light.ZLampType
import zernikalos.components.light.ZPointLamp
import zernikalos.components.light.ZSpotLamp
import zernikalos.context.ZContext
import zernikalos.math.ZColor
import kotlin.js.JsExport

@JsExport
@Serializable
class ZLight: ZObject() {

    override val type: ZObjectType = ZObjectType.LIGHT

    @ProtoNumber(4)
    var lampType: ZLampType = ZLampType.DIRECTIONAL

    @ProtoNumber(5)
    var color: ZColor = ZColor.WHITE
    @ProtoNumber(6)
    var intensity: Float = 1.0f

    @ProtoNumber(10)
    var directionalLamp: ZDirectionalLamp? = null
    @ProtoNumber(11)
    var pointLamp: ZPointLamp? = null
    @ProtoNumber(12)
    var spotLamp: ZSpotLamp? = null

    override fun internalInitialize(ctx: ZContext) {
    }

    override fun internalRender(ctx: ZContext) {
    }
}
