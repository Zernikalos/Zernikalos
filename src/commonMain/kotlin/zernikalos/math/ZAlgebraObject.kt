/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import zernikalos.ZDataType
import kotlin.js.JsExport

@JsExport
interface ZAlgebraObject {

    /**
     * The data type stored
     */
    val dataType: ZDataType

    val floatArray: FloatArray

    val byteArray: ByteArray

    /**
     * Number of elements contained within the values array with the specific type
     * defined in dataType
     */
    val size: Int

    /**
     * Number of individual instances this object represents
     */
    val count: Int

}
