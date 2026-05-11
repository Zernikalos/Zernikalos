/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.geometries

import zernikalos.ZTypes
import zernikalos.components.mesh.ZBuffer
import zernikalos.components.mesh.ZDrawMode
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZAttributeId
import zernikalos.utils.toByteArray

/**
 * Shared mesh builders for built-in geometry. Uses 16-bit indices for Metal/WebGPU compatibility.
 */
internal object GeometryMeshes {

    internal const val DEFAULT_HALF_EXTENT: Float = 0.5f

    private class BufferIdSource(start: Int = 1) {
        private var next = start
        fun next(): Int = next++
    }

    internal fun requirePositiveHalfExtent(halfExtent: Float): Float {
        require(halfExtent > 0f) { "halfExtent must be positive, was $halfExtent" }
        return halfExtent
    }

    internal fun buildCubeMesh(halfExtent: Float): ZMesh {
        val h = requirePositiveHalfExtent(halfExtent)
        val ids = BufferIdSource()
        val mesh = ZMesh()
        mesh.drawMode = ZDrawMode.TRIANGLES

        val positions = floatArrayOf(
            -h, -h, h, h, -h, h, h, h, h, -h, h, h,
            h, -h, -h, -h, -h, -h, -h, h, -h, h, h, -h,
            h, -h, -h, h, -h, h, h, h, h, h, h, -h,
            -h, -h, h, -h, -h, -h, -h, h, -h, -h, h, h,
            -h, h, -h, -h, h, h, h, h, h, h, h, -h,
            -h, -h, h, -h, -h, -h, h, -h, -h, h, -h, h,
        )

        val normals = floatArrayOf(
            0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
            0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,
            1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
            -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
            0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
            0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
        )

        val uvs = floatArrayOf(
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
        )

        val vertexCount = 24
        addVec3Attribute(mesh, ids, ZAttributeId.POSITION, positions, vertexCount)
        addVec3Attribute(mesh, ids, ZAttributeId.NORMAL, normals, vertexCount)
        addVec2Attribute(mesh, ids, ZAttributeId.UV, uvs, vertexCount)

        val indexData = ShortArray(36)
        var w = 0
        for (face in 0 until 6) {
            val base = face * 4
            indexData[w++] = base.toShort()
            indexData[w++] = (base + 1).toShort()
            indexData[w++] = (base + 2).toShort()
            indexData[w++] = base.toShort()
            indexData[w++] = (base + 2).toShort()
            indexData[w++] = (base + 3).toShort()
        }
        addIndexBuffer(mesh, ids, indexData)

        return mesh
    }

    internal fun buildPlaneMeshXZ(halfExtent: Float): ZMesh {
        val w = requirePositiveHalfExtent(halfExtent)
        val ids = BufferIdSource()
        val mesh = ZMesh()
        mesh.drawMode = ZDrawMode.TRIANGLES

        val positions = floatArrayOf(
            -w, 0f, -w,
            w, 0f, -w,
            w, 0f, w,
            -w, 0f, w,
        )
        val normals = floatArrayOf(
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
        )
        val uvs = floatArrayOf(
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,
        )

        val vertexCount = 4
        addVec3Attribute(mesh, ids, ZAttributeId.POSITION, positions, vertexCount)
        addVec3Attribute(mesh, ids, ZAttributeId.NORMAL, normals, vertexCount)
        addVec2Attribute(mesh, ids, ZAttributeId.UV, uvs, vertexCount)

        val indexData = shortArrayOf(0, 1, 2, 0, 2, 3)
        addIndexBuffer(mesh, ids, indexData)

        return mesh
    }

    private fun addVec3Attribute(
        mesh: ZMesh,
        ids: BufferIdSource,
        attr: ZAttributeId,
        data: FloatArray,
        vertexCount: Int,
    ) {
        mesh.addBuffer(
            ZBuffer(
                id = attr.id,
                dataType = ZTypes.VEC3F,
                name = attr.attrName,
                size = 3,
                count = vertexCount,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = false,
                bufferId = ids.next(),
                dataArray = data.toByteArray(),
            ),
        )
    }

    private fun addVec2Attribute(
        mesh: ZMesh,
        ids: BufferIdSource,
        attr: ZAttributeId,
        data: FloatArray,
        vertexCount: Int,
    ) {
        mesh.addBuffer(
            ZBuffer(
                id = attr.id,
                dataType = ZTypes.VEC2F,
                name = attr.attrName,
                size = 2,
                count = vertexCount,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = false,
                bufferId = ids.next(),
                dataArray = data.toByteArray(),
            ),
        )
    }

    private fun addIndexBuffer(mesh: ZMesh, ids: BufferIdSource, indexData: ShortArray) {
        mesh.addBuffer(
            ZBuffer(
                id = ZAttributeId.INDICES.id,
                dataType = ZTypes.USHORT,
                name = ZAttributeId.INDICES.attrName,
                size = 1,
                count = indexData.size,
                normalized = false,
                offset = 0,
                stride = 0,
                isIndexBuffer = true,
                bufferId = ids.next(),
                dataArray = indexData.toByteArray(),
            ),
        )
    }
}
