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
import zernikalos.components.*
import zernikalos.loader.ZLoaderContext
import zernikalos.math.ZTransform
import zernikalos.search.findInTree
import zernikalos.search.treeAsList
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ZSkeleton internal constructor(data: ZSkeletonData): ZComponentTemplate<ZSkeletonData>(data) {

    @JsName("init")
    constructor(): this(ZSkeletonData())

    var root: ZBone by data::root

    val bones: Array<ZBone>
        get() = treeAsList(root).toTypedArray()

    val transform: ZTransform by root::transform

    fun findBoneByName(name: String): ZBone? {
        return findInTree(root) { bone: ZBone -> bone.name == name }
    }

}

@Serializable
data class ZSkeletonDataWrapper(
    @ProtoNumber(1)
    override var refId: Int,
    @ProtoNumber(2)
    override var isReference: Boolean,
    @ProtoNumber(100)
    override var data: ZSkeletonData? = null
): ZRefComponentWrapper<ZSkeletonData>

@Serializable
data class ZSkeletonData(
    @ProtoNumber(104)
    var root: ZBone = ZBone()
): ZComponentData()

class ZSkeletonSerializer(loaderContext: ZLoaderContext): ZRefComponentSerializer<ZSkeleton, ZSkeletonData, ZSkeletonDataWrapper>(loaderContext) {
    override val deserializationStrategy: DeserializationStrategy<ZSkeletonDataWrapper>
        get() = ZSkeletonDataWrapper.serializer()

    override fun createComponentInstance(data: ZSkeletonData): ZSkeleton {
        return ZSkeleton(data)
    }

}