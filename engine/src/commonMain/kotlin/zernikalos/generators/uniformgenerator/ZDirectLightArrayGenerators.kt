/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.uniformgenerator

import zernikalos.ZTypes
import zernikalos.components.light.ZPointLamp
import zernikalos.components.light.ZSpotLamp
import zernikalos.math.ZAlgebraObjectCollection
import zernikalos.math.ZScalar
import zernikalos.objects.ZLight
import zernikalos.utils.toByteArray

private fun packDirectLight(light: ZLight): FloatArray {
    val f = light.transform.forward
    val p = light.transform.position
    val c = light.color.floatArray
    val lamp = light.lamp
    val range = when (lamp) {
        is ZPointLamp -> lamp.range
        is ZSpotLamp -> lamp.range
        else -> 0f
    }
    val decay = when (lamp) {
        is ZPointLamp -> lamp.decay
        is ZSpotLamp -> lamp.decay
        else -> 0f
    }
    val inner = (lamp as? ZSpotLamp)?.innerAngle ?: 0f
    val outer = (lamp as? ZSpotLamp)?.outerAngle ?: 0f
    return floatArrayOf(
        f.x, f.y, f.z, 0f,
        p.x, p.y, p.z, 0f,
        c[0], c[1], c[2], c[3],
        light.intensity,
        light.lampType.ordinal.toFloat(),
        range,
        decay,
        inner,
        outer,
    )
}

val ZDirectLightsArrayGenerator: ZUniformGenerator = { ctx, _ ->
    val list = collectDirectLights(ctx)
    val totalFloats = MAX_DIRECT_LIGHTS * DIRECT_LIGHT_FLOAT_COUNT
    val coll = ZAlgebraObjectCollection(ZTypes.FLOAT, totalFloats)
    var byteOffset = 0
    val strideBytes = DIRECT_LIGHT_FLOAT_COUNT * Float.SIZE_BYTES
    repeat(MAX_DIRECT_LIGHTS) { i ->
        val floats = if (i < list.size) packDirectLight(list[i]) else FloatArray(DIRECT_LIGHT_FLOAT_COUNT)
        floats.toByteArray().copyInto(coll.byteArray, destinationOffset = byteOffset)
        byteOffset += strideBytes
    }
    coll
}

/** Active direct light count (0..MAX_DIRECT_LIGHTS). */
val ZLightDirectCountGenerator: ZUniformGenerator = { ctx, _ ->
    ZScalar(collectDirectLights(ctx).size.toFloat())
}
