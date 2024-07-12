/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

import zernikalos.ZTreeNode

/**
 * Traverses a tree structure starting from the given node and returns an iterator over the nodes.
 *
 * @param node The root node of the tree.
 * @return An iterator over the nodes in the tree.
 * @param T The type of the node that implements the ZTreeNode interface.
 */
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