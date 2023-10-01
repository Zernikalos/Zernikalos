package zernikalos.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZkTransformSerializer::class)
class ZTransform() {

    @Transient
    private val _matrix: ZMatrix4 = ZMatrix4.Identity

    private val _location: ZVector3 = ZVector3.Zero
    private val _rotation: ZQuaternion = ZQuaternion.Identity
    private val _scale: ZVector3 = ZVector3.Ones

    private val _forward: ZVector3 = ZVector3.Forward
    private val _right: ZVector3 = ZVector3.Right
    private val _up: ZVector3 = ZVector3.Up


    @JsName("initWithArgs")
    constructor(location: ZVector3, rotation: ZQuaternion, scale: ZVector3): this() {
        _location.copy(location)
        _rotation.copy(rotation)
        _scale.copy(scale)
    }

    var location: ZVector3
        get() = _location
        set(value) {
            _location.copy(value)
        }

    var rotation: ZQuaternion
        get() = _rotation
        set(value) {
            _rotation.copy(value)
        }

    var scale: ZVector3
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: ZMatrix4
        get() {
            ZMatrix4.identity(_matrix)
            ZMatrix4.translate(_matrix, _location)
            ZMatrix4.rotate(_matrix, _rotation)
            ZMatrix4.scale(_matrix, _scale)
            return _matrix
        }

    fun setLocation(x: Float, y: Float, z: Float) {
        _location.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        ZQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
    }

    @JsName("rotateByVector")
    fun setRotation(angle: Float, axis: ZVector3) {
        ZQuaternion.fromAngleAxis(_rotation, angle, axis)
    }

    @JsName("setLookAtUp")
    fun lookAt(look: ZVector3, up: ZVector3) {
        val m = ZMatrix4()
        ZMatrix4.lookAt(m, _location, look, up)
        ZQuaternion.fromMatrix4(_rotation, m)
    }

    fun lookAt(look: ZVector3) {
        lookAt(look, _up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        _location.x += x
        _location.y += y
        _location.z += z
    }

    @JsName("translateByVector")
    fun translate(v: ZVector3) {
        translate(v.x, v.y, v.z)
    }

    @JsName("scaleByValues")
    fun scale(sx: Float, sy: Float, sz: Float) {
        _scale.setValues(sx, sy, sz)
    }

    @JsName("scaleByFactor")
    fun scale(s: Float) {
        _scale.setValues(s, s, s)
    }

    @JsName("scaleByVector")
    fun scale(v: ZVector3) {
        _scale.setValues(v.x, v.y, v.z)
    }

    @JsName("rotateByQuat")
    fun rotate(q: ZQuaternion) {
        ZQuaternion.mult(_rotation, _rotation, q)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val q = ZQuaternion()
        ZQuaternion.fromAngleAxis(q, angle, x, y, z)
        rotate(q)
    }

    @JsName("rotateByAngleAxisVector")
    fun rotate(angle: Float, axis: ZVector3) {
        rotate(angle, axis.x, axis.y, axis.z)
    }

    @JsName("rotateAroundPointAxesThrough")
    fun rotateAround(angle: Float, point: ZVector3, axis: ZVector3, through: ZVector3) {
        val q = ZQuaternion()
        ZQuaternion.fromAngleAxis(q, angle, axis)
        ZQuaternion.mult(_rotation, _rotation, q)

        val v = ZVector3()
        //V-P
        ZVector3.subtract(v, through, point)
        //R(V-P)
        ZVector3.rotateVector(v, _rotation, v)
        //Loc = P + R(V-P)
        ZVector3.add(_location, point, v)
    }

    fun rotateAround(angle: Float, point: ZVector3, axis: ZVector3) {
        rotateAround(angle, point, axis, _location)
    }

    private fun transformLocalAxis() {
        ZMatrix4.mult(_forward, _matrix, _forward)
        ZMatrix4.mult(_up, _matrix, _up)
        ZMatrix4.mult(_right, _matrix, _right)
    }

}

@Serializable
data class ZkTransformSurrogate(val location: ZVector3, val rotation: ZQuaternion, val scale: ZVector3)

class ZkTransformSerializer : KSerializer<ZTransform> {
    override val descriptor: SerialDescriptor = ZkTransformSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZTransform) {

    }

    override fun deserialize(decoder: Decoder): ZTransform {
        val surrogate = decoder.decodeSerializableValue(ZkTransformSurrogate.serializer())
        return ZTransform(surrogate.location, surrogate.rotation, surrogate.scale)
    }
}