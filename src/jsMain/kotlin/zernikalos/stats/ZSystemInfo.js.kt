/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.stats

import kotlinx.browser.window

@JsModule("ua-parser-js")
external interface BrowserInfo {
    val name: String
    val version: String
}

@JsModule("ua-parser-js")
@JsNonModule
external class UAParser(userAgent: String) {
    fun getBrowser(): BrowserInfo
}

actual fun getZOsInfo(): ZOsInfo {
    val parser = UAParser(window.navigator.userAgent)
    return ZOsInfo(
        os = ZOperativeSystem.WEB,
        osVersion = parser.getBrowser().version
    )
}
