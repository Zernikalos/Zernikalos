package zernikalos.math

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals

class ZMatrix4Test {

    @Test
    fun testDefaultConstructorIsIdentity() {
        val m = ZMatrix4()
        val expected = ZMatrix4()
        ZMatrix4.identity(expected)
        assertMatrixEquals(expected, m)
    }

    @Test
    fun testFloatArrayConstructor() {
        val values = FloatArray(16) { it.toFloat() }
        val m = ZMatrix4(values)
        assertContentEquals(values, m.floatArray)
    }

    @Test
    fun testGetAndSet() {
        val m = ZMatrix4()
        m[5] = 42f
        assertEquals(42f, m[5])
    }

    @Test
    fun testGetAndSetIJ() {
        val m = ZMatrix4()
        m[1, 2] = 42f
        assertEquals(42f, m[1, 2])
    }

    @Test
    fun testPlus() {
        val m1 = ZMatrix4(FloatArray(16) { 1f })
        val m2 = ZMatrix4(FloatArray(16) { 2f })
        val result = m1 + m2
        val expected = ZMatrix4(FloatArray(16) { 3f })
        assertMatrixEquals(expected, result)
    }

    @Test
    fun testMinus() {
        val m1 = ZMatrix4(FloatArray(16) { 3f })
        val m2 = ZMatrix4(FloatArray(16) { 2f })
        val result = m1 - m2
        val expected = ZMatrix4(FloatArray(16) { 1f })
        assertMatrixEquals(expected, result)
    }

    @Test
    fun testTimes() {
        val m1 = ZMatrix4()
        m1.translate(1f, 2f, 3f)
        val m2 = ZMatrix4()
        m2.scale(2f, 3f, 4f)

        val result = m1 * m2

        val expected = ZMatrix4()
        ZMatrix4.mult(expected, m1, m2)

        assertMatrixEquals(expected, result)
    }

    @Test
    fun testIdentity() {
        val m = ZMatrix4(FloatArray(16) { 5f })
        m.identity()
        val expected = ZMatrix4()
        ZMatrix4.identity(expected)
        assertMatrixEquals(expected, m)
    }

    @Test
    fun testTranspose() {
        val m = ZMatrix4()
        m[1, 0] = 1f
        m[2, 0] = 2f
        m[0, 1] = 3f

        m.transpose()

        assertEquals(3f, m[1, 0])
        assertEquals(2f, m[0, 2])
        assertEquals(1f, m[0, 1])
    }

    @Test
    fun testInvert() {
        val m = ZMatrix4()
        m.translate(10f, 20f, 30f)
        val inverted = m.inverted()
        val identity = m * inverted
        assertMatrixEquals(ZMatrix4(), identity)
    }

    @Test
    fun testTranslate() {
        val m = ZMatrix4()
        m.translate(1f, 2f, 3f)
        assertEquals(1f, m[12])
        assertEquals(2f, m[13])
        assertEquals(3f, m[14])
    }

    @Test
    fun testScale() {
        val m = ZMatrix4()
        m.scale(2f, 3f, 4f)
        assertEquals(2f, m[0])
        assertEquals(3f, m[5])
        assertEquals(4f, m[10])
    }

    @Test
    fun testLookAt() {
        val eye = ZVector3(0f, 0f, 5f)
        val center = ZVector3(0f, 0f, 0f)
        val up = ZVector3(0f, 1f, 0f)
        val viewMatrix = ZMatrix4()
        ZMatrix4.lookAt(viewMatrix, eye, center, up)

        val expected = ZMatrix4(floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, -5f, 1f
        ))

