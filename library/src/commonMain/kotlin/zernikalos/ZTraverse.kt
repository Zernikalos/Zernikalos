/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos

import zernikalos.objects.ZObject

/**
 * Returns an iterator over the tree nodes from the parameter [obj] to the root node.
 * The first node returned from the iterator will be the parameter [obj] itself.
 * @param obj some node in the tree.
 */
fun upToRoot(obj: ZObject): Iterator<ZObject> {
    var current: ZObject = obj
    return object : Iterator<ZObject> {
        override fun hasNext(): Boolean {
            return current.hasParent
        }

        override fun next(): ZObject {
            val nextValue = current
            current = nextValue.parent!!
            return nextValue
        }

    }
}
