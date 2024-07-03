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
    override fun bind() {
    }

    override fun unbind() {
    }

    override fun render() {
        ctx.scene?.render(ctx)
    }
}