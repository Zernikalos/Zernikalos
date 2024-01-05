package zernikalos.utils

import zernikalos.ZTreeNode

fun <T: ZTreeNode<T>>findInTree(tree: T, predicate: (node: T)-> Boolean): T? {
    val it = treeTraverse(tree)
    while (it.hasNext()) {
        val node = it.next()
        if (predicate(node)) {
            return node
        }
    }
    return null
}