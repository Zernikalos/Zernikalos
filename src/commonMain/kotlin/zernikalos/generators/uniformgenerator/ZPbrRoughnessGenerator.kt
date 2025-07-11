/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.math.ZScalar
import zernikalos.math.ZVoidAlgebraObject
import zernikalos.objects.ZModel
import zernikalos.objects.ZObject

class ZPbrRoughnessGenerator: ZUniformGenerator {
    override fun compute(
        sceneContext: ZSceneContext,
        obj: ZObject
    ): ZAlgebraObject {
        val model = obj as? ZModel ?: return ZVoidAlgebraObject()
        val material = model.material?.takeIf { it.usesPbr } ?: return ZVoidAlgebraObject()

        return material.pbr?.roughness?.let { ZScalar(it) } ?: ZVoidAlgebraObject()
    }

}
