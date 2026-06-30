# Proposal: GPU Active Pass Layer (Phase B)

**Status**: Landed (Phase B)  
**Created**: 2026-06  
**Parent**: [GPU Rendering Pipeline Refactor](../gpu-rendering-pipeline-refactor.md)  
**Related**: [Render Target Resources](../../render-target-resources.md)

This document is the **single implementation guide** for Phase B. It replaces scattered plan notes and consolidates design decisions agreed during review.


| Area                                                           | Status                    |
| -------------------------------------------------------------- | ------------------------- |
| `ZGpuRenderPass` + `ZGpuPassState` in `zernikalos.context.gpu` | **Done**                  |
| `activePass` / `withActivePass` on `ZRenderingContext`         | **Done**                  |
| Draw-site migration (Metal + WebGPU)                           | **Done**                  |
| Remove `renderEncoder` / `renderPass` globals                  | **Done**                  |
| Compile validation (JS, iOS, Android)                          | **Done**                  |
| Visual smoke test (DemoApps / iosSample)                       | **Done** (confirmed before C1) |
| OpenGL real pass adapter                                       | **Deferred** (Phase F)    |
| `ZGpuFrame` / `ZGpuCommandEncoder`                             | **Done** (Phase C2) |
| Multi-pass, `ZGpuDevice` factories                             | **Deferred** (D–E) |


---

## 1. Summary

Today, component renderers issue draw commands through **mutable globals** on the rendering context (`ctx.renderEncoder` on Metal, `ctx.renderPass` on WebGPU). That blocks multi-pass rendering and hides backend differences behind untyped handles.

Phase B introduces `**expect`/`actual`** types under `**zernikalos.context.gpu**` (no `*Native` typealiases, no handle wrappers), exposes `**activePass**` on `**ZRenderingContext**`, and migrates draw sites to record through it. Public scene-graph APIs stay unchanged: `bind()` / `render()` / `unbind()` and `ZObject.render(ctx)`.

---

## 2. Problem (before Phase B — resolved)

```
ZRenderer.beginRenderPass()
  → context.renderEncoder / context.renderPass  (global, one slot)

ZModelRenderer.render()
  → ctx.renderEncoder?.setRenderPipelineState(...)
  → ctx.renderPass?.setPipeline(...)
```


| Issue                               | Why it matters                                                                   |
| ----------------------------------- | -------------------------------------------------------------------------------- |
| Single global encoder per frame     | A second pass would overwrite the same slot                                      |
| Platform types leak into components | `MTLRenderCommandEncoderProtocol`, `GPURenderPassEncoder` scattered in renderers |
| No common draw contract             | Hard to reason about what Metal and WebGPU actually share                        |


**Already fixed** (parent proposal §11): WebGPU viewport builds its render-pass descriptor once per frame via `buildRenderPassDescriptor()` in `prepareFrame`, not via `viewport.render()`.

---

## 3. Scope of this proposal

### In scope (Phase B)

- `expect`/`actual` GPU types: `ZGpuRenderPass` (common: `applyPassState` + `end` only)
- Draw/binding methods on each `actual` with **native** backend types (no `ZGpuPipeline` / `ZGpuBuffer` wrappers)
- `activePass` + `withActivePass` on `ZRenderingContext`
- Renderer sets `activePass` around `configureRenderState` + `renderScene`
- Migrate ~10 draw-site files from globals to `activePass`
- Remove `renderEncoder` / `renderPass` globals from contexts
- OpenGL: stub `actual` implementations (no real pass model yet)

### Out of scope (later phases)


| Topic                                              | Phase                                             |
| -------------------------------------------------- | ------------------------------------------------- |
| `ZGpuRenderPassDescriptor` in common               | **C1** ([spec](./gpu-phase-c1-render-pass-descriptor.md)) |
| `ZGpuFrame`, `ZGpuCommandEncoder`                  | **C2** ([landed](./gpu-phase-c2-frame-orchestration.md)) |
| `ZGpuDevice`                                       | D                                                 |
| Factory-based pipeline/bind-group creation         | D                                                 |
| Multi-pass frame graph                             | E                                                 |
| OpenGL real adapter                                | F                                                 |
| `ZContext.activePass`                              | **Not planned** — pass lives on rendering context |
| `encode(pass)` or pass parameters on `render(ctx)` | **Rejected**                                      |


