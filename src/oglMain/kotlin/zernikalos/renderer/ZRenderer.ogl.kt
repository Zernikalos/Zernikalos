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
    actual override fun bind() {
    }

    actual override fun unbind() {
    }

    actual override fun render() {
        ctx.scene?.render(ctx)
    }

    actual override fun resize(width: Int, height: Int) {
        ctx.scene?.onScreenResize(ctx, width, height)
    }
}