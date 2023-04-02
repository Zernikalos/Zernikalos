package zernikalos.components.camera

import zernikalos.math.ZMatrix4F

abstract class ZLens(var near: Float, var far: Float) {

    var width: Float? = null
    var height: Float? = null

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

    private fun computeAspectRatio(): Float {
        if (width == null || height == null) {
            return 1f
        }
        return width!! / height!!
    }
}