---

## 4. Concepts

### 4.1 Render pass vs pipeline

**The pipeline does not contain the render pass.** The render pass is the **recording scope** (where you render: attachments, clears). The pipeline is **draw state** you set inside that scope (how you render: shaders, vertex layout).

```
Frame
  └── CommandEncoder
        └── ZGpuRenderPass          ← scope (WHERE)
              ├── setPipeline(...)  ← state (HOW)
              ├── setVertexBuffer / binding extras
              └── drawIndexed
```

This matches Metal (`renderCommandEncoder` → `setRenderPipelineState`) and WebGPU (`beginRenderPass` → `setPipeline`).


| Object                 | Lifetime            | Created                                                               |
| ---------------------- | ------------------- | --------------------------------------------------------------------- |
| `ZGpuRenderPass`       | Per pass, per frame | `beginRenderPass`                                                     |
| Native pipeline handle | Asset               | `initialize()` on model/material (Phase D may wrap as `ZGpuPipeline`) |


### 4.2 What Metal and WebGPU share

The **draw flow** is the same; only the **resource binding model** differs.


| Step          | Metal                                                | WebGPU                                   |
| ------------- | ---------------------------------------------------- | ---------------------------------------- |
| Pass state    | `applyPassState` (cull, depth on encoder)            | Mostly in pipeline/descriptor (no-op OK) |
| Pipeline      | `setRenderPipelineState`                             | `setPipeline`                            |
| Vertex buffer | `setVertexBuffer`                                    | `setVertexBuffer`                        |
| Index buffer  | `setIndexBuffer` + `drawIndexed` (**Done** on Metal) | `setIndexBuffer`                         |
| Draw          | `drawIndexedPrimitives`                              | `drawIndexed`                            |
| End pass      | `endEncoding`                                        | `end`                                    |


**Binding (different per backend — not in common `expect`):**


| Backend | Mechanism                                                                   |
| ------- | --------------------------------------------------------------------------- |
| WebGPU  | `setBindGroup` (uniforms + textures in groups)                              |
| Metal   | `setUniformBuffer` (vertex/fragment slots) + `setFragmentTexture` / sampler |


---

## 5. Resolved design decisions


| #   | Decision                                                                                                                                      |
| --- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Use `**expect class` / `actual class`** for `ZGpuRenderPass`. **No** handle wrappers (`ZGpuPipeline`, `ZGpuBuffer`, `.wrap()`).               |
| 2   | `**activePass` on `ZRenderingContext`**, not on `ZContext`.                                                                                   |
| 3   | **Common `expect` API** = `applyPassState` + `end()` only. Pipeline, buffers, draw, binding = **methods on each `actual`** with native types. |
| 4   | **Keep** `bind()` / `render()` / `unbind()` signatures. Recording via `ctx.activePass as ZGpuRenderPass`.                                     |
| 5   | `**ZGpuRenderPass` is created** in platform renderers with `internal` constructor.                                                            |
| 6   | **OGL Phase B**: stub `applyPassState` / `end` only.                                                                                          |
| 7   | **Depth convention** unchanged: +Z forward, clear `1.0`, compare `LESS` (see parent §9.4).                                                    |


---

## 6. Package layout (as landed)

```text
engine/src/commonMain/kotlin/zernikalos/context/gpu/
  ZGpuPassState.kt
  ZGpuRenderPass.kt         // expect: applyPassState + end

engine/src/metalMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPass.metal.kt   // + setPipeline, buffers, draw, binding (native types)

engine/src/webgpuMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPass.wgpu.kt    // + setPipeline, buffers, draw, setBindGroup (native types)

engine/src/oglMain/kotlin/zernikalos/context/gpu/
  ZGpuRenderPass.ogl.kt     // stub applyPassState + end
```

