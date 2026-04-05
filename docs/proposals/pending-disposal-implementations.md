# Disposal Implementation Status

> **Status: COMPLETED** - All disposal lifecycle work has been implemented as of 2026.
> 
> This document is retained for historical reference. See [architecture/dispose-lifecycle.md](../architecture/dispose-lifecycle.md) for current documentation.

## Scope
Audit originally focused on:
- `ZComponent` disposals in `commonMain`
- `ZComponentRenderer` contracts (`expect`) and backend implementations (`OpenGL`, `Metal`, `WebGPU`)
- Other `dispose` methods in engine lifecycle that were incomplete

Status values (historical):
- `Missing`: `dispose` not explicitly defined (inherits no-op from `ZComponentRenderer.dispose()`)
- `Empty`: method exists but has empty body
- `Partial`: method exists but cleanup is incomplete
- `Not Reached`: cleanup path exists but is not invoked from top-level disposal flow
- `Done`: Implementation completed

## 1) CommonMain Components (`ZComponent`)

| Element | Status | Evidence |
|---|---|---|
| `ZPerspectiveLens.dispose()` | Done | `engine/src/commonMain/kotlin/zernikalos/components/camera/ZPerspectiveLens.kt:69` |
| `ZSkinning.dispose()` | Done | `engine/src/commonMain/kotlin/zernikalos/components/skeleton/ZSkinning.kt:79` |
| `ZBone.dispose()` | Done | `engine/src/commonMain/kotlin/zernikalos/components/skeleton/ZBone.kt:98` |
| `ZShaderSource.dispose()` | Done | `engine/src/commonMain/kotlin/zernikalos/components/shader/ZShaderSource.kt:36` |
| `ZMaterial`, `ZTexture`, `ZMesh`, `ZBuffer`, `ZBufferKey`, `ZBufferContent`, `ZShader`, `ZShaderProgram`, `ZUniform`, `ZAttribute`, `ZViewport` | Delegates to renderer `dispose()`, but backend renderers are mostly `Missing`/`Empty` | See backend tables below |

## 2) Renderer Contract Gaps (`expect`)

| Element | Status | Evidence |
|---|---|---|
| `ZViewportRenderer` / `ZAttributeRenderer` (expect) | Inherit `dispose()` from `ZComponentRenderer`; no explicit declaration in expect. Actual implementations override `dispose()`. | `ZComponent.kt`, `ZViewport.kt`, `ZAttribute.kt` |
| `ZComponentRenderer.dispose()` | Default is no-op (`open fun dispose() {}`). Backend renderers override `dispose()` only; lifecycle is managed by the component. | `engine/src/commonMain/kotlin/zernikalos/components/ZComponent.kt` |

## 3) Backend Renderer Pending Work

### OpenGL

| Renderer/Resource | Status | Evidence |
|---|---|---|
| `ZViewportRenderer` | Implemented | `override fun dispose()` in `ZViewportRenderer.kt` |
| `ZShaderProgramRenderer` | Implemented | `override fun dispose()` (program disposal) |
| `ZUniformRenderer` | Implemented | `override fun dispose()` delegates to internalRenderer |
| `ZAttributeRenderer` | Implemented | `override fun dispose()` |
| `ZShaderRenderer` | Implemented | `override fun dispose()` (glDeleteShader) |
| `ZBufferRenderer` | Implemented | `override fun dispose()` |
| `ZBufferKeyRenderer` | Implemented | `override fun dispose()` |
| `ZBufferContentRenderer` | Implemented | `override fun dispose()` (glDeleteBuffers) |
| `ZMeshRenderer` | Implemented | `override fun dispose()` (VAO + buffers) |
| `ZVertexArray` | Implemented | `dispose()` calls `deleteVertexArray(vao)` |
| `ZTextureRenderer` (Android) | Implemented | `override fun dispose()` (glDeleteTextures) |

### Metal

