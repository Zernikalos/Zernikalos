/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.light

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ZLampType {
    DIRECTIONAL,
    POINT,
    SPOT
}

@JsExport
@Serializable
abstract class ZLamp {

}

@JsExport
@Serializable
class ZDirectionalLamp: ZLamp() {
}

@JsExport
@Serializable
class ZPointLamp: ZLamp() {
    @ProtoNumber(1)
    var range: Float = 0.0f
    @ProtoNumber(2)
    var decay: Float = 0.0f
}

@JsExport
@Serializable
class ZSpotLamp: ZLamp() {
    @ProtoNumber(1)
    var range: Float = 0.0f
    @ProtoNumber(2)
    var decay: Float = 0.0f
    @ProtoNumber(3)
    var innerAngle: Float = 0.0f
    @ProtoNumber(4)
    var outerAngle: Float = 0.0f
}
