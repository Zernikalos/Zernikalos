/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.objects

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.context.ZRenderingContext
import zernikalos.context.ZSceneContext
import zernikalos.math.ZTransform
import zernikalos.math.ZVector3
import zernikalos.utils.randomId
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
@Polymorphic
abstract class ZObject {
    @ProtoNumber(1)
    val id: String = randomId()

    @ProtoNumber(2)
    private var _name: String = ""

    var name: String
        get() {
            if (_name == "") {
                return "${type.name.lowercase()}_$id"
            }
            return _name
        }
        set(value) {
            _name = value
        }

    @ProtoNumber(3)
    var transform: ZTransform = ZTransform()

    @JsName("children")
    @Transient
    var children: Array<@Polymorphic ZObject> = emptyArray()

    abstract val type: ZObjectType

    @Transient
    private var _parent: ZObject? = null

    val parent: ZObject?
        get() = _parent

    val hasParent: Boolean
        get() = _parent != null

    val isRoot: Boolean
        get() = !hasParent

    private var _initialized: Boolean = false
    val isInitialized: Boolean
        get() = _initialized

    // TODO: Ugly hack for assigning parent after load from exporter
    init {
        for (child in children) {
            assignThisParent(child)
        }
    }

    fun initialize(sceneContext: ZSceneContext, renderingContext: ZRenderingContext) {
        internalInitialize(sceneContext, renderingContext)
        children.forEach { child ->
            child.initialize(sceneContext, renderingContext) }
        _initialized = true
    }

    fun render(sceneContext: ZSceneContext, ctx: ZRenderingContext) {
        internalRender(sceneContext, ctx)
        children.forEach { child -> child.render(sceneContext, ctx) }
    }

    fun addChild(child: ZObject) {
        children += child
        assignThisParent(child)
    }

    private fun assignThisParent(obj: ZObject) {
        obj._parent = this
    }

    fun lookAt(look: ZVector3) {
        lookAt(look, ZVector3.Up)
    }

    @JsName("lookAtWithUp")
    fun lookAt(look: ZVector3, up: ZVector3) {
        transform.lookAt(look, up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        transform.translate(x, y, z)
    }

    protected abstract fun internalInitialize(sceneContext: ZSceneContext, ctx: ZRenderingContext)

    protected abstract fun internalRender(sceneContext: ZSceneContext, ctx: ZRenderingContext)

}