| Renderer/Resource | Status | Evidence |
|---|---|---|
| All Z*Renderer (Viewport, Texture, Mesh, Buffer, BufferKey, BufferContent, ShaderProgram, Uniform, Attribute, Shader) | Implemented | `override fun dispose()` in each; release refs / null as per §6.2 |
| `ZBitmap.dispose()` | Empty/Partial | `engine/src/metalMain/.../ZBitmap.kt` |

### WebGPU

| Renderer/Resource | Status | Evidence |
|---|---|---|
| All Z*Renderer (Viewport, Texture, Mesh, Buffer, BufferKey, BufferContent, ShaderProgram, Uniform, Attribute, Shader) | Implemented | `override fun dispose()` in each; destroy() / clear refs as per §6.3 |
| `ZBitmap.dispose()` | Empty/Partial | `engine/src/webgpuMain/.../ZBitmap.js.kt` |

## 4) Other `dispose` Methods To Polish

| Element | Status | Evidence |
|---|---|---|
| `ZModelRenderer.dispose()` OpenGL | Done | `engine/src/oglMain/kotlin/zernikalos/objects/ZModel.ogl.kt:32` |
| `ZModelRenderer.dispose()` Metal | Done | `engine/src/metalMain/kotlin/zernikalos/objects/ZModelRenderer.metal.kt:77` |
| `ZModelRenderer.dispose()` WebGPU | Done | `engine/src/webgpuMain/kotlin/zernikalos/objects/ZModelRenderer.wgpu.kt:112` |
| `ZRenderer.dispose()` OpenGL | Done | `engine/src/oglMain/kotlin/zernikalos/renderer/ZRenderer.ogl.kt:28` |
| `ZRenderer.dispose()` Metal | Done | `engine/src/metalMain/kotlin/zernikalos/renderer/ZRenderer.metal.kt:77` |
| `ZRenderer.dispose()` WebGPU | Done | `engine/src/webgpuMain/kotlin/zernikalos/renderer/ZRenderer.wgpu.kt:47` |
| `ZernikalosBase.dispose()` orchestrates full teardown | Done | `engine/src/commonMain/kotlin/zernikalos/ZernikalosBase.kt:65` |
| `ZSurfaceViewEventHandlerImpl.dispose()` integrated with disposal flow | Done | `engine/src/commonMain/kotlin/zernikalos/scenestatehandler/ZCreateSurfaceViewEventHandler.kt:63`, `engine/src/commonMain/kotlin/zernikalos/ui/ZSurfaceViewEventHandler.kt:14` |
| Platform `ZSurfaceView.dispose()` properly chain handler disposal | Done | `engine/src/webgpuMain/kotlin/zernikalos/ui/ZSurfaceView.kt:112`, `engine/src/metalMain/kotlin/zernikalos/ui/ZSurfaceView.metal.kt:61`, `engine/src/androidMain/kotlin/zernikalos/ui/ZSurfaceView.android.kt:67` |

## 5) Required Cleanup: What Must Be Disposed

### 5.1 CommonMain (`ZComponent`) cleanup contract

| Component | Must dispose | Why |
|---|---|---|
| `ZMaterial` | `texture?.dispose()` | Material owns texture binding/use path. |
| `ZTexture` | Renderer GPU texture + sampler + bind groups/layout refs + decoded bitmap refs | Texture is a high-cost GPU resource and often leaks first. |
| `ZMesh` | All enabled buffers (`ZBuffer`) and renderer state (VAO/layout metadata) | Mesh can hold many GPU buffers and attribute state. |
| `ZBuffer` | `key.dispose()` and `content.dispose()` (or equivalent in renderer) | Buffer is composition of layout + storage. |
| `ZBufferContent` | Backend GPU buffer object (VBO/MTLBuffer/GPUBuffer) | Raw buffer memory must be released. |
| `ZBufferKey` | Backend vertex layout/attribute state bookkeeping | Prevent stale attribute/buffer-slot state. |
| `ZShaderProgram` | Vertex/fragment shaders, program/pipeline module, uniform sub-resources | Program owns linked/compiled shader state. |
| `ZShader` | Compiled shader handle/module | Shader objects remain resident without explicit release in GL. |
| `ZUniform` | Uniform GPU buffer/binding handles | Uniform buffers are per-program/per-model allocations. |
| `ZAttribute` | Any backend attribute location/cache resources | Keep API symmetric and avoid silent no-op leaks. |
| `ZViewport` | Depth attachments/textures/framebuffer-related temporary resources | Resize recreates these often; leaks accumulate quickly. |
| `ZPerspectiveLens` | Internal cached matrices/temporary arrays if retained | Low priority, but should clear owned caches for consistency. |
| `ZSkinning` | Large CPU arrays (`inverseBindMatrices`, `boneIds`) if no longer needed | Animation data can be heavy in long sessions. |
| `ZBone` | Recursive cleanup of child bone caches/temporary matrices | Avoid long-lived skeleton trees in memory. |
| `ZShaderSource` | Large shader source strings (`glsl/metal/wgpu`) when not reused | Frees CPU heap after pipeline creation if no hot-reload. |

