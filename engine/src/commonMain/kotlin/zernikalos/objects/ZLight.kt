/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.light.*
import zernikalos.context.ZContext
import zernikalos.math.ZColor
import kotlin.js.JsExport

@JsExport
@Serializable
class ZLight: ZObject() {

    @Transient
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
    @ProtoNumber(13)
    var ambientLamp: ZAmbientLamp? = null

    override fun internalInitialize(ctx: ZContext) {
        when (lampType) {
            ZLampType.AMBIENT -> {
                if (ctx.sceneContext.activeAmbientLight == null) {
                    ctx.sceneContext.activeAmbientLight = this
                }
            }
            else -> {
                if (ctx.sceneContext.activeLight == null) {
                    ctx.sceneContext.activeLight = this
                }
            }
        }
    }

    override fun internalRender(ctx: ZContext) {
    }

    override fun internalDispose(ctx: ZContext) {
    }
}
