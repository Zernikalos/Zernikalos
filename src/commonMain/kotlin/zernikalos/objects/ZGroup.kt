/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import kotlin.js.JsExport

@JsExport
@Serializable
@SerialName("Group")
class ZGroup: ZObject() {

    override val type = ZObjectType.GROUP

    override fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
    }

    override fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext) {

    }
}
