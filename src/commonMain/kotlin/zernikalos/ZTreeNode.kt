/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import kotlin.js.JsExport

@JsExport
interface ZTreeNode<T: ZTreeNode<T>> {

    var parent: T?
    val hasParent: Boolean

    val children: Array<T>
}