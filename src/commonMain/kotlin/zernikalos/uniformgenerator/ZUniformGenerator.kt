/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.uniformgenerator

import zernikalos.context.ZSceneContext
import zernikalos.math.ZAlgebraObject
import zernikalos.objects.ZObject
import kotlin.js.JsExport

@JsExport
interface ZUniformGenerator {

    fun compute(sceneContext: ZSceneContext, obj: ZObject): ZAlgebraObject

}
