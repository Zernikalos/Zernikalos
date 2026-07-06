package zernikalos.context

import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector4
import zernikalos.math.assertMatrixEquals
import zernikalos.math.epsilon
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class BackendCorrectionMatrixTest {

    /** Reference GL → D3D/WebGPU theoretical matrix (not used by backend actuals). */
    private val glToD3dClipCorrection = ZMatrix4(
        floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, -1f, 0f, 0f,
            0f, 0f, 0.5f, 0f,
            0f, 0f, 0.5f, 1f,
        )
    )

    /** Metal backend: Z remap only, Y unchanged. */
    private val glToMetalZRemap = ZMatrix4(
        floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 0.5f, 0f,
            0f, 0f, 0.5f, 1f,
        )
    )

    @Test
    fun glToMetalZRemap_hasExpectedElements() {
        assertEquals(1f, glToMetalZRemap[0], epsilon)
        assertEquals(1f, glToMetalZRemap[5], epsilon)
        assertEquals(0.5f, glToMetalZRemap[10], epsilon)
        assertEquals(0.5f, glToMetalZRemap[14], epsilon)
        assertEquals(1f, glToMetalZRemap[15], epsilon)
    }

    @Test
    fun glToMetalZRemap_preservesY() {
        val input = ZVector4(0f, 1f, 0f, 1f)
        val output = ZVector4()
        ZVector4.multMatrix(output, glToMetalZRemap, input)
        assertEquals(1f, output.y, epsilon)
    }

    @Test
    fun glToMetalZRemap_remapsGlNdcZMinusOneToZero() {
        val glNearNdc = ZVector4(0f, 0f, -1f, 1f)
        val output = ZVector4()
        ZVector4.multMatrix(output, glToMetalZRemap, glNearNdc)
        assertEquals(0f, output.z / output.w, epsilon)
    }

    @Test
    fun glToMetalZRemap_remapsGlNdcZPlusOneToOne() {
        val glFarNdc = ZVector4(0f, 0f, 1f, 1f)
        val output = ZVector4()
        ZVector4.multMatrix(output, glToMetalZRemap, glFarNdc)
        assertEquals(1f, output.z / output.w, epsilon)
    }

    @Test
    fun glToD3dClipCorrection_flipsY() {
        val input = ZVector4(0f, 1f, 0f, 1f)
        val output = ZVector4()
        ZVector4.multMatrix(output, glToD3dClipCorrection, input)
        assertEquals(-1f, output.y, epsilon)
    }

    @Test
    fun correctedPerspective_matchesManualMultiply() {
        val projection = ZMatrix4()
        ZMatrix4.perspective(projection, (PI / 4.0).toFloat(), 16f / 9f, 1f, 100f)
        val expected = ZMatrix4()
        ZMatrix4.mult(expected, glToMetalZRemap, projection)
        val actual = glToMetalZRemap * projection
        assertMatrixEquals(expected, actual)
    }
}
