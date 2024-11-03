/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable
class ZTransform() {

    @Transient
    private val _matrix: ZMatrix4 = ZMatrix4.Identity

    @ProtoNumber(1)
    private val _position: ZVector3 = ZVector3.Zero

    @ProtoNumber(2)
    private val _rotation: ZQuaternion = ZQuaternion.Identity

    @ProtoNumber(3)
    private val _scale: ZVector3 = ZVector3.Ones

    @Transient
    private var _forward: ZVector3 = ZVector3.Forward
    @Transient
    private var _right: ZVector3 = ZVector3.Right
    @Transient
    private var _up: ZVector3 = ZVector3.Up


    @JsName("initWithArgs")
    constructor(position: ZVector3, rotation: ZQuaternion, scale: ZVector3): this() {
        _position.copy(position)
        _rotation.copy(rotation)
        _scale.copy(scale)
    }

    var forward: ZVector3
        get() = _forward
        set(value) {
            _forward = value.normalized
            ZVector3.cross(_right, _forward, _up)
            _right.normalize()
            ZVector3.cross(_up, _right, _forward)
            _up.normalize()
        }

    var right: ZVector3
        get() = _right
        set(value) {
            _right = value.normalized
            ZVector3.cross(_up, _right, _forward)
            _up.normalize()
            ZVector3.cross(_forward, _up, _right)
            _forward.normalize()
        }

    var up: ZVector3
        get() = _up
        set(value) {
            _up = value.normalized
            ZVector3.cross(_forward, _up, _right)
            _forward.normalize()
            ZVector3.cross(_right, _forward, _up)
            _right.normalize()
        }

    var position: ZVector3
        get() = _position
        set(value) {
            _position.copy(value)
        }

    var rotation: ZQuaternion
        get() = _rotation
        set(value) {
            _rotation.copy(value)
            updateLocalAxis()
        }

    var scale: ZVector3
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: ZMatrix4
        get() {
            ZMatrix4.identity(_matrix)
            ZMatrix4.setTranslation(_matrix, _position)
            ZMatrix4.rotate(_matrix, _rotation)
            ZMatrix4.scale(_matrix, _scale)
            return _matrix
        }

    fun setPosition(x: Float, y: Float, z: Float) {
        _position.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        ZQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
        updateLocalAxis()
    }

    @JsName("rotateByVector")
    fun setRotation(angle: Float, axis: ZVector3) {
        ZQuaternion.fromAngleAxis(_rotation, angle, axis)
    }

    @JsName("setLookAtUp")
    fun lookAt(look: ZVector3, up: ZVector3) {
        val m = ZMatrix4()
        ZMatrix4.lookAt(m, _position, look, up)
        ZQuaternion.fromMatrix4(_rotation, m)
        updateLocalAxis()
    }

    fun lookAt(look: ZVector3) {
        lookAt(look, _up)
    }

    fun translate(x: Float, y: Float, z: Float) {
        _position.x += x
        _position.y += y
        _position.z += z
    }

    @JsName("translateByVector")
    fun translate(v: ZVector3) {
        translate(v.x, v.y, v.z)
    }

    @JsName("scaleByValues")
    fun scale(sx: Float, sy: Float, sz: Float) {
        _scale.setValues(sx, sy, sz)
    }

    @JsName("scaleByFactor")
    fun scale(s: Float) {
        _scale.setValues(s, s, s)
    }

    @JsName("scaleByVector")
    fun scale(v: ZVector3) {
        _scale.setValues(v.x, v.y, v.z)
    }

    @JsName("rotateByQuat")
    fun rotate(q: ZQuaternion) {
        ZQuaternion.mult(_rotation, _rotation, q)
        updateLocalAxis()
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val q = ZQuaternion()
        ZQuaternion.fromAngleAxis(q, angle, x, y, z)
        rotate(q)
    }

    @JsName("rotateByAngleAxisVector")
    fun rotate(angle: Float, axis: ZVector3) {
        rotate(angle, axis.x, axis.y, axis.z)
    }

    @JsName("rotateAroundPointAxesThrough")
    fun rotateAround(angle: Float, point: ZVector3, axis: ZVector3, through: ZVector3) {
        val q = ZQuaternion()
        ZQuaternion.fromAngleAxis(q, angle, axis)
        ZQuaternion.mult(_rotation, _rotation, q)

        val v = ZVector3()
        //V-P
        ZVector3.subtract(v, through, point)
        //R(V-P)
        ZVector3.rotateVector(v, _rotation, v)
        //Loc = P + R(V-P)
        ZVector3.add(_position, point, v)
        updateLocalAxis()
    }

    fun rotateAround(angle: Float, point: ZVector3, axis: ZVector3) {
        rotateAround(angle, point, axis, _position)
    }

    private fun updateLocalAxis() {
        ZVector3.rotateVector(_forward, _rotation, ZVector3.Forward)
        ZVector3.rotateVector(_right, _rotation, ZVector3.Right)
        ZVector3.rotateVector(_up, _rotation, ZVector3.Up)
    }

}
