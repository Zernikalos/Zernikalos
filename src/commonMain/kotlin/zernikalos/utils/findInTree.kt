/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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