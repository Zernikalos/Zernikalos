/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

/**
 * ZAttributesEnabler is a configuration class that specifies which attributes and settings
 * should be enabled in a shader program. This includes options for enabling vertex positions,
 * normals, colors, textures, skinning, and flipping the Y coordinate of textures.
 *
 * @property usePosition Indicates whether vertex positions should be included in the shader attributes.
 * @property useNormals Indicates whether normals should be included in the shader attributes.
 * @property useColors Indicates whether colors should be included in the shader attributes.
 * @property useTextures Indicates whether textures should be included in the shader attributes.
 * @property useSkinning Indicates whether skinning attributes should be included in the shader,
 *                       allowing for skeletal animation.
 * @property flipTextureY Indicates whether the Y axis of textures should be flipped, potentially
 *                        required for different texture coordinate conventions.
 * @property maxBones Specifies the maximum number of bones supported for skinning; this determines the
 *                    size of the bone matrix arrays in uniforms.
 */
internal class ZAttributesEnabler {
    var usePosition: Boolean = true
    var useNormals: Boolean = false
    var useColors: Boolean = false
    var useTextures: Boolean = false
    var useSkinning: Boolean = false
    var flipTextureY: Boolean = false

    var maxBones: Int = 0
}