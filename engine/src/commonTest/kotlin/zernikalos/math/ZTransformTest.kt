package zernikalos.math

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class ZTransformTest {

    private fun rad(degrees: Float): Float = degrees * PI.toFloat() / 180f

    @Test
    fun testDefaultConstructor() {
        val transform = ZTransform()
        assertVectorEquals(ZVector3.Zero, transform.position)
        assertQuaternionEquals(ZQuaternion.Identity, transform.rotation)
        assertVectorEquals(ZVector3.Ones, transform.scale)
    }

    @Test
    fun testInitWithArgs() {
        val position = ZVector3(1f, 2f, 3f)
        val rotation = ZQuaternion()
        ZQuaternion.fromAngleAxis(rotation, rad(90f), ZVector3.Up)
        val scale = ZVector3(2f, 2f, 2f)
        val transform = ZTransform(position, rotation, scale)

        assertVectorEquals(position, transform.position)
        assertQuaternionEquals(rotation, transform.rotation)
        assertVectorEquals(scale, transform.scale)
    }

    @Test
    fun testSetPosition() {
        val transform = ZTransform()
        val newPosition = ZVector3(10f, 20f, 30f)
        transform.position = newPosition
        assertVectorEquals(newPosition, transform.position)
    }

    @Test
    fun testSetRotation() {
        val transform = ZTransform()
        val newRotation = ZQuaternion()
        ZQuaternion.fromAngleAxis(newRotation, rad(45f), ZVector3.Right)
        transform.rotation = newRotation
        assertQuaternionEquals(newRotation, transform.rotation)
    }

    @Test
    fun testSetScale() {
        val transform = ZTransform()
        val newScale = ZVector3(3f, 3f, 3f)
        transform.scale = newScale
        assertVectorEquals(newScale, transform.scale)
    }

    @Test
    fun testTranslate() {
        val transform = ZTransform()
        transform.translate(1f, 2f, 3f)
        assertVectorEquals(ZVector3(1f, 2f, 3f), transform.position)
    }

    @Test
    fun testRotate() {
        val transform = ZTransform()
        val rotation = ZQuaternion()
        ZQuaternion.fromAngleAxis(rotation, rad(90f), ZVector3.Up)
        transform.rotate(rotation)
        assertQuaternionEquals(rotation, transform.rotation)
    }

    @Test
    fun testLookAt() {
        val transform = ZTransform()
        val target = ZVector3(10f, 0f, 0f)
        transform.lookAt(target)

        val expectedForward = target.normalized
        assertVectorEquals(expectedForward, transform.forward, "Forward vector should point to the target")
    }

    @Test
    fun setRotationDegrees_matchesRadianApi() {
        val tDeg = ZTransform()
        tDeg.setRotationDegrees(90f, ZVector3.Up)
        val tRad = ZTransform()
        tRad.setRotation(rad(90f), ZVector3.Up)
        assertQuaternionEquals(tRad.rotation, tDeg.rotation)
    }

    @Test
    fun rotateDegrees_matchesRadianApi() {
        val tDeg = ZTransform()
        tDeg.rotateDegrees(45f, ZVector3.Right)
        val tRad = ZTransform()
        tRad.rotate(rad(45f), ZVector3.Right)
        assertQuaternionEquals(tRad.rotation, tDeg.rotation)
    }

    @Test
    fun rotationEulerDegrees_setYawInDegrees() {
        val t = ZTransform()
        t.rotationEulerDegrees = ZEuler(0f, 0f, 90f)
        assertEquals(90f, t.yawDegrees, 0.5f)
    }

    @Test
    fun yawPitchRollDegrees_consistentWithRadians() {
        val t = ZTransform()
        t.setRotationDegrees(90f, ZVector3.Up)
        assertEquals(Angles.radiansToDegrees(t.yaw), t.yawDegrees, 0.01f)
    }

    @Test
    fun testMatrix() {
        val position = ZVector3(1f, 2f, 3f)
        val rotation = ZQuaternion()
        ZQuaternion.fromAngleAxis(rotation, rad(90f), ZVector3.Up)
        val scale = ZVector3(2f, 2f, 2f)
        val transform = ZTransform(position, rotation, scale)

        val matrix = transform.matrix

        val expectedMatrix = ZMatrix4()
        ZMatrix4.identity(expectedMatrix)
        ZMatrix4.scale(expectedMatrix, scale)
        ZMatrix4.rotate(expectedMatrix, rotation)
        ZMatrix4.setTranslation(expectedMatrix, position)

        for (i in 0 until 16) {
            assertEquals(expectedMatrix.floatArray[i], matrix.floatArray[i], epsilon)
        }
    }
}
