package zernikalos.math

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZVector2Test {

    private val delta = 1e-6f

    @Test
    fun `default constructor produces zero vector`() {
        val v = ZVector2()
        assertEquals(0f, v.x, delta)
        assertEquals(0f, v.y, delta)
    }

    @Test
    fun `constructor with values sets components correctly`() {
        val v = ZVector2(3f, 4f)
        assertEquals(3f, v.x, delta)
        assertEquals(4f, v.y, delta)
    }

    @Test
    fun `norm2 computes Euclidean length`() {
        val v = ZVector2(3f, 4f)
        assertEquals(5f, v.norm2, delta)
    }

    @Test
    fun `plus operator adds vectors component wise`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, -4f)
        val r = v1 + v2
        assertEquals(4f, r.x, delta)
        assertEquals(-2f, r.y, delta)
    }

    @Test
    fun `minus operator subtracts vectors component wise`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, -4f)
        val r = v1 - v2
        assertEquals(-2f, r.x, delta)
        assertEquals(6f, r.y, delta)
    }

    @Test
    fun `dot operator returns scalar product`() {
        val v1 = ZVector2(1f, 2f)
        val v2 = ZVector2(3f, 4f)
        val dot = v1 * v2 // times(ZVector2)
        assertEquals(11f, dot, delta)
    }

    @Test
    fun `scalar multiplication scales vector`() {
        val v = ZVector2(2f, 3f)
        val r = v * 2f
        assertEquals(4f, r.x, delta)
        assertEquals(6f, r.y, delta)
    }

    @Test
    fun `normalized returns unit length vector`() {
        val v = ZVector2(0f, 5f)
        val n = v.normalized
        assertTrue(abs(n.x - 0f) < delta)
        assertTrue(abs(n.y - 1f) < delta)
    }

    @Test
    fun `lerp interpolates between vectors`() {
        val a = ZVector2(0f, 0f)
        val b = ZVector2(10f, 10f)
        val r = ZVector2()
        ZVector2.lerp(r, 0.5f, a, b)
        assertEquals(5f, r.x, delta)
        assertEquals(5f, r.y, delta)
    }
}
