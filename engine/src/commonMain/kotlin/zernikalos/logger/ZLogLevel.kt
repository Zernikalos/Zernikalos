/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.logger

import kotlin.js.JsExport

@JsExport
enum class ZLogLevel(private val priority: Int): Comparator<ZLogLevel> {
    DEBUG(0),
    INFO(1),
    WARNING(2),
    ERROR(3);

    // positive if a > b
    // negative if a < b
    override fun compare(a: ZLogLevel, b: ZLogLevel): Int {
        return a.priority - b.priority
    }

}