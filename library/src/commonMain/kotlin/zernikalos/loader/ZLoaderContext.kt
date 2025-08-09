/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import zernikalos.components.ZRef

/**
 * This class is used to store the components loaded by the engine.
 */
class ZLoaderContext {

    private val componentsMap: HashMap<String, ZRef> = hashMapOf()

    /**
     * Returns true if the component with the given refId has been loaded.
     */
    fun hasComponent(refId: String): Boolean {
        return componentsMap.containsKey(refId)
    }

    /**
     * Adds a component to the store with the given refId.
     *
     * @param refId The refId of the component to add.
     * @param component The component to add. If it is null, the function does nothing.
     */
    fun addComponent(refId: String, component: ZRef?) {
        if (component == null) {
            return
        }
        componentsMap[refId] = component
    }

    /**
     * Retrieves a component from the store with the given refId.
     *
     * @param refId The refId of the component to retrieve.
     * @return The component with the given refId, or null if not found.
     * @throws Error If the component with the given refId is not found.
     */
    fun getComponent(refId: String): ZRef? {
        if (!componentsMap.containsKey(refId)) {
            throw Error("Unable to find the required component with refId: $refId")
        }
        return componentsMap[refId]
    }
}