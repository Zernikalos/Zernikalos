# Lighting system implementation plan (Zernikalos engine)

This document is the **engine-facing implementation plan** for ambient lighting and multiple direct lights. It complements [`lighting-system.md`](./lighting-system.md), which specifies the GLSL-oriented shading model. Here we map that intent onto **today's Zernikalos architecture**: **`ZScene` graph**, uniform blocks, per-backend shader generators, and render-time data flow — with lighting **decoupled** from `ZSceneContext.activeLight` in favor of **collecting enabled `ZLight` nodes from the scene** (see §2.1).

---

## 1. Goals

1. **Global ambient** — Configurable color and intensity (Phong: classic ambient term; PBR: indirect-light fallback as in the spec), authored as an **ambient `ZLight`** (see §3.1).
2. **Multiple direct lights** — Forward accumulation of up to `MAX_LIGHTS` directional / point / spot lights per frame, with a dynamic `lightCount`.
3. **Single coherent contract** — Light positions, directions, and fragment attributes evaluated in **one linear space** (recommended: **view space**, matching `v_viewPosition` / `in.viewPosition` today).

Non-goals for this phase: shadows, IBL, probes, deferred or clustered lighting, light culling (see `lighting-system.md` §7).

---

## 2. Current architecture (as implemented)

Understanding the baseline avoids duplicate work and names the real touch points.

### 2.1 Scene: single active light (current) → scene graph collection (target)

**Current behavior**

- `ZSceneContext` exposes **`activeLight: ZLight?`** (`engine/.../context/ZSceneContext.kt`).
- `ZLight.internalInitialize` sets `activeLight` only if it is still `null`, so **the first initialized light wins**; additional lights do not feed the renderer (`engine/.../objects/ZLight.kt`).

**Preferred direction (remove context coupling)**

- **Do not** drive lighting from `sceneContext.activeLight`. Instead, **gather every `ZLight` in the active scene** using **`findAllLights(root)`** (`engine/src/commonMain/kotlin/zernikalos/search/ZFinder.kt`), passing the scene's root `ZObject`. That function returns all lights in **depth-first preorder** (same order as `treeTraverse`). Then **filter to lights that are enabled** (and, if applicable, whose parent chain is active — same rules as rendering visibility once defined).
- The result is an ordered list (or fixed array) **built each frame or invalidated when the graph changes**, then **clamped** to `MAX_LIGHTS` for the GPU buffer. Uniform upload reads **only** this list — not a slot on `ZSceneContext`.
- **`ZLight.internalInitialize` should stop writing `activeLight`.** Remove **`activeLight`** from `ZSceneContext` / `ZContext` once all call sites use the collected list (grep for `activeLight` and migrate tools or tests).
- **Enabled flag:** If `ZObject` does not yet expose an `enabled` (or visibility) flag, add one so lights can be toggled without removing them from the tree; collection must **skip** disabled objects (and typically their subtrees if that matches engine rules).

### 2.2 Uniform system: one light block

- `LightUniformsDef` / `ZLightUniformBlock` declares **`u_lightBlock`** with one set of fields: direction, position, color, intensity, type, range, decay, inner/outer angles (`engine/.../components/shader/ZUniformDescriptor.kt`).
- `UNIFORM_KEYS.BLOCK_LIGHT` / `UNIFORM_IDS.BLOCK_LIGHT` is binding **18** (used by Metal buffer indices and WGSL `@binding`).

### 2.3 Uniform generators: read only `activeLight` (current)

- Today, all `ZLight*Generator` implementations source data from **`sceneContext.activeLight`** (e.g. `ZLightDirectionGenerator`, `ZLightPositionGenerator`, ... under `engine/.../generators/uniformgenerator/`).
- **Target (same pipeline as §2.1):** The lights uniform buffer must be filled from the **same** list that rendering uses: **`findAllLights(root)` → filter enabled → clamp to `MAX_LIGHTS` → transform to view space** (§2.6). Implement that either by **refactoring generators** to read from a `ZLightingState` / packed array populated once per frame from that list, or by a **single block generator** that writes the whole `lights[]` + `lightCount` in one go. **Do not** read `sceneContext.activeLight` for GPU lighting once this is in place.

### 2.4 Shaders: three backends to keep in sync

When `useLighting` is enabled on a material, `ZShaderGenerator.addRequiredUniforms` attaches **`BLOCK_LIGHT`** (`engine/.../generators/shadergenerator/ZShaderGenerator.kt`).

Concrete sources:

| Backend | Primary file(s) |
|--------|------------------|
| OpenGL ES (Android) | `ZDefaultShader.android.kt` — GLSL: single `u_light`, `computeLightContribution`, PBR/Phong helpers |
| Metal | `ZDefaultShader.metal.kt` — `LightUniforms`, same lighting math, PBR includes **hardcoded** `ambient = 0.03 * albedo` inside `calculatePBRColor` |
| WebGPU | `ZSkinningShader.wgpu.kt` (and related) — `LightUniforms` struct + bindings |

