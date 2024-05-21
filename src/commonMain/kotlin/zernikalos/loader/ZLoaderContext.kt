/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import zernikalos.components.ZComponent

class ZLoaderContext {

    private val componentsMap: HashMap<Int, ZComponent<*, *>> = hashMapOf()

    fun addComponent(refId: Int, component: ZComponent<*, *>?) {
        if (component == null) {
            return
        }
        componentsMap[refId] = component
    }

    fun getComponent(refId: Int): ZComponent<*, *>? {
        if (!componentsMap.containsKey(refId)) {
            throw Error("Unable to find the required component with refId: $refId")
        }
        return componentsMap[refId]
    }
}