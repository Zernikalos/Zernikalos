package zernikalos

import zernikalos.objects.ZObject

fun upToRoot(obj: ZObject): Iterator<ZObject> {
    var current: ZObject = obj
    return object : Iterator<ZObject> {
        override fun hasNext(): Boolean {
            return current.hasParent
        }

        override fun next(): ZObject {
            val nextValue = current
            current = nextValue.parent!!
            return nextValue
        }

    }
}