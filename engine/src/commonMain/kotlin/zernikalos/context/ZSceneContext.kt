/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.generators.uniformgenerator.ZUniformGenerator
import zernikalos.objects.ZCamera
import zernikalos.objects.ZLight
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

    var activeLight: ZLight? = null

    /** First ambient [ZLight] registered during scene init; superseded by scene-graph collection in a later phase. */
    var activeAmbientLight: ZLight? = null

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

class ZSceneContextDefault(): ZSceneContext()

@JsExport
fun createSceneContext(): ZSceneContext {
    return ZSceneContext()
}

@JsExport
fun createDefaultSceneContext(): ZSceneContext {
    return ZSceneContextDefault()
}
