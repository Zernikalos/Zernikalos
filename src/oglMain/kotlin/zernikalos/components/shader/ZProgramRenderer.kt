/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.components.*
import zernikalos.context.GLWrap
import zernikalos.context.ZGLRenderingContext
import zernikalos.context.ZRenderingContext
import kotlin.js.JsName

class ZProgram(): ZRenderizableComponent<ZProgramRenderer>(), ZBindeable {

    override fun createRenderer(ctx: ZRenderingContext): ZProgramRenderer {
        return ZProgramRenderer(ctx)
    }

    override fun bind() {
        renderer.bind()
    }

    override fun unbind() {
    }

    fun link() {
        renderer.link()
    }

}

class ZProgramRenderer(ctx: ZRenderingContext): ZBaseComponentRender(ctx) {

    lateinit var programId: GLWrap

    override fun initialize() {
        ctx as ZGLRenderingContext

        val p = ctx.createProgram()
        // TODO
        /* if (program <= 0) {
            val err = context.getError()
            throw Error("Unable to create program ${err}")
        } */
        programId = p
    }

    override fun bind() {
        ctx as ZGLRenderingContext

        ctx.useProgram(programId)
    }

    fun link() {
        ctx as ZGLRenderingContext

        ctx.linkProgram(programId)
    }

}