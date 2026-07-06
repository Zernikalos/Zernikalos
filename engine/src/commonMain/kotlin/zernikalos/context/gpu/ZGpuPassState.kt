/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context.gpu

import zernikalos.context.ZCullMode
import zernikalos.context.ZDepthCompare
import zernikalos.context.ZFrontFace

data class ZGpuPassState(
    val cullMode: ZCullMode = ZCullMode.None,
    val frontFace: ZFrontFace = ZFrontFace.CW,
    val depthTest: Boolean = true,
    val depthWrite: Boolean = true,
    val depthCompare: ZDepthCompare = ZDepthCompare.Less,
)
