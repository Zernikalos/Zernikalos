package zernikalos.math

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class AnglesTest {

    @Test
    fun degreesToRadians_rightAngles() {
        assertEquals(0f, Angles.degreesToRadians(0f), epsilon)
        assertEquals((PI / 2.0).toFloat(), Angles.degreesToRadians(90f), epsilon)
        assertEquals(PI.toFloat(), Angles.degreesToRadians(180f), epsilon)
    }

    @Test
    fun radiansToDegrees_roundTrip() {
        val rad = 0.35f
        assertEquals(rad, Angles.degreesToRadians(Angles.radiansToDegrees(rad)), epsilon)
    }
}
