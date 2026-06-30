# Phase C1: Common `ZGpuRenderPassDescriptor` + Viewport Builder

**Status**: Landed (Phase C1)  
**Created**: 2026-06  
**Parent**: [GPU Rendering Pipeline Refactor](../gpu-rendering-pipeline-refactor.md)  
**Previous**: [GPU Active Pass Layer (Phase B)](./gpu-active-pass-layer.md)  
**Next**: [Phase C2 — Frame + command encoder orchestration](./gpu-phase-c2-frame-orchestration.md) (landed)
**Related**: [Render Target Resources](../../render-target-resources.md), [GPU Raster-State Enum Unification](../gpu-raster-state-enums-unification.md)

This document is the **single implementation guide** for Phase C1. It introduces a backend-agnostic render-pass **descriptor model** in common code and moves the viewport to build it. It does **not** introduce `ZGpuFrame`, `ZGpuCommandEncoder`, or `ZGpuDevice` — those belong to C2.

---

## 1. Summary

| Area | Status |
|------|--------|
| Phase B (`activePass`, draw migration) | **Done** |
| Common `ZGpuRenderPassDescriptor` + attachment desc types | **Done** |
| Viewport builds common descriptor (WebGPU primary) | **Done** |
| WebGPU renderer consumes common desc at pass begin | **Done** |
| Metal applies clear/load policy from common desc | **Done** |
| OpenGL | **Stub** — returns `null`; unchanged behaviour |
| `ZGpuFrame` / `ZGpuCommandEncoder` | **Deferred to C2** — now landed |
| `ZRenderTarget` offscreen attachments | **Deferred** ([render-target-resources](../../render-target-resources.md)) |

Today, WebGPU stores a native `GPURenderPassDescriptor` inside `ZViewportRenderer` and the renderer reaches into `viewport.renderer.renderPassDescriptor` — a backend type leak and a broken layering boundary. C1 replaces that with a **common descriptor value** (load/store ops, clear colours) while **platform texture views stay on the viewport renderer** until `ZRenderTarget` lands.

---

## 2. Problem today

```
ZRenderer.wgpu.prepareFrame()
  → viewport.buildRenderPassDescriptor()     // Boolean; mutates WebGPU-native state

ZRenderer.wgpu.beginRenderPass()
  → viewport.renderer.renderPassDescriptor   // GPURenderPassDescriptor — platform type
  → createRenderPass(descriptor.toGpu())
```

| Issue | Why it matters |
|-------|----------------|
| No common pass-descriptor type | Multi-pass and offscreen targets cannot share a builder contract |
| Renderer reads `viewport.renderer` internals | Bypasses `ZViewport` API; hard to test and extend |
| Clear/load policy encoded only in WebGPU strings | Metal clear comes from `MTKView`; viewport `clearColor` unused on Metal |
| `buildRenderPassDescriptor(): Boolean` | Callers cannot inspect or forward the descriptor to future frame graph (C2/E) |

**Already good** (keep): WebGPU descriptor shell reuse, intra-frame idempotency, swapchain view refresh, depth texture recreate on resize — see Phase A viewport slice in parent §11.

---

## 3. Scope

### In scope (C1)

- Common attachment enums + descriptor data classes in **`context/ZGpuEnums.kt`**: `ZLoadOp`, `ZStoreOp`, `ZGpuColorAttachmentDesc`, `ZGpuDepthStencilAttachmentDesc`, `ZGpuRenderPassDescriptor`
- Change viewport API: `buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?`
- WebGPU `ZViewportRenderer` builds and caches common descriptor + native views; private native encode at pass begin
- WebGPU `ZRenderer.wgpu.kt` stops reading `GPURenderPassDescriptor` from viewport renderer
- Metal: viewport builds common descriptor from `clearColor`; renderer applies clear/load to `MTLRenderPassDescriptor` from `MTKView`
- OGL: `buildRenderPassDescriptor()` returns `null` (no behaviour change)

### Out of scope (C1 — later phases)

