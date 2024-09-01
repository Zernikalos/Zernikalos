/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@Serializable(with = ZkTransformSerializer::class)
class ZTransform() {

    @Transient
    private val _matrix: ZMatrix4 = ZMatrix4.Identity

    private val _position: ZVector3 = ZVector3.Zero
    private val _rotation: ZQuaternion = ZQuaternion.Identity
    private val _scale: ZVector3 = ZVector3.Ones

    private var _forward: ZVector3 = ZVector3.Forward
    private var _right: ZVector3 = ZVector3.Right
    private var _up: ZVector3 = ZVector3.Up


    @JsName("initWithArgs")
    constructor(location: ZVector3, rotation: ZQuaternion, scale: ZVector3): this() {
        _position.copy(location)
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
        }

    var scale: ZVector3
        get() = _scale
        set(value) {
            _scale.copy(value)
        }

    val matrix: ZMatrix4
        get() {
            ZMatrix4.identity(_matrix)
            ZMatrix4.translate(_matrix, _position)
            ZMatrix4.rotate(_matrix, _rotation)
            ZMatrix4.scale(_matrix, _scale)
            return _matrix
        }

    fun setLocation(x: Float, y: Float, z: Float) {
        _position.setValues(x, y, z)
    }

    fun setRotation(angle: Float, x: Float, y: Float, z: Float) {
        ZQuaternion.fromAngleAxis(_rotation, angle, x, y, z)
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
    }

    fun rotateAround(angle: Float, point: ZVector3, axis: ZVector3) {
        rotateAround(angle, point, axis, _position)
    }

    private fun transformLocalAxis() {
        ZMatrix4.mult(_forward, _matrix, _forward)
        ZMatrix4.mult(_up, _matrix, _up)
        ZMatrix4.mult(_right, _matrix, _right)
    }

}

@Serializable
data class ZkTransformSurrogate(val location: ZVector3, val rotation: ZQuaternion, val scale: ZVector3)

class ZkTransformSerializer : KSerializer<ZTransform> {
    override val descriptor: SerialDescriptor = ZkTransformSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZTransform) {

    }

    override fun deserialize(decoder: Decoder): ZTransform {
        val surrogate = decoder.decodeSerializableValue(ZkTransformSurrogate.serializer())
        return ZTransform(surrogate.location, surrogate.rotation, surrogate.scale)
    }
}