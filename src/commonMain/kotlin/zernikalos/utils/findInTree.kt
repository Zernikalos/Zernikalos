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
 * Finds a specific node in a tree on the given predicate.
 *
 * @param tree The root node of the tree.
 * @param predicate The predicate function to determine if a node matches the search criteria.
 * @return The first node that matches the predicate, or null if no matching node is found.
 */
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