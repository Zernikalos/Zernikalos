/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import zernikalos.ZDataType
import zernikalos.ZTypes

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
class ZAlgebraObjectCollection(dataSize: Int): ZAlgebraObject {

    private var _dataType: ZDataType = ZTypes.FLOAT

    private var _count: Int = 1

    override val values: FloatArray = FloatArray(dataSize)

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
    constructor(dataType: ZDataType, count: Int): this(dataType.size * count) {
        _dataType = dataType
        _count = count
    }

    fun add(index: Int, value: ZAlgebraObject) {
        if (value.dataType.type != dataType.type) {
            throw Error("Unable to add object of type ${value.dataType.type} into a collection of ${dataType.type}")
        }

        value.values.copyInto(values, index * size)
    }

    fun addAll(index: Int, values: Array<ZAlgebraObject>) {
        var auxIndex = index
        values.forEach {
            add(auxIndex, it)
            auxIndex++
        }
    }

    fun addAll(index: Int, values: List<ZAlgebraObject>) {
        var auxIndex = index
        values.forEach {
            add(auxIndex, it)
            auxIndex++
        }
    }

    fun addAll(values: Array<ZAlgebraObject>) {
        addAll(0, values)
    }

    fun addAll(values: List<ZAlgebraObject>) {
        addAll(0, values)
    }
}