No separate `zernikalos/gpu/` package — GPU pass types live next to `ZRenderingContext` under `context/gpu/`.

---

## 7. API contracts

### 7.1 Common types

```kotlin
// ZGpuPassState.kt — unchanged
data class ZGpuPassState(...)

// ZGpuRenderPass.kt — lifecycle + pass-wide state only
expect class ZGpuRenderPass {
    fun applyPassState(state: ZGpuPassState)
    fun end()
}
```

No `ZGpuPipeline`, `ZGpuBuffer`, or `ZGpuIndexFormat` in common.

### 7.2 Platform methods on `actual class ZGpuRenderPass` (native types)

**Metal** (`ZGpuRenderPass.metal.kt`):

```kotlin
fun setPipeline(pipeline: MTLRenderPipelineStateProtocol)
fun setVertexBuffer(slot: Int, buffer: MTLBufferProtocol, offset: Long = 0)
fun setIndexBuffer(buffer: MTLBufferProtocol, format: ULong = MTLIndexTypeUInt16)
fun drawIndexed(indexCount: Int, drawMode: ZDrawMode, firstIndex: Int = 0)
fun setUniformBuffer(stage: ZShaderType, slot: Int, buffer: MTLBufferProtocol, offset: Long = 0)
fun setFragmentTexture(slot: Int, texture: MTLTextureProtocol, sampler: MTLSamplerStateProtocol)
```

**WebGPU** (`ZGpuRenderPass.wgpu.kt`):

```kotlin
fun setPipeline(pipeline: GPURenderPipeline)
fun setVertexBuffer(slot: Int, buffer: GPUBuffer)
fun setIndexBuffer(buffer: GPUBuffer, format: String = "uint16")
fun drawIndexed(indexCount: Int)
fun setBindGroup(index: Int, group: GPUBindGroup)
```

Draw sites cast `ctx.activePass as? ZGpuRenderPass` and call these methods with handles created at `initialize()`.

---

## 8. `activePass` on `ZRenderingContext`

```kotlin
interface ZRenderingContext {
    fun initWithSurfaceView(surfaceView: ZSurfaceView)
    val activePass: ZGpuRenderPass?
    fun <R> withActivePass(pass: ZGpuRenderPass, block: () -> R): R
}
```

Platform contexts implement storage and scoping:

```kotlin
// metal / webgpu rendering context
var activePass: ZGpuRenderPass? = null

inline fun <R> withActivePass(pass: ZGpuRenderPass, block: () -> R): R {
    val previous = activePass
    activePass = pass
    try { return block() }
    finally { activePass = previous }
}
```

`ZGLRenderingContext`: `activePass` always `null` until Phase F.

### Renderer integration

```kotlin
// ZRendererBase — conceptual change inside renderFrame()
beginRenderPass()  // platform creates ZGpuRenderPass, opens native encoder
renderingContext.withActivePass(pass) {
    configureRenderState()  // Metal: pass.applyPassState(...)
    renderScene()           // scene graph; components use ctx.activePass
}
endRenderPass()    // pass.end(); clear activePass
```


| Backend | `beginRenderPass` creates                                     |
| ------- | ------------------------------------------------------------- |
| Metal   | `ZGpuRenderPass(encoder, depthState)`                         |
| WebGPU  | `ZGpuRenderPass(encoder)` after `beginRenderPass(descriptor)` |


---

## 9. Draw path (unchanged public API)

```kotlin
// ZModelRenderer.render() — signature unchanged
actual fun render() {
    val pass = ctx.activePass ?: return
    pass.setPipeline(gpuPipeline)
    // WebGPU only:
    pass.setBindGroup(0, uniformBindGroup)
    // Metal only:
    // uniforms/textures bound in component bind() via pass.setUniformBuffer / setFragmentTexture

    model.shaderProgram.bind()
    model.mesh.bind()
    model.mesh.render()   // pass.drawIndexed(...)
    model.mesh.unbind()
    model.shaderProgram.unbind()
}
```

