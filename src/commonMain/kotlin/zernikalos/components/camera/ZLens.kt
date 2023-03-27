package zernikalos.components.camera

import zernikalos.math.ZMatrix4F

abstract class ZLens {

    enum class Types {
        ORTHOGONAL,
        PROJECTIVE
    }

    var width: Float? = null
    var height: Float? = null

    var near: Float
    var far: Float
    protected var _aspectRatio: Float? = null
    var useComputedAspectRatio: Boolean = false

    protected val matrix = ZMatrix4F.Identity

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

    abstract val projectionMatrix: ZMatrix4F

    constructor(near: Float, far: Float) {
        this.near = near
        this.far = far
    }

    private fun computeAspectRatio(): Float {
        if (width == null || height == null) {
            return 1f
        }
        return width!! / height!!
    }
}