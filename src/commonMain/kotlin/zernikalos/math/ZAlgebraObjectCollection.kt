package zernikalos.math

import zernikalos.ZDataType

class ZAlgebraObjectCollection(override val dataType: ZDataType, override val count: Int): ZAlgebraObject {
    override val values: FloatArray
    override val size: Int
        get() = dataType.size

    init {
        values = FloatArray(count * size)
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
}