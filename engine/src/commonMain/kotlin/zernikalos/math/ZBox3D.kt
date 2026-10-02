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
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Axis-aligned 3D box stored as origin corner + size.
 *
 * [left], [top], and [front] are the minimum x, y, and z of the box.
 * [width], [height], and [depth] must be non-negative.
 *
 * Serialization persists only [min] and [max] via [ZBox3DDto].
 */
@JsExport
@Serializable(with = ZBox3DSerializer::class)
class ZBox3D(
    var top: Float = 0f,
    var left: Float = 0f,
    var front: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f,
    var depth: Float = 0f
) {

    @JsName("initWithMinMax")
    constructor(min: ZVector3, max: ZVector3) : this(
        top = min.y,
        left = min.x,
        front = min.z,
        width = max.x - min.x,
        height = max.y - min.y,
        depth = max.z - min.z
    ) {
        require(width >= 0f && height >= 0f && depth >= 0f) {
            "ZBox3D min must be component-wise <= max"
        }
    }

    val min: ZVector3
        get() = ZVector3(left, top, front)

    val max: ZVector3
        get() = ZVector3(left + width, top + height, front + depth)

    val center: ZVector3
        get() = ZVector3(
            left + width * 0.5f,
            top + height * 0.5f,
            front + depth * 0.5f
        )

    /**
     * True when all extents are zero (default / uninitialized empty box).
     * A flat AABB with only one axis at zero is not considered empty.
     */
    val isEmpty: Boolean
        get() = width == 0f && height == 0f && depth == 0f

    fun contains(point: ZVector3): Boolean {
        return contains(this, point)
    }

    @JsName("intersectsBox")
    fun intersects(other: ZBox3D): Boolean {
        return intersects(this, other)
    }

    @JsName("intersectsSphere")
    fun intersects(sphere: ZBoundingSphere): Boolean {
        return intersects(this, sphere)
    }

    companion object Op {

        fun copy(result: ZBox3D, box: ZBox3D) {
            result.top = box.top
            result.left = box.left
            result.front = box.front
            result.width = box.width
            result.height = box.height
            result.depth = box.depth
        }

        fun contains(box: ZBox3D, point: ZVector3): Boolean {
            return point.x >= box.left && point.x <= box.left + box.width &&
                point.y >= box.top && point.y <= box.top + box.height &&
                point.z >= box.front && point.z <= box.front + box.depth
        }

        @JsName("intersectsBox")
        fun intersects(a: ZBox3D, b: ZBox3D): Boolean {
            return a.left < b.left + b.width &&
                a.left + a.width > b.left &&
                a.top < b.top + b.height &&
                a.top + a.height > b.top &&
                a.front < b.front + b.depth &&
                a.front + a.depth > b.front
        }

        /**
         * True when the sphere overlaps the AABB (including containment either way).
         */
        @JsName("intersectsSphere")
        fun intersects(box: ZBox3D, sphere: ZBoundingSphere): Boolean {
            val closestX = sphere.center.x.coerceIn(box.left, box.left + box.width)
            val closestY = sphere.center.y.coerceIn(box.top, box.top + box.height)
            val closestZ = sphere.center.z.coerceIn(box.front, box.front + box.depth)
            val dx = closestX - sphere.center.x
            val dy = closestY - sphere.center.y
            val dz = closestZ - sphere.center.z
            return dx * dx + dy * dy + dz * dz <= sphere.radius * sphere.radius
        }
    }
}

/**
 * Wire representation of [ZBox3D] for kotlinx.serialization / protobuf.
 * Runtime keeps corner+size; the format only stores min/max.
 */
@Serializable
private data class ZBox3DDto(
    @ProtoNumber(1) val min: ZVector3,
    @ProtoNumber(2) val max: ZVector3,
)

internal class ZBox3DSerializer : KSerializer<ZBox3D> {
    override val descriptor: SerialDescriptor
        get() = ZBox3DDto.serializer().descriptor

    override fun deserialize(decoder: Decoder): ZBox3D {
        val dto = decoder.decodeSerializableValue(ZBox3DDto.serializer())
        return ZBox3D(dto.min, dto.max)
    }

    override fun serialize(encoder: Encoder, value: ZBox3D) {
        val dto = ZBox3DDto(min = value.min, max = value.max)
        encoder.encodeSerializableValue(ZBox3DDto.serializer(), dto)
    }
}
