/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

import zernikalos.objects.ZCamera
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject
import zernikalos.objects.ZObjectType

/**
 * A search index for ZObjects, which allows efficient retrieval based on name and type.
 *
 * @param obj The ZObject to initialize the search index with.
 */
class ZSearchIndex(val obj: ZObject) {

    /**
     * Finds a ZObject in the search index by its name.
     *
     * @param name The name of the ZObject to search for.
     * @return The ZObject with the specified name, or null if not found.
     */
    fun findByName(name: String): ZObject? {
        return findInTree(obj) { obj.name == name }
    }

    /**
     * Finds the first ZModel in the hierarchy.
     *
     * @return The first ZModel found, or null if no ZModel is present in the hierarchy.
     */
    fun findFirstModel(): ZModel? {
        return findInTree(obj) {obj.type == ZObjectType.MODEL} as ZModel?
    }

    /**
     * Finds the first ZCamera in the hierarchy.
     *
     * @return The first ZCamera found, or null if no ZCamera is present in the hierarchy.
     */
    fun findFirstCamera(): ZCamera? {
        return findInTree(obj) {obj.type == ZObjectType.CAMERA} as ZCamera?
    }

}