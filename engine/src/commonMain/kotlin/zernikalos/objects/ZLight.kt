/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.light.ZLamp
import zernikalos.components.light.ZLampType
import zernikalos.context.ZContext
import zernikalos.math.ZColor
import kotlin.js.JsExport

@JsExport
@Serializable
class ZLight: ZObject() {

    @Transient
    override val type: ZObjectType = ZObjectType.LIGHT

    @ProtoNumber(5)
    var color: ZColor = ZColor.WHITE
    @ProtoNumber(6)
    var intensity: Float = 1.0f

    @ProtoNumber(10)
    @Contextual
    var lamp: ZLamp? = null

    val lampType: ZLampType
        get() = lamp?.lampType ?: throw IllegalStateException("No lamp type specified for light")

    override fun internalInitialize(ctx: ZContext) {
        // Lighting is now discovered via findAllLights()/findAllDirectLights()/findAmbientLight()
        // from the scene graph each frame. No registration in context needed.
    }

    override fun internalRender(ctx: ZContext) {
    }

    override fun internalDispose(ctx: ZContext) {
    }
}
