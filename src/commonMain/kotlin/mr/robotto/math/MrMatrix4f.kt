package mr.robotto.math

import kotlin.math.abs

class MrMatrix4f {
    val values: FloatArray

    constructor() {
        values = FloatArray(16)
        identity()
    }

    constructor(values: Array<Float>) {
        checkDimension(values)
        this.values = values.toFloatArray()
    }

    constructor(values: FloatArray) {
        checkDimension(values)
        this.values = values.copyOf()
    }

    operator fun get(i: Int, j: Int): Float {
        val k = 4 * j + i
        return values[k]
    }

    operator fun set(i: Int, j: Int, v: Float) {
        val k = 4 * j + i
        values[k] = v
    }

    operator fun plus(m: MrMatrix4f): MrMatrix4f {
        val result = MrMatrix4f()
        MrMatrix4f.add(result, this, m)
        return result
    }

    operator fun minus(m: MrMatrix4f): MrMatrix4f {
        val result = MrMatrix4f()
        MrMatrix4f.subtract(result, this, m)
        return result
    }

    operator fun times(m: MrMatrix4f): MrMatrix4f {
        val result = MrMatrix4f()
        MrMatrix4f.mult(result, this, m)
        return result
    }

    fun identity() {
        Companion.identity(this)
    }

    fun transpose() {
        MrMatrix4f.transpose(this, this)
    }

    fun translate(translation: MrVector3f) {
        translateIp(this, translation)
    }

    fun invert() {
        Companion.invert(this, this)
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

    companion object {

        fun identity(result: MrMatrix4f) {
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

        fun add(result: MrMatrix4f, m1: MrMatrix4f, m2: MrMatrix4f) {
            for (i in 0..15) {
                result.values[i] = m1.values[i] + m2.values[i]
            }
        }

        fun subtract(result: MrMatrix4f, m1: MrMatrix4f, m2: MrMatrix4f) {
            for (i in 0..15) {
                result.values[i] = m1.values[i] - m2.values[i]
            }
        }

        fun mult(result: MrMatrix4f, lm: MrMatrix4f, rm: MrMatrix4f) {
            for (i in 0 .. 3) {
                val rm_i0 = rm.values[4 * i]
                var ri0 = lm.values[0] * rm_i0;
                var ri1 = lm.values[1] * rm_i0;
                var ri2 = lm.values[2] * rm_i0;
                var ri3 = lm.values[3] * rm_i0;

                for (j in 1..3) {
                    val rm_ij = rm.values[j + 4 * i];
                    ri0 += lm.values[4 * j] * rm_ij;
                    ri1 += lm.values[1 + 4 * j] * rm_ij;
                    ri2 += lm.values[2 + 4 * j] * rm_ij;
                    ri3 += lm.values[3 + 4 * j] * rm_ij;
                }

                result.values[4 * i] = ri0;
                result.values[1 + 4 * i] = ri1;
                result.values[2 + 4 * i] = ri2;
                result.values[3 + 4 * i] = ri3;
            }
        }

        fun transpose(result: MrMatrix4f, m: MrMatrix4f) {
            for (i in 0..3) {
                val k = i * 4
                result.values[i] = m.values[k];
                result.values[i + 4] = m.values[k + 1];
                result.values[i + 8] = m.values[k + 2];
                result.values[i + 12] = m.values[k + 3];
            }
        }

        fun translate(result: MrMatrix4f, m: MrMatrix4f, translation: MrVector3f) {
            for (i in 0..11) {
                result.values[i] = m.values[i]
            }
            for (i in 0..3) {
                result.values[12 + i] = m.values[i] * translation.x + m.values[4 + i] * translation.y + m.values[8 + i] * translation.z + m.values[12 + i]
            }
        }

        fun translateIp(result: MrMatrix4f, translation: MrVector3f) {
            for (i in 0..3) {
                result.values[12 + i] = result.values[i] * translation.x + result.values[4 + i] * translation.y + result.values[8 + i] * translation.z + result.values[12 + i]
            }
        }

        fun invert(result: MrMatrix4f, m: MrMatrix4f): Boolean {
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

            return true;
        }

        fun lookAt(result: MrMatrix4f, eye: MrVector3f, center: MrVector3f, up: MrVector3f) {
            val f = MrVector3f()

            // See the OpenGL GLUT documentation for gluLookAt for a description
            // of the algorithm. We implement it in a straightforward way:
            MrVector3f.subtract(f, center, eye)

            // Normalize f
            f.normalize()

            // compute s = f x up (x means "cross product")
            val s = MrVector3f()
            MrVector3f.cross(s, f, up)

            // and normalize s
            s.normalize()

            // compute u = s x f
            val u = MrVector3f()
            MrVector3f.cross(u, s, f)

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

            val negEye = MrVector3f()
            MrVector3f.multScalar(negEye,-1.0f, eye)
            MrMatrix4f.translateIp(result, negEye)
        }

    }

}