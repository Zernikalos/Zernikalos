/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.renderer

import zernikalos.components.ZResizable
import zernikalos.context.ZContext
import zernikalos.logger.ZLoggable

abstract class ZRendererBase(protected val ctx: ZContext): ZLoggable, ZResizable {

    open fun initialize() {
        val scene = ctx.sceneContext.scene
        scene?.initialize(ctx)
    }

    open fun update() {
        // Process all accumulated input events synchronously with the frame
        if (!ctx.eventQueue.isEmpty) {
            ctx.eventQueue.processAll()
        }
    }

}

expect class ZRenderer(ctx: ZContext): ZRendererBase {
    fun bind()
    fun unbind()
    fun update()
    fun render()
    override fun onViewportResize(width: Int, height: Int)
}

