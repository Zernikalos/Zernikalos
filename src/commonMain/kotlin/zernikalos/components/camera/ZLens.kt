package zernikalos.components.camera

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.math.ZMatrix4
import kotlin.js.JsExport

@Serializable
@JsExport
abstract class ZLens(@ProtoNumber(1) var near: Float, @ProtoNumber(2) var far: Float) {

    var width: Float? = null
    var height: Float? = null

    @ProtoNumber(3)
    protected var _aspectRatio: Float? = null

    var useComputedAspectRatio: Boolean = false

    protected val matrix = ZMatrix4.Identity

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

    abstract val projectionMatrix: ZMatrix4

    private fun computeAspectRatio(): Float {
        if (width == null || height == null) {
            return 1f
        }
        return width!! / height!!
    }
}