# Android: “black textures” with correct WebGPU rendering (UBO / std140 mismatch)

## Summary

On Android (OpenGL ES 3.x), textured meshes can appear **fully black** while the **same asset** looks acceptable under **WebGPU**. Logcat may show **successful JPEG decode** (e.g. Skia `SkJpegCodec::onGetPixels`), which suggests the texture upload path is not the primary failure.

Two separate causes can produce that contrast:

1. **Lighting / shader parity:** On WebGPU, the **default** model pipeline compiles only [`ZSkinningShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt) ([`ZDefaultShaderGenerator.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.wgpu.kt)). Its fragment shader has a **full PBR + optional scene lights** path, but **no Phong lighting branch**—for **non-PBR** materials (typical Phong assets) it ends with **`return baseColor`** (sampled texture / vertex color), i.e. **no scene light evaluation** on that path. GLES always runs the Phong branch with **`USE_LIGHTING`** and **scene** uniform blocks, so with no lights the shaded result can go to **~0 (black)**. See [Cross-backend lighting parity](#cross-backend-lighting-parity-webgpu-gles-metal).
2. **UBO layout risk on GLES:** Even with lights present, **packed Kotlin uniform block sizes** may still diverge from GLSL **`std140`** for blocks such as `u_lightBlock`, producing corrupted reads (documented below).

Do not treat “looks fine on WebGPU” as proof that scene lighting, Phong parity, or UBO packing is correct: WebGPU may simply be drawing **albedo without Phong lighting** for that material path.

## Symptom

- Diffuse / albedo sampling looks like a **black surface** on Android GLES.
- **WebGPU** may still show a **visible textured mesh** without adding `ZLight` nodes because the default WGSL path for **non-PBR** materials returns **`baseColor` only** (no Phong pass in [`ZSkinningShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt)). **Metal** matches GLES when the lit uniform path is active; it can fall back to a **stub** direction if `USE_LIGHTING` is off for that program.
- Image decode logs can show a valid resolution (e.g. 1024×1024) for the texture asset.

## Misleading hypothesis

The first guess is often **texture upload**, format, or texture unit binding. Those remain worth checking (`glGetError`, `glActiveTexture` vs `sampler2D` location), but they are **not** the leading explanation once a **fragment-shader bypass** shows the texture clearly (see below).

## Diagnostic (recommended)

Add a **temporary** fragment path that outputs **only** `texture(u_texture, v_uv)` (no Phong/PBR lighting), e.g. guarded by `#define ZDEBUG_UNLIT_TEXTURE` in the Android default fragment shader source.

- If the texture **appears correct** → sampling and binding are fine; the bug is in **lighting uniforms** (Phong, direct lights, ambient) or **UBO data** feeding them.
- If it **stays black** → investigate GL texture upload, units, and `glGetError` after `glTexImage2D`.

Remove the debug define before shipping.

## Root cause (engine-specific)

CPU-side uniform block sizing uses a **packed sum** of member sizes:

- `UniformBlockDef.byteSize` and `ZUniformBlockData.byteSize` sum `dataType.byteSize * count` for each member ([`UniformBlockDef.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/UniformBlockDef.kt), [`ZUniform.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniform.kt)).
- `ZUniformBlockRenderer` allocates and uploads that many bytes ([`ZUniformBlockRenderer.kt`](../../engine/src/oglMain/kotlin/zernikalos/components/shader/internal/ZUniformBlockRenderer.kt)).

GLSL **uniform blocks default to `std140`**. That ruleset adds:

- **Trailing padding** inside structs (e.g. `DirectLight` in [`ZDefaultShader.android.kt`](../../engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.android.kt)) so each struct instance is a multiple of 16 bytes.
- **End-of-block padding** so the whole block size is often a multiple of 16 bytes.

If the GPU expects a **larger** block than the CPU uploads, or if **`directCount` / `ambientIntensity` / light vectors** sit at **different byte offsets** than the packed concatenation assumes, the shader can read **zeros or garbage**. For Phong + lighting, that often yields **ambient + direct sum ≈ 0** → **black**, even with a valid texture.

WebGPU uses **WGSL** uniform layout rules, which do not have to match `std140` byte-for-byte; the same Kotlin packing can “accidentally” align on one backend and fail on GLES.

## Cross-backend lighting parity (WebGPU, GLES, Metal)

### Quick reference (default skinned model shader)

| Backend | Primary shader source | Phong material + skinning | Scene `ZLight` for that path |
|--------|------------------------|---------------------------|------------------------------|
| **GLES** | [`ZDefaultShader.android.kt`](../../engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.android.kt) | Full Phong + `USE_LIGHTING`; `u_lightBlock` + `u_ambientLightBlock` | **Required** for non-zero lit shading when ambient/direct counts are zero |
| **Metal** | [`ZDefaultShader.metal.kt`](../../engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.metal.kt) | With `USE_LIGHTING`: same idea as GLES (`LightUniforms` + `AmbientLightUniforms`). Without: **`calculateBlinnPhongColorNoLighting`** stub (`normalize(float3(5,-5,5))`, `float3(2)`) | Lit path: **yes**. Fallback: **no** (fixed stub) |
| **WebGPU** | [`ZSkinningShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt) only ([`ZDefaultShaderGenerator.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.wgpu.kt)) | **No Phong fragment branch** — non-PBR branch is **`return baseColor`** after texture sample | **Not used** for Phong shading on this path (albedo pass-through) |
| **WebGPU** | PBR branch inside same `fs_main` | With `USE_PBR_MATERIAL` + `USE_NORMALS` + `USE_LIGHTING`: **`light` / `ambientLight`** uniforms, `light.directCount`, loop over `light.lights` | **Yes** (same generators as other backends). If `USE_LIGHTING` off: **`calculatePBRColorNoLighting`** stub direction `vec3(5,5,5)`, color `vec3(2)` |

### WebGPU — what actually runs today

1. **Active WGSL:** [`ZDefaultShaderGenerator.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.wgpu.kt) sets `source.wgpuShaderSource = shaderPreprocessor(skinningShaderSource, enabler)` — only [`ZSkinningShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt) is compiled for the default WebGPU model pipeline.

2. **Non-PBR (e.g. Phong-tagged materials from assets):** The fragment shader has **`#ifdef USE_PBR_MATERIAL`** … **`#else`** → **`return baseColor`** (see `fs_main` tail in `ZSkinningShader.wgpu.kt`). So the GPU shows **texture × sampled `baseColor`** without Phong diffuse/specular or scene light uniforms on that branch. That is **not** the same image as a correctly lit Phong render on GLES.

3. **PBR:** When `USE_PBR_MATERIAL` is on, the same file uses **`LightUniforms` / `AmbientLightUniforms`** when `USE_LIGHTING` is defined, or a **fixed** stub in `calculatePBRColorNoLighting` when it is not.

4. **Unused file:** [`ZPhongSkinningShader.webgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZPhongSkinningShader.webgpu.kt) contains a standalone Blinn-Phong implementation with **hardcoded** `lightPos` / `lightColor`, but **nothing in the engine tree imports or selects `phongSkinningShaderSource`** today — treat it as a **future / reference** implementation (see also [`lighting-implementation-plan.md`](../proposals/lightning/lighting-implementation-plan.md)). Wiring it in would change parity expectations.

**Effect:** The same Phong-skinned `.zko` can look **acceptably bright** on WebGPU (flat albedo) with an empty light list, while GLES computes **Phong lighting from scene uniforms** and can output **black** when those uniforms contribute ~0.

### Metal — default shader (scene lights vs fallback stub)

[`ZDefaultShader.metal.kt`](../../engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.metal.kt) aligns with GLES when **`USE_PHONG_MATERIAL`**, **`USE_NORMALS`**, and **`USE_LIGHTING`** are all enabled: the fragment shader consumes `LightUniforms` and `AmbientLightUniforms` buffers (same conceptual model as `u_lightBlock` / `u_ambientLightBlock` on GLES).

If Phong + normals are active **without** `USE_LIGHTING`, Metal uses a **fallback** path: `calculateBlinnPhongColorNoLighting` with a **hardcoded** direction `normalize(float3(5.0, -5.0, 5.0))` and `float3(2.0)` (fragment shader branch around the `#elif defined(USE_NORMALS)` under `USE_PHONG_MATERIAL`). That resembles the WebGPU Phong skinning stub more than the full GLES lit path.

PBR has an analogous “no lighting uniform” fallback using `normalize(float3(5.0, 5.0, 5.0))` and `float3(2.0)`.

### GLES (Android)

Phong with **`USE_LIGHTING`** uses block uniforms populated from [`findAllDirectLights`](../../engine/src/commonMain/kotlin/zernikalos/search/ZFinder.kt) / [`findAmbientLight`](../../engine/src/commonMain/kotlin/zernikalos/search/ZFinder.kt). No lights and zero ambient intensity → diffuse + ambient terms can vanish → **black** albedo, even though texture sampling works.

### Alignment recommendation

- **Implement Phong in the active WebGPU skinning shader** (extend `ZSkinningShader.wgpu.kt` with a Phong branch analogous to GLES/Metal, fed by the same light/ambient uniforms), **or** route Phong materials to a **single** WGSL module that already matches GLES (and delete or wire [`ZPhongSkinningShader.webgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZPhongSkinningShader.webgpu.kt) explicitly).
- Until then, document samples: WebGPU Phong assets are **albedo-only** in the default pipeline unless converted to PBR.
- Optionally audit when Metal hits the **fallback** branch vs full lighting so behavior matches `ZModel` expectations across targets.

## Blocks at highest risk

- `u_lightBlock` — array of `DirectLight` structs plus `directCount` ([`ZUniformDescriptor.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt), [`ZDirectLightArrayGenerators.kt`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZDirectLightArrayGenerators.kt)).
- `u_phongMaterialBlock` — multiple `vec4` plus trailing `float` (`shininess`).
- `u_ambientLightBlock` — `vec4` + `float` (`ambientIntensity`).

See also: [Uniform block definition system](../architecture/uniform-block-system.md).

## Proposed solutions (stable)

### 1. Size UBOs from the driver (baseline fix)

After program link, for each uniform block index:

- Query `GL_UNIFORM_BLOCK_DATA_SIZE`.
- Allocate the UBO with **`max(cpuPackedSize, gpuBlockSize)`** (or exactly `gpuBlockSize` if you zero the whole buffer).
- **Zero-initialize** the buffer, then write only the regions you control.

This avoids undersized buffers even if the packed Kotlin sum is slightly short.

### 2. Match `std140` offsets explicitly (correct fix)

Do **not** rely on “sum of logical member sizes” as the memory layout for blocks that contain **structs** or **vec4 + scalar** tails.

Pick one approach:

- **Kotlin:** Implement a small **std140 layout helper** (or fixed offset tables per block) when copying each generator’s output into the UBO blob, inserting explicit padding between members and at the end of structs/blocks.
- **GLSL:** Use `layout(std140)` and, for maximum rigidity, `layout(offset = N)` on each block member (more maintenance, very explicit).

Keep `DIRECT_LIGHT_FLOAT_COUNT`, `MAX_DIRECT_LIGHTS`, and the GLSL `DirectLight` struct in sync (already documented in code comments).

### 3. Development assertions

In debug builds, compare:

- `UniformBlockDef.byteSize` / `ZUniformBlockData.byteSize`
- vs `GL_UNIFORM_BLOCK_DATA_SIZE`

Log or assert when they differ for `u_lightBlock`, `u_phongMaterialBlock`, and `u_ambientLightBlock`.

### 4. Clean up temporary shader diagnostics

Remove any `#define ZDEBUG_UNLIT_TEXTURE` (or equivalent) once UBO layout is fixed so Phong/PBR lighting runs again in production.

## Related files

| Area | File |
|------|------|
| UBO init/bind | [`ZUniformBlockRenderer.kt`](../../engine/src/oglMain/kotlin/zernikalos/components/shader/internal/ZUniformBlockRenderer.kt) |
| Packed block size | [`ZUniform.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniform.kt), [`UniformBlockDef.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/UniformBlockDef.kt) |
| Light / Phong / ambient descriptors | [`ZUniformDescriptor.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt) |
| GLES fragment shader (light blocks) | [`ZDefaultShader.android.kt`](../../engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.android.kt) |
| Metal fragment shader (Phong / PBR / lighting fallback) | [`ZDefaultShader.metal.kt`](../../engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.metal.kt) |
| WebGPU default skinning WGSL (PBR + non-PBR branches) | [`ZSkinningShader.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt), [`ZDefaultShaderGenerator.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.wgpu.kt) |
| WebGPU Phong WGSL (**unused** by generator today; hardcoded light if wired) | [`ZPhongSkinningShader.webgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZPhongSkinningShader.webgpu.kt) |
| Packed direct lights | [`ZDirectLightArrayGenerators.kt`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZDirectLightArrayGenerators.kt), [`ZDirectLightsUniform.kt`](../../engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/ZDirectLightsUniform.kt) |

## Status

**Open** — document describes the diagnosed failure mode and recommended remediation; implementation should follow solution (1)+(2) above.
