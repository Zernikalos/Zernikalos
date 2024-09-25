/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events

import zernikalos.math.ZVector2
import kotlin.js.JsExport

@JsExport
interface ZEvent

enum class ZTouchState {
    NONE,
    START,
    MOVE,
    END
}


class ZTouchEvent(
    val position: ZVector2 = ZVector2.Zero,
    val state: ZTouchState = ZTouchState.START,
    val deltaPosition: ZVector2 = ZVector2.Zero,
): ZEvent