/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.ui.ZSurfaceView

class ZDefaultContextCreator: ZContextCreator {

    override fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext {
        return createDefaultSceneContext()
    }

    override fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext {
        return ZGLRenderingContext(surfaceView)
    }

}

fun createDefaultContextCreator(): ZContextCreator {
    return ZDefaultContextCreator()
}