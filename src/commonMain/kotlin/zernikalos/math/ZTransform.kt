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
class ZTransform {

    @Transient
    private val _matrix: ZMatrix4F = ZMatrix4F.Identity

    private val _location: ZVector3F = ZVector3F.Zero
    private val _rotation: ZQuaternion = ZQuaternion.Identity
    private val _scale: ZVector3F = ZVector3F.Ones

    private val _forward: ZVector3F = ZVector3F.Forward
    private val _right: ZVector3F = ZVector3F.Right
    private val _up: ZVector3F = ZVector3F.Up

    @JsName("defaultCtor")
    constructor()

    @JsName("argsCtor")
    constructor(location: ZVector3F, rotation: ZQuaternion, scale: ZVector3F) {
        _location.copy(location)
        _rotation.copy(rotation)
        _scale.copy(scale)
    }

    var location: ZVector3F
        get() = _location
        set(value) {
            _location.copy(value)
        }

    var rotation: ZQuaternion
        get() = _rotation
        set(value) {
            _rotation.copy(value)
        }

    var scale: ZVector3F
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: ZMatrix4F
        get() {
            ZMatrix4F.identity(_matrix)
            ZMatrix4F.translate(_matrix, _location)
            ZMatrix4F.rotate(_matrix, _rotation)
            ZMatrix4F.scale(_matrix, _scale)
            return _matrix
        }

    fun setLocation(x: Float, y: Float, z: Float) {
        _location.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        ZQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
    }

    @JsName("rotateByVector")
    fun setRotation(angle: Float, axis: ZVector3F) {
        ZQuaternion.fromAngleAxis(_rotation, angle, axis)
    }

    @JsName("setLookAtUp")
    fun lookAt(look: ZVector3F, up: ZVector3F) {
        val m = ZMatrix4F()
        ZMatrix4F.lookAt(m, _location, look, up)
        ZQuaternion.fromMatrix4(_rotation, m)
    }

    fun lookAt(look: ZVector3F) {
        lookAt(look, _up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        _location.x += x
        _location.y += y
        _location.z += z
    }

    @JsName("translateByVector")
    fun translate(v: ZVector3F) {
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
    fun scale(v: ZVector3F) {
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
    fun rotate(angle: Float, axis: ZVector3F) {
        rotate(angle, axis.x, axis.y, axis.z)
    }

    @JsName("rotateAroundPointAxesThrough")
    fun rotateAround(angle: Float, point: ZVector3F, axis: ZVector3F, through: ZVector3F) {
        val q = ZQuaternion()
        ZQuaternion.fromAngleAxis(q, angle, axis)
        ZQuaternion.mult(_rotation, _rotation, q)

        val v = ZVector3F()
        //V-P
        ZVector3F.subtract(v, through, point)
        //R(V-P)
        ZVector3F.rotateVector(v, _rotation, v)
        //Loc = P + R(V-P)
        ZVector3F.add(_location, point, v)
    }

    fun rotateAround(angle: Float, point: ZVector3F, axis: ZVector3F) {
        rotateAround(angle, point, axis, _location)
    }

    private fun transformLocalAxis() {
        ZMatrix4F.mult(_forward, _matrix, _forward)
        ZMatrix4F.mult(_up, _matrix, _up)
        ZMatrix4F.mult(_right, _matrix, _right)
    }

}

@Serializable
data class ZkTransformSurrogate(val location: ZVector3F, val rotation: ZQuaternion, val scale: ZVector3F)

class ZkTransformSerializer : KSerializer<ZTransform> {
    override val descriptor: SerialDescriptor = ZkTransformSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZTransform) {

    }

    override fun deserialize(decoder: Decoder): ZTransform {
        val surrogate = decoder.decodeSerializableValue(ZkTransformSurrogate.serializer())
        return ZTransform(surrogate.location, surrogate.rotation, surrogate.scale)
    }
}