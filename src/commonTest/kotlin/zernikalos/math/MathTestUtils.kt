package zernikalos.math

import kotlin.test.assertEquals

const val epsilon = 1e-6f

fun assertMatrixEquals(expected: ZMatrix4, actual: ZMatrix4, message: String? = null) {
    for (i in 0 until 16) {
        assertEquals(expected[i], actual[i], epsilon, message)
    }
}

fun assertQuaternionEquals(expected: ZQuaternion, actual: ZQuaternion, message: String? = null) {
    assertEquals(expected.w, actual.w, epsilon, message)
    assertEquals(expected.x, actual.x, epsilon, message)
    assertEquals(expected.y, actual.y, epsilon, message)
    assertEquals(expected.z, actual.z, epsilon, message)
}

fun assertVectorEquals(expected: ZVector2, actual: ZVector2, message: String? = null) {
    assertEquals(expected.x, actual.x, epsilon, message)
    assertEquals(expected.y, actual.y, epsilon, message)
}

fun assertVectorEquals(expected: ZVector3, actual: ZVector3, message: String? = null) {
    assertEquals(expected.x, actual.x, epsilon, message)
    assertEquals(expected.y, actual.y, epsilon, message)
    assertEquals(expected.z, actual.z, epsilon, message)
}

fun assertVectorEquals(expected: ZVector4, actual: ZVector4, message: String? = null) {
    assertEquals(expected.x, actual.x, epsilon, message)
    assertEquals(expected.y, actual.y, epsilon, message)
    assertEquals(expected.z, actual.z, epsilon, message)
    assertEquals(expected.w, actual.w, epsilon, message)
}
