/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.ZRenderingContext

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, private val data: ZViewportData) : ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun render() {
    }

    actual fun onViewportResize(width: Int, height: Int) {
    }

}