Any change to the lighting contract must be applied to **each** path that defines `USE_LIGHTING` / equivalent.

### 2.5 Material behavior today

- **PBR:** Direct lighting is one light; **ambient is not a scene uniform** — it is **`vec3(0.03) * albedo`** inside `calculatePBRColor` (GLSL and Metal).
- **Phong:** Per-material ambient is multiplied into the result inside `calculateBlinnPhongColor`; there is **no separate global ambient uniform**.

### 2.6 Space consistency (critical follow-up)

Fragment shaders use **view-space** `viewPosition`. Light uniforms are currently filled from **`transform.position` / `transform.forward`** without an explicit **view-matrix transform** in the generators. A robust lighting rollout should **either** transform light position and direction by the active camera's view matrix **on the CPU** when uploading uniforms **or** document and implement a single chosen space end-to-end. This should be treated as **part of the lighting work**, not an optional polish item, once multiple lights and ambient are centralized.

---

## 3. Target data model (engine side)

### 3.1 Global ambient: new `ZLamp` kind

**Approach:** Add **`ZLampType.AMBIENT`** and a **`ZAmbientLamp`** component (parallel to `ZDirectionalLamp` / `ZPointLamp` / `ZSpotLamp` in `ZLamp.kt`). Represent global ambient as a normal **`ZLight`** with `lampType == AMBIENT`, using existing **`ZLight.color`** and **`ZLight.intensity`** for the ambient term (no position/direction semantics in the shader — see `lighting-system.md`).

**Why this fits the engine:** same scene graph as other lights, **enabled/disabled** with the rest of objects, serializes with protobuf like other `ZLight` nodes, discoverable with **`findAllLights`** (then **split by type** — see §3.3). Editors get one concept ("add light") with a type picker.