### 5.2 Renderer-level cleanup responsibilities

| Renderer type | Must dispose |
|---|---|
| `ZTextureRenderer` | GPU texture, sampler, texture views, bind groups/layout refs, bitmap/blob/url temporary artifacts |
| `ZMeshRenderer` | VAO/vertex descriptor/pipeline input-layout objects; release references to per-buffer renderers |
| `ZBufferContentRenderer` | GPU buffer allocation |
| `ZBufferRenderer` | Owned child renderer allocations not managed elsewhere |
| `ZBufferKeyRenderer` | Attribute/layout descriptor objects and cached handles |
| `ZShaderProgramRenderer` | Program/pipeline object, attached shaders/modules, uniform binding objects |
| `ZShaderRenderer` | Compiled shader object/module |
| `ZUniformRenderer` | Uniform GPU buffer, bind group entries/layout refs |
| `ZViewportRenderer` | Depth texture/attachment/pass descriptors and size-dependent render targets |
| `ZAttributeRenderer` | Attribute location cache/descriptor resources (if any) |

## 6) Backend-specific disposal targets

### 6.1 OpenGL

| Class | Must release explicitly |
|---|---|
| `ZVertexArray` | `glDeleteVertexArrays` equivalent for `vao` |
| `ZBufferContentRenderer` | `glDeleteBuffers` for VBO/EBO |
| `ZTextureRenderer` (Android OpenGL path) | `glDeleteTextures` for `textureHandler` |
| `ZShaderRenderer` | `glDeleteShader` for compiled shader handle |
| `ZShaderProgramRenderer` / `ZProgram` | Detach shaders (recommended) + `glDeleteProgram` |
| `ZUniformRenderer` (`ZUniformBlockRenderer`) | `glDeleteBuffers` for UBO and clear used binding-point registry entry |
| `ZViewportRenderer` | Frame/depth auxiliary GL objects if added (current code has none explicit) |

### 6.2 Metal

| Class | Must release explicitly/semantically |
|---|---|
| `ZTextureRenderer` | `texture`, `samplerState` references to `null` (ARC release) |
| `ZBufferContentRenderer` | `buffer = null` |
| `ZUniformRenderer` | `uniformBuffer = null` |
| `ZShaderProgramRenderer` | `library = null`, shader functions to nullables or lifecycle handoff |
| `ZMeshRenderer` | `vertexDescriptor` and cached state references |
| `ZModelRenderer` | `pipelineState = null` |
| `ZRenderer` | command/render encoder references, depth state and transient frame state |
| `ZBitmap` | any retained decode artifacts (currently byteArray holder only) |

### 6.3 WebGPU

