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

    @ProtoNumber(100) val scene: ZScene?,
    @ProtoNumber(101) val group: ZGroup?,
    @ProtoNumber(102) val model: ZModel?,
    @ProtoNumber(103) val camera: ZCamera?,
    @ProtoNumber(104) val skeleton: ZSkeleton?,
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