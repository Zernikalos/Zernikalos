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

    actual fun update() {
        ctx.scene?.update(ctx)
    }

    actual fun render() {
        ctx.scene?.render(ctx)
    }

    actual override fun onViewportResize(width: Int, height: Int) {
        ctx.scene?.onViewportResize(ctx, width, height)
    }
}
