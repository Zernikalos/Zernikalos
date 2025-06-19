/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.utils.toFloatArray
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * A collection class for managing and storing multiple instances of `ZAlgebraObject`.
 *
 * This class allows for efficient storage and manipulation of algebraic objects by organizing
 * them in a continuous `FloatArray`. The data type and count of objects contained in the collection
 * are determined upon initialization.
 *
 * @constructor Initializes a new instance of `ZAlgebraObjectCollection` with a specified data size.
 *
 * byteDataSize The total number of elements allocated in the collection.
 */
@JsExport
class ZAlgebraObjectCollection(byteDataSize: Int): ZAlgebraObject {

    private var _dataType: ZDataType = ZTypes.FLOAT

    private var _count: Int = 1

    override val byteArray: ByteArray = ByteArray(byteDataSize)

    override val floatArray: FloatArray
        get() = byteArray.toFloatArray()

    override val dataType: ZDataType
        get() = _dataType

    override val count: Int
        get() = _count

    override val size: Int
        get() = floatArray.size

    override val byteSize: Int
        get() = byteArray.size

    /**
     * Secondary constructor for the `ZAlgebraObjectCollection` class that initializes the collection
     * with a specified data type and count of algebraic objects.
     *
     * This constructor calculates the data size by multiplying the element size of the given
     * data type with the specified count of objects.
     *
     * @param dataType The data type of the objects that the collection will manage.
     * @param count The number of instances of the algebraic object to be managed by the collection.
     */
    @JsName("initWithDataTypeAndCount")
    constructor(dataType: ZDataType, count: Int): this(dataType.byteSize * count) {
        _dataType = dataType
        _count = count
    }

    /**
     * Copies the values from the specified `ZAlgebraObject` into the collection
     * starting at the location defined by `index`.
     *
     * @param index the position in the collection where the values should be copied.
     * @param value the `ZAlgebraObject` containing the values to be copied into the collection.
     * @throws Error if the data type of the `ZAlgebraObject` does not match the collection's data type.
     */
    fun copyInto(offset: Int, value: ZAlgebraObject): Int {
        if (value.dataType.type != dataType.type) {
            throw Error("Unable to add object of type ${value.dataType.type} into a collection of ${dataType.type}")
        }

        value.byteArray.copyInto(byteArray, offset)

        return value.byteArray.size
    }


    /**
     * Copies all values from the specified list of `ZAlgebraObject` starting at the given index
     * into this collection. Each object in the list is copied sequentially into the collection.
     *
     * @param offset the starting position in the collection from where the values should be copied.
     * @param values a list of `ZAlgebraObject` that contains the values to be copied into the collection.
     */
    private fun copyAllFromIndex(offset: Int, values: List<ZAlgebraObject>) {
        var auxOffset = offset
        values.forEach {
            val writtenBytes = copyInto(auxOffset, it)
            auxOffset += writtenBytes
        }
    }

    /**
     * Copies all values from the specified list of `ZAlgebraObject` into this collection.
     *
     * @param values a list of `ZAlgebraObject` that contains the values to be copied into the collection.
     */
    fun copyAll(values: List<ZAlgebraObject>) {
        copyAllFromIndex(0, values)
    }
}
