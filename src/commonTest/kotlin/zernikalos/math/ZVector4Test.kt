package zernikalos.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ZVector4Test {

    private val epsilon = 1e-6f

    @Test
    fun testDefaultConstructor() {
        val v = ZVector4()
        assertEquals(0f, v.x)
        assertEquals(0f, v.y)
        assertEquals(0f, v.z)
        assertEquals(0f, v.w)
    }

    @Test
    fun testValuesConstructor() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        assertEquals(1f, v.x)
        assertEquals(2f, v.y)
        assertEquals(3f, v.z)
        assertEquals(4f, v.w)
    }

    @Test
    fun testValueConstructor() {
        val v = ZVector4(5f)
        assertEquals(5f, v.x)
        assertEquals(5f, v.y)
        assertEquals(5f, v.z)
        assertEquals(5f, v.w)
    }

    @Test
    fun testVec3Constructor() {
        val v3 = ZVector3(1f, 2f, 3f)
        val v4 = ZVector4(v3)
        assertEquals(1f, v4.x)
        assertEquals(2f, v4.y)
        assertEquals(3f, v4.z)
        assertEquals(1f, v4.w)
    }

    @Test
    fun testSetValues() {
        val v = ZVector4()
        v.setValues(1f, 2f, 3f, 4f)
        assertEquals(1f, v.x)
        assertEquals(2f, v.y)
        assertEquals(3f, v.z)
        assertEquals(4f, v.w)
    }

    @Test
    fun testSetOperator() {
        val v = ZVector4()
        v[0] = 1f
        v[1] = 2f
        v[2] = 3f
        v[3] = 4f
        assertEquals(1f, v.x)
        assertEquals(2f, v.y)
        assertEquals(3f, v.z)
        assertEquals(4f, v.w)
    }

    @Test
    fun testPlus() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = v1 + v2
        assertEquals(6f, result.x)
        assertEquals(8f, result.y)
        assertEquals(10f, result.z)
        assertEquals(12f, result.w)
    }

    @Test
    fun testMinus() {
        val v1 = ZVector4(5f, 6f, 7f, 8f)
        val v2 = ZVector4(1f, 2f, 3f, 4f)
        val result = v1 - v2
        assertEquals(4f, result.x)
        assertEquals(4f, result.y)
        assertEquals(4f, result.z)
        assertEquals(4f, result.w)
    }

    @Test
    fun testTimesVector() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = v1 * v2
        assertEquals(70f, result)
    }

    @Test
    fun testTimesScalar() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        val result = v * 2f
        assertEquals(2f, result.x)
        assertEquals(4f, result.y)
        assertEquals(6f, result.z)
        assertEquals(8f, result.w)
    }

    @Test
    fun testZero() {
        val v = ZVector4(1f, 2f, 3f, 4f)
        v.zero()
        assertEquals(0f, v.x)
        assertEquals(0f, v.y)
        assertEquals(0f, v.z)
        assertEquals(0f, v.w)
    }

    @Test
    fun testNormalize() {
        val v = ZVector4(3f, 4f, 0f, 0f)
        v.normalize()
        assertTrue(kotlin.math.abs(v.norm2 - 1f) < epsilon)
        assertEquals(0.6f, v.x, epsilon)
        assertEquals(0.8f, v.y, epsilon)
        assertEquals(0f, v.z, epsilon)
        assertEquals(0f, v.w, epsilon)
    }

    @Test
    fun testDot() {
        val v1 = ZVector4(1f, 2f, 3f, 4f)
        val v2 = ZVector4(5f, 6f, 7f, 8f)
        val result = ZVector4.dot(v1, v2)
        assertEquals(70f, result)
    }

    @Test
    fun testLerp() {
        val v1 = ZVector4(0f, 0f, 0f, 0f)
        val v2 = ZVector4(10f, 10f, 10f, 10f)
        val result = ZVector4()
        ZVector4.lerp(result, 0.5f, v1, v2)
        assertEquals(5f, result.x)
        assertEquals(5f, result.y)
        assertEquals(5f, result.z)
        assertEquals(5f, result.w)
    }
}
