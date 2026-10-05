package zernikalos.math

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class AnglesTest {

    @Test
    fun degreesToRadians_rightAngles() {
        assertEquals(0f, degreesToRadians(0f), epsilon)
        assertEquals((PI / 2.0).toFloat(), degreesToRadians(90f), epsilon)
        assertEquals(PI.toFloat(), degreesToRadians(180f), epsilon)
    }

    @Test
    fun radiansToDegrees_roundTrip() {
        val rad = 0.35f
        assertEquals(rad, degreesToRadians(radiansToDegrees(rad)), epsilon)
    }
}
