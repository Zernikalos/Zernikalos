/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

internal data class ZVulkanVertexAttributeDescriptor(
    val location: Int,
    val binding: Int,
    val format: Int,
    val offset: Int,
    val stride: Int,
)

internal data class ZVulkanUniformBindingDescriptor(
    val engineBinding: Int,
    val descriptorBinding: Int,
)

internal fun ZAttributeId.vulkanLocation(): Int = when (this) {
    ZAttributeId.POSITION -> 0
    ZAttributeId.NORMAL -> 1
    ZAttributeId.COLOR -> 2
    ZAttributeId.UV -> 3
    ZAttributeId.BONE_WEIGHT -> 4
    ZAttributeId.BONE_INDEX -> 5
    ZAttributeId.INDICES,
    ZAttributeId.CUSTOM -> -1
}
