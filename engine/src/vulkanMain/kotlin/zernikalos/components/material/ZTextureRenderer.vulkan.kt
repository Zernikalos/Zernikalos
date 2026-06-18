/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import zernikalos.components.ZComponentRenderer
import zernikalos.context.ZRenderingContext

actual class ZTextureRenderer actual constructor(
    ctx: ZRenderingContext,
    private val data: ZTextureData,
): ZComponentRenderer(ctx) {
    actual override fun initialize() {
    }

    actual override fun render() {
    }

    actual override fun dispose() {
    }
}
