package zernikalos

import zernikalos.objects.ZkObject

fun upToRoot(obj: ZkObject): Iterator<ZkObject> {
    var current: ZkObject = obj
    return object : Iterator<ZkObject> {
        override fun hasNext(): Boolean {
            return current.hasParent
        }

        override fun next(): ZkObject {
            val nextValue = current
            current = nextValue.parent!!
            return nextValue
        }

    }
}