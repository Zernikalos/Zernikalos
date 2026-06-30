# Proposal: Render Target Resources (`ZRenderTarget`)

**Status**: Draft
**Created**: 2026
**Related**: [GPU Rendering Pipeline Refactor](./gpu-rendering-pipeline-refactor.md), [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md), [Deterministic Dispose Lifecycle](./deterministic-dispose-lifecycle.md)

---

## Summary

Offscreen rendering (editor viewports, thumbnails, shadow maps, post-processing intermediates, reflection probes) needs a **first-class GPU resource** that owns color/depth attachments and can produce a `ZGpuRenderPassDescriptor`. That responsibility must not be folded into `ZViewport`, which should remain focused on **camera framing, view box, and clear policy**.

This proposal defines `ZRenderTarget` as a standalone resource, how it connects to `ZViewport` and multi-pass rendering, and a phased path from the current swapchain-only setup.

---

## 1. Problem

Today:

| Backend | Where "target" lives | Limitation |
|---------|----------------------|------------|
| WebGPU | Swapchain via `webGPUContext.getCurrentTexture()` inside `ZViewportRenderer` | Depth texture on viewport; no reusable offscreen target abstraction |
| Metal | `MTKView` drawable + implicit depth | Platform-owned; no engine-level render target type |
| OpenGL | Default framebuffer | Immediate mode; no FBO wrapper in common code |

`ZViewport` (WebGPU) already creates `depthTexture` and builds `renderPassDescriptor` in `render()`. Mixing swapchain, offscreen editor surfaces, and future shadow atlases into `ZViewport` would conflate:

- **Where** we render (GPU attachments)
- **What region** we render (view box)
- **How** we render (clear color, pass state)

The [GPU pipeline refactor](./gpu-rendering-pipeline-refactor.md) needs attachment sources for multiple passes per frame; this proposal supplies that piece without bloating the viewport component.

---

## 2. Goals

1. Introduce **`ZRenderTarget`** — owns color and optional depth attachments at a given size.
2. Keep **`ZViewport`** as view/clear/pass-state policy; viewport **references** a target.
3. Support **swapchain** (screen) and **offscreen** (texture) with the same descriptor builder API.
4. Integrate with **`ZGpuRenderPassDescriptor`** from the GPU pipeline proposal.
5. Define **resize** and **dispose** ownership clearly.

### Non-goals (first iteration)

- MRT (multiple color attachments per target).
- MSAA resolve targets (can be added later on the same type).
- Automatic render-target pooling / LRU cache.
- Editor UI integration in Nest (consumer of this API only).

---

## 3. Concepts

### 3.1 Viewport vs render target

| Concept | Responsibility | Example |
|---------|----------------|---------|
| `ZViewport` | View box, clear color, pass state defaults | Full-window game view |
| `ZRenderTarget` | GPU color/depth surfaces at `width × height` | 512×512 editor preview texture |
| `ZGpuRenderPassDescriptor` | Per-frame/pass binding of attachments + load/store ops | Built from target + clear policy |

Analogy: viewport is the **camera framing**; render target is the **film/sensor**.

### 3.2 Target kinds

```kotlin
sealed interface ZRenderTargetSource {
    /** Platform swapchain / canvas drawable (size follows surface). */
    data object Swapchain : ZRenderTargetSource

    /** Fixed or resizable offscreen color (+ optional depth). */
    data class Offscreen(val width: Int, val height: Int, val hasDepth: Boolean) : ZRenderTargetSource
}
```

Swapchain targets are **virtual**: they do not own the color texture (it changes every frame), but they know how to obtain the current drawable view for the descriptor builder.

---

## 4. Proposed API (`commonMain`)

```kotlin
class ZRenderTarget internal constructor(
    val source: ZRenderTargetSource,
    val device: ZGpuDevice,
) {
    val width: Int
    val height: Int
    val hasDepth: Boolean

    /** (Re)allocate GPU surfaces after resize. No-op for swapchain except refreshing cached size. */
    fun resize(width: Int, height: Int)

    /**
     * Update attachment views for this frame (swapchain must refresh drawable view).
     * Returns null if surface is unavailable (zero size, lost device, etc.).
     */
    fun prepareForFrame(): ZRenderTargetFrameViews?

    /** Build pass descriptor combining target attachments and viewport clear/load policy. */
    fun buildPassDescriptor(clear: ZRenderTargetClearPolicy): ZGpuRenderPassDescriptor?

    fun dispose()
}

data class ZRenderTargetFrameViews(
    val colorView: ZGpuTextureView,
    val depthView: ZGpuTextureView?,
)

data class ZRenderTargetClearPolicy(
    val color: ZColor,
    val colorLoadOp: ZLoadOp = ZLoadOp.CLEAR,
    val depthLoadOp: ZLoadOp = ZLoadOp.CLEAR,
    val depthClearValue: Float = 1.0f,  // far plane; see GPU pipeline proposal §9.4
)
```

### 4.1 Viewport integration

```kotlin
class ZViewport {
    /** Default: swapchain target provided by renderer / surface at init. */
    var renderTarget: ZRenderTarget

    fun buildRenderPassDescriptor(): ZGpuRenderPassDescriptor? {
        return renderTarget.buildPassDescriptor(
            ZRenderTargetClearPolicy(color = clearColor)
        )
    }
}
```

Scene rendering and camera logic stay on `ZViewport`; only the **attachment source** is externalized.

