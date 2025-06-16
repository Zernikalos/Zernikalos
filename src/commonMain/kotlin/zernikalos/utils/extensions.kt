/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

// By default, all graphic APIs use little endian byte order.
fun FloatArray.toByteArray(littleEndian: Boolean = true): ByteArray {
    val byteArray = ByteArray(this.size * 4)
    this.forEachIndexed { index, value ->
        val intBits = value.toBits()
        val baseIndex = index * 4

        if (littleEndian) {
            // Little Endian: LSB first
            byteArray[baseIndex + 0] = (intBits and 0xFF).toByte()          // LSB
            byteArray[baseIndex + 1] = (intBits shr 8 and 0xFF).toByte()
            byteArray[baseIndex + 2] = (intBits shr 16 and 0xFF).toByte()
            byteArray[baseIndex + 3] = (intBits shr 24 and 0xFF).toByte()   // MSB
        } else {
            // Big Endian: MSB first
            byteArray[baseIndex + 0] = (intBits shr 24 and 0xFF).toByte()   // MSB
            byteArray[baseIndex + 1] = (intBits shr 16 and 0xFF).toByte()
            byteArray[baseIndex + 2] = (intBits shr 8 and 0xFF).toByte()
            byteArray[baseIndex + 3] = (intBits and 0xFF).toByte()          // LSB
        }
    }
    return byteArray
}

fun ShortArray.toByteArray(littleEndian: Boolean = true): ByteArray {
    val byteArray = ByteArray(this.size * 2)
    this.forEachIndexed { index, value ->
        val intBits = value.toInt()
        val baseIndex = index * 2

        if (littleEndian) {
            // Little Endian: LSB first
            byteArray[baseIndex + 0] = (intBits and 0xFF).toByte()          // LSB
            byteArray[baseIndex + 1] = (intBits shr 8 and 0xFF).toByte()    // MSB
        } else {
            // Big Endian: MSB first
            byteArray[baseIndex + 0] = (intBits shr 8 and 0xFF).toByte()    // MSB
            byteArray[baseIndex + 1] = (intBits and 0xFF).toByte()          // LSB
        }
    }
    return byteArray
}

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
fun ByteArray.toFloatArray(littleEndian: Boolean = true): FloatArray {
    require(size % 4 == 0) { "ByteArray size must be a multiple of 4 to convert to FloatArray." }

    val floats = FloatArray(size / 4)
    var byteIndex = 0
    for (i in floats.indices) {
        val intBits = if (littleEndian) {
            // Little Endian: bytes are ordered LSB .. MSB
            (this[byteIndex++].toInt() and 0xFF) or
                ((this[byteIndex++].toInt() and 0xFF) shl 8) or
                ((this[byteIndex++].toInt() and 0xFF) shl 16) or
                ((this[byteIndex++].toInt() and 0xFF) shl 24)
        } else {
            // Big Endian: MSB .. LSB (network order)
            ((this[byteIndex++].toInt() and 0xFF) shl 24) or
                ((this[byteIndex++].toInt() and 0xFF) shl 16) or
                ((this[byteIndex++].toInt() and 0xFF) shl 8) or
                (this[byteIndex++].toInt() and 0xFF)
        }
        floats[i] = Float.fromBits(intBits)
    }
    return floats
}