| Topic | Phase |
|-------|-------|
| `ZGpuFrame`, `ZGpuCommandEncoder`, shrink `ZRendererBase` hooks | **C2** |
| `ZGpuDevice` resource factory | D |
| `ZRenderTarget`, offscreen attachment ownership | [render-target-resources](../../render-target-resources.md) |
| `ZGpuTextureView` wrapper type in common | Optional with `ZRenderTarget`; **not required for C1** |
| Multi-pass frame graph | E |
| OpenGL FBO pass descriptor | F |
| MRT, MSAA resolve attachments | Future extension on same descriptor types |

---

## 4. Resolved design decisions

| # | Decision |
|---|----------|
| 1 | Common enums + descriptor data classes live in **`zernikalos.context.ZGpuEnums.kt`** (same file as `ZCullMode`, `ZFrontFace`, `ZDepthCompare`). Platform encode helpers stay in `context/gpu/ZGpuRenderPassDescriptor.*.kt`. |
| 2 | **Descriptor = policy value** (load/store ops, clear colours). **Views stay platform-native** on the viewport renderer until `ZRenderTarget`. |
| 3 | **No `ZGpuTextureView` wrapper in C1** — encode step merges common desc + native views at pass begin (WebGPU) or pass descriptor mutation (Metal). |
| 4 | **`buildRenderPassDescriptor()` returns `ZGpuRenderPassDescriptor?`**, not `Boolean`. `null` = attachments unavailable (zero size, lost swapchain, etc.). |
| 5 | **Idempotent builder** — safe to call from `prepareFrame` and again from `beginRenderPass` in the same frame; reuse cached shell when swapchain/size unchanged (preserve Phase A optimization). |
| 6 | **Do not expose native descriptors on `ZViewportRenderer` public surface** — renderer encodes via internal/platform helpers only. |
| 7 | **Depth convention unchanged**: +Z forward, clear `1.0f`, compare `LESS` (parent §9.4). |
| 8 | **`buildSwapchainPassDescriptor(clearColor)` in `ZViewport.kt`** — swapchain pass policy is viewport-owned; WebGPU attachment wiring also lives on the viewport renderer. |
| 9 | **Single color attachment** in C1 (matches current engine). MRT fields can extend the list later without API break. |

---

## 5. Concepts

### 5.1 Descriptor vs pass vs views

```
ZViewport (policy)
  ├── clearColor, viewBox
  └── buildRenderPassDescriptor() → ZGpuRenderPassDescriptor  (WHAT: load/store/clear)

ZViewportRenderer (platform resources)
  ├── depthTexture, swapchain view handles   (WHERE: GPU surfaces)
  └── encodeNativePass(desc) at pass begin     (merge policy + views)

ZRenderer.beginRenderPass()
  └── native encoder from encoded descriptor
        └── ZGpuRenderPass (Phase B)
```

| Object | Contains | Lifetime |
|--------|----------|----------|
| `ZGpuRenderPassDescriptor` | Load/store ops, clear values, optional label | Rebuilt/refreshed per frame; shell reused |
| Platform texture views | Swapchain + depth views | Refreshed per frame; depth texture on resize |
| `ZGpuRenderPass` | Draw recording scope | Per pass, per frame (Phase B) |

### 5.2 Relationship to Phase B

Phase B solved **draw recording** (`activePass`). C1 solves **pass attachment binding** (descriptor). They compose:

```kotlin
// C1 + B together (conceptual — C2 may refactor hook names)
prepareFrame()
val passDesc = viewport.buildRenderPassDescriptor() ?: return

beginRenderPass()
val nativeDesc = encodePassDescriptor(passDesc, viewport) ?: return false
val pass = openPass(nativeDesc)
renderingContext.withActivePass(pass) {
    configureRenderState()
    renderScene()   // components use ctx.activePass
}
pass.end()
```

C1 does **not** require changing draw sites or `ZGpuRenderPass` method signatures.

---

## 6. Package layout

