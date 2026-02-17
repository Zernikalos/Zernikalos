package zernikalos.math

import kotlin.test.assertEquals

const val epsilon = 1e-6f

fun assertMatrixEquals(expected: ZMatrix4, actual: ZMatrix4, message: String? = null) {
    assertMatrixEquals(expected, actual, epsilon, message)
}

fun assertMatrixEquals(expected: ZMatrix4, actual: ZMatrix4, epsilon: Float, message: String? = null) {
    for (i in 0 until 16) {
        val row = i % 4
        val col = i / 4
        val customMessage = buildString {
            if (message != null) append("$message\n")
            append("Failed at index [$i] (row $row, column $col)\n")
            append("Expected value: ${expected[i]}, actual value: ${actual[i]}\n")
            append("\nExpected matrix:\n${expected}\n")
            append("Actual matrix:\n${actual}")
        }
        assertEquals(expected[i], actual[i], epsilon, customMessage)
    }
}

fun assertQuaternionEquals(expected: ZQuaternion, actual: ZQuaternion, epsilon: Float = 0.0001f, message: String? = null) {
    assertEquals(expected.w, actual.w, epsilon, "Error in w component")
    assertEquals(expected.x, actual.x, epsilon, "Error in x component")
    assertEquals(expected.y, actual.y, epsilon, "Error in y component")
    assertEquals(expected.z, actual.z, epsilon, "Error in z component")
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
