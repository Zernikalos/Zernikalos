/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

expect class ZGpuFrame {
    fun begin(): Boolean
    fun beginRecording(): ZGpuCommandEncoder?
    fun submit(encoder: ZGpuCommandEncoder)
    fun end()
}