        assertMatrixEquals(expected, viewMatrix)
    }

    @Test
    @Ignore
    fun testLookAtDifferentPositon() {
        var eye = ZVector3(10f, 10f, 10f)
        var center = ZVector3(0f, 0f, 0f)
        var up = ZVector3(0f, 1f, 0f)
        var viewMatrix = ZMatrix4()
        ZMatrix4.lookAt(viewMatrix, eye, center, up)

        var expected = ZMatrix4(floatArrayOf(
            -0.70710677f, -0.40824828f, 0.57735026f, 0f,
            0f, 0.81649655f, 0.57735026f, 0f,
            -0.70710677f, 0.40824828f, -0.57735026f, 0f,
            0f, 0f, -17.320508f, 1f
        ))

        assertMatrixEquals(expected, viewMatrix, 0.0001f)
    }

    @Test
    fun testLookAtDifferentUpVector() {
        var eye = ZVector3(0f, 0f, 5f)
        var center = ZVector3(0f, 0f, 0f)
        var up = ZVector3(0f, -1f, 0f)
        var viewMatrix = ZMatrix4()
        ZMatrix4.lookAt(viewMatrix, eye, center, up)

        var expected = ZMatrix4(floatArrayOf(
            -1f, 0f, 0f, 0f,
            0f, -1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, -5f, 1f
        ))

        assertMatrixEquals(expected, viewMatrix)
    }

    @Test
    @Ignore
    fun testLookAtNotOrigin() {
        var eye = ZVector3(5f, 5f, 5f)
        var center = ZVector3(1f, 1f, 1f)
        var up = ZVector3.Up
        var viewMatrix = ZMatrix4()
        ZMatrix4.lookAt(viewMatrix, eye, center, up)

        var expected = ZMatrix4(floatArrayOf(
            -0.70710677f, -0.40824828f, 0.57735026f, 0f,
            0f, 0.81649655f, 0.57735026f, 0f,
            -0.70710677f, 0.40824828f, -0.57735026f, 0f,
            0f, 0f, -6.928203f, 1f
        ))

        assertMatrixEquals(expected, viewMatrix, 0.0001f)
    }

    @Test
    fun testPerspective() {
        val m = ZMatrix4()
        ZMatrix4.perspective(m, 90f, 1f, 0.1f, 100f)
        assertEquals(1f, m[0], epsilon)
        assertEquals(1f, m[5], epsilon)
        assertTrue(kotlin.math.abs(m[10] - (-1.002)) < 0.001f)
        assertEquals(-1f, m[11], epsilon)
        assertTrue(kotlin.math.abs(m[14] - (-0.200)) < 0.001f)
    }

    @Test
    fun testFromQuaternion() {
        val q = ZQuaternion()
        q.fromAngleAxis(90f, 0f, 1f, 0f)
        val m = ZMatrix4.fromQuaternion(q)

        val v = ZVector4(1f, 0f, 0f, 1f)
        val rotatedV = ZVector4()
        ZVector4.multMatrix(rotatedV, m, v)

        assertEquals(0f, rotatedV.x, epsilon)
        assertEquals(0f, rotatedV.y, epsilon)
        assertEquals(-1f, rotatedV.z, epsilon)
    }

    // region Companion Object Tests

    @Test
    fun testCompanionIdentity() {
        val m = ZMatrix4(FloatArray(16) { 5f })
        ZMatrix4.identity(m)
        val expected = ZMatrix4() // Default constructor is identity
        assertMatrixEquals(expected, m)
    }

    @Test
    fun testCompanionAdd() {
        val m1 = ZMatrix4(FloatArray(16) { 1f })
        val m2 = ZMatrix4(FloatArray(16) { 2f })
        val result = ZMatrix4()
        ZMatrix4.add(result, m1, m2)
        val expected = ZMatrix4(FloatArray(16) { 3f })
        assertMatrixEquals(expected, result)
    }

    @Test
    fun testCompanionSubtract() {
        val m1 = ZMatrix4(FloatArray(16) { 3f })
        val m2 = ZMatrix4(FloatArray(16) { 2f })
        val result = ZMatrix4()
        ZMatrix4.subtract(result, m1, m2)
        val expected = ZMatrix4(FloatArray(16) { 1f })
        assertMatrixEquals(expected, result)
    }

    @Test
    fun testCompanionMult() {
        val m1 = ZMatrix4()
        m1.translate(1f, 2f, 3f)
        val m2 = ZMatrix4()
        m2.scale(2f, 3f, 4f)

        val result = ZMatrix4()
        ZMatrix4.mult(result, m1, m2)

        val expected = m1 * m2 // Use operator for expected value
        assertMatrixEquals(expected, result)
    }

    @Test
    fun testCompanionTranspose() {
        val m = ZMatrix4()
        m[1, 0] = 1f
        m[2, 0] = 2f
        m[0, 1] = 3f
        val originalM = ZMatrix4(m.floatArray.copyOf())

        val result = ZMatrix4()
        ZMatrix4.transpose(result, m)

        val expected = ZMatrix4(m.floatArray.copyOf())
        expected.transpose()
        assertMatrixEquals(expected, result)

        // Also check original matrix is unchanged
        assertMatrixEquals(originalM, m, "Original matrix should not be modified by companion transpose")
    }

    @Test
    fun testCompanionInvert() {
        val m = ZMatrix4()
        m.translate(10f, 20f, 30f)

        val result = ZMatrix4()
        val success = ZMatrix4.invert(result, m)
        assertTrue(success, "Matrix should be invertible")

        val identity = m * result
        assertMatrixEquals(ZMatrix4(), identity)
    }

    @Test
    fun testCompanionTranslateInPlace() {
        val m = ZMatrix4() // Starts as identity
        ZMatrix4.translate(m, 1f, 2f, 3f)

        val expected = ZMatrix4()
        expected.translate(1f, 2f, 3f)
        assertMatrixEquals(expected, m)
    }

    @Test
    fun testCompanionScaleInPlace() {
        val m = ZMatrix4() // Starts as identity
        ZMatrix4.scale(m, ZVector3(2f, 3f, 4f))

        val expected = ZMatrix4()
        expected.scale(2f, 3f, 4f)
        assertMatrixEquals(expected, m)
    }

    // endregion
}
