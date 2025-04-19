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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.action.ZSkeletalAction
import zernikalos.components.material.ZTexture
import zernikalos.utils.ZSemVer
import kotlin.js.JsExport

/** Current ZKO format version */
const val ZKO_VERSION = "0.8.0"

/** Current ZKO format version */
val ZkoVersion = ZSemVer.parse(ZKO_VERSION)

@JsExport
@Serializable
data class ZkoHeader(
    @ProtoNumber(1) val version: String
) {

    val currentVersion = ZSemVer.parse(version)

    init {
        require(ZkoVersion.isCompatibleWith(currentVersion)) {
            throw SerializationException("Wrong ZKO file version, " +
                "expecting ${ZkoVersion.toString()} compatible version, " +
                "got ${currentVersion.toString()}")
        }

    }
}


/**
 * Zernikalos KObject file format.
 *
 * This format holds a hierarchy of objects, which can be of any type,
 * including meshes, groups, skeletons, and more.
 *
 * @param header the Zernikalos KObject file format version
 * @param textures all the textures used by the objects in the file
 * @param objects the objects in the file, which can be of any type
 * @param hierarchy the hierarchy of the objects in the file
 * @param actions any actions stored in the file
 */
@Serializable
data class ZkoFormat(
    @ProtoNumber(1) val header: ZkoHeader,
    @ProtoNumber(2) val textures: List<@Contextual ZTexture>,
    @ProtoNumber(3) val objects: List<@Contextual ZkoObjectProto>,
    @ProtoNumber(4) val hierarchy: ZkoHierarchyNode,
    @ProtoNumber(5) val actions: List<ZSkeletalAction>? = null
)
