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
import zernikalos.components.ZComponentData
import zernikalos.math.ZMatrix4

@Serializable
abstract class ZLensData(): ZComponentData() {

    @ProtoNumber(1)
    var near: Float = 0f

    @ProtoNumber(2)
    var far: Float = 0f

    @ProtoNumber(3)
    var _aspectRatio: Float? = null

    var width: Float? = null
    var height: Float? = null

    var useComputedAspectRatio: Boolean = false

    protected val matrix = ZMatrix4.Identity
    abstract val projectionMatrix: ZMatrix4

    constructor(
        near: Float,
        far: Float
    ): this() {
        this.near = near
        this.far = far
    }

    constructor(
        near: Float,
        far: Float,
        aspectRatio: Float
    ): this() {
        this.near = near
        this.far = far
        this.aspectRatio = aspectRatio
    }

    var aspectRatio: Float
    get() {
        if (_aspectRatio != null && !useComputedAspectRatio) {
            return _aspectRatio as Float
        }
        return computeAspectRatio()
    }
    set(value) {
        _aspectRatio = value
        useComputedAspectRatio = false
    }

    fun setDimensions(width: Int, height: Int) {
        this.width = width.toFloat()
        this.height = height.toFloat()
    }

    private fun computeAspectRatio(): Float {
        if (width == null || height == null) {
            return 1f
        }
        return width!! / height!!
    }
}