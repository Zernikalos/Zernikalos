/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import zernikalos.context.ZContext
import kotlin.js.JsExport

@JsExport
@Serializable
class ZLight: ZObject() {

    override val type: ZObjectType = ZObjectType.LIGHT

    override fun internalInitialize(ctx: ZContext) {
    }

    override fun internalRender(ctx: ZContext) {
    }
}
