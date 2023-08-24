package zernikalos.components.material

import android.graphics.Bitmap
import android.graphics.BitmapFactory

actual class ZBitmap {

    val nativeBitmap: Bitmap

    actual constructor(byteArray: ByteArray) {
        nativeBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    actual fun dispose() {
        nativeBitmap.recycle()
    }

}