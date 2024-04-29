package zernikalos.components.material

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.ImageBitmap
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.js.Promise

actual class ZBitmap {

    val nativeData: ArrayBufferView
    val htmlImageElement: HTMLImageElement
    var imageBitmap: ImageBitmap? = null
    var imageBitmapPromise: Promise<ImageBitmap>? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    actual constructor(byteArray: ByteArray) {
        nativeData = Uint8Array(byteArray.toTypedArray())
        coroutineScope.launch {
            imageBitmap = buildImageBitmap(nativeData)
        }
        imageBitmapPromise = buildImageBitmapAsync(nativeData)
        htmlImageElement = buildImage(nativeData)
    }

    actual fun dispose() {
    }

}

fun buildImage(nativeData: ArrayBufferView): HTMLImageElement {
    val blob = Blob(arrayOf(nativeData), BlobPropertyBag("image/jpeg"))
    val imageUrl = URL.createObjectURL(blob)
    val img: HTMLImageElement = document.createElement("img") as HTMLImageElement
    img.src = imageUrl
    return img
}

suspend fun buildImageBitmap(dataArray: ArrayBufferView): ImageBitmap {
    val blob = Blob(arrayOf(dataArray), BlobPropertyBag("image/jpeg"))
    return window.createImageBitmap(blob).await()
}

fun buildImageBitmapAsync(dataArray: ArrayBufferView): Promise<ImageBitmap> {
    val blob = Blob(arrayOf(dataArray), BlobPropertyBag("image/jpeg"))
    return window.createImageBitmap(blob)
}