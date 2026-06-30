# Proposal: GPU Raster-State Enum Unification

**Status**: Proposed  
**Created**: 2026-06  
**Parent**: [GPU Rendering Pipeline Refactor](./gpu-rendering-pipeline-refactor.md)  
**Related**: [GPU Active Pass Layer](./phases/gpu-active-pass-layer.md) (Phase B), [Phase C1 — Render Pass Descriptor](./phases/gpu-phase-c1-render-pass-descriptor.md)

This document describes what remains to unify **cull mode**, **front face**, and **depth compare** values across backends. Phase B introduced canonical enums in common; backends still carry legacy equivalents and several fields in `ZGpuPassState` are not fully wired.

---

## 1. Summary

Phase B added three common enums in `zernikalos.context` (`ZGpuEnums.kt`) and threaded them through `ZGpuPassState` and Metal `applyPassState`. The same concepts still exist elsewhere:

| Concept | Canonical (common) | Legacy / backend-specific |
|---------|-------------------|---------------------------|
| Cull mode | `ZCullMode` | OGL `CullModeType`, WebGPU `GPUCullMode`, Metal `MTLCullMode*` |
| Front face | `ZFrontFace` | WebGPU string in `GPUPrimitiveState`, Metal `MTLWinding*` |
| Depth compare | `ZDepthCompare` | WebGPU `GPUCompareFunction`, Metal `MTLCompareFunction*` (fixed in renderer init) |

**Goal**: one vocabulary in common (`ZCullMode`, `ZFrontFace`, `ZDepthCompare`), with **private per-backend mappers** at the boundary — same pattern as consolidating `ZGpuShaderStage` into existing `ZShaderType`.

**Not in scope here**: draw topology, blend state, stencil ops, or full `ZGpuDevice` factory work (Phase C/D).

---

## 2. Canonical model (already landed)

```kotlin
// engine/src/commonMain/kotlin/zernikalos/context/ZGpuEnums.kt
enum class ZCullMode { None, Front, Back }
enum class ZFrontFace { CCW, CW }
enum class ZDepthCompare { Less, LessEqual, Greater, Always }

// engine/src/commonMain/kotlin/zernikalos/context/gpu/ZGpuPassState.kt
data class ZGpuPassState(
    val cullMode: ZCullMode = ZCullMode.Front,
    val frontFace: ZFrontFace = ZFrontFace.CW,
    val depthTest: Boolean = true,
    val depthWrite: Boolean = true,
    val depthCompare: ZDepthCompare = ZDepthCompare.Less,
)
```

These enums are the **only** types that common code and pass descriptors should reference going forward.

---

## 3. Problem today

### 3.1 Fragmentation map

```
Common                          Metal                    WebGPU                         OGL
────────                        ─────                    ──────                         ───
ZCullMode          ──partial──► MTLCullMode* (mapper)   GPUCullMode strings            CullModeType + ExpectCullModeType
ZFrontFace         ──partial──► MTLWinding* (mapper)    GPUPrimitiveState.frontFace?   (no enum)
ZDepthCompare      ──unused───► MTLCompareFunctionLess   GPUCompareFunction strings     DEPTH_TEST on/off only
                                 (renderer init only)
```

### 3.2 What is wired vs not

| Field | Metal | WebGPU | OGL |
|-------|-------|--------|-----|
| `cullMode` | ✅ `ZGpuRenderPass.applyPassState` | ❌ `GPUCullMode.NONE` in `ZModelRenderer.wgpu.kt` | ❌ `CullModeType.FRONT` in `ZViewportRenderer` |
| `frontFace` | ✅ `applyPassState` | ❌ not set on `GPUPrimitiveState` | ❌ no model |
| `depthTest` / `depthWrite` | ⚠️ fixed `MTLDepthStencilDescriptor` in `ZRenderer.metal.kt` init | ⚠️ hardcoded on `GPUDepthStencilState` | ⚠️ `ExpectEnabler.DEPTH_TEST` only |
| `depthCompare` | ❌ not read from `ZGpuPassState` | ❌ `GPUCompareFunction.LESS` inline | ❌ no compare enum |

### 3.3 Value-set mismatches

Decisions required before deleting legacy types:

| Enum | Canonical values | Extra elsewhere | Recommendation |
|------|------------------|-----------------|----------------|
| Cull | `None`, `Front`, `Back` | OGL `FRONT_AND_BACK` | Add `ZCullMode.FrontAndBack` **or** map OGL call sites to disable cull + document OGL-only escape; prefer adding the variant if any material needs it |
| Front face | `CCW`, `CW` | WebGPU `"ccw"` / `"cw"` strings | Add `GPUFrontFace` **or** mapper from `ZFrontFace` → string; no separate Kotlin enum |
| Depth compare | 4 values | WebGPU 8 values (`never`, `equal`, `not-equal`, `greater-equal`, …) | Keep **pass-state subset** in `ZDepthCompare`; extend enum only when a real use case appears; mappers map unsupported WebGPU values at factory boundaries |

---

## 4. Precedent: `ZShaderType`

`ZGpuShaderStage` (Metal-only duplicate) was removed in favour of existing common `ZShaderType` (`VERTEX_SHADER`, `FRAGMENT_SHADER`). Metal `ZGpuRenderPass.setUniformBuffer` now takes `ZShaderType`.

Raster-state enums should follow the same rule: **no backend-specific Kotlin enums** for concepts already in common; only **mappers** from `Z*` → native constants/strings at the `actual` layer.

---

## 5. Proposed design

### 5.1 Mapper placement

Keep mappers **private** in each backend source set (not in common `expect`):

```
engine/src/metalMain/.../context/gpu/ZGpuEnums.metal.kt   // ZCullMode.toMetal(), etc.
engine/src/webgpuMain/.../context/gpu/ZGpuEnums.wgpu.kt   // ZCullMode.toWebGpu(), etc.
engine/src/oglMain/.../context/gpu/ZGpuEnums.ogl.kt       // ZCullMode.toGl(), etc.
```

Alternatively, colocate mappers at the bottom of existing files (`ZGpuRenderPass.metal.kt` already has `toMetal()` for cull/face). Prefer a **single `ZGpuEnums.*.kt` per backend** once more than two mappers exist.

### 5.2 Single configuration path

All raster policy should flow from **`ZGpuPassState`** (or future `viewport.passState`), not ad-hoc constants in renderers:

```
ZRenderer.configureRenderState()
  → pass.applyPassState(viewport.passState)   // or scene default

Pipeline creation (WebGPU)
  → GPUPrimitiveState(cullMode = state.cullMode.toWebGpu(), frontFace = state.frontFace.toWebGpu())
  → GPUDepthStencilState(depthCompare = state.depthCompare.toWebGpu(), depthWriteEnabled = state.depthWrite, ...)
```

Metal depth is trickier: `MTLDepthStencilState` is often created once per renderer. Options:

1. **Short term**: build depth stencil from `ZGpuPassState` in `configureRenderState` and pass into `ZGpuRenderPass` (may require recreating state when compare/write changes).
2. **Long term**: per-pass depth/stencil overrides via encoder + dynamic state where the API allows.

Document the chosen Metal strategy in the implementation log when landing.

### 5.3 OGL migration

Replace `CullModeType` + `ExpectCullModeType` with `ZCullMode`:

- `ZGLRenderingContext.cullFace(mode: Int)` → `cullFace(mode: ZCullMode)` or apply via `applyPassState` stub until Phase F.
- `ZViewportRenderer`: `CullModeType.FRONT` → `ZCullMode.Front`.
- Remove `CullModeType` and `ExpectCullModeType` once all call sites migrate.

### 5.4 WebGPU migration

Replace direct `GPUCullMode` / `GPUCompareFunction` usage in component renderers:

| File | Today | Target |
|------|-------|--------|
| `ZModelRenderer.wgpu.kt` | `GPUCullMode.NONE`, `GPUCompareFunction.LESS` | Read from `ZGpuPassState` + mappers |
| Other pipeline builders | (grep `GPUCullMode`, `GPUCompareFunction`) | Same |

Keep `GPUCullMode` / `GPUCompareFunction` in `declarations.kt` as **JS interop shims** (like WebGPU spec string constants), not as engine vocabulary.

### 5.5 Optional: `GPUFrontFace` shim

Add to `declarations.kt` for symmetry:

```kotlin
object GPUFrontFace {
    const val CCW = "ccw"
    const val CW = "cw"
}
```

Mapper: `ZFrontFace.toWebGpu(): String`.

---

## 6. Implementation checklist

### Phase G1 — Mappers + Metal depth wiring

