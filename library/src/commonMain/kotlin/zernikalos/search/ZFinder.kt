/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.search

import zernikalos.objects.ZCamera
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject
import zernikalos.objects.ZObjectType
import kotlin.js.JsExport

/**
 * Finds an object in a tree structure by its name.
 *
 * @param root The root node of the tree.
 * @param name The name of the object to search for.
 * @return The first object with the matching name, or null if no object is found.
 */
@JsExport
fun findObjectByName(root: ZObject, name: String): ZObject? {
    return findInTree(root) {it.name == name}
}

/**
 * Finds the first [ZModel] in a given [ZObject] tree starting at [root].
 *
 * @param root The root [ZObject] from which to start the search.
 * @return The first [ZModel] object found within the tree, or null if no [ZModel] object is found.
 */
@JsExport
fun findFirstModel(root: ZObject): ZModel? {
    return findInTree(root) {it.type == ZObjectType.MODEL} as ZModel?
}

/**
 * Finds the first [ZCamera] in a given [ZObject] tree starting at [root].
 *
 * @param root The root [ZObject] from which to start the search.
 * @return The first [ZCamera] object found within the tree, or null if no [ZCamera] object is found.
 */
@JsExport
fun findFirstCamera(root: ZObject): ZCamera? {
    return findInTree(root) {it.type == ZObjectType.CAMERA} as ZCamera?
}