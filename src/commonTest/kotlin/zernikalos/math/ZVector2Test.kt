package zernikalos.math

import kotlin.test.Test
import kotlin.test.assertEquals

class ZVector2Test {

    @Test
    fun `default constructor produces zero vector`() {
        val v = ZVector2()
        assertVectorEquals(ZVector2(0f, 0f), v)
    }

    @Test
    fun `constructor with values sets components correctly`() {
        val v = ZVector2(3f, 4f)
        assertVectorEquals(ZVector2(3f, 4f), v)
    }

    @Test
    fun `norm2 computes Euclidean length`() {
        val v = ZVector2(3f, 4f)
        assertEquals(5f, v.norm2, epsilon)
    }

    @Test
    fun `plus operator adds vectors component wise`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, -4f)
        val r = v1 + v2
        assertVectorEquals(ZVector2(4f, -2f), r)
    }

    @Test
    fun `minus operator subtracts vectors component wise`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, -4f)
        val r = v1 - v2
        assertVectorEquals(ZVector2(-2f, 6f), r)
    }

    @Test
    fun `dot operator returns scalar product`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, 4f)
        val dot = v1 * v2 // times(ZVector2)
        assertEquals(11f, dot, epsilon)
    }

    @Test
    fun `scalar multiplication scales vector`() {
        val v = ZVector2(2f, 3f)
        val r = v * 2f
        assertVectorEquals(ZVector2(4f, 6f), r)
    }

    @Test
    fun `normalized returns unit length vector`() {
        val v = ZVector2(0f, 5f)
        val n = v.normalized
        assertVectorEquals(ZVector2(0f, 1f), n)
    }

    @Test
    fun `lerp interpolates between vectors`() {
        val a = ZVector2(0f, 0f)
        val b = ZVector2(10f, 10f)
        val r = ZVector2()
        ZVector2.lerp(r, 0.5f, a, b)
        assertVectorEquals(ZVector2(5f, 5f), r)
    }
}