```text
engine/src/commonMain/kotlin/zernikalos/context/
  ZGpuEnums.kt                 // ZLoadOp, ZStoreOp + descriptor data classes (extend existing file)

engine/src/webgpuMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPassDescriptor.wgpu.kt   // encodeWebGPURenderPassDescriptor(...); ZLoadOp/ZStoreOp mappers

engine/src/metalMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPassDescriptor.metal.kt  // applyPassDescriptorToMetal(...); ZLoadOp/ZStoreOp mappers

engine/src/oglMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPassDescriptor.ogl.kt    // stub / no-op
```

`ZGpuPassState` and `ZGpuRenderPass` remain in `context/gpu/` (Phase B). C1 adds load/store enums and pass-descriptor **values** to the existing canonical GPU vocabulary file under `context/`.

Viewport changes:

```text
engine/src/commonMain/kotlin/zernikalos/components/ZViewport.kt   // + buildSwapchainPassDescriptor(clearColor)
engine/src/webgpuMain/kotlin/zernikalos/components/ZViewportRenderer.kt
engine/src/metalMain/kotlin/zernikalos/components/ZViewportRenderer.kt
engine/src/oglMain/kotlin/zernikalos/components/ZViewportRenderer.kt

engine/src/webgpuMain/kotlin/zernikalos/renderer/ZRenderer.wgpu.kt
engine/src/metalMain/kotlin/zernikalos/renderer/ZRenderer.metal.kt
```

---

## 7. API contracts

### 7.1 Common types (`context/ZGpuEnums.kt`)

Add to the existing file alongside raster-state enums:

```kotlin
// engine/src/commonMain/kotlin/zernikalos/context/ZGpuEnums.kt
package zernikalos.context

import zernikalos.math.ZColor

enum class ZCullMode { /* existing */ }
enum class ZFrontFace { /* existing */ }
enum class ZDepthCompare { /* existing */ }

enum class ZLoadOp { Load, Clear }
enum class ZStoreOp { Store, Discard }

data class ZGpuColorAttachmentDesc(
    val loadOp: ZLoadOp = ZLoadOp.Clear,
    val storeOp: ZStoreOp = ZStoreOp.Store,
    val clearValue: ZColor,
)

data class ZGpuDepthStencilAttachmentDesc(
    val depthLoadOp: ZLoadOp = ZLoadOp.Clear,
    val depthStoreOp: ZStoreOp = ZStoreOp.Store,
    val depthClearValue: Float = 1.0f,
)

data class ZGpuRenderPassDescriptor(
    val label: String? = null,
    val colorAttachments: List<ZGpuColorAttachmentDesc>,
    val depthStencilAttachment: ZGpuDepthStencilAttachmentDesc? = null,
)
```

Use `zernikalos.math.ZColor` for colour clears (already on viewport). No JsExport on these types in C1 (internal renderer/viewport path only).

### 7.2 Viewport API (common)

```kotlin
// ZViewport.kt — replace Boolean return
fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor? =
    renderer.buildRenderPassDescriptor()

// expect ZViewportRenderer
fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?
```

Default builder for a swapchain viewport — **`internal` helper in `ZViewport.kt`** (WebGPU and Metal viewport renderers call it):

```kotlin
// ZViewport.kt
internal fun buildSwapchainPassDescriptor(clearColor: ZColor): ZGpuRenderPassDescriptor =
    ZGpuRenderPassDescriptor(
        label = "Main viewport pass",
        colorAttachments = listOf(
            ZGpuColorAttachmentDesc(
                loadOp = ZLoadOp.Clear,
                storeOp = ZStoreOp.Store,
                clearValue = clearColor,
            )
        ),
        depthStencilAttachment = ZGpuDepthStencilAttachmentDesc(
            depthLoadOp = ZLoadOp.Clear,
            depthStoreOp = ZStoreOp.Store,
            depthClearValue = 1.0f,
        ),
    )
```

Not in `ZGpuEnums.kt`: that file holds **portable GPU vocabulary** (enums + descriptor shapes). Viewport-specific defaults (label, single swapchain attachment layout) belong next to `ZViewport` / `clearColor`.

