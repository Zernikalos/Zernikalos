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
class ZkTransform {

    @Transient
    private val _matrix: ZkMatrix4F = ZkMatrix4F.Identity

    private val _location: ZkVector3F = ZkVector3F.Zero
    private val _rotation: ZkQuaternion = ZkQuaternion.Identity
    private val _scale: ZkVector3F = ZkVector3F.Ones

    private val _forward: ZkVector3F = ZkVector3F(1f, 0f, 0f)
    private val _right: ZkVector3F = ZkVector3F(0f, 1f, 0f)
    private val _up: ZkVector3F = ZkVector3F(0f, 0f, 1f)

    @JsName("defaultCtor")
    constructor()

    @JsName("argsCtor")
    constructor(location: ZkVector3F, rotation: ZkQuaternion, scale: ZkVector3F) {
        _location.copy(location)
        _rotation.copy(rotation)
        _scale.copy(scale)
    }

    var location: ZkVector3F
        get() = _location
        set(value) {
            _location.copy(value)
        }

    var rotation: ZkQuaternion
        get() = _rotation
        set(value) {
            _rotation.copy(value)
        }

    var scale: ZkVector3F
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: ZkMatrix4F
        get() {
            ZkMatrix4F.identity(_matrix)
            ZkMatrix4F.translate(_matrix, _location)
            ZkMatrix4F.rotate(_matrix, _rotation)
            ZkMatrix4F.scale(_matrix, _scale)
            return _matrix
        }

    fun setLocation(x: Float, y: Float, z: Float) {
        _location.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        ZkQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
    }

    @JsName("rotateByVector")
    fun setRotation(angle: Float, axis: ZkVector3F) {
        ZkQuaternion.fromAngleAxis(_rotation, angle, axis)
    }

    @JsName("setLookAtUp")
    fun setLookAt(look: ZkVector3F, up: ZkVector3F) {
        val m = ZkMatrix4F()
        ZkMatrix4F.lookAt(m, _location, look, up)
        ZkQuaternion.fromMatrix4(_rotation, m)
    }

    fun setLookAt(look: ZkVector3F) {
        setLookAt(look, _up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        _location.x += x
        _location.y += y
        _location.z += z
    }

    @JsName("translateByVector")
    fun translate(v: ZkVector3F) {
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
    fun scale(v: ZkVector3F) {
        _scale.setValues(v.x, v.y, v.z)
    }

    @JsName("rotateByQuat")
    fun rotate(q: ZkQuaternion) {
        ZkQuaternion.mult(_rotation, _rotation, q)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val q = ZkQuaternion()
        ZkQuaternion.fromAngleAxis(q, angle, x, y, z)
        rotate(q)
    }

    @JsName("rotateByAngleAxisVector")
    fun rotate(angle: Float, axis: ZkVector3F) {
        rotate(angle, axis.x, axis.y, axis.z)
    }

    @JsName("rotateAroundPointAxesThrough")
    fun rotateAround(angle: Float, point: ZkVector3F, axis: ZkVector3F, through: ZkVector3F) {
        val q = ZkQuaternion()
        ZkQuaternion.fromAngleAxis(q, angle, axis)
        ZkQuaternion.mult(_rotation, _rotation, q)

        val v = ZkVector3F()
        //V-P
        ZkVector3F.subtract(v, through, point)
        //R(V-P)
        ZkVector3F.rotateVector(v, _rotation, v)
        //Loc = P + R(V-P)
        ZkVector3F.add(_location, point, v)
    }

    fun rotateAround(angle: Float, point: ZkVector3F, axis: ZkVector3F) {
        rotateAround(angle, point, axis, _location)
    }

    private fun transformLocalAxis() {
        ZkMatrix4F.mult(_forward, _matrix, _forward)
        ZkMatrix4F.mult(_up, _matrix, _up)
        ZkMatrix4F.mult(_right, _matrix, _right)
    }

}

@Serializable
data class ZkTransformSurrogate(val location: ZkVector3F, val rotation: ZkQuaternion, val scale: ZkVector3F)

class ZkTransformSerializer : KSerializer<ZkTransform> {
    override val descriptor: SerialDescriptor = ZkTransformSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZkTransform) {

    }

    override fun deserialize(decoder: Decoder): ZkTransform {
        val surrogate = decoder.decodeSerializableValue(ZkTransformSurrogate.serializer())
        return ZkTransform(surrogate.location, surrogate.rotation, surrogate.scale)
    }
}