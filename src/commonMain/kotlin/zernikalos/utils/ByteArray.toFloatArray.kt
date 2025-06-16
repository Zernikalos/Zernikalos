/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

/**
 * Converts this [ByteArray] into a [FloatArray].
 *
 * The bytes are interpreted using the same big-endian order produced by
 * [FloatArray.toByteArray]: the first byte corresponds to the most significant
 * bits of the float and the fourth byte to the least significant.
 *
 * @throws IllegalArgumentException if the size of the array is not a multiple
 * of 4.
 */
fun ByteArray.toFloatArray(): FloatArray {
    require(size % 4 == 0) { "ByteArray size must be a multiple of 4 to convert to FloatArray." }

    val floats = FloatArray(size / 4)
    var byteIndex = 0
    for (i in floats.indices) {
        val intBits =
            ((this[byteIndex++].toInt() and 0xFF) shl 24) or
            ((this[byteIndex++].toInt() and 0xFF) shl 16) or
            ((this[byteIndex++].toInt() and 0xFF) shl 8) or
            (this[byteIndex++].toInt() and 0xFF)
        floats[i] = Float.fromBits(intBits)
    }
    return floats
}
