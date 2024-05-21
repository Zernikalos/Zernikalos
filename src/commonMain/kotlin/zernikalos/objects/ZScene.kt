/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import zernikalos.components.ZViewport
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import kotlin.js.JsExport

@JsExport
@Serializable
class ZScene: ZObject() {

    override val type = ZObjectType.SCENE

    @Transient
    var viewport: ZViewport = ZViewport()

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        viewport.initialize(ctx)
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        viewport.render()
    }
}
