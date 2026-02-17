package zernikalos.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZVector4Test {

    @Test
    fun testDefaultConstructor() {
        val v = ZVector4()
        assertVectorEquals(ZVector4(0f, 0f, 0f, 0f), v)
    }

    @Test
    fun testValuesConstructor() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        assertVectorEquals(ZVector4(1f, 2f, 3f, 4f), v)
    }

    @Test
    fun testValueConstructor() {
        val v = ZVector4(5f)
        assertVectorEquals(ZVector4(5f, 5f, 5f, 5f), v)
    }

    @Test
    fun testVec3Constructor() {
        val v3 = ZVector3(1f, 2f, 3f)
        val v4 = ZVector4(v3)
        assertVectorEquals(ZVector4(1f, 2f, 3f, 1f), v4)
    }

    @Test
    fun testSetValues() {
        val v = ZVector4()
        v.setValues(1f, 2f, 3f, 4f)
        assertVectorEquals(ZVector4(1f, 2f, 3f, 4f), v)
    }

    @Test
    fun testSetOperator() {
        val v = ZVector4()
        v[0] = 1f
        v[1] = 2f
        v[2] = 3f
        v[3] = 4f
        assertVectorEquals(ZVector4(1f, 2f, 3f, 4f), v)
    }

    @Test
    fun testPlus() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = v1 + v2
        assertVectorEquals(ZVector4(6f, 8f, 10f, 12f), result)
    }

    @Test
    fun testMinus() {
        val v1 = ZVector4(5f, 6f, 7f, 8f)
        val v2 = ZVector4(1f, 2f, 3f, 4f)
        val result = v1 - v2
        assertVectorEquals(ZVector4(4f, 4f, 4f, 4f), result)
    }

    @Test
    fun testTimesVector() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = v1 * v2
        assertEquals(70f, result, epsilon)
    }

    @Test
    fun testTimesScalar() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        val result = v * 2f
        assertVectorEquals(ZVector4(2f, 4f, 6f, 8f), result)
    }

    @Test
    fun testZero() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        v.zero()
        assertVectorEquals(ZVector4(0f, 0f, 0f, 0f), v)
    }

    @Test
    fun testNormalize() {
        val v = ZVector4(3f, 4f, 0f, 0f)
        v.normalize()
        assertTrue(kotlin.math.abs(v.norm2 - 1f) < epsilon)
        assertVectorEquals(ZVector4(0.6f, 0.8f, 0f, 0f), v)
    }

    @Test
    fun testDot() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = ZVector4.dot(v1, v2)
        assertEquals(70f, result, epsilon)
    }

    @Test
    fun testLerp() {
        val v1 = ZVector4(0f, 0f, 0f, 0f)
        val v2 = ZVector4(10f, 10f, 10f, 10f)
        val result = ZVector4()
        ZVector4.lerp(result, 0.5f, v1, v2)
        assertVectorEquals(ZVector4(5f, 5f, 5f, 5f), result)
    }
}
