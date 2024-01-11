package zernikalos.components.material

actual class ZBitmap {

    val byteArray: ByteArray

    actual constructor(byteArray: ByteArray) {
        this.byteArray = byteArray
    }

    actual fun dispose() {
    }

}