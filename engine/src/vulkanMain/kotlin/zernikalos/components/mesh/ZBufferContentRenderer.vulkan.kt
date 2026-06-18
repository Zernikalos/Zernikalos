/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.mesh

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZBufferContentRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZBufferContentData,
): ZComponentRenderer(ctx) {
    val bytes: ByteArray
        get() = data.dataArray

    actual override fun initialize() {
    }

    actual override fun bind() {
    }

    actual override fun unbind() {
    }

    actual override fun dispose() {
    }
}
