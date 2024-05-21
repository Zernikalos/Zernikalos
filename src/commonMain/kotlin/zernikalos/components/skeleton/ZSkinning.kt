/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.skeleton

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.ZBasicComponent
import zernikalos.components.ZComponentData
import zernikalos.components.ZComponentSerializer
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZSkinningSerializer::class)
class ZSkinning internal constructor(data: ZSkinningData): ZBasicComponent<ZSkinningData>(data) {

    @JsName("init")
    constructor(): this(ZSkinningData())

    var boneIndices: Array<Int> by data::boneIndices

}

@Serializable
data class ZSkinningData(
    @ProtoNumber(1)
    var boneIndices: Array<Int> = emptyArray()
): ZComponentData()

class ZSkinningSerializer: ZComponentSerializer<ZSkinning, ZSkinningData>() {
    override val deserializationStrategy: DeserializationStrategy<ZSkinningData>
        get() = ZSkinningData.serializer()

    override fun createComponentInstance(data: ZSkinningData): ZSkinning {
        return ZSkinning(data)
    }

}