/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.context.ZContext
import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector3
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a camera object within the Zernikalos engine.
 *
 * @constructor Creates a ZCamera object with a default lens and default lookAt and up vectors.
 * @constructor Creates a ZCamera object with a specified lookAt point, up vector, and lens.
 */
@JsExport
@Serializable
class ZCamera(): ZObject() {

    override val type = ZObjectType.CAMERA

    /**
     * Represents the lens used in the camera object.
     *
     * @property near The distance to the near clipping plane.
     * @property far The distance to the far clipping plane.
     * @property fov The vertical field of view in degrees.
     * @property aspectRatio The aspect ratio (width/height)
     * */
    @ProtoNumber(4)
    var lens: ZPerspectiveLens

    /**
     * Represents the projection matrix used by the camera lens.
     *
     * @return The projection matrix.
     */
    val projectionMatrix: ZMatrix4
        get() = lens.projectionMatrix

    /**
     * The viewMatrix property represents the transformation matrix that defines the view of an object
     * in a 3D space.
     *
     * @see ZCamera
     * @see ZMatrix4
     */
    val viewMatrix: ZMatrix4
        get() = transform.matrix

    /**
     * The viewProjectionMatrix represents the matrix that combines the view matrix and the projection matrix.
     * It is a derived value, calculated by multiplying the projectionMatrix and viewMatrix.
     * The resulting matrix transforms positions from world space to clip space.
     *
     * @return The viewProjectionMatrix as a [ZMatrix4].
     */
    val viewProjectionMatrix: ZMatrix4
        get() = projectionMatrix * viewMatrix

    init {
        transform.lookAt(ZVector3.Zero, ZVector3.Up)
        lens = ZPerspectiveLens.Default
    }

    /**
     * Initializes a ZCamera with the provided lookAt point, Up vector, and lens to be used.
     *
     * @param lookAt The position to look at
     * @param up The up direction
     * @param lens The perspective lens
     */
    @JsName("initWithLens")
    constructor(lookAt: ZVector3, up: ZVector3, lens: ZPerspectiveLens): this() {
        transform.lookAt(lookAt, up)
        this.lens = lens
    }

    /**
     * Constructs a new ZCamera object with the given lookAt point and up vector.
     * This constructor initializes the transform of the camera with the provided lookAt vector and up vector.
     * It also sets the lens of the camera to the default perspective lens.
     *
     * @param lookAt The position in world space that the camera should look at.
     * @param up The up vector of the camera.
     */
    @JsName("initWithLookUp")
    constructor(lookAt: ZVector3, up: ZVector3): this() {
        transform.lookAt(lookAt, up)
        lens = ZPerspectiveLens.Default
    }

    override fun internalInitialize(ctx: ZContext) {
    }

    override fun internalRender(ctx: ZContext) {
    }

    fun setViewportResize(width: Int, height: Int) = lens.onViewportResize(width, height)

    override fun internalOnViewportResize(ctx: ZContext, width: Int, height: Int) {
        lens.onViewportResize(width, height)
    }

    companion object {
        /**
         * Represents a default perspective camera. This camera provides a perspective view of the scene.
         *
         * @property DefaultPerspectiveCamera The default perspective camera instance.
         */
        val DefaultPerspectiveCamera: ZCamera
            get() = ZCamera(ZVector3.Zero, ZVector3.Up)
    }

}
