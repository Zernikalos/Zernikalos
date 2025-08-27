/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.components.shader.UNIFORM_NAMES
import zernikalos.generators.uniformgenerator.*
import zernikalos.objects.ZCamera
import zernikalos.objects.ZScene
import kotlin.js.JsExport

@JsExport
open class ZSceneContext {

    private var _scene: ZScene? = null
    var scene: ZScene?
        get() = _scene
        set(value) {
            _scene = value
        }

    var activeCamera: ZCamera? = null

    private val uniformsGeneratorMap = HashMap<String, ZUniformGenerator>()

    val isInitialized: Boolean
        get() = scene?.isInitialized == true

    fun getUniform(key: String): ZUniformGenerator? {
        if (uniformsGeneratorMap.containsKey(key)) {
            return uniformsGeneratorMap[key]
        }
        return null
    }

    fun addUniformGenerator(key: String, generator: ZUniformGenerator) {
        uniformsGeneratorMap[key] = generator
    }

}

class ZSceneContextDefault(): ZSceneContext() {
    init {
        addUniformGenerator(UNIFORM_NAMES.MODEL_VIEW_PROJECTION_MATRIX, ZModelViewProjectionMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.VIEW_MATRIX, ZViewMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.PROJECTION_MATRIX, ZProjectionMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.MODEL_MATRIX, ZModelMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.BONES, ZBoneMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.BIND_MATRIX, ZBindMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.INVERSE_BIND_MATRIX, ZInverseBindMatrixGenerator())
        addUniformGenerator(UNIFORM_NAMES.PBR_COLOR, ZPbrColorGenerator())
        addUniformGenerator(UNIFORM_NAMES.PBR_METALNESS, ZPbrMetalnessGenerator())
        addUniformGenerator(UNIFORM_NAMES.PBR_ROUGHNESS, ZPbrRoughnessGenerator())
        addUniformGenerator(UNIFORM_NAMES.PBR_EMISSIVE, ZPbrEmissiveGenerator())
        addUniformGenerator(UNIFORM_NAMES.PBR_EMISSIVE_INTENSITY, ZPbrEmissiveIntensityGenerator())
        addUniformGenerator(UNIFORM_NAMES.PHONG_AMBIENT, ZPhongAmbientGenerator())
        addUniformGenerator(UNIFORM_NAMES.PHONG_DIFFUSE, ZPhongDiffuseGenerator())
        addUniformGenerator(UNIFORM_NAMES.PHONG_SPECULAR, ZPhongSpecularGenerator())
        addUniformGenerator(UNIFORM_NAMES.PHONG_SHININESS, ZPhongShininessGenerator())
    }
}

@JsExport
fun createSceneContext(): ZSceneContext {
    return ZSceneContext()
}

@JsExport
fun createDefaultSceneContext(): ZSceneContext {
    return ZSceneContextDefault()
}
