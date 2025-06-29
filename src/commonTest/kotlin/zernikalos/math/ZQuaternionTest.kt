package zernikalos.math

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZQuaternionTest {

    private val epsilon = 1e-6f

    private fun assertQuaternionEquals(expected: ZQuaternion, actual: ZQuaternion, message: String? = null) {
        assertEquals(expected.w, actual.w, epsilon, message)
        assertEquals(expected.x, actual.x, epsilon, message)
        assertEquals(expected.y, actual.y, epsilon, message)
        assertEquals(expected.z, actual.z, epsilon, message)
    }

    @Test
    fun testDefaultConstructorIsIdentity() {
        val q = ZQuaternion()
        assertEquals(1f, q.w)
        assertEquals(0f, q.x)
        assertEquals(0f, q.y)
        assertEquals(0f, q.z)
    }

    @Test
    fun testValuesConstructor() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        assertEquals(1f, q.w)
        assertEquals(2f, q.x)
        assertEquals(3f, q.y)
        assertEquals(4f, q.z)
    }

    @Test
    fun testPlus() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion(5f, 6f, 7f, 8f)
        val result = q1 + q2
        assertEquals(6f, result.w)
        assertEquals(8f, result.x)
        assertEquals(10f, result.y)
        assertEquals(12f, result.z)
    }

    @Test
    fun testMinus() {
        val q1 = ZQuaternion(5f, 6f, 7f, 8f)
        val q2 = ZQuaternion(1f, 2f, 3f, 4f)
        val result = q1 - q2
        assertEquals(4f, result.w)
        assertEquals(4f, result.x)
        assertEquals(4f, result.y)
        assertEquals(4f, result.z)
    }

    @Test
    fun testTimesQuaternion() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion(5f, 6f, 7f, 8f)
        val result = q1 * q2
        val expected = ZQuaternion(-60f, 12f, 30f, 24f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testTimesScalar() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        val result = q * 2f
        assertEquals(2f, result.w)
        assertEquals(4f, result.x)
        assertEquals(6f, result.y)
        assertEquals(8f, result.z)
    }

    @Test
    fun testIdentity() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        q.identity()
        assertEquals(1f, q.w)
        assertEquals(0f, q.x)
        assertEquals(0f, q.y)
        assertEquals(0f, q.z)
    }

    @Test
    fun testConjugate() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        q.conjugate()
        assertEquals(1f, q.w)
        assertEquals(-2f, q.x)
        assertEquals(-3f, q.y)
        assertEquals(-4f, q.z)
    }

    @Test
    fun testNormalize() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        q.normalize()
        assertTrue(q.isNormalized)
    }

    @Test
    fun testInvert() {
        val q = ZQuaternion(0.5f, 0.5f, 0.5f, 0.5f)
        val inverted = ZQuaternion()
        ZQuaternion.invert(inverted, q)
        val identity = q * inverted
        assertQuaternionEquals(ZQuaternion(1f, 0f, 0f, 0f), identity)
    }

    @Test
    fun testFromAngleAxis() {
        val q = ZQuaternion()
        val angle = 90f
        val axis = ZVector3(0f, 1f, 0f)
        q.fromAngleAxis(angle, axis.x, axis.y, axis.z)

        val halfAngle = (angle * (kotlin.math.PI / 180.0) / 2.0).toFloat()
        assertEquals(kotlin.math.cos(halfAngle), q.w, epsilon)
        assertEquals(axis.x * kotlin.math.sin(halfAngle), q.x, epsilon)
        assertEquals(axis.y * kotlin.math.sin(halfAngle), q.y, epsilon)
        assertEquals(axis.z * kotlin.math.sin(halfAngle), q.z, epsilon)
    }

    @Test
    fun testToMatrix4AndBack() {
        val q = ZQuaternion()
        q.fromAngleAxis(45f, 0f, 1f, 0f)
        val m = q.toMatrix4()
        val q2 = ZQuaternion()
        q2.fromMatrix4(m)
        assertQuaternionEquals(q, q2)
    }

    @Test
    @Ignore
    fun testFromEulerAndBack() {
        val euler = ZEuler(30f, 60f, 90f)
        val q = ZQuaternion.fromEuler(euler)
        val eulerBack = q.toEuler()

        assertEquals(euler.roll, eulerBack.roll, epsilon, "Roll should match")
        assertEquals(euler.pitch, eulerBack.pitch, epsilon, "Pitch should match")
        assertEquals(euler.yaw, eulerBack.yaw, epsilon, "Yaw should match")
    }

    @Test
    fun testSlerp() {
        val q1 = ZQuaternion(1f, 0f, 0f, 0f)
        val q2 = ZQuaternion()
        q2.fromAngleAxis(90f, 0f, 1f, 0f)

        val result = ZQuaternion.slerp(0.5f, q1, q2)

        val expected = ZQuaternion()
        expected.fromAngleAxis(45f, 0f, 1f, 0f)

        assertQuaternionEquals(expected, result)
    }

    //region Companion object tests
    @Test
    fun testOperationCopy() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion()
        ZQuaternion.copy(q2, q1)
        assertQuaternionEquals(q1, q2)
    }

    @Test
    fun testOperationIdentity() {
        val q = ZQuaternion()
        ZQuaternion.identity(q)
        assertQuaternionEquals(ZQuaternion(1f, 0f, 0f, 0f), q)
    }

    @Test
    fun testOperationZero() {
        val q = ZQuaternion()
        ZQuaternion.zero(q)
        assertQuaternionEquals(ZQuaternion(0f, 0f, 0f, 0f), q)
    }

    @Test
    fun testOperationAdd() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion(5f, 6f, 7f, 8f)
        val result = ZQuaternion()
        ZQuaternion.add(result, q1, q2)
        val expected = ZQuaternion(6f, 8f, 10f, 12f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationSubtract() {
        val q1 = ZQuaternion(5f, 6f, 7f, 8f)
        val q2 = ZQuaternion(1f, 2f, 3f, 4f)
        val result = ZQuaternion()
        ZQuaternion.subtract(result, q1, q2)
        val expected = ZQuaternion(4f, 4f, 4f, 4f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationConjugate() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        val result = ZQuaternion()
        ZQuaternion.conjugate(result, q)
        val expected = ZQuaternion(1f, -2f, -3f, -4f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationDot() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion(5f, 6f, 7f, 8f)
        val result = ZQuaternion.dot(q1, q2)
        assertEquals(70f, result, epsilon)
    }

    @Test
    fun testOperationMult() {
        val q1 = ZQuaternion(1f, 2f, 3f, 4f)
        val q2 = ZQuaternion(5f, 6f, 7f, 8f)
        val result = ZQuaternion()
        ZQuaternion.mult(result, q1, q2)
        val expected = ZQuaternion(-60f, 12f, 30f, 24f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationMultScalar() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        val result = ZQuaternion()
        ZQuaternion.multScalar(result, 2f, q)
        val expected = ZQuaternion(2f, 4f, 6f, 8f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationNormalize() {
        val q = ZQuaternion(1f, 2f, 3f, 4f)
        val result = ZQuaternion()
        ZQuaternion.normalize(result, q)
        assertTrue(result.isNormalized)
    }

    @Test
    fun testOperationInvert() {
        val q = ZQuaternion(0.5f, 0.5f, 0.5f, 0.5f)
        val inverted = ZQuaternion()
        ZQuaternion.invert(inverted, q)
        val result = ZQuaternion()
        ZQuaternion.mult(result, q, inverted)
        assertQuaternionEquals(ZQuaternion(1f, 0f, 0f, 0f), result)
    }

    @Test
    fun testOperationRotate() {
        val q = ZQuaternion(1f, 0f, 0f, 0f)
        val result = ZQuaternion()
        val angle = 90f
        val axis = ZVector3(0f, 1f, 0f)
        ZQuaternion.rotate(result, q, angle, axis)

        val expected = ZQuaternion()
        expected.fromAngleAxis(angle, axis.x, axis.y, axis.z)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationFromVec3() {
        val v = ZVector3(1f, 2f, 3f)
        val result = ZQuaternion()
        ZQuaternion.fromVec3(result, v)
        val expected = ZQuaternion(0f, 1f, 2f, 3f)
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationFromAngleAxis() {
        val result = ZQuaternion()
        val angle = 90f
        val axis = ZVector3(0f, 1f, 0f)
        ZQuaternion.fromAngleAxis(result, angle, axis)

        val halfAngle = (angle * (kotlin.math.PI / 180.0) / 2.0).toFloat()
        val expected = ZQuaternion(
            kotlin.math.cos(halfAngle),
            axis.x * kotlin.math.sin(halfAngle),
            axis.y * kotlin.math.sin(halfAngle),
            axis.z * kotlin.math.sin(halfAngle)
        )
        assertQuaternionEquals(expected, result)
    }

    @Test
    fun testOperationFromMatrix4() {
        val q = ZQuaternion()
        q.fromAngleAxis(45f, 0f, 1f, 0f)
        val m = q.toMatrix4()
        val result = ZQuaternion()
        ZQuaternion.fromMatrix4(result, m)
        assertQuaternionEquals(q, result)
    }

    @Test
    @Ignore
    fun testOperationFromEuler() {
        val euler = ZEuler(30f, 60f, 90f)
        val result = ZQuaternion()
        ZQuaternion.fromEuler(result, euler)
        val eulerBack = result.toEuler()

        assertEquals(euler.roll, eulerBack.roll, epsilon, "Roll should match")
        assertEquals(euler.pitch, eulerBack.pitch, epsilon, "Pitch should match")
        assertEquals(euler.yaw, eulerBack.yaw, epsilon, "Yaw should match")
    }

    @Test
    fun testOperationSlerp() {
        val q1 = ZQuaternion(1f, 0f, 0f, 0f)
        val q2 = ZQuaternion()
        q2.fromAngleAxis(90f, 0f, 1f, 0f)
        val result = ZQuaternion()
        ZQuaternion.slerp(result, 0.5f, q1, q2)

        val expected = ZQuaternion()
        expected.fromAngleAxis(45f, 0f, 1f, 0f)

        assertQuaternionEquals(expected, result)
    }
    //endregion
}
