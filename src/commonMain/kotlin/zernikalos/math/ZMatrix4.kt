/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.Serializable
import zernikalos.ZDataType
import zernikalos.ZTypes
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.tan

/**
 * A 4x4 matrix implementation.
 */
@JsExport
@Serializable
class ZMatrix4(): ZAlgebraObject {
    override val dataType: ZDataType
        get() = ZTypes.MAT4F

    override val floatArray: FloatArray = FloatArray(16)

    override val count: Int
        get() = 1

    override val size: Int
        get() = floatArray.size

    init {
        identity()
    }

    /**
     * Initializes a ZMatrix4 object with the provided array of float values.
     *
     * @throws Error if the provided array does not contain exactly 16 elements.
     * @param values An array of float values to initialize the matrix with. The array should contain exactly 16 elements.
     */
    @JsName("initWithValues")
    constructor(values: Array<Float>): this() {
        require(values.size == 16) { "Invalid Matrix 4x4 dimension" }
        copyFromValues(values)
    }

    /**
     * Constructs a 4x4 matrix from a given array of float values.
     *
     * @param values the float array containing 16 values to initialize the matrix
     * @throws Error if the provided array does not contain exactly 16 values
     */
    @JsName("initWithFloatArray")
    constructor(values: FloatArray): this() {
        require(values.size == 16) { "Invalid Matrix 4x4 dimension" }
        copyFromValues(values)
    }

    /**
     * Retrieves the value at the specified index. 0-15 index element.
     *
     * @param i The index of the element to retrieve.
     * @return The float value at the specified index.
     */
    operator fun get(i: Int): Float {
        return floatArray[i]
    }

    /**
     * Sets the value at the specified index. 0-15 index element.
     *
     * @param i The index of the element to retrieve.
     * @return The float value at the specified index.
     */
    operator fun set(i: Int, v: Float) {
        floatArray[i] = v
    }

    /**
     * Retrieves the value at the specified 2D matrix coordinates.
     *
     * @param i The row index (0..3) of the matrix.
     * @param j The column index (0..3) of the matrix.
     * @return The float value at the specified 2D coordinates.
     */
    @JsName("getIJ")
    operator fun get(i: Int, j: Int): Float {
        val k = 4 * j + i
        return floatArray[k]
    }

    /**
     * Sets the value at the specified 2D matrix coordinates.
     *
     * @param i The row index (0..3) of the matrix.
     * @param j The column index (0..3) of the matrix.
     * @return The float value at the specified 2D coordinates.
     */
    @JsName("setIJ")
    operator fun set(i: Int, j: Int, v: Float) {
        val k = 4 * j + i
        floatArray[k] = v
    }

    /**
     * Performs the Matrix addition operation.
     *
     * @return The result of the matrix addition.
     */
    operator fun plus(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        add(result, this, m)
        return result
    }

    /**
     * Performs the Matrix subtraction operation.
     *
     * @return The result of the matrix addition.
     */
    operator fun minus(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        subtract(result, this, m)
        return result
    }

    /**
     * Performs the Matrix multiplication operation.
     *
     * @return The result of the matrix addition.
     */
    operator fun times(m: ZMatrix4): ZMatrix4 {
        val result = ZMatrix4()
        mult(result, this, m)
        return result
    }

    /**
     * Sets the current matrix to be the identity matrix.
     */
    fun identity() {
        identity(this)
    }

    /**
     * Transposes the current ZMatrix4 instance in place.
     *
     * The transpose operation flips the matrix over its diagonal,
     * switching the row and column indices of the matrix elements.
     */
    fun transpose() {
        transposeIp(this)
    }

    /**
     * Translates the current matrix by the given vector.
     *
     * @param translation The vector by which to translate.
     */
    @JsName("translateByVector")
    fun translate(translation: ZVector3) {
        translate(this, translation)
    }

    /**
     * Translates the current matrix by the specified x, y, and z values.
     *
     * @param x The translation distance along the x-axis.
     * @param y The translation distance along the y-axis.
     * @param z The translation distance along the z-axis.
     */
    fun translate(x: Float, y: Float, z: Float) {
        translate(this, x, y, z)
    }

