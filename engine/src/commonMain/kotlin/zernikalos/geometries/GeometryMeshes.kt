/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.geometries

import zernikalos.components.mesh.ZDrawMode
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZAttributeId

/**
 * Shared mesh builders for built-in geometry. Uses 16-bit indices for Metal/WebGPU compatibility.
 */
internal object GeometryMeshes {

    internal const val DEFAULT_HALF_EXTENT: Float = 0.5f

    internal fun requirePositiveHalfExtent(halfExtent: Float): Float {
        require(halfExtent > 0f) { "halfExtent must be positive, was $halfExtent" }
        return halfExtent
    }

    internal fun buildCubeMesh(halfExtent: Float): ZMesh {
        val h = requirePositiveHalfExtent(halfExtent)
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
        mesh.addVec3Buffer(ZAttributeId.POSITION, vertexCount, positions)
        mesh.addVec3Buffer(ZAttributeId.NORMAL, vertexCount, normals)
        mesh.addVec2Buffer(ZAttributeId.UV, vertexCount, uvs)

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
        mesh.addUShortIndexBuffer(indexData)

        return mesh
    }

    internal fun buildPlaneMeshXZ(halfExtent: Float): ZMesh {
        val w = requirePositiveHalfExtent(halfExtent)
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
        mesh.addVec3Buffer(ZAttributeId.POSITION, vertexCount, positions)
        mesh.addVec3Buffer(ZAttributeId.NORMAL, vertexCount, normals)
        mesh.addVec2Buffer(ZAttributeId.UV, vertexCount, uvs)

        val indexData = shortArrayOf(0, 1, 2, 0, 2, 3)
        mesh.addUShortIndexBuffer(indexData)

        return mesh
    }
}
