/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import zernikalos.context.ZRenderingContext

actual class ZModelRenderer actual constructor(
    val ctx: ZRenderingContext,
    val model: ZModel
) {
    actual fun initialize() {
    }

    actual fun render() {
        model.shaderProgram.bind()
        model.material?.bind()

        model.mesh.bind()
        model.mesh.render()
        model.mesh.unbind()

        model.material?.unbind()
        model.shaderProgram.unbind()
    }

}