    /**
     * Inverts the current ZMatrix4 instance in place.
     *
     * This method updates the current matrix to its inverse, altering the original matrix data.
     * If the matrix is not invertible (i.e., its determinant is zero), no changes to the matrix
     * will be applied and false will be returned.
     */
    fun invert(): Boolean {
        return invert(this, this)
    }

    /**
     * Returns a new ZMatrix4 that is the inverse of the current matrix.
     *
     * @return A new ZMatrix4 representing the inverse of the current matrix.
     */
    fun inverted(): ZMatrix4 {
        val m = ZMatrix4()
        invert(m, this)
        return m
    }

    /**
     * Scales the current matrix by the specified vector.
     *
     * @param s The vector by which to scale the matrix.
     */
    @JsName("scaleByVector")
    fun scale(s: ZVector3) {
        scale(this, s)
    }

    /**
     * Scales the current matrix by the specified vector.
     *
     * @param s The vector by which to scale the matrix.
     */
    fun scale(x: Float, y: Float, z: Float) {
        scale(this, this, x, y, z)
    }

    override fun toString(): String {
        return this.floatArray.contentToString()
    }

    private fun copyFromValues(values: FloatArray) {
        values.forEachIndexed { index, value ->
            this.floatArray[index] = value
        }
    }

    private fun copyFromValues(values: Array<Float>) {
        values.forEachIndexed { index, value ->
            this.floatArray[index] = value
        }
    }