### 7.3 Platform encode (not in common `expect`)

**WebGPU** — private function in `ZGpuRenderPassDescriptor.wgpu.kt`:

```kotlin
internal fun encodeWebGPURenderPassDescriptor(
    desc: ZGpuRenderPassDescriptor,
    colorView: GPUTextureView,
    depthView: GPUTextureView,
    // optional: reuse cached GPURenderPassColorAttachment / depth shells
): GPURenderPassDescriptor
```

Maps `ZLoadOp`/`ZStoreOp` → `GPULoadOp`/`GPUStoreOp` strings via private mappers in the same file (or `ZGpuEnums.wgpu.kt` when raster unification lands).

**Metal** — apply policy onto platform-provided descriptor:

```kotlin
internal fun applyPassDescriptorToMetal(
    desc: ZGpuRenderPassDescriptor,
    renderPassDescriptor: MTLRenderPassDescriptor,
)
```

Sets `colorAttachments[0].loadAction`, `clearColor`, and depth attachment clear/load actions. Does **not** choose drawables — `MTKView.currentRenderPassDescriptor` still supplies attachments.

**OpenGL** — no encode; viewport returns `null`.

### 7.4 Renderer integration (target)

**WebGPU** (`ZRenderer.wgpu.kt`):

```kotlin
override fun prepareFrame(): Boolean {
    val viewport = ctx.scene?.viewport ?: return false
    return viewport.buildRenderPassDescriptor() != null
}

override fun beginRenderPass(): Boolean {
    val viewport = ctx.scene?.viewport ?: return false
    val passDesc = viewport.buildRenderPassDescriptor() ?: return false
    val renderer = viewport.renderer as ZViewportRenderer
    val nativeDesc = renderer.encodeNativeRenderPass(passDesc) ?: return false
    val encoder = gpuCtx.createRenderPass(nativeDesc.toGpu()) ?: return false
    // ... existing ZGpuRenderPass setup
}
```

Remove public `var renderPassDescriptor: GPURenderPassDescriptor?` from viewport renderer (or make `private` during migration, then delete).

**Metal** (`ZRenderer.metal.kt`):

```kotlin
override fun beginRenderPass(): Boolean {
    val platformDesc = nativeView.currentRenderPassDescriptor ?: return false
    ctx.scene?.viewport?.buildRenderPassDescriptor()?.let { commonDesc ->
        applyPassDescriptorToMetal(commonDesc, platformDesc)
    }
    // ... existing encoder + ZGpuRenderPass setup
}
```

---

## 8. WebGPU viewport migration notes

Preserve existing behaviour from `ZViewportRenderer.buildRenderPassDescriptor()`:

| Behaviour | Keep in C1 |
|-----------|------------|
| Depth texture create/destroy on resize | Yes |
| Descriptor shell reuse when size + swapchain unchanged | Yes |
| Refresh swapchain `createView()` each frame on fast path | Yes |
| Update `clearValue` from viewport `clearColor` each frame | Yes — now via common desc |
| `render()` remains no-op | Yes |
| Zero-sized view box → `null` | Yes |

Replace:

- `var renderPassDescriptor: GPURenderPassDescriptor?` → `private var cachedPassDescriptor: ZGpuRenderPassDescriptor?`
- Keep private native attachment shells (`colorAttachment`, `depthAttachment`) for reuse during encode

Add:

```kotlin
internal fun encodeNativeRenderPass(desc: ZGpuRenderPassDescriptor): GPURenderPassDescriptor?
```

---

## 9. Implementation checklist

Execute in this order.

### Step 1 — Common descriptor types

- [x] Extend `context/ZGpuEnums.kt`: add `ZLoadOp`, `ZStoreOp`, `ZGpuColorAttachmentDesc`, `ZGpuDepthStencilAttachmentDesc`, `ZGpuRenderPassDescriptor`
- [x] Add `internal fun buildSwapchainPassDescriptor(clearColor: ZColor)` in `ZViewport.kt`

### Step 2 — Viewport API

