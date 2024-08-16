/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.stats

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import zernikalos.Version
import kotlin.js.JsExport

@JsExport
@Serializable
enum class ZPlatformName {
    ANDROID,
    IOS,
    WEB,
    LINUX
}

@JsExport
@Serializable
data class ZPlatformInfo(
    val name: ZPlatformName,
    val version: String
)

@Serializable
@JsExport
class ZStats {

    // This need to be split in order to make it work with JSON encoding correctly
    val platform: ZPlatformInfo

    val version: String
    val zkoVersion: String

    init {
        platform = getZPlatformInfo()
        version = Version.VERSION
        zkoVersion = Version.ZKO_VERSION
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return toJson()
    }

}

expect fun getZPlatformInfo(): ZPlatformInfo
