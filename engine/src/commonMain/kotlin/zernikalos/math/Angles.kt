/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlin.math.PI

/** Converts [degrees] to radians. */
fun degreesToRadians(degrees: Float): Float = degrees * (PI.toFloat() / 180f)

/** Converts [radians] to degrees. */
fun radiansToDegrees(radians: Float): Float = radians * (180f / PI.toFloat())
