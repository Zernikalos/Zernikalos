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
import kotlin.js.JsExport

@JsExport
@Serializable
class ZSkeletalAction(
    @ProtoNumber(1) val name: String
) {
    @ProtoNumber(2)
    var duration: Float = 0f

    @ProtoNumber(10)
    private val _keyFrames: ArrayList<ZKeyFrame> = ArrayList()

    val keyFrames: Array<ZKeyFrame>
        get() = _keyFrames.toTypedArray()

    fun addKeyFrame(keyFrame: ZKeyFrame) {
        _keyFrames.add(keyFrame)
    }
}