package zernikalos.math

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toByteArray
import kotlin.math.cos
import kotlin.math.sin

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

    constructor(roll: Float, pitch: Float, yaw: Float) : this() {
        _values[0] = roll
        _values[1] = pitch
        _values[2] = yaw
    }

    constructor(other: ZEuler) : this() {
        ZEuler.copy(this, other)
    }

    var roll: Float
        get() = _values[0]
        set(value) {
            _values[0] = value
        }

    var pitch: Float
        get() = _values[1]
        set(value) {
            _values[1] = value
        }

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

        fun fromQuaternion(result: ZEuler, q: ZQuaternion) {
            // Extract quaternion components
            val w = q.w
            val x = q.x
            val y = q.y
            val z = q.z

            // Roll (x-axis rotation)
            val sinr_cosp = 2f * (w * x + y * z)
            val cosr_cosp = 1f - 2f * (x * x + y * y)
            result.roll = kotlin.math.atan2(sinr_cosp, cosr_cosp)

            // Pitch (y-axis rotation)
            val sinp = 2f * (w * y - z * x)
            // Replace copySign with simple sign check
            result.pitch = when {
                kotlin.math.abs(sinp) >= 1f ->
                    if (sinp > 0) kotlin.math.PI.toFloat() / 2f else -kotlin.math.PI.toFloat() / 2f
                else -> kotlin.math.asin(sinp)
            }

            // Yaw (z-axis rotation)
            val siny_cosp = 2f * (w * z + x * y)
            val cosy_cosp = 1f - 2f * (y * y + z * z)
            result.yaw = kotlin.math.atan2(siny_cosp, cosy_cosp)
        }

        fun fromQuaternion(q: ZQuaternion): ZEuler {
            val result = ZEuler()
            fromQuaternion(result, q)
            return result
        }
    }
}
