/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.renderer

import zernikalos.context.ZContext

actual class ZRenderer actual constructor(ctx: ZContext) : ZRendererBase(ctx) {
    actual fun bind() {
    }

    actual fun unbind() {
    }

    actual fun render() {
        renderFrame()
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.onViewportResize(ctx, width, height)
    }

    actual fun dispose() {
        // No persistent encoder/context refs to clear; OpenGL state is per-frame.
    }
}
