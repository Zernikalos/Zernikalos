/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import zernikalos.objects.ZObject
import kotlin.js.Promise

/**
 * Loads a [ZObject] from the specified URL.
 *
 * This function fetches the data from the URL, converts into a [ZObject] instance.
 *
 * @param url The URL from which to load the data.
 * @return A [Promise] that resolves to the loaded [ZObject].
 */
@JsExport
fun loadFromUrl(url: String): Promise<ZKo> {
    return window.fetch(url).then { response ->
        response.arrayBuffer()
    }.then { buffer: ArrayBuffer ->
        val int8Array = Int8Array(buffer)
        loadFromProto(int8Array as ByteArray)
    }
}
