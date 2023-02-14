package mr.robotto

import mr.robotto.objects.MrObject

fun upToRoot(obj: MrObject): Iterator<MrObject> {
    var current: MrObject = obj
    return object : Iterator<MrObject> {
        override fun hasNext(): Boolean {
            return current.hasParent
        }

        override fun next(): MrObject {
            val nextValue = current
            current = nextValue.parent!!
            return nextValue
        }

    }
}