/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.camera

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable
@JsExport
open class ZPerspectiveLens: ZLens {

    @ProtoNumber(4)
    var fov: Float

    @JsName("init")
    constructor(near: Float, far: Float, fov: Float) : super(near, far) {
        this.fov = fov
    }

    @JsName("initWithAspect")
    constructor(near: Float, far: Float, fov: Float, aspectRatio: Float) : this(near, far, fov) {
        _aspectRatio = aspectRatio
    }

    override val projectionMatrix: ZMatrix4
        get() {
            ZMatrix4.perspective(matrix, fov, aspectRatio, near, far)
            return matrix
        }

    companion object {
        val Default: ZPerspectiveLens
            get() = ZPerspectiveLens(1f, 100f, 45f)

    }
}