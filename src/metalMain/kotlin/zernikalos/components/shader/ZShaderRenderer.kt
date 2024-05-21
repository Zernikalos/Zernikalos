/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import platform.Foundation.NSError
import platform.Metal.MTLCompileOptions
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLLibraryProtocol
import zernikalos.context.ZRenderingContext
import zernikalos.components.ZComponentRender
import zernikalos.context.ZMtlRenderingContext

actual class ZShaderRenderer actual constructor(ctx: ZRenderingContext, data: ZShaderData) : ZComponentRender<ZShaderData>(ctx, data) {

    lateinit var library: MTLLibraryProtocol
    lateinit var vertexShader: MTLFunctionProtocol
    lateinit var fragmentShader: MTLFunctionProtocol

    actual override fun initialize() {

    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun initialize(source: ZShaderSource) {
        ctx as ZMtlRenderingContext
        val err: CPointer<ObjCObjectVar<NSError?>>? = null

        library = ctx.device.newLibraryWithSource(source.metalShaderSource, MTLCompileOptions(), err)!!

        vertexShader = library.newFunctionWithName("vertexShader")!!
        fragmentShader = library.newFunctionWithName("fragmentShader")!!
    }

}