- [x] Change `ZViewport.buildRenderPassDescriptor()` return type to `ZGpuRenderPassDescriptor?`
- [x] Update `expect class ZViewportRenderer.buildRenderPassDescriptor()` signature
- [x] OGL returns `null`; Metal builds policy desc via `buildSwapchainPassDescriptor`

### Step 3 — WebGPU viewport builder

- [x] Refactor `ZViewportRenderer` (WebGPU) to produce `ZGpuRenderPassDescriptor`
- [x] Move native `GPURenderPassDescriptor` construction to `encodeNativeRenderPass`
- [x] Remove public `renderPassDescriptor` field
- [x] Preserve descriptor caching / idempotency from Phase A

### Step 4 — Platform encode helpers

- [x] `encodeWebGPURenderPassDescriptor` + load/store mappers in `ZGpuRenderPassDescriptor.wgpu.kt`
- [x] `applyPassDescriptorToMetal` in `ZGpuRenderPassDescriptor.metal.kt`

### Step 5 — Renderer wiring

- [x] Update `ZRenderer.wgpu.kt` to use common desc + encode path
- [x] Update `ZRenderer.metal.kt` to apply common clear policy
- [x] Confirm `prepareFrame` / `beginRenderPass` still pair correctly

### Step 6 — Validate

- [x] Grep: no `GPURenderPassDescriptor` on viewport public API
- [x] Grep: no `renderPassDescriptor` access from renderer except encode helper
- [x] Compile: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain`
- [x] Visual parity smoke test (confirmed by user before C1 implementation)

---

## 10. Exit criteria

| Criterion | Required |
|-----------|----------|
| Common `ZGpuRenderPassDescriptor` + attachment desc types exist | Yes |
| Viewport exposes `buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?` | Yes |
| WebGPU pass begin uses common desc + encode; no public native descriptor on viewport | Yes |
| Metal applies viewport clear policy via common desc | Yes |
| Draw path (`activePass`) unchanged | Yes |
| Visual parity with pre-C1 behaviour | Yes |
| `ZGpuFrame` / `ZGpuCommandEncoder` introduced | No — deferred to C2, now landed |

---

## 11. What we explicitly avoid (C1)

- Introducing `ZGpuFrame` or replacing `ZRendererBase` hooks (C2)
- `ZRenderTarget` or offscreen attachment ownership
- `ZGpuTextureView` public wrapper type (unless encode becomes unmanageable without it)
- Putting native view handles inside common `ZGpuRenderPassDescriptor`
- JsExport on descriptor types
- Multi-pass orchestration (Phase E)
- Changing `ZGpuRenderPass` draw API from Phase B

---

## 12. Handoff to C2

C1 made descriptors **plain common values** so C2 could accept them without further viewport refactors. C2 has since landed:

- Introduced `ZGpuFrame` + `ZGpuCommandEncoder`
- Passed `ZGpuRenderPassDescriptor` into `encoder.beginRenderPass(descriptor)` instead of ad-hoc `createRenderPass` on the rendering context
- Shrunk `ZRenderer.metal.kt` / `ZRenderer.wgpu.kt` to device/frame wiring

Spec/log: [gpu-phase-c2-frame-orchestration.md](./gpu-phase-c2-frame-orchestration.md).

---

## 13. Implementation log

### 2026-06 — Phase C1: Common render pass descriptor

**Scope**: `ZLoadOp`/`ZStoreOp` and descriptor types in `context/ZGpuEnums.kt`, viewport builder, WebGPU encode + Metal apply, renderer wiring.

**Files touched**:

| Area | Files |
|------|-------|
| Common | `context/ZGpuEnums.kt`, `components/ZViewport.kt` |
| Encode | `context/gpu/ZGpuRenderPassDescriptor.wgpu.kt`, `ZGpuRenderPassDescriptor.metal.kt` |
| Viewport | `ZViewportRenderer.kt` (webgpu, metal, ogl) |
| Renderer | `ZRenderer.wgpu.kt`, `ZRenderer.metal.kt` |

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.
