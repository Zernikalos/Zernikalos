/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.*

@Serializable
data class ZkoObjectProto(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val refId: String,

    @ProtoNumber(3) val scene: ZScene?,
    @ProtoNumber(4) val group: ZGroup?,
    @ProtoNumber(5) val model: ZModel?,
    @ProtoNumber(6) val camera: ZCamera?,
    @ProtoNumber(7) val skeleton: ZSkeleton?,
    @ProtoNumber(100) val children: Array<ZkoObjectProto>? = emptyArray()
) {
    val zObject: ZObject
        get() {
            val obj = detectZObject()
            return obj
        }

    private fun detectZObject(): ZObject {
        when (type) {
            ZObjectType.SCENE.name -> return scene!!
            ZObjectType.GROUP.name -> return group!!
            ZObjectType.MODEL.name -> return model!!
            ZObjectType.CAMERA.name -> return camera!!
            ZObjectType.SKELETON.name -> return skeleton!!
        }
        throw Error("Type has not been found on object")
    }
}