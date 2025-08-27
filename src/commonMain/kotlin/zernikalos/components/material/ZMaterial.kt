/*
 * Copyright (c) 2024-2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.material

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import zernikalos.math.ZColor
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.math.max

/**
 * Represents a material component that can be applied to 3D objects for rendering.
 *
 * This class supports multiple material systems including Physically Based Rendering (PBR) and
 * traditional Phong lighting models. Materials can also include textures and are automatically
 * integrated with the shader generation system.
 *
 * @constructor Creates a material with the specified material data.
 * @param data The material data containing PBR, Phong, and texture information.
 */
@JsExport
@Serializable(with = ZMaterialSerializer::class)
class ZMaterial
internal constructor(private val data: ZMaterialData):
    ZRenderizableComponent<ZMaterialRenderer>(), ZBindeable {

    @JsName("init")
    constructor(): this(ZMaterialData())

    var texture: ZTexture? by data::texture

    var pbr: ZPbrMaterialData? by data::pbr

    var phong: ZPhongMaterialData? by data::phong

    val usesPbr: Boolean
        get() = pbr != null

    val usesPhong: Boolean
        get() = phong != null

    override fun createRenderer(ctx: ZRenderingContext): ZMaterialRenderer {
        return ZMaterialRenderer(ctx, data)
    }

    override fun bind() = renderer.bind()
    override fun unbind() = renderer.unbind()
}

/**
 * Represents data describing a Phong lighting model material.
 *
 * These attributes are commonly used in rendering engines to achieve traditional lighting effects with
 * ambient, diffuse, and specular components using the Blinn-Phong lighting model.
 *
 * @constructor Initializes a [ZPhongMaterialData] object with specified values for ambient, diffuse,
 * specular colors and shininess factor.
 */
@Serializable
@JsExport
data class ZPhongMaterialData(
    @ProtoNumber(1)
    var diffuse: ZColor,
    @ProtoNumber(2)
    var ambient: ZColor,
    @ProtoNumber(3)
    var specular: ZColor,
    @ProtoNumber(4)
    var _shininess: Float
) {
    /**
     * Controls the sharpness of specular highlights.
     * 
     * Range: 0.0 to 500.0
     * - 0.0: Completely matte surface (no specular highlights)
     * - 1-50: Matte materials (fabric, wood, plastic)
     * - 50-200: Semi-glossy materials (ceramic, oxidized metal)
     * - 200-500: Very glossy materials (polished metal, mirrors)
     */
    var shininess: Float
        get() = _shininess
        set(value) {
            _shininess = value.coerceIn(0f, 500f)
        }

    init {
        this.shininess = this._shininess
    }
}

/**
 * Represents data describing a physically-based rendering (PBR) material.
 *
 * These attributes are commonly used in rendering engines to achieve realistic material appearances under
 * various lighting conditions.
 *
 * @constructor Initializes a [ZPbrMaterialData] object with specified values, clamping any out-of-range values for
 * emissive intensity, metalness, and roughness to their valid ranges.
 */
@Serializable
@JsExport
data class ZPbrMaterialData(
    /**
     * The color of the material.
     */
    @ProtoNumber(1)
    var color: ZColor,
    /**
     * The emissive color of the material.
     */
    @ProtoNumber(2)
    var emissive: ZColor,

    @ProtoNumber(3)
    private var _emissiveIntensity: Float,
    @ProtoNumber(4)
    private var _metalness: Float,
    @ProtoNumber(5)
    private var _roughness: Float
) {
    /**
     * Defines the intensity of the emissive property of a material.
     *
     * The intensity value determines how strongly the material appears to emit light.
     * It can be greater than 1.0 for High Dynamic Range (HDR) effects, but it cannot be negative.
     * If a negative value is provided, it will automatically be clamped to 0.
     */
    var emissiveIntensity: Float
        get() = _emissiveIntensity
        set(value) {
            // Intensity can be > 1.0 for HDR, but shouldn't be negative.
            _emissiveIntensity = max(0f, value)
        }

    /**
     * Defines the metalness of a material.
     *
     * Metalness is represented as a factor between 0.0 and 1.0.
     * A value of `0.0` signifies a non-metallic surface, while `1.0` represents a fully metallic surface.
     * The value is clamped to ensure it remains within the valid range.
     */
    var metalness: Float
        get() = _metalness
        set(value) {
            // Metalness is a factor from 0.0 to 1.0
            _metalness = value.coerceIn(0f, 1f)
        }

    /**
     * Defines the roughness of a material.
     *
     * Roughness is represented as a factor between 0.0 and 1.0.
     * A value of `0.0` indicates a perfectly smooth surface, while `1.0` represents a completely rough surface.
     * The value is clamped to ensure it remains within the valid range.
     */
    var roughness: Float
        get() = _roughness
        set(value) {
            _roughness = value.coerceIn(0f, 1f)
        }

    init {
        // Clamp initial values from constructor by calling the public setters
        this.emissiveIntensity = this._emissiveIntensity
        this.metalness = this._metalness
        this.roughness = this._roughness
    }
}

@Serializable
@JsExport
data class ZMaterialData(
    @ProtoNumber(10)
    var pbr: ZPbrMaterialData? = null,
    @ProtoNumber(11)
    var phong: ZPhongMaterialData? = null,
    @Contextual @ProtoNumber(100)
    var texture: ZTexture? = null
): ZComponentData()

class ZMaterialRenderer(ctx: ZRenderingContext, private val data: ZMaterialData): ZComponentRenderer(ctx) {
    override fun initialize() {
        data.texture?.initialize(ctx)
    }

    override fun bind() {
        data.texture?.bind()
    }

    override fun unbind() {
        data.texture?.unbind()
    }

}

class ZMaterialSerializer: ZComponentSerializer<ZMaterial, ZMaterialData>() {
    override val kSerializer: KSerializer<ZMaterialData> = ZMaterialData.serializer()

    override fun createComponentInstance(data: ZMaterialData): ZMaterial {
        return ZMaterial(data)
    }
}
