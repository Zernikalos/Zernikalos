package zernikalos.utils

import zernikalos.ZTreeNode

fun <T: ZTreeNode<T>>treeTraverse(node: T): Iterator<T> {
    val q = arrayListOf(node)
    return object : Iterator<T> {
        override fun hasNext(): Boolean = !q.isEmpty()

        override fun next(): T {
            val n = q.removeFirst()
            q.addAll(n.children)

            return n
        }

    }
}