### 4.2 Renderer integration (per frame)

```kotlin
fun render() {
    val target = scene.viewport.renderTarget
    target.prepareForFrame() ?: return

    encoder.beginRenderPass(scene.viewport.buildRenderPassDescriptor()!!).use { pass ->
        ctx.withActivePass(pass) {
            scene.render(ctx)
        }
    }
}
```

---

## 5. Backend notes

### 5.1 WebGPU

| Piece | Swapchain target | Offscreen target |
|-------|------------------|------------------|
| Color | `getCurrentTexture().createView()` each frame | Owned `GPUTexture` + view |
| Depth | Owned depth texture (resize with target) | Owned depth texture |
| Lifetime | Color view per frame; depth texture until resize | All owned until `dispose()` |

Migrate existing `ZViewportRenderer.depthTexture` and descriptor construction into `ZRenderTarget` + viewport clear policy.

### 5.2 Metal

| Piece | Swapchain target | Offscreen target |
|-------|------------------|------------------|
| Color | `MTKView.currentDrawable` / pass descriptor from `currentRenderPassDescriptor` | `MTLTexture` render target + optional depth |
| Depth | From `MTKView.depthStencilPixelFormat` | Owned depth texture |

`ZRenderTarget` for swapchain wraps `MTKView` descriptor factory; offscreen creates textures via `MTLDevice`.

### 5.3 OpenGL

| Piece | Swapchain | Offscreen |
|-------|-----------|-----------|
| Implementation | Default framebuffer (FBO id 0) | FBO + color texture + depth renderbuffer |

`ZRenderTarget` OGL adapter binds FBO in `prepareForFrame()` / pass scope.

---

## 6. Use cases (future consumers)

| Use case | Target type | Pass notes |
|----------|-------------|------------|
| Main game view | Swapchain | One pass; current behavior |
| Nest editor viewport | Offscreen N×M | Same scene/camera; different target bound on viewport |
| Thumbnail generator | Offscreen small | Single pass; readback optional |
| Shadow map | Offscreen depth-only (or color+depth) | Separate pass; `ZSceneTree` filter (future) |
| Post-processing ping-pong | Two offscreen color targets | Two passes; out of scope for v1 |

---

## 7. Lifecycle and ownership

Ownership graph:

1. `ZernikalosBase` / renderer creates **default swapchain** `ZRenderTarget` when surface is ready.
2. `ZScene.viewport.renderTarget` defaults to swapchain target; host may assign offscreen target for editor.
3. `ZRenderTarget` owns GPU textures for offscreen; swapchain target owns only depth (if not provided by platform view).
4. `dispose()` order: scene/viewport → render targets → device (per [deterministic dispose](./deterministic-dispose-lifecycle.md)).

Resize rules:

- Swapchain: size from `onViewportResize` / surface callbacks.
- Offscreen: explicit `resize(w, h)` or recreate; invalid mid-frame if resize races — freeze dimensions for the duration of `render()` (same rule as GPU pipeline proposal).

---

## 8. Proposed phases

### Phase RT-A — Types and swapchain adapter

- [ ] Add `ZRenderTarget`, `ZRenderTargetClearPolicy`, `ZRenderTargetFrameViews` in `commonMain`.
- [ ] WebGPU: move depth texture + descriptor attachment wiring from `ZViewportRenderer` into swapchain `ZRenderTarget`.
- [ ] Viewport delegates `buildRenderPassDescriptor()` to target + clear policy.

### Phase RT-B — Offscreen target

- [ ] Implement offscreen `ZRenderTarget` on WebGPU and Metal (OGL FBO optional).
- [ ] `resize()` / `dispose()` with deterministic lifecycle.
- [ ] Sample: render scene to texture, sample in second pass or blit to screen (minimal validation).

### Phase RT-C — Editor / Nest consumer contract

- [ ] Document host API: create offscreen target, assign to editor viewport, readback if needed.
- [ ] No Nest implementation in engine repo — API only.

---

## 9. Relationship to other proposals

| Proposal | Relationship |
|----------|--------------|
| [GPU Rendering Pipeline Refactor](./gpu-rendering-pipeline-refactor.md) | Supplies `ZGpuRenderPassDescriptor`, multi-pass orchestration, `activePass` on `ZContext`. This proposal supplies **attachment sources** for those descriptors. |
| Future `ZSceneTree` | Pass-specific object lists; independent of where attachments live. |
| [Deterministic Dispose Lifecycle](./deterministic-dispose-lifecycle.md) | `ZRenderTarget.dispose()` releases owned textures/FBOs. |

---

## 10. Open questions

1. Should depth be **mandatory** on all offscreen targets, or optional per use case (e.g. UI thumbnail without depth)?
2. Who creates the default swapchain target — `ZRenderer` init or `ZContextCreator` when the surface is ready?
3. Color format policy: always swapchain `preferredFormat`, or configurable for offscreen (sRGB vs linear) for editor?

---

## 11. Success criteria

- `ZViewport` no longer owns depth textures or swapchain views directly (WebGPU).
- Host can assign an offscreen `ZRenderTarget` to a viewport and render the same scene to a texture.
- `buildPassDescriptor()` works for swapchain and offscreen without duplicating attachment logic per backend.
- Depth clear remains **1.0** and compare **LESS**, consistent with [GPU pipeline §9.4](./gpu-rendering-pipeline-refactor.md#94-depth-convention-standard-z-not-reversed-z).
