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
import zernikalos.components.ZRef
import zernikalos.context.ZContext
import zernikalos.logger.ZLoggable
import zernikalos.math.ZTransform
import zernikalos.math.ZVector3
import zernikalos.search.ZTreeNode
import zernikalos.utils.genRefId
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a generic object within the Zernikalos engine. This abstract class serves as the base for all objects
 * within the engine, providing common properties and functions that are essential for the engine's operation.
 *
 * @property id A unique identifier for the object, automatically generated to ensure uniqueness.
 * @property name The name of the object. If not explicitly set, a default name is generated based on the object's type and ID.
 * @property transform An instance of [ZTransform] representing the object's position, rotation, and scale in the 3D space.
 * @property children An array of [ZObject]s that are children of this object, allowing for a hierarchical structure.
 * @property parent A reference to the parent [ZObject], if any. Null if the object has no parent, making it a root object.
 * @property hasParent A boolean indicating whether this object has a parent object.
 * @property isRoot A boolean indicating whether this object is a root object (i.e., has no parent).
 * @property isInitialized A boolean indicating whether the object has been initialized. This is set to true after the first call to [initialize].
 *
 * The class provides several functions for object manipulation and lifecycle management, including:
 * - [initialize]: Initializes the object and its children, preparing them for rendering. This function should be called before the object is rendered for the first time.
 * - [render]: Renders the object and its children. This function is responsible for drawing the object on the screen.
 * - [addChild]: Adds a child object to this object, establishing a parent-child relationship.
 * - [lookAt]: Adjusts the object's orientation so that it "looks at" a specified point in space.
 * - [translate]: Moves the object by a specified amount along each axis.
 *
 * Abstract functions [internalInitialize] and [internalRender] must be implemented by subclasses to define specific initialization and rendering behaviors.
 *
 */
@JsExport
@Serializable
@Polymorphic
abstract class ZObject: ZRef, ZTreeNode<ZObject>, ZLoggable {
    @ProtoNumber(1)
    override val refId: String = genRefId()

    @ProtoNumber(2)
    private var _name: String = ""

    var name: String
        get() {
            if (_name == "") {
                return "${type.name.lowercase()}_${refId.substring(0, 6)}"
            }
            return _name
        }
        set(value) {
            _name = value
        }

    @ProtoNumber(3)
    var transform: ZTransform = ZTransform()

    @Transient
    override var children: Array<@Polymorphic ZObject>
        get() = _children.toTypedArray()
        set(value) {
            _children = arrayListOf(*value)
        }

    private var _children: ArrayList<ZObject> = arrayListOf()

    abstract val type: ZObjectType

    @Transient
    private var _parent: ZObject? = null

    override val parent: ZObject?
        get() = _parent

    private var _initialized: Boolean = false
    val isInitialized: Boolean
        get() = _initialized

    /**
     * Initializes the object and its children, preparing them for rendering. This function should be called before the object is rendered for the first time.
     * It sets the object's state to initialized and recursively initializes all child objects.
     *
     * @param ctx The context of the current scene, providing necessary information and services for initialization.
     */
    fun initialize(ctx: ZContext) {
        internalInitialize(ctx)
        children.forEach { child ->
            child.initialize(ctx) }
        _initialized = true
    }

    /**
     * Event handler for the viewport resizing.
     * This method should not be called by the user
     *
     * @param ctx The context of the current scene, providing necessary information and services for resizing.
     * @param width The new width of the viewport.
     * @param height The new height of the viewport.
     */
    fun onViewportResize(ctx: ZContext, width: Int, height: Int) {
        internalOnViewportResize(ctx, width, height)
        children.forEach { child ->
            child.onViewportResize(ctx, width, height)
        }
    }

    /**
     * Renders the object and its children to the screen. This function is responsible for drawing the object on the screen.
     * It should be called every frame to update the object's appearance based on its current state and transformations.
     *
     * @param ctx The context of the current scene, providing necessary information and services for rendering.
     */
    fun render(ctx: ZContext) {
        internalRender(ctx)
        children.forEach { child -> child.render(ctx) }
    }

    /**
     * Adds a child object to this object, establishing a parent-child relationship. The child object will be rendered relative to this object's transform.
     *
     * @param child The child object to add.
     */
    fun addChild(child: ZObject) {
        _children += child
        reparent(child)
    }

    fun removeChild(child: ZObject) {
        _children.remove(child)
        child._parent = null
    }

    @JsName("removeChildAt")
    fun removeChild(idx: Int) {
        val removed = _children.removeAt(idx)
        removed._parent = null
    }

    private fun reparent(obj: ZObject) {
        obj._parent = this
    }

    /**
     * Adjusts the object's orientation so that it "looks at" a specified point in space.
     *
     * @param look The point in space to look at.
     */
    fun lookAt(look: ZVector3) {
        lookAt(look, ZVector3.Up)
    }

    /**
     * Adjusts the object's orientation so that it "looks at" a specified point in space, with a specified up direction. This changes the object's rotation to face the point, while aligning its up direction with the specified up vector.
     *
     * @param look The point in space to look at.
     * @param up The up direction vector to align with.
     */
    @JsName("lookAtWithUp")
    fun lookAt(look: ZVector3, up: ZVector3) {
        transform.lookAt(look, up)
    }

    /**
     * Moves the object by a specified amount along each axis. This changes the object's position based on the given x, y, and z offsets.
     *
     * @param x The amount to move along the X axis.
     * @param y The amount to move along the Y axis.
     * @param z The amount to move along the Z axis.
     */
    fun translate(x: Float, y: Float, z: Float) {
        transform.translate(x, y, z)
    }

    /**
     * Abstract method to be implemented by subclasses for specific initialization behaviors. This method is called by the `initialize` method.
     *
     * @param ctx The context of the current scene, providing necessary information and services for initialization.
     */
    protected abstract fun internalInitialize(ctx: ZContext)

    /**
     * Abstract method to be implemented by subclasses for specific rendering behaviors. This method is called by the `render` method.
     *
     * @param ctx The context of the current scene, providing necessary information and services for rendering.
     */
    protected abstract fun internalRender(ctx: ZContext)

    /**
     * Resizes the object and its children to the specified width and height.
     * This function should be implemented by subclasses for specific resizing behaviors.
     *
     * @param ctx The context of the current scene, providing necessary information and services for resizing.
     * @param width The new width of the object.
     * @param height The new height of the object.
     */
    protected open fun internalOnViewportResize(ctx: ZContext, width: Int, height: Int) {

    }

}
