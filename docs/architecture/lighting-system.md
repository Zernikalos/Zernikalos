# Lighting System Architecture

## Overview

The Zernikalos engine implements a forward lighting system supporting multiple simultaneous lights with ambient and direct lighting contributions. The system is designed to work consistently across OpenGL ES (Android), Metal (iOS), and WebGPU (Web) backends.

## Core Components

### Light Objects

Lights are represented as [`ZLight`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZLight.kt) objects in the scene graph, containing:

- `color`: Light color (RGB)
- `intensity`: Light intensity multiplier
- `lamp`: The specific lamp type (via [`ZLamp`](../../engine/src/commonMain/kotlin/zernikalos/components/light/ZLamp.kt) hierarchy)

### Lamp Types

Defined in [`ZLampType`](../../engine/src/commonMain/kotlin/zernikalos/components/light/ZLamp.kt):

| Type | Class | Description |
|------|-------|-------------|
| `AMBIENT` | `ZAmbientLamp` | Global non-directional illumination |
| `DIRECTIONAL` | `ZDirectionalLamp` | Parallel rays (sunlight-style) |
| `POINT` | `ZPointLamp` | Omni-directional light with range and decay |
| `SPOT` | `ZSpotLamp` | Cone-shaped light with inner/outer angles |

Factory methods in `ZLight` companion object:

```kotlin
val ambient = ZLight.createAmbientLight()
val directional = ZLight.createDirectionalLight()
val point = ZLight.createPointLight()
val spot = ZLight.createSpotLight()
```

## Discovery and Collection

Lights are discovered dynamically from the scene graph each frame using functions in [`ZFinder`](../../engine/src/commonMain/kotlin/zernikalos/search/ZFinder.kt):

```kotlin
// All lights (depth-first preorder)
fun findAllLights(root: ZObject): List<ZLight>

// Only enabled direct lights (directional, point, spot)
fun findAllDirectLights(root: ZObject): List<ZLight>

// First enabled ambient light
fun findAmbientLight(root: ZObject): ZLight?
```

Important notes:
- Only **enabled** lights are considered (`isEnabled == true`)
- Direct lights are filtered to exclude ambient lights
- Discovery is performed per-frame or cached on scene changes

## GPU Limits

```kotlin
// Maximum direct lights (ambient excluded) sent to GPU
const val MAX_DIRECT_LIGHTS = 4
```

The engine clamps the direct light array to this limit. Excess lights are ignored (not batched or tiled for this phase).

## Uniform System

### Ambient Light Uniforms

Generators in [`ZAmbientLightUniformGenerators`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZAmbientLightUniformGenerators.kt):

| Uniform | Generator | Description |
|-----------|-----------|-------------|
| `AmbientLight.color` | `ZAmbientLightColorGenerator` | RGB color from scene's ambient light (black if none) |
| `AmbientLight.intensity` | `ZAmbientLightParamsGenerator` | Intensity scalar (0.0 if no ambient light) |

### Direct Light Array

The [`ZDirectLightsUniform`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZDirectLightsUniform.kt) packs direct lights into a GPU-friendly blob:

```kotlin
// Floats per DirectLight (must match shader struct)
const val DIRECT_LIGHT_FLOAT_COUNT = 20

// Layout per light (3× vec4 + 6 scalars):
// - direction: vec4 (xyz + padding)
// - position: vec4 (xyz + padding)
// - color: vec4 (rgb + padding)
// - intensity: float
// - type: float (0=directional, 1=point, 2=spot)
// - range: float
// - decay: float
// - innerAngle: float
// - outerAngle: float
```

Collection happens via [`collectDirectLights()`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZDirectLightsUniform.kt) which uses `findAllDirectLights()` and `take(MAX_DIRECT_LIGHTS)`.

## Shader Structure

### Uniform Block Layout

```glsl
// Ambient (global, separate from light array)
uniform AmbientLight {
    vec4 color;  // rgb + padding
    float intensity;
};

// Individual direct light struct
struct DirectLight {
    vec4 direction;
    vec4 position;
    vec4 color;
    float intensity;
    float type;        // 0=directional, 1=point, 2=spot
    float range;
    float decay;
    float innerAngle;
    float outerAngle;
};

// Light array block
uniform DirectLights {
    DirectLight lights[MAX_DIRECT_LIGHTS];
    int lightCount;  // Actual active lights (<= MAX_DIRECT_LIGHTS)
};
```

### Light Type Semantics

- `0.0` = Directional (uses direction, ignores position)
- `1.0` = Point (uses position, attenuates by range/decay)
- `2.0` = Spot (uses position + direction, cone angles)

### Shader Accumulation Pattern

```glsl
vec3 finalColor = ambientContribution;

for (int i = 0; i < lightCount; i++) {
    DirectLight light = lights[i];
    vec3 contribution = evaluateLight(light, ...);
    finalColor += contribution;
}
```

## Backend Implementation

Lighting is implemented consistently across backends:

| Backend | Shader File | Notes |
|---------|-------------|-------|
| OpenGL ES | [`ZDefaultShader.android.kt`](../../engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.android.kt) | GLSL with loop unrolling |
| Metal | [`ZDefaultShader.metal.kt`](../../engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.metal.kt) | MSL with similar structure |
| WebGPU | [`ZDefaultShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.wgpu.kt) | WGSL with `@binding` syntax |

## Space Consistency

All lighting calculations happen in **view space**:
- Light positions and directions are transformed by the camera's view matrix on CPU
- Fragment positions (`v_viewPosition`) are in view space
- Normals are transformed to view space for lighting

This ensures consistent lighting regardless of world/object transforms.

## Material Integration

Lighting uniforms are registered when materials have `useLighting = true`. The [`ZShaderGenerator`](../../engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderGenerator.kt) adds the lighting uniform blocks to the shader program.

### Phong Materials

- Ambient: `ambientLight.color * ambientLight.intensity * material.ambient * baseColor`
- Direct: Per-light diffuse + specular accumulation

### PBR Materials

- Ambient treated as indirect fallback: `ambientLight.color * ambientLight.intensity * albedo * kD`
- Direct: Cook-Torrance BRDF per light with accumulation

## Future Considerations

Current implementation limitations (acceptable for current phase):
- No shadows
- No real IBL (Image-Based Lighting)
- No reflection probes
- No deferred/clustered lighting
- No light culling (only simple MAX_DIRECT_LIGHTS clamp)
- No normal mapping / tangent space
- No area lights

The architecture is designed to evolve toward these features while maintaining the current forward-rendering foundation.
