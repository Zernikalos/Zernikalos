package zernikalos.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toByteArray

/**
 * Euler angles in **radians** using roll (X), pitch (Y), and yaw (Z) component order
 * for conversions via [ZQuaternion.fromEuler].
 */
class ZEuler(): ZAlgebraObject {

    private val _values: FloatArray = FloatArray(3)

    override val dataType: ZDataType = ZTypes.EULER

    override val floatArray: FloatArray
        get() = _values

    override val byteArray: ByteArray
        get() = floatArray.toByteArray()

    override val size: Int = 3

    override val count: Int = 1

    override val byteSize: Int
        get() = dataType.byteSize

    /** All angle components in radians. */
    constructor(roll: Float, pitch: Float, yaw: Float) : this() {
        _values[0] = roll
        _values[1] = pitch
        _values[2] = yaw
    }

    constructor(other: ZEuler) : this() {
        ZEuler.copy(this, other)
    }

    /** Rotation about X in radians. */
    var roll: Float
        get() = _values[0]
        set(value) {
            _values[0] = value
        }

    /** Rotation about Y in radians. */
    var pitch: Float
        get() = _values[1]
        set(value) {
            _values[1] = value
        }

    /** Rotation about Z in radians. */
    var yaw: Float
        get() = _values[2]
        set(value) {
            _values[2] = value
        }

    fun toQuaternion(): ZQuaternion {
        return ZQuaternion.fromEuler(this)
    }

    override fun toString(): String {
        return "ZEuler(roll=${_values[0]}, pitch=${_values[1]}, yaw=${_values[2]})"
    }

    companion object Op {

        val Zero: ZEuler
            get() = ZEuler(0f, 0f, 0f)

        fun copy(result: ZEuler, e: ZEuler) {
            result.yaw = e.yaw
            result.pitch = e.pitch
            result.roll = e.roll
        }

        /**
         * Fills [result] with Euler angles (roll, pitch, yaw) in radians extracted from [q].
         */
        fun fromQuaternion(result: ZEuler, q: ZQuaternion) {
            val halfPi = (PI / 2.0).toFloat()
            val w = q.w
            val x = q.x
            val y = q.y
            val z = q.z

            val sinr_cosp = 2f * (w * x + y * z)
            val cosr_cosp = 1f - 2f * (y * y + z * z)
            result.roll = atan2(sinr_cosp, cosr_cosp)

            val sinp = 2f * (w * y - z * x)
            result.pitch = when {
                abs(sinp) >= 1f ->
                    if (sinp > 0) halfPi else -halfPi
                else -> asin(sinp)
            }

            val siny_cosp = 2f * (w * z + x * y)
            val cosy_cosp = 1f - 2f * (y * y + z * z)
            result.yaw = atan2(siny_cosp, cosy_cosp)
        }

        /** Euler angles in radians. */
        fun fromQuaternion(q: ZQuaternion): ZEuler {
            val result = ZEuler()
            fromQuaternion(result, q)
            return result
        }
    }
}
