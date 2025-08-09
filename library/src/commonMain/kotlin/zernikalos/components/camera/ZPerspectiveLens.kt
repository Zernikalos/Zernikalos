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
import zernikalos.components.ZResizable
import zernikalos.components.ZSerializableComponent
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a perspective lens used for rendering in Zernikalos.
 *
 * @param data The ZPerspectiveLensData associated with the lens
 *
 * @property fov The field of view angle for the lens
 * @property projectionMatrix The projection matrix for the lens
 *
 * @constructor Constructs a ZPerspectiveLens with the given data.
 * @constructor Constructs a ZPerspectiveLens with the given near value, far value, and field of view angle.
 * @constructor Constructs a ZPerspectiveLens with the given near value, far value, field of view angle, and aspect ratio.
 *
 * @see ZResizable
 */
@Serializable(with = ZPerspectiveLensSerializer::class)
@JsExport
open class ZPerspectiveLens internal constructor(data: ZPerspectiveLensData):
    ZSerializableComponent<ZPerspectiveLensData>(data), ZResizable {

    @JsName("init")
    constructor(near: Float, far: Float, fov: Float) : this(ZPerspectiveLensData(near, far, fov))

    @JsName("initWithAspect")
    constructor(near: Float, far: Float, fov: Float, aspectRatio: Float) : this(ZPerspectiveLensData(near, far, aspectRatio, fov))

    /**
     * Represents the field of view angle for the lens in a ZPerspectiveLens.
     *
     * @property fov The field of view angle for the lens.
     *     This angle determines the extent of the scene that is visible in the camera's view.
     *     A larger angle will capture a wider view, while a smaller angle will capture a narrower view.
     *
     * @see ZPerspectiveLens
     */
    var fov by data::fov

    val projectionMatrix: ZMatrix4 by data::projectionMatrix

    companion object {
        val Default: ZPerspectiveLens
            get() = ZPerspectiveLens(1f, 100f, 45f)

    }

    override fun onViewportResize(width: Int, height: Int) {
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
        return "[ZPerspectiveLensData(near: $near, far: $far, fov: $fov)]"
    }
}

class ZPerspectiveLensSerializer: ZComponentSerializer<ZPerspectiveLens, ZPerspectiveLensData>() {
    override val kSerializer: KSerializer<ZPerspectiveLensData>
        get() = ZPerspectiveLensData.serializer()

    override fun createComponentInstance(data: ZPerspectiveLensData): ZPerspectiveLens {
        return ZPerspectiveLens(data)
    }
}