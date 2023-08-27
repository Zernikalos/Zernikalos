package zernikalos.components.material

import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.DataView
import org.khronos.webgl.Uint8Array

actual class ZBitmap {

    val nativeData: ArrayBufferView

    actual constructor(byteArray: ByteArray) {
        nativeData = Uint8Array(byteArray.toTypedArray())
    }

    actual fun dispose() {
    }

}