Component renderers hold native resources from `initialize()` and pass them to `actual` methods:

```kotlin
val pass = ctx.activePass as? ZGpuRenderPass ?: return
pass.setPipeline(nativePipeline)  // GPURenderPipeline or MTLRenderPipelineStateProtocol
```

Phase B does **not** introduce `ZGpuDevice` factories (Phase D).

---

## 10. Implementation checklist

Execute in this order. Items reflect the **final** design (no handle wrappers — see §12 and §14 simplification entry).

### Step 1 — Common GPU types

- Add `zernikalos/context/gpu/` in `commonMain`: `ZGpuPassState`, `expect class ZGpuRenderPass` (`applyPassState` + `end` only)
- Add OGL `actual` stub (`applyPassState` / `end` no-ops)

### Step 2 — Metal + WebGPU implementations

- `ZGpuRenderPass` actual with common methods + platform draw/binding methods (native types)
- Metal: `setPipeline`, buffers, `drawIndexed`, `setUniformBuffer` (`ZShaderType`), `setFragmentTexture`
- WebGPU: `setPipeline`, buffers, `drawIndexed`, `setBindGroup`
- **Not done (by design)**: `ZGpuPipeline`, `ZGpuBuffer`, or other handle wrappers — rejected in simplification pass

### Step 3 — Context + renderer

- Extend `ZRenderingContext` with `activePass` and `withActivePass`
- `ZMtlRenderingContext` and `ZWebGPURenderingContext` implement storage + scoping
- `ZGLRenderingContext`: `activePass` always `null`; `withActivePass` runs block unchanged
- `ZRendererBase` / `ZRenderer.metal.kt` / `ZRenderer.wgpu.kt`: create pass, scope with `withActivePass`, call `pass.end()`

### Step 4 — Migrate draw sites

Replace `ctx.renderEncoder` / `ctx.renderPass` with `ctx.activePass`:


| File                                        | Backend       | Status   |
| ------------------------------------------- | ------------- | -------- |
| `ZModelRenderer`                            | metal, webgpu | **Done** |
| `ZMeshRenderer`                             | metal, webgpu | **Done** |
| `ZBufferRenderer`                           | webgpu        | **Done** |
| `ZBufferContentRenderer` / `ZBufferContent` | metal, webgpu | **Done** |
| `ZTextureRenderer`                          | metal         | **Done** |
| `ZUniformRenderer`                          | metal         | **Done** |


- Metal mesh: index buffer via `setIndexBuffer` + `drawIndexed` (no inline index in `drawIndexedPrimitives` args at draw sites)

### Step 5 — Cleanup + validate

