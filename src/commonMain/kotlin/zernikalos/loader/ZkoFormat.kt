/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.action.ZSkeletalAction
import kotlin.js.JsExport

const val ZKO_VERSION = "0.4.0"

@JsExport
@Serializable
data class ZkoHeader(
    @ProtoNumber(1) val version: String
) {
    init {
        require(version == ZKO_VERSION) {
            throw SerializationException("Wrong ZKO file version, expecting $ZKO_VERSION, got $version")
        }

    }
}

@Serializable
data class ZkoFormat(
    @ProtoNumber(1) val header: ZkoHeader,
    @ProtoNumber(3) val objects: Map<String, ZkoObjectProto>,
    @ProtoNumber(4) val hierarchy: ZkoHierarchyNode,
    @ProtoNumber(5) val actions: List<ZSkeletalAction>? = null
)
