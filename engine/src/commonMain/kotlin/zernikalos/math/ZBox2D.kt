/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Represents a 2D box with top, left, width, and height properties.
 *
 * @property top The top coordinate of the box.
 * @property left The left coordinate of the box.
 * @property width The width of the box.
 * @property height The height of the box.
 */
@Serializable
@JsExport
class ZBox2D(var top: Int = 0, var left: Int = 0, var width: Int = 0, var height: Int = 0)