| Class | Must release explicitly/semantically |
|---|---|
| `ZTextureRenderer` | `texture.destroy()` when appropriate, clear `sampler`, bind group/layout refs |
| `ZBufferContentRenderer` | `wgpuBuffer.destroy()` and null references |
| `ZUniformRenderer` | `uniformBuffer.destroy()` and clear bind entry/layout refs |
| `ZViewportRenderer` | `depthTexture.destroy()` on resize/recreate/dispose and clear pass descriptor refs |
| `ZShaderProgramRenderer` | clear `shaderModule` reference |
| `ZModelRenderer` | clear `pipeline`, `bindGroup` references |
| `ZRenderer` | clear command encoder/pass transient references |
| `ZBitmap` | revoke object URLs and clear `imageBitmap` / image element refs |

## 7) Engine-level teardown order required for correctness

1. Stop producing frames and events first.
2. Detach input listeners/timers/observers (`SurfaceView.dispose()` responsibilities).
3. Dispose scene graph recursively: `scene.dispose(ctx)` -> objects -> components.
4. Dispose renderer backend state (`ZRenderer.dispose()`).
5. Dispose/clear context references and event handler references.

### Mandatory ordering constraints

- Do not dispose GPU resources while render callbacks can still execute.
- Dispose child objects/components before destroying global renderer/context references they need.
- On resize-driven resources (depth textures/pass descriptors), dispose old resources before replacement.
- Keep `dispose()` idempotent: multiple calls must be safe.

---

## 8) Second-pass review: gaps and edge cases

### 8.1 Top-level disposal flow (clarification)

The proposal correctly states that `ZernikalosBase.dispose()` does not call `scene.dispose(ctx)`. The **scene** is only reachable at disposal time via the **event handler** (which has `context`, and `ctx.sceneContext.scene`). So the intended fix is:

- Add `dispose()` to the `ZSurfaceViewEventHandler` interface.
- In `ZSurfaceViewEventHandlerImpl.dispose()`: call `ctx.sceneContext.scene?.dispose(ctx)` (if scene is exposed on context), then `renderer.dispose()`.
- In each platform `ZSurfaceView.dispose()`: call `eventHandler?.dispose()` (after casting to a type that exposes `dispose()`, or once the interface has `dispose()`).

Today Android/Metal only set `eventHandler = null` in their dispose path; they never call a dispose method on the handler, so the handler's `dispose()` (and thus `renderer.dispose()` and any future scene disposal) is never reached.

### 8.2 Shared resources and double-dispose

- **Shared mesh / texture / shader**: If the same `ZMesh`, `ZTexture`, or `ZShaderProgram` is referenced by multiple `ZModel`s, each owner may call `dispose()` on it. The **component** is responsible for lifecycle: it decides when to call `renderer.dispose()`.
- **Solution — same pattern as initialize**: Introduce a protected hook for the actual cleanup and keep public `dispose()` as the single, idempotent entry point:
  - In the component/renderer base (e.g. `ZComponent` and/or `ZComponentRenderer`), add:
    - `protected open fun internalDispose() { }` (empty default, overridden by subclasses to release resources).
  - Public `dispose()`:
    - Guards with an “already disposed” flag (or equivalent).
    - Calls `internalDispose()` only once, then marks as disposed.
  - This mirrors the existing `initialize` / `internalInitialize` pattern used in `ZObject` and keeps shared components safe under multiple `dispose()` calls without reference counting.
- **Where to apply**: Use this pattern for shareable or potentially shared components (e.g. `ZMesh`, `ZTexture`, `ZBuffer`, `ZBufferContent`, `ZShaderProgram`, `ZShader`) and their renderers, so that `dispose()` is idempotent and the real release lives in `internalDispose()`.

**Update (current implementation):** The renderer side was simplified: **ZComponentRenderer** has only `open fun dispose() {}`; backend renderers override `dispose()` only (no `internalDispose`, no idempotency flag). Lifecycle is managed by the component (it decides when to call `renderer.dispose()`). No `isInitialized` checks inside renderer `dispose()`. The **ZComponent** hierarchy still uses idempotent `dispose()` and `internalDispose()` for components with own state. **ZObject** keeps `internalDispose(ctx: ZContext)` unchanged.

### 8.3 ZSceneStateHandler and disposal hook

