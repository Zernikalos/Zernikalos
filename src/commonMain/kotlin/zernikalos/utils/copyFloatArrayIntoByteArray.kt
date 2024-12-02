/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

import zernikalos.ZTypes

/**
 * Copies the contents of a float array into a byte array starting at a specified offset.
 * Each float is converted into its IEEE 754 binary representation and stored as four bytes.
 *
 * @param source The source array containing floats to be copied.
 * @param dest The destination byte array where the float data will be copied.
 * @param offset The starting position in the destination array where the copy will begin,
 * expressed as the number of floats (not the number of bytes).
 */
internal fun copyFloatArrayIntoByteArray(source: FloatArray, dest: ByteArray, offset: Int) {
    for (i in source.indices) {
        val float = source[i]
        val byteOffset = (offset + i) * ZTypes.FLOAT.byteSize
        val intBits = float.toBits()

        dest[byteOffset] = (intBits shr 24).toByte()
        dest[byteOffset + 1] = (intBits shr 16).toByte()
        dest[byteOffset + 2] = (intBits shr 8).toByte()
        dest[byteOffset + 3] = intBits.toByte()
    }
}