    /**
     * Provides a collection of operations for manipulating 4x4 matrices.
     */
    companion object Op {

        /**
         * Provides a new instance of the identity 4x4 matrix.
         *
         * This matrix is a special kind of matrix in which all the elements on the main diagonal are ones,
         * and all the other elements are zeros.
         *
         * Accessing this property will return a new identity matrix each time.
         */
        val Identity: ZMatrix4
            get() = ZMatrix4()

        /**
         * Sets the given matrix to be the identity matrix.
         *
         * An identity matrix is a square matrix with ones on the main diagonal and zeros elsewhere.
         *
         * @param result The matrix to be set as the identity matrix.
         */
        fun identity(result: ZMatrix4) {
            result.floatArray[0]  = 1.0f
            result.floatArray[1]  = 0.0f
            result.floatArray[2]  = 0.0f
            result.floatArray[3]  = 0.0f

            result.floatArray[4]  = 0.0f
            result.floatArray[5]  = 1.0f
            result.floatArray[6]  = 0.0f
            result.floatArray[7]  = 0.0f

            result.floatArray[8]  = 0.0f
            result.floatArray[9]  = 0.0f
            result.floatArray[10] = 1.0f
            result.floatArray[11] = 0.0f

            result.floatArray[12] = 0.0f
            result.floatArray[13] = 0.0f
            result.floatArray[14] = 0.0f
            result.floatArray[15] = 1.0f
        }

        /**
         * Adds two 4x4 matrices and stores the result in the provided result matrix.
         *
         * @param result The matrix to store the result of the addition.
         * @param m1 The first matrix to add.
         * @param m2 The second matrix to add.
         */
        fun add(result: ZMatrix4, m1: ZMatrix4, m2: ZMatrix4) {
            for (i in 0..15) {
                result.floatArray[i] = m1.floatArray[i] + m2.floatArray[i]
            }
        }

        /**
         * Subtracts two 4x4 matrices and stores the result in the provided result matrix.
         *
         * @param result The matrix to store the result of the subtraction.
         * @param m1 The first matrix to subtract from.
         * @param m2 The second matrix to be subtracted.
         */
        fun subtract(result: ZMatrix4, m1: ZMatrix4, m2: ZMatrix4) {
            for (i in 0..15) {
                result.floatArray[i] = m1.floatArray[i] - m2.floatArray[i]
            }
        }

        /**
         * Multiplies two 4x4 matrices, storing the result in the result matrix.
         *
         * @param result The ZMatrix4 instance where the result of the multiplication will be stored.
         * @param lm The left matrix (ZMatrix4) to be multiplied.
         * @param rm The right matrix (ZMatrix4) to be multiplied.
         */
        fun mult(result: ZMatrix4, lm: ZMatrix4, rm: ZMatrix4) {
            for (i in 0 .. 3) {
                val rm_i0 = rm.floatArray[4 * i]
                var ri0 = lm[0] * rm_i0
                var ri1 = lm[1] * rm_i0
                var ri2 = lm[2] * rm_i0
                var ri3 = lm[3] * rm_i0

                for (j in 1..3) {
                    val rm_ij = rm.floatArray[j + 4 * i]
                    ri0 += lm[4 * j] * rm_ij
                    ri1 += lm[1 + 4 * j] * rm_ij
                    ri2 += lm[2 + 4 * j] * rm_ij
                    ri3 += lm[3 + 4 * j] * rm_ij
                }

                result[4 * i] = ri0
                result[1 + 4 * i] = ri1
                result[2 + 4 * i] = ri2
                result[3 + 4 * i] = ri3
            }
        }

        /**
         * Transposes the given 4x4 matrix `m` and stores the result in `result`.
         *
         * The transpose operation flips the matrix over its diagonal,
         * switching the row and column indices of the matrix elements.
         *
         * @param result The matrix to store the transposed result.
         * @param m The input matrix to transpose.
         */
        fun transpose(result: ZMatrix4, m: ZMatrix4) {
            for (i in 0..3) {
                val k = i * 4
                result.floatArray[i] = m.floatArray[k]
                result.floatArray[i + 4] = m.floatArray[k + 1]
                result.floatArray[i + 8] = m.floatArray[k + 2]
                result.floatArray[i + 12] = m.floatArray[k + 3]
            }
        }

        // TODO: This should be an instance method
        fun transposeIp(result: ZMatrix4) {
            // https://en.wikipedia.org/wiki/In-place_matrix_transposition
            for (i in 0.. 3) {
                for (j in i+1..3) {
                    // (i, j)
                    val k1 = 4 * j + i
                    // (j, i)
                    val k2 = 4 * i + j
                    val tmp = result.floatArray[k2]
                    result.floatArray[k2] = result.floatArray[k1]
                    result.floatArray[k1] = tmp
                }
            }
        }

        /**
         * Sets the translation component of a matrix using the specified translation vector.
         *
         * @param result The matrix in which the translation component will be set.
         * @param translation The vector defining the translation to apply.
         */
        @JsName("setTranslationByVector")
        fun setTranslation(result: ZMatrix4, translation: ZVector3) {
            setInnerTranslation(result, translation.x, translation.y, translation.z)
        }

        /**
         * Sets the translation components of the provided 4x4 matrix to the specified x, y, and z values.
         *
         * @param result The ZMatrix4 instance where the translation will be set.
         * @param x The translation distance along the x-axis.
         * @param y The translation distance along the y-axis.
         * @param z The translation distance along the z-axis.
         */
        fun setTranslation(result: ZMatrix4, x: Float, y: Float, z: Float) {
            setInnerTranslation(result, x, y, z)
        }

        @JsName("translateByVectorCopy")
        fun translate(result: ZMatrix4, m: ZMatrix4, translation: ZVector3) {
            for (i in 0..m.size) {
                result.floatArray[i] = m.floatArray[i]
            }
            innerTranslate(result, translation.x, translation.y, translation.z)
        }

        @JsName("translateByVector")
        fun translate(result: ZMatrix4, translation: ZVector3) {
            innerTranslate(result, translation.x, translation.y, translation.z)
        }

        fun translate(result: ZMatrix4, x: Float, y: Float, z: Float) {
            innerTranslate(result, x, y, z)
        }

        private fun innerTranslate(result: ZMatrix4, x: Float, y: Float, z: Float) {
            result.floatArray[12] += x
            result.floatArray[13] += y
            result.floatArray[14] += z
        }

        private fun setInnerTranslation(result: ZMatrix4, x: Float, y: Float, z: Float) {
            result.floatArray[12] = x
            result.floatArray[13] = y
            result.floatArray[14] = z
        }

        /**
         * Rotates the given ZMatrix4 result by the specified ZQuaternion.
         *
         * @param result The ZMatrix4 to be rotated. This matrix will be modified to contain the result of the rotation.
         * @param q The ZQuaternion representing the rotation to be applied.
         */
        fun rotate(result: ZMatrix4, q: ZQuaternion) {
            val rotQuat = ZMatrix4()
            fromQuaternion(rotQuat, q)
            val aux = ZMatrix4(result.floatArray)
            mult(result, aux, rotQuat)
        }

        /**
         * Scales a given 4x4 matrix by specified x, y, and z scaling factors.
         *
         * @param result The matrix to store the result of the scaling operation.
         * @param m The original matrix to be scaled.
         * @param x The scaling factor along the x-axis.
         * @param y The scaling factor along the y-axis.
         * @param z The scaling factor along the z-axis.
         */
        @JsName("scaleByValues")
        fun scale(result: ZMatrix4, m: ZMatrix4, x: Float, y: Float, z: Float) {
            result[0, 0] = m[0, 0] * x
            result[0, 1] = m[0, 1] * y
            result[0, 2] = m[0, 2] * z
            result[0, 3] = m[0, 3]

            result[1, 0] = m[1, 0] * x
            result[1, 1] = m[1, 1] * y
            result[1, 2] = m[1, 2] * z
            result[1, 3] = m[1, 3]

            result[2, 0] = m[2, 0] * x
            result[2, 1] = m[2, 1] * y
            result[2, 2] = m[2, 2] * z
            result[2, 3] = m[2, 3]

            result[3, 0] = m[3, 0] * x
            result[3, 1] = m[3, 1] * y
            result[3, 2] = m[3, 2] * z
            result[3, 3] = m[3, 3]
        }

        @JsName("scaleByVectorCopy")
        fun scale(result: ZMatrix4, m: ZMatrix4, s: ZVector3) {
            scale(result, m, s.x, s.y, s.z)
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
            val src0  = m.floatArray[0]
            val src4  = m.floatArray[1]
            val src8  = m.floatArray[2]
            val src12 = m.floatArray[3]
            val src1  = m.floatArray[4]
            val src5  = m.floatArray[5]
            val src9  = m.floatArray[6]
            val src13 = m.floatArray[7]
            val src2  = m.floatArray[8]
            val src6  = m.floatArray[9]
            val src10 = m.floatArray[10]
            val src14 = m.floatArray[11]
            val src3  = m.floatArray[12]
            val src7  = m.floatArray[13]
            val src11 = m.floatArray[14]
            val src15 = m.floatArray[15]

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
            result[0] = dst0  * invDet
            result[1] = dst1  * invDet
            result[2] = dst2  * invDet
            result[3] = dst3  * invDet

            result[4] = dst4  * invDet
            result[5] = dst5  * invDet
            result[6] = dst6  * invDet
            result[7] = dst7  * invDet

            result[8] = dst8  * invDet
            result[9] = dst9  * invDet
            result[10] = dst10 * invDet
            result[11] = dst11 * invDet

            result[12] = dst12 * invDet
            result[13] = dst13 * invDet
            result[14] = dst14 * invDet
            result[15] = dst15 * invDet

            return true
        }

        /**
         * Sets up a view transformation matrix to represent a camera looking at a specified target.
         *
         * @param result The matrix to store the result of the lookAt operation.
         * @param eye The position of the camera.
         * @param center The point the camera is looking at.
         * @param up The up direction from the camera's point of view.
         */
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

            result[0] = s.x
            result[1] = u.x
            result[2] = -f.x
            result[3] = 0.0f

            result[4] = s.y
            result[5] = u.y
            result[6] = -f.y
            result[7] = 0.0f

            result[8] = s.z
            result[9] = u.z
            result[10] = -f.z
            result[11] = 0.0f

            result[12] = 0.0f
            result[13] = 0.0f
            result[14] = 0.0f
            result[15] = 1.0f

            val negEye = ZVector3()
            ZVector3.multScalar(negEye, -1.0f, eye)
            translate(result, negEye)
        }

        /**
         * Converts a quaternion into a 4x4 matrix representing the same rotation.
         *
         * @param result The 4x4 matrix to be populated with the rotation matrix.
         * @param q The quaternion to be converted into a rotation matrix.
         */
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

            result[0] = f / aspect
            result[1] = 0f
            result[2] = 0f
            result[3] = 0f

            result[4] = 0f
            result[5] = f
            result[6] = 0f
            result[7] = 0f

            result[8] = 0f
            result[9] = 0f
            result[10] = (far + near) * rangeReciprocal
            result[11] = -1f

            result[12] = 0f
            result[13] = 0f
            result[14] = 2 * far * near * rangeReciprocal
            result[15] = 0f
        }

    }

}