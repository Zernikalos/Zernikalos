/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components

import zernikalos.context.*

actual class ZViewportRenderer actual constructor(ctx: ZRenderingContext, private val data: ZViewportData): ZComponentRenderer(ctx) {
    actual override fun initialize() {
        ctx as ZWebGPURenderingContext
        // WebGPU viewport initialization
        // Note: WebGPU handles depth testing differently from OpenGL
        // It's configured in the render pipeline
    }

    actual override fun render() {
        ctx as ZWebGPURenderingContext
        // Clear color is handled through the render pass in WebGPU
        // This will be implemented when setting up the render pass
    }

    actual fun onViewportResize(width: Int, height: Int) {
        ctx as ZWebGPURenderingContext
        // WebGPU viewport resizing
        // This will be handled through the render pipeline configuration
        // and swapchain recreation
    }
}
