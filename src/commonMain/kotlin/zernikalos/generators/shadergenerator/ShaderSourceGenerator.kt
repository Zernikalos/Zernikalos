/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

import zernikalos.components.shader.ZShaderSource
import zernikalos.logger.ZLoggable

class ZAttributesEnabler {
    var usePosition: Boolean = true
    var useNormals: Boolean = false
    var useColors: Boolean = false
    var useTextures: Boolean = false
    var useSkinning: Boolean = false
}

expect class ZShaderSourceGenerator(): ZLoggable {

    fun buildShaderSource(enabler: ZAttributesEnabler, source: ZShaderSource)

}
