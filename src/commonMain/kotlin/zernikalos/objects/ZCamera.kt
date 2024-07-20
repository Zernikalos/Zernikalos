/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.camera.ZPerspectiveLens
import zernikalos.context.ZContext
import zernikalos.math.ZMatrix4
import zernikalos.math.ZVector3
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
class ZCamera: ZObject {

    override val type = ZObjectType.CAMERA

    @ProtoNumber(4)
    var lens: ZPerspectiveLens

    val projectionMatrix: ZMatrix4
        get() = lens.projectionMatrix

    val viewMatrix: ZMatrix4
        get() = transform.matrix

    val viewProjectionMatrix: ZMatrix4
        get() = projectionMatrix * viewMatrix

    @JsName("initWithLens")
    constructor(lookAt: ZVector3, up: ZVector3, lens: ZPerspectiveLens) {
        this.transform.lookAt(lookAt, up)
        this.lens = lens
    }

    @JsName("initWithLookUp")
    constructor(lookAt: ZVector3, up: ZVector3) {
        this.transform.lookAt(lookAt, up)
        this.lens = ZPerspectiveLens.Default
    }

    @JsName("init")
    constructor() {
        this.transform.lookAt(ZVector3.Zero, ZVector3.Up)
        this.lens = ZPerspectiveLens.Default
    }

    override fun internalInitialize(ctx: ZContext) {
    }

    override fun internalRender(ctx: ZContext) {
    }

    override fun internalResize(ctx: ZContext, width: Int, height: Int) {
        lens.setDimensions(width, height)
    }

    companion object {
        val DefaultPerspectiveCamera: ZCamera
            get() = ZCamera(ZVector3.Zero, ZVector3.Up)
    }

}
