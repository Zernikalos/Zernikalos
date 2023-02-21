package mr.robotto.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = MrTransformSerializer::class)
class MrTransform {

    @Transient
    private val _matrix: MrMatrix4f = MrMatrix4f.Identity

    private val _location: MrVector3f   = MrVector3f.Zero
    private val _rotation: MrQuaternion = MrQuaternion.Identity
    private val _scale:    MrVector3f   = MrVector3f.Ones

    private val _forward: MrVector3f = MrVector3f(1f, 0f, 0f)
    private val _right:   MrVector3f = MrVector3f(0f, 1f, 0f)
    private val _up:      MrVector3f = MrVector3f(0f, 0f, 1f)

    @JsName("defaultCtor")
    constructor()

    @JsName("argsCtor")
    constructor(location: MrVector3f, rotation: MrQuaternion, scale: MrVector3f) {
        _location.copy(location)
        _rotation.copy(rotation)
        _scale.copy(scale)
    }

    var location: MrVector3f
        get() = _location
        set(value) {
            _location.copy(value)
        }

    var rotation: MrQuaternion
        get() = _rotation
        set(value) {
            _rotation.copy(value)
        }

    var scale: MrVector3f
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: MrMatrix4f
        get() {
            MrMatrix4f.identity(_matrix)
            MrMatrix4f.translate(_matrix, _location)
            MrMatrix4f.rotate(_matrix, _rotation)
            MrMatrix4f.scale(_matrix, _scale)
            return _matrix
        }

    fun setLocation(x: Float, y: Float, z: Float) {
        _location.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        MrQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
    }

    @JsName("rotateByVector")
    fun setRotation(angle: Float, axis: MrVector3f) {
        MrQuaternion.fromAngleAxis(_rotation, angle, axis)
    }

    @JsName("setLookAtUp")
    fun setLookAt(look: MrVector3f, up: MrVector3f) {
        val m = MrMatrix4f()
        MrMatrix4f.lookAt(m, _location, look, up)
        MrQuaternion.fromMatrix4(_rotation, m)
    }

    fun setLookAt(look: MrVector3f) {
        setLookAt(look, _up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        _location.x += x
        _location.y += y
        _location.z += z
    }

    @JsName("translateByVector")
    fun translate(v: MrVector3f) {
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
    fun scale(v: MrVector3f) {
        _scale.setValues(v.x, v.y, v.z)
    }

    @JsName("rotateByQuat")
    fun rotate(q: MrQuaternion) {
        MrQuaternion.mult(_rotation, _rotation, q)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val q = MrQuaternion()
        MrQuaternion.fromAngleAxis(q, angle, x, y, z)
        rotate(q)
    }

    @JsName("rotateByAngleAxisVector")
    fun rotate(angle: Float, axis: MrVector3f) {
        rotate(angle, axis.x, axis.y, axis.z)
    }

    @JsName("rotateAroundPointAxesThrough")
    fun rotateAround(angle: Float, point: MrVector3f, axis: MrVector3f, through: MrVector3f) {
        val q = MrQuaternion()
        MrQuaternion.fromAngleAxis(q, angle, axis)
        MrQuaternion.mult(_rotation, _rotation, q)

        val v = MrVector3f()
        //V-P
        MrVector3f.subtract(v, through, point)
        //R(V-P)
        MrVector3f.rotateVector(v, _rotation, v)
        //Loc = P + R(V-P)
        MrVector3f.add(_location, point, v)
    }

    fun rotateAround(angle: Float, point: MrVector3f, axis: MrVector3f) {
        rotateAround(angle, point, axis, _location)
    }

    private fun transformLocalAxis() {
        MrMatrix4f.mult(_forward, _matrix, _forward)
        MrMatrix4f.mult(_up, _matrix, _up)
        MrMatrix4f.mult(_right, _matrix, _right)
    }

}

@Serializable
data class MrTransformSurrogate(val location: MrVector3f, val rotation: MrQuaternion, val scale: MrVector3f)

class MrTransformSerializer : KSerializer<MrTransform> {
    override val descriptor: SerialDescriptor = MrTransformSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: MrTransform) {

    }

    override fun deserialize(decoder: Decoder): MrTransform {
        val surrogate = decoder.decodeSerializableValue(MrTransformSurrogate.serializer())
        return MrTransform(surrogate.location, surrogate.rotation, surrogate.scale)
    }
}