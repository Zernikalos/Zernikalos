/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.math.ZColor

enum class ZCullMode {
    None,
    Front,
    Back,
}

enum class ZFrontFace {
    CCW,
    CW,
}

enum class ZDepthCompare {
    Less,
    LessEqual,
    Greater,
    Always,
}

enum class ZLoadOp {
    Load,
    Clear,
}

enum class ZStoreOp {
    Store,
    Discard,
}

data class ZGpuColorAttachmentDesc(
    val loadOp: ZLoadOp = ZLoadOp.Clear,
    val storeOp: ZStoreOp = ZStoreOp.Store,
    val clearValue: ZColor,
)

data class ZGpuDepthStencilAttachmentDesc(
    val depthLoadOp: ZLoadOp = ZLoadOp.Clear,
    val depthStoreOp: ZStoreOp = ZStoreOp.Store,
    val depthClearValue: Float = 1.0f,
)

data class ZGpuRenderPassDescriptor(
    val label: String? = null,
    val colorAttachments: List<ZGpuColorAttachmentDesc>,
    val depthStencilAttachment: ZGpuDepthStencilAttachmentDesc? = null,
)
