/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.utils

@JsExport
data class MapPairJs(val key: Any, val value: Any)

@JsExport
fun mapFlatJs(m: Map<*, *>): Array<MapPairJs> {
    val l: ArrayList<MapPairJs> = arrayListOf()
    m.entries.forEach {
        val pair = MapPairJs(it.key!!, it.value!!)
        l.add(pair)
    }
    return l.toTypedArray()
}