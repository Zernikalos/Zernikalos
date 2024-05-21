/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.*

@Serializable
data class ProtoZkObject(
    @ProtoNumber(1) val type: String,
    @ProtoNumber(2) val scene: ZScene?,
    @ProtoNumber(3) val group: ZGroup?,
    @Contextual @ProtoNumber(4) val model: ZModel?,
    @ProtoNumber(5) val camera: ZCamera?,
    @ProtoNumber(6) val skeleton: ZSkeleton?,
    @ProtoNumber(100) val children: Array<ProtoZkObject>? = emptyArray()
) {
    val zObject: ZObject
        get() {
            val obj = detectZObject()
            fillChildren(obj)
            return obj
        }

    private fun fillChildren(obj: ZObject) {
        children?.forEach { child ->
            obj.addChild(child.zObject)
        }
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