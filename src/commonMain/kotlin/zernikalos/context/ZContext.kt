/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import zernikalos.utils.genRefId
import kotlin.js.JsExport

// TODO: sceneContext and renderingContext must be internal
@JsExport
class ZContext(val sceneContext: ZSceneContext, val renderingContext: ZRenderingContext) {

    val refId: String = genRefId()

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    var scene: ZScene? by sceneContext::scene

    var activeCamera: ZCamera? by sceneContext::activeCamera

    val isInitialized: Boolean by sceneContext::isInitialized
}