**Population:** After `findAllLights(root)`, **exclude** `AMBIENT` from the **direct** light array (`lights[]` / loop). Resolve **ambient color and intensity** from the **enabled** ambient `ZLight`(s) into the **dedicated ambient members** of the unified lighting block (§3.2) — **never** as an entry in `lights[]`. If **zero** ambient lights, use shader defaults (e.g. intensity `0` or a small fallback matching today's PBR baseline). If **multiple** ambient lights are present, define an explicit policy (e.g. **first in traversal order**, or sum — pick one and document it; simplest is **one ambient per scene** enforced in tools).

### 3.2 Unified lighting uniform block (ambient + direct lights)

Replace the single-light block with **one** extended block (conceptually still `u_lighting` / `LightUniforms`-style naming in shaders; see `lighting-system.md` for the split between ambient and direct terms in GLSL) containing:

- **Ambient (global):** `vec4` color + `float` intensity (or equivalent layout matching the spec's `u_ambientLight`), populated from the resolved **`AMBIENT`** `ZLight`(s), **not** a separate binding.
- **`lights[MAX_LIGHTS]`** — each element matches the existing per-light field set (direction, position, color, intensity, type, range, decay, innerAngle, outerAngle). **Only directional / point / spot** belong here.
- **`lightCount`** — `0 … MAX_LIGHTS` for **direct** lights only.

**Rule:** Ambient is a **`ZLight` in the scene graph** for authoring, but **does not occupy a slot** in `lights[]` and **does not increment `lightCount`**. That keeps `MAX_LIGHTS` for **geometric** direct lights only and avoids a special-case iteration branch per "ambient row" in the shader.

`MAX_LIGHTS` is a **shader compile-time constant** duplicated in Kotlin for upload sizing (start with **4** or **8** per `lighting-system.md`).

### 3.3 Population policy

Each frame (or once per scene graph change, if cached):

1. From the **active `ZScene`** root `ZObject`, call **`findAllLights(root)`** (see §2.1), then keep only lights with **`enabled == true`** (and consistent visibility rules).
2. **Partition by `lampType`:** **`AMBIENT`** → resolve ambient fields in the **unified lighting block** (§3.2). **Directional / point / spot** → the **direct** light list only.
3. For **direct** lights only: optionally sort by priority or distance (future); for v1, **stable depth-first scene-graph order** is acceptable.
4. **Clamp** the direct list to **`MAX_LIGHTS`**.
5. Transform each **direct** light's position and direction into **view space** using `activeCamera` view matrix (see §2.6). (Ambient has no direction; do not feed ambient nodes into the direct-light loop.)

**No `activeLight`:** Do not keep a parallel "primary light" on context unless an external tool absolutely requires it; prefer a single source of truth (the collected list) to avoid drift.

---

## 4. Uniform and binding layout changes

### 4.1 New / changed keys

In `UNIFORM_KEYS` / `UNIFORM_IDS`:

- **Extend** the existing lighting block key (e.g. **`BLOCK_LIGHT`** / **`BLOCK_LIGHTING`**) so **one** uniform buffer holds **ambient members (not in `lights[]`) + `lights[]` + `lightCount`** (§3.2). Prefer a **rename** only if it clarifies the combined layout (e.g. `BLOCK_LIGHTING`); any **binding id** change must be updated on all backends.
- Treat the new layout as a **shader / buffer version bump** if the GPU struct size or layout changes.

Document the layout in code comments next to `UniformBlockDef`.

### 4.2 Generators

- **Single block:** Prefer **one** generator pass (or coordinated members) that fills **ambient** (from the resolved **`AMBIENT`** `ZLight`(s), §3.1 / §3.3) **and** **`lights[]` + `lightCount`** inside the **same** `UniformBlockDef`. Avoid `ZSceneContext` as the owner of lighting data.
- **Lights:** Either per-member generators with **index** for array slots, or **one block-level write** for the full struct.

### 4.3 `ZShaderGenerator.addRequiredUniforms`

- When `useLighting` is true, register **one** extended lighting block (§3.2) — **no** separate ambient binding.

---

## 5. Shader refactor (all backends)

Align with the pseudocode in `lighting-system.md` §9:

1. **Split** material helpers:
   - `calculatePhongAmbient` / `calculatePhongDirectLight` (direct: diffuse + specular for **one** light).
   - `calculatePBRAmbient` (fallback indirect) / `calculatePBRDirectLight` (one light).
2. **Remove** embedded ambient inside `calculatePBRColor` / single-light combined functions — ambient must come **only** from the ambient pass + explicit direct sum.
3. **Phong:** Global ambient should follow the spec: `ambientLight * materialAmbient * baseColor` (or equivalent), **not** duplicated per light; direct lights contribute diffuse/specular only.
4. **Main fragment:**  
   `final = ambientTerm + sum(direct_i) + emissive` then tonemap once.

### 5.1 Shader source files (complete inventory in repo)

These are the **Kotlin-embedded shader sources** that today define **`USE_LIGHTING`**, **`u_lightBlock` / `LightUniforms`**, **`computeLightContribution`**, or equivalent WGSL helpers. All must stay consistent when the unified lighting block (§3.2) lands.

**Primary (per backend — full forward PBR/Phong + lighting)**

| Backend | Path |
|--------|------|
| OpenGL ES (Android) | `engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.android.kt` |
| Metal (iOS / Apple) | `engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.metal.kt` |
| WebGPU (WGSL) | `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningShader.wgpu.kt` |

**Generator entry points (inject `#define USE_LIGHTING` / assemble WGSL)**

| Path | Role |
|------|------|
| `engine/src/androidMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.android.kt` | Prepends `USE_LIGHTING` when `useLighting` is enabled. |
| `engine/src/metalMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.metal.kt` | Same for Metal. |
| `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/ZDefaultShaderGenerator.wgpu.kt` | Builds WGSL via `shaderPreprocessor` + `ZSkinningShader.wgpu.kt`. |
| `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/shaderPreprocessor.wgpu.kt` | Defines `USE_LIGHTING` for WGSL `//#ifdef` branches (update if new lighting-related defines are added). |

**Uniform ↔ GLSL name mapping (must match shader struct layout)**

| Path | Role |
|------|------|
| `engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt` | `LightUniformsDef` / `u_lightBlock`, `UNIFORM_KEYS.BLOCK_LIGHT` — extend/rename when the GPU layout changes. |
| `engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderGenerator.kt` | Registers `BLOCK_LIGHT` when `useLighting` is true — align with §4.3. |

**Search** the repo for `USE_LIGHTING`, `LightUniforms`, `u_lightBlock`, `computeLightContribution`, and `BLOCK_LIGHT` when applying changes; today these occurrences are concentrated in the files above.

### 5.2 Related WGSL / skinning (not on `BLOCK_LIGHT` yet)

These WebGPU sources **do not** bind `LightUniforms` today but touch lighting math or skinning; revisit if you want **one** lighting model everywhere:

| Path | Note |
|------|------|
| `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZPhongSkinningShader.webgpu.kt` | Hardcoded `lightPos` / `lightColor` in Phong path — candidate to wire to the same uniforms later. |
| `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZSkinningNoPbrShader.wgpu.kt` | Textured skinning only; no scene lights. |
| `engine/src/webgpuMain/kotlin/zernikalos/generators/shadergenerator/libs/ZDefaultShader.wgpu.kt` | Minimal textured pass; no `LightUniforms`. |

**Skinning overlap:** The **main** skinned lighting path for WebGPU is **`ZSkinningShader.wgpu.kt`** (listed in §5.1). Keep it in sync with GLES/Metal default shaders so skinned and non-skinned materials do not diverge.

---

## 6. GPU API and buffer size notes

- **Uniform buffer limits:** sizeof(extended lighting struct: **ambient fields + `lights[]` + `lightCount`**) must fit **per-platform** max UBO size (conservative mobile limits apply).
- Size the **single** extended lighting UBO so ambient + `lights[]` + `lightCount` stays within **per-platform** limits; only split bindings if you hit a hard limit (avoid unless necessary).

---

## 7. Scene graph and object API

### 7.1 `ZLight` and scene ownership

- **Remove** `internalInitialize` logic that sets `sceneContext.activeLight`.
- **Collect** lights with **`findAllLights(root)`** (see §2.1), then filter to enabled `ZLight` nodes; no dependency on initialization order.
- If using a **cached** light list for performance, invalidate it when children are added/removed or when a light's `enabled` state changes.
- Ensure `internalDispose` / removal from graph **drops** the light from the next collection pass (no stale context pointer).

### 7.2 Optional helpers

- **Discovery:** Use **`findAllLights(root)`** in **`ZFinder.kt`** (see §2.1), then **filter by `lampType`** for direct vs ambient (§3.3). Add **`findAllDirectLights`** / **`findAmbientLights`** wrappers if that keeps call sites clear.
- Document ordering guarantees for artists and tools (especially **multiple ambient** policy — §3.1).

---

## 8. Serialization and tooling (if applicable)

- Extend **`ZLampType`** / **`ZLight`** protobuf (or equivalent) with **`AMBIENT`** and **`ZAmbientLamp`** so ambient is a first-class node in saved scenes — same path as other lamps.
- **Versioning:** Bump format version if new fields or enum values are added so old `.zko` files load with sane defaults.

---

## 9. Verification

1. **No direct lights:** Mesh still visible with **ambient only** (PBR and Phong).
2. **Two+ lights:** Sum of contributions matches expectation (e.g. two dim opposing directionals).
3. **Point/spot:** Attenuation and cone still correct after **view-space** transform.
4. **Cross-backend:** Same scene on Android (GLES), iOS (Metal), Web (WebGPU) — no binding mismatch crashes, no black or blown-out frames.
5. **Performance sanity:** Forward cost scales with `lightCount` (optional: simple FPS test with `MAX_LIGHTS` filled).

---

## 10. Suggested implementation order

| Phase | Work |
|-------|------|
| **A** | Add **`ZLampType.AMBIENT`** + **`ZAmbientLamp`**; extend the **lighting uniform block** with ambient fields + generators fed from that node; refactor PBR/Phong shaders; remove hardcoded `0.03` PBR ambient; keep **single** direct light as-is. |
| **B** | Add **view-space** transforms for the existing single light (fix space consistency before multiplying lights). |
| **C** | Replace single light with **`lights[]` + `lightCount`**; implement **scene-graph collection** (enabled lights only) + clamp; loop in all backends. |
| **D** | **Delete `activeLight`** from context; wire generators to collected list; add **`enabled`** on `ZObject` if missing; update samples and docs; serialization for **`AMBIENT`** lamp type (§8). |

---

## 11. Traceability to `lighting-system.md`

| Spec topic | Engine anchor |
|------------|----------------|
| `u_ambientLight` (spec) / ambient members in `u_lighting` | Single extended **`UniformBlockDef`**: ambient fields + `lights[]` + `lightCount`; values from **`ZLight` + `ZLampType.AMBIENT`** for ambient (§3.1) |
| `u_lighting` array + `lightCount` | Same block as above; replaces `LightUniformsDef` single-light struct |
| `computeLightContribution` | Same function, indexed by `lights[i]` |
| Phong vs PBR ambient semantics | Shader split + material uniforms unchanged except Phong ambient usage |
| Tonemap once at end | Already intended; keep after full linear sum |

This plan is intended to be executed **as-is** in order; adjust `MAX_LIGHTS` and binding ids only after checking uniform buffer budgets on the minimum supported devices you care about.

---

## Phases

**Phase A:** Add `ZLampType.AMBIENT` + `ZAmbientLamp`, extend the lighting uniform block with ambient members (without consuming slots), and refactor shaders to separate ambient vs direct sum (removing the hardcoded 0.03 in PBR).

**Phase B:** Fix the space issue (view space) so light positions/directions match viewPosition.

**Phase C:** Change the block from a single light to `lights[]` + `lightCount` and loop per light in each backend.

**Phase D:** Remove `activeLight`, collect lights with `findAllLights(root)` + enabled filter + clamp, and connect generators to that list.
