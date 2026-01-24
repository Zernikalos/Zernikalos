/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.ImageBitmap
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.js.Promise

@JsExport
actual class ZBitmap {

    private val nativeData: ArrayBufferView
    private val htmlImageElement: HTMLImageElement
    private var _imageBitmap: ImageBitmap? = null
    private var imageBitmapPromise: Promise<ImageBitmap>? = null

    val imageBitmap: ImageBitmap?
        get() = _imageBitmap

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val isLoading: Promise<Nothing?>

    @JsName("initWithByteArray")
    actual constructor(byteArray: ByteArray) {
        nativeData = Uint8Array(byteArray.toTypedArray())
        // TODO: This part needs some review
        coroutineScope.launch {
            _imageBitmap = buildImageBitmap(nativeData)
        }
        imageBitmapPromise = buildImageBitmapAsync(nativeData)

        isLoading = Promise { res, rej ->
            imageBitmapPromise?.then { img ->
                _imageBitmap = img
                console.log("[ZBitmap] Bitmap loaded successfully: width=${img.width}, height=${img.height}")
                res(null)
            }?.catch { e ->
                rej(e)
            }
        }

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
