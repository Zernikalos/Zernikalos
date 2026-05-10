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
import kotlin.math.PI

/**
 * Represents a perspective lens used for rendering in Zernikalos.
 *
 * @param data The ZPerspectiveLensData associated with the lens
 *
 * @property fov Vertical field of view in radians
 * @property projectionMatrix The projection matrix for the lens
 *
 * @constructor Constructs a ZPerspectiveLens with the given data.
 * @constructor Constructs a ZPerspectiveLens with the given near value, far value, and vertical field of view in radians.
 * @constructor Constructs a ZPerspectiveLens with the given near value, far value, vertical field of view in radians, and aspect ratio.
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
     * Vertical field of view in radians. Larger values widen the view; smaller values narrow it.
     *
     * @see ZPerspectiveLens
     */
    var fov by data::fov

    val projectionMatrix: ZMatrix4 by data::projectionMatrix

    companion object {
        /** Default vertical FOV ≈ 45° expressed as π/4 radians. */
        val Default: ZPerspectiveLens
            get() = ZPerspectiveLens(1f, 100f, (PI / 4.0).toFloat())

    }

    override fun onViewportResize(width: Int, height: Int) {
        data.setDimensions(width, height)
    }

    override fun internalDispose() {
        // Release any internal cached matrices if retained; projection lives in data.
    }

}

@Serializable
class ZPerspectiveLensData(): ZLensData() {

    /** Vertical field of view in radians (serialized value uses the same unit). */
    @ProtoNumber(4)
    var fov: Float = 0f

    /**
     * @param fov Vertical field of view in radians.
     */
    constructor(
        near: Float = 0f,
        far: Float = 0f,
        fov: Float
    ) : this() {
        this.near = near
        this.far = far
        this.fov = fov
    }

    /**
     * @param fov Vertical field of view in radians.
     */
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
