/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.stats

import kotlinx.browser.window

/**
 * Extracts browser version from user agent string using regex.
 * Supports Chrome, Edge, Firefox, Safari, and Opera.
 * Order matters: Edge must be checked before Chrome because Edge UA contains "Chrome".
 */
private fun extractBrowserVersion(userAgent: String): String {
    // Edge (Chromium) - check before Chrome because it contains both Edg and Chrome
    val edgeMatch = Regex("Edg/([0-9.]+)").find(userAgent)
    if (edgeMatch != null) return edgeMatch.groupValues[1]

    // Opera - check before Chrome because it contains OPR and Chrome
    val operaMatch = Regex("OPR/([0-9.]+)").find(userAgent)
    if (operaMatch != null) return operaMatch.groupValues[1]

    // Chrome
    val chromeMatch = Regex("Chrome/([0-9.]+)").find(userAgent)
    if (chromeMatch != null) return chromeMatch.groupValues[1]

    // Firefox
    val firefoxMatch = Regex("Firefox/([0-9.]+)").find(userAgent)
    if (firefoxMatch != null) return firefoxMatch.groupValues[1]

    // Safari - uses Version/ instead of Safari/
    val safariMatch = Regex("Version/([0-9.]+).*Safari/").find(userAgent)
    if (safariMatch != null) return safariMatch.groupValues[1]

    return "unknown"
}

actual fun getZPlatformInfo(): ZPlatformInfo {
    val userAgent = window.navigator.userAgent
    return ZPlatformInfo(
        name = ZPlatformName.WEB,
        version = extractBrowserVersion(userAgent)
    )
}
