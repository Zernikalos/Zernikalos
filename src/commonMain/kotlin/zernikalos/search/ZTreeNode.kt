/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.search

import kotlin.js.JsExport

/**
 * ZTreeNode interface represents a node in a tree structure.
 *
 * @param T the type of the node that implements ZTreeNode interface
 */
@JsExport
interface ZTreeNode<T: ZTreeNode<T>> {

    val parent: T?
    val hasParent: Boolean

    val children: Array<T>
}