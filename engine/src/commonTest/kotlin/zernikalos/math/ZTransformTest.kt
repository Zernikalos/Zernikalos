package zernikalos.math

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ZTransformTest {

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
        ZQuaternion.fromAngleAxis(rotation, 90f, ZVector3.Up)
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
        ZQuaternion.fromAngleAxis(newRotation, 45f, ZVector3.Right)
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
        ZQuaternion.fromAngleAxis(rotation, 90f, ZVector3.Up)
        transform.rotate(rotation)
        assertQuaternionEquals(rotation, transform.rotation)
    }

    @Test
    @Ignore
    fun testLookAt() {
        val transform = ZTransform()
        val target = ZVector3(10f, 0f, 0f)
        transform.lookAt(target)

        val expectedForward = target.normalized
        assertVectorEquals(expectedForward, transform.forward, "Forward vector should point to the target")
    }

    @Test
    fun testMatrix() {
        val position = ZVector3(1f, 2f, 3f)
        val rotation = ZQuaternion()
        ZQuaternion.fromAngleAxis(rotation, 90f, ZVector3.Up)
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