- [ ] Create `ZGpuEnums.metal.kt` with `ZCullMode`, `ZFrontFace`, `ZDepthCompare` → Metal mappers
- [ ] Move existing `toMetal()` from `ZGpuRenderPass.metal.kt` into that file
- [ ] Wire `depthCompare`, `depthTest`, `depthWrite` from `ZGpuPassState` into Metal depth stencil (decide create-once vs per-frame)
- [ ] Remove hardcoded `MTLCompareFunctionLess` as the only source of truth in `ZRenderer.metal.kt`

### Phase G2 — WebGPU pipeline builders

- [ ] Create `ZGpuEnums.wgpu.kt` mappers
- [ ] Migrate `ZModelRenderer.wgpu.kt` (and any other pipeline sites) to `ZGpuPassState`
- [ ] Set `GPUPrimitiveState.frontFace` from `ZFrontFace`
- [ ] Grep cleanup: no `GPUCullMode` / `GPUCompareFunction` in component renderers except inside mappers

### Phase G3 — OGL vocabulary

- [ ] Create `ZGpuEnums.ogl.kt` mappers (`ZCullMode` → GL constants)
- [ ] Replace `CullModeType` usages (`ZViewportRenderer`, context APIs)
- [ ] Delete `CullModeType`, `ExpectCullModeType` (android/jvm/ogl actuals)
- [ ] Align OGL depth enable with `ZGpuPassState.depthTest` when pass adapter is real (Phase F); stub can accept state early

### Phase G4 — API cleanup & docs

- [ ] Resolve `FRONT_AND_BACK` vs `ZCullMode.None` semantics (add enum variant or document mapping)
- [ ] Default pass state: single source (viewport or renderer); align Metal `Front`+`CW` vs parent doc `Back`+`CCW` defaults
- [x] Update [gpu-active-pass-layer.md](./phases/gpu-active-pass-layer.md) implementation log when landed
- [ ] Add `ZLoadOp` / `ZStoreOp` backend mappers in C1 encode files (`ZGpuRenderPassDescriptor.wgpu.kt` / `.metal.kt`) or shared `ZGpuEnums.*.kt` when raster unification lands
- [ ] Validation: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain`, OGL/JVM if applicable

---

## 7. Files to touch (inventory)

| Area | Files |
|------|-------|
| Common | `context/ZGpuEnums.kt` (enums + pass descriptor types), `context/gpu/ZGpuPassState.kt` |
| Metal | `context/gpu/ZGpuEnums.metal.kt` (new), `context/gpu/ZGpuRenderPass.metal.kt`, `renderer/ZRenderer.metal.kt` |
| WebGPU | `context/gpu/ZGpuEnums.wgpu.kt` (new), `objects/ZModelRenderer.wgpu.kt`, `context/webgpu/declarations.kt` (optional `GPUFrontFace`) |
| OGL | `context/gpu/ZGpuEnums.ogl.kt` (new), `context/ZRenderingContext.kt`, `components/ZViewportRenderer.kt`, `androidMain`/`jvmMain` `ExpectCullModeType` actuals |
| Docs | This file; parent §implementation log entry on land |

---

## 8. Non-goals

- Replacing WebGPU `declarations.kt` spec string objects entirely — they remain the JS boundary.
- Expanding `ZDepthCompare` to the full WebGPU compare set without a concrete rendering need.
- Moving enums out of `zernikalos.context`; `ZGpuPassState` stays in `zernikalos.context.gpu`.
- Unifying unrelated OGL enums (`DrawModes`, `BufferBit`, `ExpectShaderType`) in this slice.

---

## 9. Success criteria

1. **Grep**: no `CullModeType`, no `ZGpuShaderStage`-style duplicates for raster state in Kotlin sources.
2. **Single vocabulary**: component and renderer code refers to `ZCullMode`, `ZFrontFace`, `ZDepthCompare` only.
3. **Mappers**: backend constants/strings appear only in `ZGpuEnums.*.kt` (or pass `actual` files during transition).
4. **`ZGpuPassState`**: all five fields affect behaviour on Metal and WebGPU (OGL when adapter is real).
5. **Build**: all targeted compile tasks green.

---

## 10. Open questions

1. **Defaults**: `ZGpuPassState` uses `Front` + `CW`; parent refactor doc shows `Back` + `CCW`. Which is the engine default once unified?
2. **`ZCullMode.FrontAndBack`**: add to common or treat as OGL legacy only?
3. **Metal depth stencil**: recreate when `depthCompare` changes, or one global state for the main pass?
4. **Viewport-owned `passState`**: should `ZViewport` expose `passState` now, or keep renderer-owned `configureRenderState` until Phase C descriptors land?

Resolve in review before G1 lands.
