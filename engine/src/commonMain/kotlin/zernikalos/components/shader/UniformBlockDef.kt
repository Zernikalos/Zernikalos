/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.shader

import zernikalos.ZDataType
import zernikalos.context.ZSceneContext
import zernikalos.generators.uniformgenerator.ZUniformGenerator

/**
 * Declarative definition of a single member inside a uniform block.
 * Used by [UniformBlockDef] to describe block layout and to build [ZUniformData].
 *
 * @param key Unified key (id + name) for this uniform (e.g. [UNIFORM_KEYS.BONES]).
 * @param dataType Shader data type (e.g. [zernikalos.ZTypes.MAT4F]).
 * @param count Element count for arrays (e.g. 100 for bone matrices).
 * @param glslName GLSL/variable name for this member in shader code (e.g. "u_bones").
 */
data class UniformMember(
    val key: UniformKey,
    val dataType: ZDataType,
    val count: Int = 1,
    val glslName: String = "u_${key.name.replaceFirstChar { it.lowercase() }}"
) {
    val id: Int get() = key.id
    val name: String get() = key.name
}

/**
 * Factory for uniform blocks. Defines block id, GLSL name, members (layout), and generators.
 * Builds [ZUniform] instances with embedded generators via [toZUniform].
 * Call [registerGenerators] to register per-member generators in context (for entries flow).
 */
abstract class UniformBlockDef(
    val blockKey: UniformKey,
    val glslName: String,
    val members: List<UniformMember>,
    val generators: Map<String, ZUniformGenerator>
) {
    /** Total byte size of this block (sum of all members). */
    val byteSize: Int
        get() = members.sumOf { it.dataType.byteSize * it.count }

    /**
     * Builds the [ZUniform] used by the shader program. Each member is converted to [ZUniformData].
     * The returned [ZUniform] holds generators for [ZUniform.computeValue] at render time.
     */
    fun toZUniform(): ZUniform {
        val pairs = members.map { m ->
            m.name to ZUniformData(m.id, m.glslName, m.count, m.dataType)
        }
        val unif = ZUniform(blockKey.id, glslName, pairs)
        unif.addGenerators(generators)
        return unif
    }

    fun registerGenerators(context: ZSceneContext) {
        generators.forEach { (name, generator) ->
            context.addUniformGenerator(name, generator)
        }
    }

}
