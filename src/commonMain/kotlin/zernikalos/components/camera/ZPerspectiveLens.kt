/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.camera

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZComponentSerializer
import zernikalos.components.ZComponentTemplate
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable(with = ZPerspectiveLensSerializer::class)
@JsExport
open class ZPerspectiveLens internal constructor(data: ZPerspectiveLensData):
    ZComponentTemplate<ZPerspectiveLensData>(data) {

    @JsName("init")
    constructor(near: Float, far: Float, fov: Float) : this(ZPerspectiveLensData(near, far, fov))

    @JsName("initWithAspect")
    constructor(near: Float, far: Float, fov: Float, aspectRatio: Float) : this(ZPerspectiveLensData(near, far, aspectRatio, fov))

    var fov by data::fov

    val projectionMatrix: ZMatrix4 by data::projectionMatrix

    companion object {
        val Default: ZPerspectiveLens
            get() = ZPerspectiveLens(1f, 100f, 45f)

    }

    fun setDimensions(width: Int, height: Int) {
        data.setDimensions(width, height)
    }
}

@Serializable
class ZPerspectiveLensData(): ZLensData() {

    @ProtoNumber(4)
    var fov: Float = 0f

    constructor(
        near: Float = 0f,
        far: Float = 0f,
        fov: Float
    ) : this() {
        this.near = near
        this.far = far
        this.fov = fov
    }

    constructor(
        near: Float = 0f,
        far: Float = 0f,
        fov: Float,
        aspectRatio: Float
    ) : this(near, far, fov) {
        this.aspectRatio = aspectRatio
    }

    override val projectionMatrix: ZMatrix4
        get() {
            ZMatrix4.perspective(matrix, fov, aspectRatio, near, far)
            return matrix
        }

    override fun toString(): String {
        TODO("Not yet implemented")
    }
}

class ZPerspectiveLensSerializer: ZComponentSerializer<ZPerspectiveLens, ZPerspectiveLensData>() {
    override val kSerializer: KSerializer<ZPerspectiveLensData>
        get() = ZPerspectiveLensData.serializer()

    override fun createComponentInstance(data: ZPerspectiveLensData): ZPerspectiveLens {
        return ZPerspectiveLens(data)
    }
}