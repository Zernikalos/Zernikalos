package zernikalos.math

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class ZEulerFromDegreesTest {

    @Test
    fun fromDegrees_matchesManualConversion() {
        val e = ZEuler.fromDegrees(90f, 45f, 30f)
        assertEquals((PI / 2.0).toFloat(), e.roll, epsilon)
        assertEquals((PI / 4.0).toFloat(), e.pitch, epsilon)
        assertEquals((PI / 6.0).toFloat(), e.yaw, epsilon)
    }
}