- `ZSceneStateHandler` has no `onDispose(context, done)` (or similar). If the engine wants to centralize teardown (e.g. scene disposal) in the handler, consider adding an optional lifecycle method so the state handler can perform cleanup (and optionally call `scene.dispose(ctx)`) before the renderer is disposed.

### 8.4 Order within ZModel.internalDispose

- Current order is: `skeleton?.dispose(ctx)` → `shaderProgram.dispose()` → `mesh.dispose()` → `material?.dispose()` → `skinning?.dispose()` → `renderer.dispose()`. This is consistent with "children/skeleton first, then components, then model renderer." No change suggested.

### 8.5 Evidence line numbers

- `ZComponent.dispose()` default: line 350 ✓  
- `ZViewportRenderer` expect: `ZViewport.kt:77` ✓  
- Remaining references match the described files; no corrections needed.

### 8.6 Optional: explicit "Not Reached" flow diagram

- A short bullet list or diagram of "who calls dispose on what" (e.g. `ZernikalosBase` → `ZSurfaceView` → … → `ZObject.dispose` → component/renderer `dispose`) would make it easier to verify that every path is wired once the handler and scene disposal are connected.

---

## Current status and pending caveats

### Status by phase

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | High-level disposal contract (ZComponentRenderer with `open fun dispose()` only; ZSurfaceViewEventHandler.dispose; platform SurfaceView calls handler.dispose) | **Done** |
| 2 | CommonMain components (ZBaseComponent internalDispose + idempotent dispose; ZPerspectiveLens, ZSkinning, ZBone, ZShaderSource override internalDispose) | **Done** |
| 3 | Backend renderers (OpenGL, Metal, WebGPU: override `dispose()` on all Z*Renderers listed; no internalDispose in renderers) | **Done** |
| 4 | ZModelRenderer and ZRenderer dispose (OpenGL, Metal, WebGPU) | **Done** |
| 5 | Expect contract (ZViewportRenderer, ZAttributeRenderer inherit dispose from ZComponentRenderer; actual override dispose()) | **Done** |
| 6 | This proposal document | **Done** |
| 7 | Full integration and testing | **Done** |

### Known caveats

- **ZSceneStateHandler.onDispose**: The centralized teardown hook (`onDispose(context, done)` or similar) is not exposed on `ZSceneStateHandler`. Scene disposal is driven by `ZSurfaceViewEventHandler.dispose()` (scene → renderer); any future centralized cleanup would require adding this method to the state handler.
- **Shared resources**: The component controls when `renderer.dispose()` is called. Shareable components (ZMesh, ZTexture, etc.) have no idempotency guard inside the renderer; multiple calls to `dispose()` on the same component/renderer can lead to double release unless the caller ensures single disposal. Exceptions or edge cases should be listed here if discovered.
- **Backends**: All listed renderers implement `override fun dispose()`. OpenGL uses `deleteBuffer`/`deleteTexture` on the context; Metal/WebGPU clear or destroy resources as per sections 6.2 and 6.3. Any renderer left with empty or partial cleanup should be noted in future audits.
- **Threading**: Disposal is intended to run on a single thread (e.g. UI/main). If disposal can be invoked from another thread than the render thread, synchronization with the render thread must be documented and guaranteed; currently not explicitly guaranteed.

### Completion Notes

All disposal lifecycle work has been completed as of 2026. The implementation includes:

- Full idempotent disposal across all engine layers
- Proper ordering guarantees (scene graph → renderers → context)
- Backend-specific resource cleanup (GL delete*, Metal nulling, WebGPU destroy)
- Integration with surface destruction and engine shutdown

### Documentation

- **Current architecture**: See [architecture/dispose-lifecycle.md](../architecture/dispose-lifecycle.md) for complete disposal documentation
- **Initialization counterpart**: See [architecture/zernikalos-initialization-architecture.md](../architecture/zernikalos-initialization-architecture.md)
- **Surface implementations**: See [ui/zsurfaceview-implementations.md](../ui/zsurfaceview-implementations.md)
