/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toByteArray

/**
 * Represents a scalar value in the context of the algebra system.
 *
 * A scalar is a single numerical value. This class provides functionality to
 * manage the scalar value and interact with the `ZAlgebraObject` system.
 */
class ZScalar(): ZAlgebraObject {

    private val _value = FloatArray(1)

    override val dataType: ZDataType
        get() = ZTypes.FLOAT

    override val floatArray: FloatArray
        get() = _value

    override val byteArray: ByteArray
        get() = _value.toByteArray()

    override val byteSize: Int
        get() = dataType.byteSize

    override val size: Int
        get() = 1

    override val count: Int
        get() = 1

    constructor(value: Float): this() {
        _value[0] = value
    }
}
