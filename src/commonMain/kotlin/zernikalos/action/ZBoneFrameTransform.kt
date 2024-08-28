/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.action

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZQuaternion
import zernikalos.math.ZVector3
import kotlin.js.JsExport

@JsExport
@Serializable
data class ZBoneFrameTransform(
    @ProtoNumber(1)
    var position: ZVector3? = null,
    @ProtoNumber(2)
    var rotation: ZQuaternion? = null,
    @ProtoNumber(3)
    var scale: ZVector3? = null
)