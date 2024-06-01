/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.stats

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ZOperativeSystem {
    ANDROID,
    IOS,
    WEB
}

@Serializable
@JsExport
data class ZOsInfo(
    val os: ZOperativeSystem,
    val osVersion: String
)

@Serializable
@JsExport
class ZStats {

    // This need to be split in order to make it work with JSON encoding correctly
    val osInfo: ZOsInfo

    init {
        osInfo = getZOsInfo()
    }

}

expect fun getZOsInfo(): ZOsInfo
