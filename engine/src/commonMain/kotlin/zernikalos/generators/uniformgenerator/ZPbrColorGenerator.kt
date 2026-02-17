/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.math.ZVoidAlgebraObject
import zernikalos.objects.ZModel

val ZPbrColorGenerator: ZUniformGenerator = { sceneContext, obj ->
    obj as ZModel
    val material = obj.material
    if (material?.usesPbr == true) {
        material.pbr?.color ?: ZVoidAlgebraObject()
    } else {
        ZVoidAlgebraObject()
    }
}
