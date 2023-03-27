package zernikalos.components.camera

import zernikalos.math.ZMatrix4F

open class ZPerspectiveLens: ZLens {

    var fov: Float

    constructor(near: Float, far: Float, fov: Float) : super(near, far) {
        this.fov = fov
    }

    constructor(near: Float, far: Float, fov: Float, aspectRatio: Float) : this(near, far, fov) {
        _aspectRatio = aspectRatio
    }

    override val projectionMatrix: ZMatrix4F
        get() {
            ZMatrix4F.perspective(matrix, fov, aspectRatio, near, far)
            return matrix
        }

}