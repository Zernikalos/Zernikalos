/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

fun FloatArray.toByteArray(): ByteArray {
    val byteArray = ByteArray(this.size * 4)
    this.forEachIndexed { index, value ->
        val intBits = value.toBits()
        byteArray[index * 4 + 0] = (intBits shr 24 and 0xFF).toByte()
        byteArray[index * 4 + 1] = (intBits shr 16 and 0xFF).toByte()
        byteArray[index * 4 + 2] = (intBits shr 8 and 0xFF).toByte()
        byteArray[index * 4 + 3] = (intBits and 0xFF).toByte()
    }
    return byteArray
}