package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.tan

@JsExport
@Serializable
class ZMatrix4(): ZAlgebraObject {

    override val values: FloatArray

    override val count: Int
        get() = 1

    override val size: Int
        get() = values.size

    init {
        values = FloatArray(16)
        identity()
    }

    @JsName("initWithValues")
    constructor(values: Array<Float>): this() {
        checkDimension(values)
        copyFromValues(values)
    }

    @JsName("initWithFloatArray")
    constructor(values: FloatArray): this() {
        checkDimension(values)
        copyFromValues(values)
    }

    operator fun get(i: Int): Float {
        return values[i]
    }

    @JsName("getIJ")
    operator fun get(i: Int, j: Int): Float {
        val k = 4 * j + i
        return values[k]
    }

    @JsName("setIJ")
    operator fun set(i: Int, j: Int, v: Float) {
        val k = 4 * j + i
        values[k] = v
    }

    operator fun set(i: Int, v: Float) {
        values[i] = v
    }

    operator fun plus(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        add(result, this, m)
        return result
    }

    operator fun minus(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        subtract(result, this, m)
        return result
    }

    operator fun times(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        mult(result, this, m)
        return result
    }

    fun identity() {
        identity(this)
    }

    fun transpose() {
        transposeIp(this)
    }

    @JsName("translateByVector")
    fun translate(translation: ZVector3) {
        translate(this, translation)
    }

    fun translate(x: Float, y: Float, z: Float) {
        translate(this, x, y, z)
    }

    fun invert() {
        invert(this, this)
    }

    fun scale(s: ZVector3) {
        scale(this, s)
    }

    override fun toString(): String {
        return this.values.contentToString()
    }

    private fun checkDimension(values: Array<Float>) {
        if (values.size != 16) {
            throw Error("Invalid Matrix 4x4 dimension")
        }
    }

    private fun checkDimension(values: FloatArray) {
        if (values.size != 16) {
            throw Error("Invalid Matrix 4x4 dimension")
        }
    }

    private fun copyFromValues(values: FloatArray) {
        values.forEachIndexed { index, value ->
            this.values[index] = value
        }
    }

    private fun copyFromValues(values: Array<Float>) {
        values.forEachIndexed { index, value ->
            this.values[index] = value
        }
    }

