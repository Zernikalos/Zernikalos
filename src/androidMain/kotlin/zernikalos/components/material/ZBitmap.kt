/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import android.graphics.Bitmap
import android.graphics.BitmapFactory

actual class ZBitmap actual constructor(byteArray: ByteArray) {

    val nativeBitmap: Bitmap

    init {
        nativeBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    actual fun dispose() {
        nativeBitmap.recycle()
    }

}