package zernikalos.components.camera

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZMatrix4F

@Serializable
abstract class ZLens(@ProtoNumber(1) var near: Float,@ProtoNumber(2)  var far: Float) {

    @Transient
    var width: Float? = null
    @Transient
    var height: Float? = null

    @ProtoNumber(3)
    protected var _aspectRatio: Float? = null
    @Transient
    var useComputedAspectRatio: Boolean = false

    @Transient
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