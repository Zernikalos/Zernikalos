# Proposal: GPU Rendering Pipeline Refactor (Frame, Pass, Pipeline)

**Status**: In progress (Phases A viewport slice, B active pass layer, C1 descriptor, and C2 frame orchestration landed)
**Created**: 2026  
**Related**: [Deterministic Dispose Lifecycle](./deterministic-dispose-lifecycle.md), [Render Target Resources](../render-target-resources.md), [GPU Active Pass Layer (Phase B)](./phases/gpu-active-pass-layer.md), [Phase C1 — Render Pass Descriptor](./phases/gpu-phase-c1-render-pass-descriptor.md), [Phase C2 — Frame Orchestration](./phases/gpu-phase-c2-frame-orchestration.md), [Components Architecture](../architecture/components/components-architecture.md), [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md)

---

## Summary

`ZRendererBase` now owns the common Metal/WebGPU frame loop through `ZGpuFrame` and `ZGpuCommandEncoder`. OpenGL still uses its immediate-mode path until the Phase F adapter lands. Component `bind()`/`unbind()` semantics also diverge across backends.

The **viewport slice** (see [Implementation log](#11-implementation-log)) split WebGPU descriptor construction from scene `render()` and fixed the duplicate descriptor build on the renderer path. **Phase B** landed `ZGpuRenderPass` and `activePass` on `ZRenderingContext`, migrating draw sites off encoder globals. **C1** landed common render-pass descriptors, and **C2** landed frame/command-encoder orchestration. Remaining work introduces `ZGpuDevice` resource factories and multi-pass scaffolding without rewriting the scene graph.

---

## 1. Current state (baseline)

### 1.1 Orchestration (`ZRendererBase`)

Relevant code:

| Area | Location | Notes |
|------|----------|--------|
| Frame template | `engine/.../renderer/ZRenderer.kt` | `renderFrame()` creates a `ZGpuFrame`, records through `ZGpuCommandEncoder`, and ends frame state in `finally`. |
| Metal backend | `engine/.../renderer/ZRenderer.metal.kt` | Creates `ZGpuFrame`; `configureRenderState` → `pass.applyPassState(...)`. |
| WebGPU backend | `engine/.../renderer/ZRenderer.wgpu.kt` | Creates `ZGpuFrame`; viewport descriptors are consumed by `ZGpuCommandEncoder.beginRenderPass(...)`. |
| OpenGL backend | `engine/.../renderer/ZRenderer.ogl.kt` | Still bypasses `ZGpuFrame`; scene drives GL via components until Phase F. |

Current per-frame flow:

```
render()
  └─ renderFrame()
       ├─ createGpuFrame()
       ├─ gpuFrame.begin() / beginRecording()
       ├─ viewport.buildRenderPassDescriptor()
       ├─ encoder.beginRenderPass(desc) → create `ZGpuRenderPass`
       ├─ configureRenderState() / renderScene() → scoped via `withActivePass`
       ├─ pass.end()
       ├─ encoder.finish()
       ├─ gpuFrame.submit(encoder)
       └─ gpuFrame.end()
```

### 1.2 Rendering context (per-frame state)

| Backend | Context | Per-frame refs | Draw recording (Phase B) |
|---------|---------|----------------|--------------------------|
| Metal | `ZMtlRenderingContext` | `commandBuffer`, native encoder via `makeRenderCommandEncoder` | `activePass: ZGpuRenderPass?` |
| WebGPU | `ZWebGPURenderingContext` | `commandEncoder`, native pass via `createRenderPass` | `activePass: ZGpuRenderPass?` |
| OpenGL | `ZGLRenderingContext` | immediate GL calls; no pass object | `activePass` always `null` (Phase F) |

~~Downstream component renderers reached into globals (`ctx.renderEncoder`, `ctx.renderPass`).~~ **Phase B done**: draw sites use `ZRenderingContext.activePass`.

### 1.3 Viewport and descriptor ownership (WebGPU)

`ZViewportRenderer` (WebGPU) owns the depth texture and builds `renderPassDescriptor` via `buildRenderPassDescriptor()`. `ZRenderer.wgpu.prepareFrame()` calls that builder **once** before the pass starts. Clear is via descriptor `loadOp = CLEAR` (`depthClearValue = 1.0f`).

`ZScene.internalRender()` still calls `viewport.render()` for API parity with OpenGL; on WebGPU that call is a **no-op** (ghost call — optional cleanup via `expect`/`actual` on `ZScene` later). Metal viewport `render()` is empty; OpenGL viewport `render()` performs `clearColor` + `clear`.

### 1.4 Draw-time binding (`bind` / `unbind`)

`ZBindeable` promises bind-before / unbind-after usage, but practice varies:

| Backend | Pattern | `unbind()` |
|---------|---------|------------|
| Metal | `ZModelRenderer` orchestrates bind → draw → unbind; bind encodes into `activePass` | Mostly no-op |
| WebGPU | Pipeline/bind groups in `ZModelRenderer.render()`; mesh bind sets vertex/index buffers | Not called |
| OpenGL | `useProgram` / VAO bind; some bind happens inside `ZMeshRenderer.render()` | Meaningful on GL |

This is **draw-time encoding**, not persistent per-object GPU state. The refactor does **not** replace `bind()`/`render()` with a new API; those methods record into `ZRenderingContext.activePass` (**Phase B done**).

### 1.5 Known limitations for multi-pass

1. ~~Single global `renderPass` / `renderEncoder` per frame~~ — **resolved in Phase B** via `activePass` scoping; multi-pass still needs Phase E orchestration.
2. No first-class `ZGpuRenderPassDescriptor` in common code — **Done** (Phase C1).
3. Frame/command recording lived directly in platform renderers — **Done** (Phase C2).
4. Scene traversal assumes one implicit target (swapchain / default framebuffer). **Pending** (Phase E + render targets).
5. `ZScene` still invokes `viewport.render()` on WebGPU although it is a no-op. **Optional cleanup** (Phase A remainder).

---

## 2. Goals

1. **Unified frame/pass lifecycle** — Same orchestration contract on Metal, WebGPU, and OpenGL (OpenGL via an honest adapter scope).
2. **Explicit pass recording** — `ZRenderingContext.activePass` set per pass scope; component `bind()` / `render()` read it — no mutable global encoder on context. **Done** — see [GPU Active Pass Layer](./phases/gpu-active-pass-layer.md).
3. **Separate resource lifetime from per-frame recording** — Pipelines, buffers, bind groups created at init; encoders/passes created per frame.
4. **Keep existing draw API** — Retain `bind()` / `render()` / `unbind()` on components and scene graph; GPU commands are recorded inside those methods via `ctx.activePass` (no new `encode(pass)` surface, no `pass` parameters threaded through the tree).
5. **Multi-pass readiness** — Architecture supports N passes per frame without API churn:

   ```kotlin
   encoder.beginRenderPass(shadowDesc).use { pass ->
       renderingContext.withActivePass(pass) { scene.render(ctx) }
   }
   encoder.beginRenderPass(mainDesc).use { pass ->
       renderingContext.withActivePass(pass) { scene.render(ctx) }
   }
   ```

### Non-goals (first iteration)

- Implementing shadow mapping, deferred rendering, or post-processing passes.
- Render graph / automatic pass scheduling.
- Multi-queue or async compute.
- Replacing the scene graph or component serialization model.

---

## 3. Per-frame object creation: good practice or bad practice?

This section answers whether creating descriptors, render passes, and related objects **inside the main render loop** is acceptable.

### 3.1 Taxonomy

| Object kind | Typical lifetime | Create in loop? |
|-------------|------------------|-----------------|
| `ZGpuPipeline` / `MTLRenderPipelineState` / `GPURenderPipeline` | Asset / material | **No** — init or first use, then reuse |
| `ZGpuBuffer`, textures, bind groups | Asset / mesh | **No** — init; update contents per frame if needed |
| `ZGpuCommandEncoder` / `MTLCommandBuffer` | Per frame | **Yes** — required by Metal/WebGPU model |
| `ZGpuRenderPass` encoder (pass scope) | Per pass per frame | **Yes** — required; cheap handle to recording scope |
| `ZGpuRenderPassDescriptor` **data** | Per pass per frame | **Yes** — but prefer **reuse + mutate**, not allocate |
| Swapchain texture view (WebGPU `getCurrentTexture().createView()`) | Per frame | **Yes** — texture changes each frame; view is lightweight |
| Depth texture | Viewport-sized resource | **No** — create on resize; reference in descriptor each frame |

### 3.2 Verdict for Zernikalos today

| Current practice | Assessment |
|------------------|------------|
| WebGPU: `GPURenderPassDescriptor` shell reused and mutated in `ZViewportRenderer.buildRenderPassDescriptor()` | **Done** — swapchain view and clear values refreshed per frame; full rebuild only on resize or swapchain change |
| WebGPU: `createCommandEncoder()` / `beginRenderPass()` per frame | **Correct** — matches API design |
| Metal: `commandBuffer` + `renderEncoder` per frame | **Correct** |
| Metal: `MTKView.currentRenderPassDescriptor` per frame | **Correct** — provided by platform |
| `prepareFrame` building descriptor via `viewport.render()` | **Fixed** — `prepareFrame` uses `buildRenderPassDescriptor()` |
| Creating **pipeline objects** inside the loop | **Bad practice** — not done today for models (pipelines created in `initialize()`); keep it that way |

### 3.3 Recommended policy (to encode in the new layer)

```text
CREATE ONCE (init / resize):
  - pipelines, bind group layouts, bind groups (content updates OK)
  - depth/stencil textures, MSAA targets, offscreen color targets
  - descriptor POOL/shell objects (optional optimization)

CREATE OR REFRESH EACH FRAME:
  - command encoder / command buffer
  - render pass encoder (begin/end scope)
  - swapchain texture view reference in descriptor
  - clear values and load/store ops if they change per frame

NEVER CREATE PER FRAME:
  - render pipeline state
  - GPU buffers (except streaming ring buffers under a dedicated allocator)
  - shader modules / Metal libraries
```

### 3.4 Performance note

Per-frame allocation of small Kotlin/JVM/JS descriptor wrappers is unlikely to dominate compared to GPU work at current engine scale. The refactor should still **avoid unnecessary allocation** (reused descriptor builder on viewport) to keep multi-pass viable: with 3 passes × 60 FPS, naive allocation adds up on JS and mobile.

---

## 4. Proposed architecture

### 4.1 Layering

```text
┌─────────────────────────────────────────────────────────┐
│  ZRenderer (orchestrator — one or more passes per frame) │
├─────────────────────────────────────────────────────────┤
│  ZGpuFrame → ZGpuCommandEncoder → ZGpuRenderPass        │
├─────────────────────────────────────────────────────────┤
│  ZGpuDevice (resource factory — init time)               │
│    pipelines, buffers, bind groups, textures             │
├─────────────────────────────────────────────────────────┤
│  Scene graph — existing bind() / render() / unbind(); uses ctx.activePass │
└─────────────────────────────────────────────────────────┘
```

### 4.2 Common contracts (`engine/.../gpu/`)

```kotlin
interface ZGpuDevice {
    fun createBuffer(desc: ZGpuBufferDesc): ZGpuBuffer
    fun createPipeline(desc: ZGpuPipelineDesc): ZGpuPipeline
    fun createBindGroup(layout: ZGpuBindGroupLayout, entries: ...): ZGpuBindGroup
}

class ZGpuFrame(device: ZGpuDevice) {
    fun beginRecording(): ZGpuCommandEncoder
    fun submit(buffer: ZGpuCommandBuffer)
    fun end()
}

interface ZGpuCommandEncoder {
    fun beginRenderPass(descriptor: ZGpuRenderPassDescriptor): ZGpuRenderPass
    fun finish(): ZGpuCommandBuffer
}

interface ZGpuRenderPass : AutoCloseable {
    fun applyPassState(state: ZGpuPassState)
    fun setPipeline(pipeline: ZGpuPipeline)
    fun setBindGroup(index: Int, group: ZGpuBindGroup)
    fun setVertexBuffer(slot: Int, buffer: ZGpuBuffer, offset: Long = 0)
    fun setIndexBuffer(buffer: ZGpuBuffer, format: ZGpuIndexFormat)
    fun drawIndexed(indexCount: Int, firstIndex: Int = 0, ...)
    override fun close() // endRenderPass
}

interface ZGpuPipeline // immutable, backend handle inside

data class ZGpuRenderPassDescriptor(
    val label: String? = null,
    val colorAttachments: List<ZGpuColorAttachment>,
    val depthStencilAttachment: ZGpuDepthStencilAttachment? = null,
)

data class ZGpuPassState(
    val cullMode: ZCullMode = ZCullMode.Back,
    val frontFace: ZFrontFace = ZFrontFace.CCW,
    val depthTest: Boolean = true,
    val depthWrite: Boolean = true,
    val depthCompare: ZDepthCompare = ZDepthCompare.Less,
)
```

Backend implementations: `gpu/metal/`, `gpu/webgpu/`, `gpu/ogl/` via `expect`/`actual` or internal wrappers.

### 4.3 Refactored `ZRenderer.render()` (target)

```kotlin
fun render() {
    if (!beginFrame()) return

    val encoder = gpuFrame.beginRecording()
    try {
        renderViewports(encoder)           // Phase 1: single pass
        // renderShadowPass(encoder)       // Phase 4: additional passes
        // renderPostProcess(encoder)      // future
    } finally {
        gpuFrame.submit(encoder.finish())
        endFrame()
    }
}

private fun renderViewports(encoder: ZGpuCommandEncoder) {
    val descriptor = scene.viewport.buildRenderPassDescriptor() ?: return
    encoder.beginRenderPass(descriptor).use { pass ->
        pass.applyPassState(scene.viewport.passState)
        renderingContext.withActivePass(pass) {
            scene.render(ctx)
        }
    }
}
```

### 4.4 Viewport responsibilities (split)

| Method | Responsibility |
|--------|----------------|
| `buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?` | Attachments, load/store, clear values; updates swapchain view each frame |
| `passState: ZGpuPassState` | Global pass state (cull, depth policy) |
| `onViewportResize(...)` | Recreate depth/offscreen textures |

Remove scene-visible `viewport.render()` as the descriptor construction path on WebGPU. **Done** — descriptor build is `buildRenderPassDescriptor()` only; `render()` is a no-op on WebGPU (still called from `ZScene` for OGL parity).

### 4.5 Draw path: keep `bind()` / `render()` / `unbind()`; record via `ctx.activePass`

**Decision**: do not introduce `encode(pass)` or pass parameters on `ZObject.render`, `ZModelRenderer`, or component renderers. Migration is limited to **where** commands are emitted, not **which** public methods exist.

Component renderers continue the current call pattern (`ZModelRenderer.render()` orchestrates `shaderProgram.bind()`, `mesh.bind()`, `mesh.render()`, etc.). Inside those methods, backends read `activePass` from **`ZRenderingContext`** (via the `ZRenderingContext` passed into component renderers) and emit commands there instead of using `renderEncoder` / `renderPass` globals:

```kotlin
// ZModelRenderer.render() — signature unchanged; activePass from ZRenderingContext
actual fun render() {
    val pass = ctx.activePass ?: return
    pass.setPipeline(gpuPipeline)
    pass.setBindGroup(0, uniformBindGroup)  // WebGPU only
    model.shaderProgram.bind()   // bind internals also use ctx.activePass
    model.material?.bind()
    model.mesh.bind()
    model.mesh.render()
    model.mesh.unbind()
    model.material?.unbind()
    model.shaderProgram.unbind()
}

// ZMeshRenderer.render() — signature unchanged
actual override fun render() {
    val pass = ctx.activePass ?: return
    pass.drawIndexed(indexCount, drawMode)
}
```

`ZBindeable`, `ZRenderizable`, and JsExport surfaces stay as they are. `unbind()` may remain a no-op on Metal/WebGPU where the API does not require explicit unbind.

### 4.6 OpenGL adapter

`ZGpuRenderPass` on OpenGL is a **recording scope**, not a GLES render pass object:

- `beginRenderPass` → apply viewport, clear masks, enable depth test
- `setPipeline` → `useProgram`
- `drawIndexed` → `drawElements`
- `close` → optional state pop

This keeps orchestration identical across backends without forcing FBO-based passes in Phase 1.

### 4.7 Multi-pass foundations

Design choices that unblock multiple passes:

1. **One `ZGpuCommandEncoder` per frame, many `ZGpuRenderPass` scopes** — matches Metal and WebGPU command model.
2. **Descriptors are values** — each pass carries its own color/depth attachments; no implicit “current swapchain” inside the encoder.
3. **`ZRenderingContext.activePass`** — set in `withActivePass { }` per pass; component renderers read it from their `ZRenderingContext` (no `pass` parameter on scene or component APIs).
4. **Scene traversal unchanged** — `ZObject.render(ctx)` and `ZScene.render(ctx)`; multi-pass filtering later via `ZSceneTree`, not new render signatures.
5. **Pass registry (Phase E)** — optional `ZRenderPassList` or minimal frame graph; renderer iterates passes and wraps each in `ctx.withActivePass`:

   ```kotlin
   for (passConfig in frameGraph.passes) {
       encoder.beginRenderPass(passConfig.descriptor).use { pass ->
           renderingContext.withActivePass(pass) {
               passConfig.sceneContent.render(ctx)
           }
       }
   }
   ```

---

## 5. Remaining phases

### Phase A (remainder) — Renderer documentation and Metal pass-state alignment

- [ ] Align Metal `configureRenderState` defaults with WebGPU pipeline depth state where intentional.
- [ ] Add inline documentation to `ZRendererBase` hooks mapping to GPU concepts.
- [ ] (Optional) Remove ghost `viewport.render()` from WebGPU via `expect`/`actual` on `ZScene.internalRender`.

### Phase B — Introduce `ZGpuRenderPass` wrapper (medium risk) — **Done**

**Implementation guide**: [GPU Active Pass Layer (Phase B)](./phases/gpu-active-pass-layer.md) — canonical spec for this phase (`expect`/`actual` types, `activePass` on `ZRenderingContext`, migration checklist).

- [x] Add `engine/.../context/gpu/` common types and Metal/WebGPU `actual` implementations wrapping existing encoders.
- [x] Add `activePass` / `withActivePass` on `ZRenderingContext`; renderer sets pass before `renderScene()`.
- [x] Migrate draw sites incrementally: `bind()` / `render()` use `ctx.activePass` instead of `ctx.renderEncoder` / `ctx.renderPass`.
- [x] Remove `renderEncoder` / `renderPass` globals from rendering contexts.
- [x] Visual smoke test on DemoApps / iosSample (recommended before Phase C).

**Exit criteria**: All draw commands go through `ctx.activePass`; existing `bind()` / `render()` signatures unchanged; globals removed. **Met**.

### Phase C1 — Common `ZGpuRenderPassDescriptor` + viewport builder (medium risk) — **Done**

**Implementation guide**: [gpu-phase-c1-render-pass-descriptor.md](./phases/gpu-phase-c1-render-pass-descriptor.md).

- [x] Add `ZLoadOp`, `ZStoreOp`, `ZGpuRenderPassDescriptor` and attachment desc types in `context/ZGpuEnums.kt`.
- [x] Change viewport to `buildRenderPassDescriptor(): ZGpuRenderPassDescriptor?`.
- [x] WebGPU viewport builds common desc; native encode at pass begin (remove public `GPURenderPassDescriptor` on renderer).
- [x] Metal applies viewport clear/load policy from common desc onto `MTLRenderPassDescriptor`.
- [x] Visual parity smoke test after migration.

**Exit criteria**: Pass attachment policy expressed in common code; renderer no longer reads backend descriptor types from viewport internals; draw path unchanged. **Met**.

### Phase C2 — `ZGpuFrame` + `ZGpuCommandEncoder` orchestration (medium risk) — **Done**

**Implementation guide**: [gpu-phase-c2-frame-orchestration.md](./phases/gpu-phase-c2-frame-orchestration.md).

- [x] Replace `ZRendererBase` hook sequence with `ZGpuFrame` orchestration.
- [x] Move `beginFrame`/`submitFrame` logic into `ZGpuFrame` backend types.
- [x] Wire `ZGpuCommandEncoder.beginRenderPass(ZGpuRenderPassDescriptor)` using C1 descriptors.

**Exit criteria**: `ZRenderer.metal.kt` / `ZRenderer.wgpu.kt` shrink to device/frame wiring; hooks removable or thin. **Met**.

### Phase D — Pipelines and bind groups as first-class resources (medium risk)

- [ ] Extract `ZGpuPipeline`, `ZGpuBindGroup` from `ZModelRenderer` platform code.
- [ ] `ZGpuDevice` factory methods used at model `initialize()`.
- [ ] `ZModelRenderer.render()` and related `bind()` implementations use `ctx.activePass` with `ZGpuPipeline` handles.

**Exit criteria**: No direct `GPURenderPipeline` / `MTLRenderPipelineState` in object renderers; only `ZGpuPipeline`. Public `bind()` / `render()` API unchanged.

### Phase E — Multi-pass scaffolding (higher level, API only)

- [ ] Add `ZRenderPassList` or minimal frame graph type in common code.
- [ ] Support 2+ `beginRenderPass` calls per frame in renderer (e.g. offscreen debug pass + main pass).
- [ ] Document attachment lifetime and resize rules for offscreen targets.

**Exit criteria**: Integration test or sample with two passes in one frame (e.g. clear offscreen texture, blit or sample in main pass — full post-FX not required).

### Phase F — OpenGL parity (optional / parallel)

- [ ] Implement `ZGpuRenderPass` OGL adapter.
- [ ] Migrate `ZRenderer.ogl.kt` from no-op hooks to frame/pass orchestration.

---

## 6. Migration map (current → target)

| Current | Target | Status |
|---------|--------|--------|
| `ZRendererBase.renderFrame()` hooks | `ZGpuFrame` + `ZGpuCommandEncoder` | **Done** (C2) |
| Backend pass descriptor on viewport renderer | `ZGpuRenderPassDescriptor` from viewport builder | **Done** (C1) |
| `ctx.renderEncoder` / `ctx.renderPass` | `ctx.activePass` on `ZRenderingContext` | **Done** (Phase B) |
| `ZViewportRenderer.render()` (WebGPU) builds descriptor | `buildRenderPassDescriptor()` in renderer; `render()` no-op | **Done** |
| `prepareFrame()` → `viewport.render()` | `prepareFrame()` → `viewport.buildRenderPassDescriptor()` | **Done** |
| `configureRenderState()` | `ZGpuRenderPass.applyPassState()` | **Done** (Phase B, Metal) |
| `ZModelRenderer.render()` uses context globals | Same method; uses `ctx.activePass` | **Done** (Phase B) |
| `ZBindeable.bind()` / `unbind()` | Same API; record via `ctx.activePass` inside implementations | **Done** (Phase B) |
| `ZModelRenderer` pipeline fields | Native pipeline handles at draw sites; `ZGpuPipeline` via `ZGpuDevice` deferred to Phase D | Phase B uses native types |

---

## 7. Risks and mitigations

| Risk | Mitigation |
|------|------------|
| Large touch surface across Metal/WebGPU/OGL renderers | Phased rollout; Phase B wrapper avoids big-bang rewrite |
| JS/WebGPU object churn if descriptors allocated naively per pass | Reused builder on viewport; document allocation policy (§3) |
| OpenGL forced into pass model too early | OGL adapter as scope only; FBO passes deferred |
| Breaking `ZBindeable` / `ZRenderizable` public API (JsExport) | Keep `bind()` / `render()` / `unbind()`; only change internal pass lookup to `ctx.activePass` |
| Multi-pass attachment hazards (resize during record) | Build descriptors after resize handling; freeze dimensions per frame |
| Regression in DemoApps / iosSample / androidSample | Phase B validated visually; re-check after C1 descriptor migration |

---

## 8. Success criteria

- Frame lifecycle is **identical in structure** across Metal and WebGPU (OGL adapter documented).
- **No global mutable pass encoder** on rendering context after Phase B (`activePass` is scoped via `withActivePass`; Phase C2 formalized frame recording via `ZGpuFrame`).
- Pipelines and bind groups are **not** created inside the render loop.
- Renderer can execute **two render passes in one frame** without architectural changes (Phase E).
- Scene graph and `.zko` serialization remain compatible; no asset format change required.

**Already met** (viewport slice — see §11): WebGPU builds the render-pass descriptor once per frame on the renderer path.

---

## 9. Resolved decisions

### 9.1 Active pass on `ZRenderingContext` (scoped)

**Decision**: expose `activePass` on `ZRenderingContext`, set by the renderer inside a per-pass scope. Do **not** thread `render(pass)` through `ZObject.render(ctx)`.

Canonical implementation spec: [GPU Active Pass Layer (Phase B)](./phases/gpu-active-pass-layer.md).

Rationale:

- Matches existing patterns (`activeCamera`, `scene` on `ZContext`).
- Keeps `ZObject.render(ctx)` and JsExport surface stable.
- Multi-pass is handled by the renderer swapping `activePass` between `beginRenderPass` / `endRenderPass` scopes.

Target API shape:

```kotlin
// ZRenderer (per pass)
renderingContext.withActivePass(pass) {
    scene.render(ctx)
}

// Component renderers — inside existing bind() / render(); no pass parameter
val pass = renderingContext.activePass ?: return
pass.drawIndexed(count)
```

`withActivePass` must reset `activePass` in `finally` so no pass encoder leaks across scopes or frames.

### 9.5 Keep `bind()` / `render()` / `unbind()`; no `encode(pass)` API

**Decision**: GPU command recording is folded into the **existing** component lifecycle methods. Do **not** add `encode(pass)`, `encodeDraw(pass)`, or pass parameters on `ZObject.render(ctx)`.

| Method | Role after refactor |
|--------|---------------------|
| `bind()` | Set pipeline, bind groups, vertex/index buffers, uniforms — via `ZRenderingContext.activePass` |
| `render()` | Issue draw calls (`drawIndexed`, etc.) — via `ZRenderingContext.activePass` |
| `unbind()` | Optional cleanup where the backend needs it (mainly OpenGL); no-op elsewhere |

Rationale:

- Avoids a large API churn across `ZBindeable`, `ZRenderizable`, JsExport, and all platform renderers.
- Matches how the engine already works today; only the **source of the active encoder** changes (`ZRenderingContext.activePass` instead of mutable encoder globals).
- `ZModelRenderer.render()` keeps orchestrating bind → draw → unbind; component renderers hold a `ZRenderingContext` and read `activePass` from it (no pass parameter).

**Non-goal**: renaming or removing `ZBindeable` in this refactor.

### 9.2 Scene filtering for multi-pass (future `ZSceneTree`)

**Decision**: do **not** introduce layer masks or duplicate sub-graphs in this refactor. Future pass-specific filtering will live in a dedicated **`ZSceneTree`** API, evolved from the current `search` module.

Baseline today:

| Area | Location | Notes |
|------|----------|--------|
| Tree contract | `engine/.../search/ZTreeNode.kt` | `ZObject` already implements `ZTreeNode<ZObject>`. |
| Traversal | `engine/.../search/treeTraverse.kt` | Breadth-first iterator over the object tree. |
| Queries | `engine/.../search/ZFinder.kt`, `findInTree.kt` | `findObjectByName`, `findAllLights`, `findFirstModel`, predicate-based `findInTree`. |

Future direction (separate from this proposal):

- `ZSceneTree` wraps a `ZScene` root and exposes filtered traversals (`renderables()`, `shadowCasters()`, `lights()`, custom predicates).
- Shadow / prepass / main passes call `sceneTree.<filter>().forEach { it.render(ctx) }` instead of ad-hoc flags on `ZObject`.
- This proposal only requires that **`activePass` on `ZRenderingContext` and multi-pass orchestration** do not assume a single implicit traversal — not that filtering exists yet.

### 9.3 Offscreen targets → separate proposal

**Decision**: offscreen rendering uses a standalone **`ZRenderTarget`** resource (not an extension of `ZViewport`). Detailed design is in [`render-target-resources.md`](./render-target-resources.md).

This GPU pipeline proposal references render targets only where pass descriptors need a color/depth attachment source. Viewport remains camera/view-box/clear policy; the target is what gets bound into the descriptor.

### 9.4 Depth convention: standard Z, not reversed-Z

**Decision**: keep the engine coherent with the **math basis** and **classic depth buffer** semantics. Do **not** adopt reversed-Z in this refactor.

Math convention (already documented in code):

```168:185:engine/src/commonMain/kotlin/zernikalos/math/ZVector3.kt
         * Right-handed world/model basis: +X right, +Y up, +Z forward (into the screen).
        // ...
        val Forward: ZVector3
            get() = ZVector3(0f, 0f, 1f)
```

Depth policy (aligned with current Metal/WebGPU backends):

| Setting | Value | Meaning |
|---------|-------|---------|
| World forward | **+Z** | Into the screen (CSS-like stacking intuition: increasing Z goes forward) |
| Depth buffer after projection | **0 = near**, **1 = far** | Closer geometry writes smaller depth values |
| Depth compare | **`LESS`** | Smaller depth wins (nearer fragment visible) |
| Depth clear | **`1.0`** | Start with far plane; nearer draws overwrite |

`ZMatrix4.perspective` and pipeline `depthCompare = LESS` already follow this path. `ZGpuPassState` and pipeline descriptors introduced by this refactor must document and preserve it.

**Explicitly deferred**: reversed-Z (clear `0`, compare `GREATER`) and depth prepass — revisit only if profiling or shadow-map precision requires it, with a coordinated change across math, clear values, and all pipeline states.

---

## 10. Related reading

- [`ZRenderer.kt`](../../engine/src/commonMain/kotlin/zernikalos/renderer/ZRenderer.kt) — current hook template
- [`ZRenderer.wgpu.kt`](../../engine/src/webgpuMain/kotlin/zernikalos/renderer/ZRenderer.wgpu.kt) — `prepareFrame` / descriptor flow
- [`ZViewportRenderer.kt` (WebGPU)](../../engine/src/webgpuMain/kotlin/zernikalos/components/ZViewportRenderer.kt) — descriptor construction
- [`deterministic-dispose-lifecycle.md`](./deterministic-dispose-lifecycle.md) — resource teardown order for GPU objects
- [`render-target-resources.md`](./render-target-resources.md) — offscreen `ZRenderTarget` design (companion proposal)
- [`ZVector3.kt`](../../engine/src/commonMain/kotlin/zernikalos/math/ZVector3.kt) — +Z forward basis
- [`search/`](../../engine/src/commonMain/kotlin/zernikalos/search/) — current tree traversal and find helpers (precursor to `ZSceneTree`)

---

## 11. Implementation log

Chronological record of landed work against this proposal.

### 2026-06 — Viewport slice (Phase A partial)

**Scope**: viewport component + WebGPU renderer `prepareFrame` only. No `ZGpuRenderPass`, no `ZContext.activePass`, no `ZScene` changes.

**Files touched**:

| File | Change |
|------|--------|
| `engine/.../components/ZViewport.kt` | Added `buildRenderPassDescriptor()`; documented `render()` vs builder |
| `engine/.../components/ZViewportRenderer.kt` (WebGPU) | Public builder; descriptor shell reuse; intra-frame idempotency; `render()` no-op; depth destroy on resize |
| `engine/.../components/ZViewportRenderer.kt` (Metal/OGL) | `buildRenderPassDescriptor()` returns `false`; `render()` unchanged (no-op / `glClear`) |
| `engine/.../renderer/ZRenderer.wgpu.kt` | `prepareFrame()` calls `viewport.buildRenderPassDescriptor()` instead of `viewport.render()` |

**Behaviour after this slice**:

- WebGPU descriptor is built **once per frame** on the renderer path (`prepareFrame`).
- Clear remains via pass descriptor (`loadOp = CLEAR`, `depthClearValue = 1.0f`).
- `ZScene.internalRender()` still calls `viewport.render()` — **no-op on WebGPU** (ghost call retained for OGL API parity).
- Descriptor attachment objects are reused; full rebuild only on swapchain or view-box change.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.

### 2026-06 — Phase C2: Frame + command encoder orchestration

**Scope**: `ZGpuFrame` and `ZGpuCommandEncoder` in `context/gpu/`, Metal/WebGPU actuals, common `ZRendererBase.renderFrame()` orchestration, OpenGL stubs, pass lifecycle cleanup. See [gpu-phase-c2-frame-orchestration.md](./phases/gpu-phase-c2-frame-orchestration.md) §8.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded after the C2 doc/lifecycle closure.

**Deferred to next PRs**:

- `expect`/`actual` on `ZScene` to drop ghost `viewport.render()` on WebGPU (optional).
- KDoc on `ZRendererBase` hooks.
- Metal `configureRenderState` alignment with WebGPU depth defaults.
- Later phases: multi-pass scaffolding, `ZGpuDevice` factories, OpenGL pass adapter.

### 2026-06 — Phase B: GPU active pass layer

**Scope**: `zernikalos.context.gpu/`, `activePass` on `ZRenderingContext`, draw-site migration. See [gpu-active-pass-layer.md](./phases/gpu-active-pass-layer.md) §14.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.

**Still pending**: visual smoke test on DemoApps / iosSample.

### 2026-06 — Phase C1: Common render pass descriptor

**Scope**: `ZGpuRenderPassDescriptor` in `context/ZGpuEnums.kt`, viewport builder, WebGPU encode + Metal apply. See [gpu-phase-c1-render-pass-descriptor.md](./phases/gpu-phase-c1-render-pass-descriptor.md) §13.

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, `compileAndroidMain` succeeded.