    companion object Op {

        val Identity: ZMatrix4
            get() = ZMatrix4()

        fun identity(result: ZMatrix4) {
            result.values[0]  = 1.0f
            result.values[1]  = 0.0f
            result.values[2]  = 0.0f
            result.values[3]  = 0.0f

            result.values[4]  = 0.0f
            result.values[5]  = 1.0f
            result.values[6]  = 0.0f
            result.values[7]  = 0.0f

            result.values[8]  = 0.0f
            result.values[9]  = 0.0f
            result.values[10] = 1.0f
            result.values[11] = 0.0f

            result.values[12] = 0.0f
            result.values[13] = 0.0f
            result.values[14] = 0.0f
            result.values[15] = 1.0f
        }

        fun add(result: ZMatrix4, m1: ZMatrix4, m2: ZMatrix4) {
            for (i in 0..15) {
                result.values[i] = m1.values[i] + m2.values[i]
            }
        }

        fun subtract(result: ZMatrix4, m1: ZMatrix4, m2: ZMatrix4) {
            for (i in 0..15) {
                result.values[i] = m1.values[i] - m2.values[i]
            }
        }

        fun mult(result: ZMatrix4, lm: ZMatrix4, rm: ZMatrix4) {
            for (i in 0 .. 3) {
                val rm_i0 = rm.values[4 * i]
                var ri0 = lm.values[0] * rm_i0
                var ri1 = lm.values[1] * rm_i0
                var ri2 = lm.values[2] * rm_i0
                var ri3 = lm.values[3] * rm_i0

                for (j in 1..3) {
                    val rm_ij = rm.values[j + 4 * i]
                    ri0 += lm.values[4 * j] * rm_ij
                    ri1 += lm.values[1 + 4 * j] * rm_ij
                    ri2 += lm.values[2 + 4 * j] * rm_ij
                    ri3 += lm.values[3 + 4 * j] * rm_ij
                }

                result.values[4 * i] = ri0
                result.values[1 + 4 * i] = ri1
                result.values[2 + 4 * i] = ri2
                result.values[3 + 4 * i] = ri3
            }
        }

        @JsName("multVec4")
        fun mult(result: ZVector4, m: ZMatrix4, v: ZVector4) {
            val x = v.x
            val y = v.y
            val z = v.z
            val w = v.w
            result[0] = m[0 + 4 * 0] * x + m[0 + 4 * 1] * y + m[0 + 4 * 2] * z + m[0 + 4 * 3] * w
            result[1] = m[1 + 4 * 0] * x + m[1 + 4 * 1] * y + m[1 + 4 * 2] * z + m[1 + 4 * 3] * w
            result[2] = m[2 + 4 * 0] * x + m[2 + 4 * 1] * y + m[2 + 4 * 2] * z + m[2 + 4 * 3] * w
            result[3] = m[3 + 4 * 0] * x + m[3 + 4 * 1] * y + m[3 + 4 * 2] * z + m[3 + 4 * 3] * w
        }

        @JsName("multVec3")
        fun mult(result: ZVector3, m: ZMatrix4, v: ZVector3) {
            val x = v.x
            val y = v.y
            val z = v.z
            val w = 1.0f
            result[0] = m[0 + 4 * 0] * x + m[0 + 4 * 1] * y + m[0 + 4 * 2] * z + m[0 + 4 * 3] * w
            result[1] = m[1 + 4 * 0] * x + m[1 + 4 * 1] * y + m[1 + 4 * 2] * z + m[1 + 4 * 3] * w
            result[2] = m[2 + 4 * 0] * x + m[2 + 4 * 1] * y + m[2 + 4 * 2] * z + m[2 + 4 * 3] * w
        }

        fun transpose(result: ZMatrix4, m: ZMatrix4) {
            for (i in 0..3) {
                val k = i * 4
                result.values[i] = m.values[k]
                result.values[i + 4] = m.values[k + 1]
                result.values[i + 8] = m.values[k + 2]
                result.values[i + 12] = m.values[k + 3]
            }
        }

        fun transposeIp(result: ZMatrix4) {
            // https://en.wikipedia.org/wiki/In-place_matrix_transposition
            for (i in 0.. 3) {
                for (j in i+1..3) {
                    // (i, j)
                    val k1 = 4 * j + i
                    // (j, i)
                    val k2 = 4 * i + j
                    val tmp = result.values[k2]
                    result.values[k2] = result.values[k1]
                    result.values[k1] = tmp
                }
            }
        }

        @JsName("translateByVectorCopy")
        fun translate(result: ZMatrix4, m: ZMatrix4, translation: ZVector3) {
            for (i in 0..11) {
                result.values[i] = m.values[i]
            }
            for (i in 0..3) {
                result.values[12 + i] = m.values[i] * translation.x + m.values[4 + i] * translation.y + m.values[8 + i] * translation.z + m.values[12 + i]
            }
        }

        @JsName("translateByVector")
        fun translate(result: ZMatrix4, translation: ZVector3) {
            innerTranslate(result, translation.x, translation.y, translation.z)
        }

        fun translate(result: ZMatrix4, x: Float, y: Float, z: Float) {
            innerTranslate(result, x, y, z)
        }

        private fun innerTranslate(result: ZMatrix4, x: Float, y: Float, z: Float) {
            for (i in 0..3) {
                result.values[12 + i] = result.values[i] * x + result.values[4 + i] * y + result.values[8 + i] * z + result.values[12 + i]
            }
        }

        fun rotate(result: ZMatrix4, q: ZQuaternion) {
            val rotQuat = ZMatrix4()
            fromQuaternion(rotQuat, q)
            val aux = ZMatrix4(result.values)
            mult(result, aux, rotQuat)
        }

        @JsName("scaleByVectorCopy")
        fun scale(result: ZMatrix4, m: ZMatrix4, s: ZVector3) {
            for (i in 0..3) {
                result[i] = m[i] * s.x
                result[4 + i] = m[4 + i] * s.y
                result[8 + i] = m[8 + i] * s.z
                result[12 + i] = m[12 + i]
            }
        }

        fun scale(result: ZMatrix4, s: ZVector3) {
            scale(result, result, s)
        }

        /**
         * Computes the invert matrix from the given one
         *
         * @param result Matrix where the result of the operation will be stored
         * @param m The given matrix
         */
        fun invert(result: ZMatrix4, m: ZMatrix4): Boolean {
            // Extracted from https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/opengl/java/android/opengl/Matrix.java
            val src0  = m.values[0]
            val src4  = m.values[1]
            val src8  = m.values[2]
            val src12 = m.values[3]
            val src1  = m.values[4]
            val src5  = m.values[5]
            val src9  = m.values[6]
            val src13 = m.values[7]
            val src2  = m.values[8]
            val src6  = m.values[9]
            val src10 = m.values[10]
            val src14 = m.values[11]
            val src3  = m.values[12]
            val src7  = m.values[13]
            val src11 = m.values[14]
            val src15 = m.values[15]

            // calculate pairs for first 8 elements (cofactors)
            val atmp0  = src10 * src15
            val atmp1  = src11 * src14
            val atmp2  = src9  * src15
            val atmp3  = src11 * src13
            val atmp4  = src9  * src14
            val atmp5  = src10 * src13
            val atmp6  = src8  * src15
            val atmp7  = src11 * src12
            val atmp8  = src8  * src14
            val atmp9  = src10 * src12
            val atmp10 = src8  * src13
            val atmp11 = src9  * src12

            // calculate first 8 elements (cofactors)
            val dst0  = (atmp0 * src5 + atmp3 * src6 + atmp4  * src7) - (atmp1 * src5 + atmp2 * src6 + atmp5  * src7)
            val dst1  = (atmp1 * src4 + atmp6 * src6 + atmp9  * src7) - (atmp0 * src4 + atmp7 * src6 + atmp8  * src7)
            val dst2  = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7) - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7)
            val dst3  = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6) - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6)
            val dst4  = (atmp1 * src1 + atmp2 * src2 + atmp5  * src3) - (atmp0 * src1 + atmp3 * src2 + atmp4  * src3)
            val dst5  = (atmp0 * src0 + atmp7 * src2 + atmp8  * src3) - (atmp1 * src0 + atmp6 * src2 + atmp9  * src3)
            val dst6  = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3) - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3)
            val dst7  = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2) - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2)

            // calculate pairs for second 8 elements (cofactors)
            val btmp0  = src2 * src7
            val btmp1  = src3 * src6
            val btmp2  = src1 * src7
            val btmp3  = src3 * src5
            val btmp4  = src1 * src6
            val btmp5  = src2 * src5
            val btmp6  = src0 * src7
            val btmp7  = src3 * src4
            val btmp8  = src0 * src6
            val btmp9  = src2 * src4
            val btmp10 = src0 * src5
            val btmp11 = src1 * src4

            // calculate second 8 elements (cofactors)
            val dst8  = (btmp0  * src13 + btmp3  * src14 + btmp4  * src15) - (btmp1  * src13 + btmp2  * src14 + btmp5  * src15)
            val dst9  = (btmp1  * src12 + btmp6  * src14 + btmp9  * src15) - (btmp0  * src12 + btmp7  * src14 + btmp8  * src15)
            val dst10 = (btmp2  * src12 + btmp7  * src13 + btmp10 * src15) - (btmp3  * src12 + btmp6  * src13 + btmp11 * src15)
            val dst11 = (btmp5  * src12 + btmp8  * src13 + btmp11 * src14) - (btmp4  * src12 + btmp9  * src13 + btmp10 * src14)
            val dst12 = (btmp2  * src10 + btmp5  * src11 + btmp1  * src9 ) - (btmp4  * src11 + btmp0  * src9  + btmp3  * src10)
            val dst13 = (btmp8  * src11 + btmp0  * src8  + btmp7  * src10) - (btmp6  * src10 + btmp9  * src11 + btmp1  * src8 )
            val dst14 = (btmp6  * src9  + btmp11 * src11 + btmp3  * src8 ) - (btmp10 * src11 + btmp2  * src8  + btmp7  * src9 )
            val dst15 = (btmp10 * src10 + btmp4  * src8  + btmp9  * src9 ) - (btmp8  * src9  + btmp11 * src10 + btmp5  * src8 )

            // calculate determinant
            val det: Float = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3
            if (abs(det) < 0.000001) {
                return false
            }

            // calculate matrix inverse
            val invDet = 1.0f / det
            result.values[0] = dst0  * invDet
            result.values[1] = dst1  * invDet
            result.values[2] = dst2  * invDet
            result.values[3] = dst3  * invDet

            result.values[4] = dst4  * invDet
            result.values[5] = dst5  * invDet
            result.values[6] = dst6  * invDet
            result.values[7] = dst7  * invDet

            result.values[8] = dst8  * invDet
            result.values[9] = dst9  * invDet
            result.values[10] = dst10 * invDet
            result.values[11] = dst11 * invDet

            result.values[12] = dst12 * invDet
            result.values[13] = dst13 * invDet
            result.values[14] = dst14 * invDet
            result.values[15] = dst15 * invDet

            return true
        }

        fun lookAt(result: ZMatrix4, eye: ZVector3, center: ZVector3, up: ZVector3) {
            val f = ZVector3()

            // See the OpenGL GLUT documentation for gluLookAt for a description
            // of the algorithm. We implement it in a straightforward way:
            ZVector3.subtract(f, center, eye)

            // Normalize f
            f.normalize()

            // compute s = f x up (x means "cross product")
            val s = ZVector3()
            ZVector3.cross(s, f, up)

            // and normalize s
            s.normalize()

            // compute u = s x f
            val u = ZVector3()
            ZVector3.cross(u, s, f)

            result.values[0] = s.x
            result.values[1] = u.x
            result.values[2] = -f.x
            result.values[3] = 0.0f

            result.values[4] = s.y
            result.values[5] = u.y
            result.values[6] = -f.y
            result.values[7] = 0.0f

            result.values[8] = s.z
            result.values[9] = u.z
            result.values[10] = -f.z
            result.values[11] = 0.0f

            result.values[12] = 0.0f
            result.values[13] = 0.0f
            result.values[14] = 0.0f
            result.values[15] = 1.0f

            val negEye = ZVector3()
            ZVector3.multScalar(negEye, -1.0f, eye)
            translate(result, negEye)
        }

        fun fromQuaternion(result: ZMatrix4, q: ZQuaternion) {
            identity(result)
            val x: Float = q.x
            val y: Float = q.y
            val z: Float = q.z
            val w: Float = q.w

            val xx = x * x
            val xy = x * y
            val xz = x * z
            val xw = x * w

            val yy = y * y
            val yz = y * z
            val yw = y * w

            val zz = z * z
            val zw = z * w

            result[0, 0] = 1 - 2 * (yy + zz)
            result[0, 1] = 2 * (xy - zw)
            result[0, 2] = 2 * (xz + yw)

            result[1, 0] = 2 * (xy + zw)
            result[1, 1] = 1 - 2 * (xx + zz)
            result[1, 2] = 2 * (yz - xw)

            result[2, 0] = 2 * (xz - yw)
            result[2, 1] = 2 * (yz + xw)
            result[2, 2] = 1 - 2 * (xx + yy)
        }

        /**
         * Computes the perspective projection matrix
         *
         * @param result Matrix where the result of the operation will be stored
         * @param fov Angle in degrees providing the Field Of View
         * @param aspect Desired aspect ratio
         * @param near Near clip plane
         * @param far Far clip plane
         */
        fun perspective(result: ZMatrix4, fov: Float, aspect: Float, near: Float, far: Float) {
            // Extracted from https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/opengl/java/android/opengl/Matrix.java
            val f: Float = 1f / tan(fov * (PI / 360f)).toFloat()
            val rangeReciprocal = 1.0f / (near - far)

            result.values[0] = f / aspect
            result.values[1] = 0f
            result.values[2] = 0f
            result.values[3] = 0f

            result.values[4] = 0f
            result.values[5] = f
            result.values[6] = 0f
            result.values[7] = 0f

            result.values[8] = 0f
            result.values[9] = 0f
            result.values[10] = (far + near) * rangeReciprocal
            result.values[11] = -1f

            result.values[12] = 0f
            result.values[13] = 0f
            result.values[14] = 2 * far * near * rangeReciprocal
            result.values[15] = 0f
        }

    }

}