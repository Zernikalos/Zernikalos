package zernikalos.components.camera

import zernikalos.math.Angles
import zernikalos.math.assertMatrixEquals
import zernikalos.math.epsilon
import kotlin.test.Test
import kotlin.test.assertEquals

class ZPerspectiveLensDegreesTest {

    @Test
    fun fromVerticalFovDegrees_matchesRadianConstructor() {
        val deg = 60f
        val rad = Angles.degreesToRadians(deg)
        val a = ZPerspectiveLens.fromVerticalFovDegrees(1f, 100f, deg)
        val b = ZPerspectiveLens(1f, 100f, rad)
        assertEquals(a.fov, b.fov, epsilon)
        assertMatrixEquals(a.projectionMatrix, b.projectionMatrix)
    }

    @Test
    fun setVerticalFovDegrees_updatesFov() {
        val lens = ZPerspectiveLens(1f, 100f, 0.1f)
        lens.setVerticalFovDegrees(45f)
        assertEquals(Angles.degreesToRadians(45f), lens.fov, epsilon)
    }
}
