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
import zernikalos.utils.copyFloatArrayIntoByteArray
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
 * @property dataSize The total number of elements allocated in the collection.
 */
@JsExport
class ZAlgebraObjectCollection(val dataSize: Int): ZAlgebraObject {

    private var _dataType: ZDataType = ZTypes.FLOAT

    private var _count: Int = 1

    override val values: FloatArray = FloatArray(dataSize)

    val byteArray: ByteArray = ByteArray(dataSize * ZTypes.FLOAT.byteSize)

    override val dataType: ZDataType
        get() = _dataType

    override val count: Int
        get() = _count

    override val size: Int
        get() = dataType.size

    val byteSize: Int
        get() = dataType.byteSize * count

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
    constructor(dataType: ZDataType, count: Int): this(dataType.size * count) {
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
    fun copyInto(index: Int, value: ZAlgebraObject) {
        if (value.dataType.type != dataType.type) {
            throw Error("Unable to add object of type ${value.dataType.type} into a collection of ${dataType.type}")
        }

        value.values.copyInto(values, index * size)
        copyFloatArrayIntoByteArray(value.values, byteArray, index * size)
    }

    /**
     * Copies all values from the specified array of `ZAlgebraObject` starting at the given index
     * into this collection. Each object in the array is copied sequentially into the collection.
     *
     * @param index the starting position in the collection from where the values should be copied.
     * @param values an array of `ZAlgebraObject` that contains the values to be copied into the collection.
     */
    @JsName("copyAllFromIndexAndArray")
    fun copyAllFromIndex(index: Int, values: Array<ZAlgebraObject>) {
        var auxIndex = index
        values.forEach {
            copyInto(auxIndex, it)
            auxIndex++
        }
    }

    /**
     * Copies all values from the specified list of `ZAlgebraObject` starting at the given index
     * into this collection. Each object in the list is copied sequentially into the collection.
     *
     * @param index the starting position in the collection from where the values should be copied.
     * @param values a list of `ZAlgebraObject` that contains the values to be copied into the collection.
     */
    fun copyAllFromIndex(index: Int, values: List<ZAlgebraObject>) {
        var auxIndex = index
        values.forEach {
            copyInto(auxIndex, it)
            auxIndex++
        }
    }

    /**
     * Copies all values from the specified array of `ZAlgebraObject` into this collection.
     *
     * @param values an array of `ZAlgebraObject` that contains the values to be copied into the collection.
     */
    @JsName("copyAllFromArray")
    fun copyAll(values: Array<ZAlgebraObject>) {
        copyAllFromIndex(0, values)
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