- Remove `var renderEncoder` / `var renderPass` from rendering contexts
- Grep: no remaining direct encoder globals in component code
- Compile: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain`
- Visual smoke test on DemoApps / iosSample (recommended before Phase C)

### Follow-ups outside Phase B scope

- Wire `ZGpuPassState.depthTest` / `depthWrite` / `depthCompare` through Metal `applyPassState` — tracked in [gpu-raster-state-enums-unification.md](./gpu-raster-state-enums-unification.md)
- Remove commented legacy encoder code in `ZWebGPURenderingContext` (optional cleanup)
- OpenGL real pass adapter — Phase F

---

## 11. Exit criteria


| Criterion                                                                 | Status      |
| ------------------------------------------------------------------------- | ----------- |
| Common: `ZGpuPassState` + `expect ZGpuRenderPass { applyPassState, end }` | **Met**     |
| Draw/binding uses native types on each `actual`; no handle wrappers       | **Met**     |
| `bind()` / `render()` / `unbind()` public signatures unchanged            | **Met**     |
| `renderEncoder` / `renderPass` globals removed from rendering contexts    | **Met**     |
| Builds pass on JS, iOS, Android                                           | **Met**     |
| Visual regression check on sample apps                                    | **Pending** |


---

## 12. What we explicitly avoid

- `ZGpuPipeline` / `ZGpuBuffer` handle wrappers and `.wrap()` factories
- `interface ZGpuRenderPass` with per-backend wrapper classes
- Putting `setBindGroup` in common `expect` with Metal no-ops
- Putting `setUniformBuffer` in common `expect` with WebGPU no-ops
- Threading `pass` as a parameter through `ZObject.render(ctx)`
- Moving `activePass` to `ZContext` (rendering context is the correct owner)

---

## 13. Future phases (pointer to parent doc)

After Phase B lands, continue with [gpu-rendering-pipeline-refactor.md](../gpu-rendering-pipeline-refactor.md):

- **Phase C1**: common `ZGpuRenderPassDescriptor`; viewport builder — [gpu-phase-c1-render-pass-descriptor.md](./gpu-phase-c1-render-pass-descriptor.md)
- **Phase C2**: `ZGpuFrame`, `ZGpuCommandEncoder` — [gpu-phase-c2-frame-orchestration.md](./gpu-phase-c2-frame-orchestration.md)
- **Phase D**: `ZGpuDevice` factories; pipelines/bind groups created only at init
- **Phase E**: Multiple `beginRenderPass` per frame
- **Phase F**: OpenGL real adapter

Parent proposal §9.1 already references this document and records `activePass` on `ZRenderingContext`.

---

## 14. Implementation log

### 2026-06 — Phase B: GPU active pass layer

**Scope**: `zernikalos.context.gpu/`, `activePass` on `ZRenderingContext`, draw-site migration, removal of encoder globals.

**Files touched**:


| Area             | Files                                                                                                                                                                                             |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Common GPU types | `engine/.../context/gpu/ZGpuPassState.kt`, `ZGpuRenderPass.kt`                                                                                                                                    |
| Metal GPU        | `engine/.../context/gpu/ZGpuRenderPass.metal.kt`                                                                                                                                                  |
| WebGPU GPU       | `engine/.../context/gpu/ZGpuRenderPass.wgpu.kt`                                                                                                                                                   |
| OGL stubs        | `engine/.../context/gpu/ZGpuRenderPass.ogl.kt`                                                                                                                                                    |
| Context          | `context/ZRenderingContext.kt` (common), `ZMtlRenderingContext`, `ZWebGPURenderingContext`, `ZGLRenderingContext` (android/ogl)                                                                   |
| Renderer         | `renderer/ZRenderer.kt`, `ZRenderer.metal.kt`, `ZRenderer.wgpu.kt`                                                                                                                                |
| Draw sites       | `ZModelRenderer` (metal/wgpu), `ZMeshRenderer` (metal/wgpu), `ZBufferRenderer.wgpu`, `ZBufferContent` / `ZBufferContentRenderer` (metal/wgpu), `ZUniformRenderer.metal`, `ZTextureRenderer.metal` |


**Behaviour after this slice**:

- `ctx.activePass` on `ZRenderingContext` is the single draw-recording surface for Metal and WebGPU component renderers.
- `renderEncoder` / `renderPass` globals removed from rendering contexts.
- `ZRendererBase` scopes `configureRenderState` + `renderScene` with `withActivePass`.
- Public `bind()` / `render()` / `unbind()` signatures unchanged.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.

**Still pending**: visual smoke test on DemoApps / iosSample before Phase C.

**Deferred**: `ZGpuDevice`, multi-pass frame graph, OGL real pass adapter (Phase F).

### 2026-06 — Simplification: no handle wrappers

**Scope**: Remove interim `ZGpuPipeline`, `ZGpuBuffer`, `ZGpuBindGroup`, `ZGpuTexture`, `ZGpuSampler` wrappers. Common `ZGpuRenderPass` reduced to `applyPassState` + `end()`. Draw/binding methods use native Metal/WebGPU types on each `actual`.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.

### 2026-06 — Documentation sync

**Scope**: Align checklist (§10), package layout (§6), and file inventory with landed code under `context/gpu/`; mark Phase B complete except visual smoke test.
