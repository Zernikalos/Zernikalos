# Proposal: Deterministic Dispose Lifecycle for Engine, Scene, and Render Resources

**Status**: Draft
**Created**: 2026
**Related**: [Zernikalos Initialization Architecture](../architecture/zernikalos-initialization-architecture.md), [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md), [Scene State Handler Factory API](./scene-state-handler-factory-api.md)

---

## Summary

`ZernikalosBase.dispose()` currently delegates only to `surfaceView.dispose()`. This is not enough for WebGPU and other backends because render loop callbacks, renderer internals, scene objects, and component GPU resources are still alive or still reachable for one more frame. The result is invalid-texture/invalid-command-buffer cascades during window close or fast attach/detach cycles.

This proposal introduces a deterministic, ownership-driven dispose architecture across four layers:

1. `UI` (`ZSurfaceView` and platform delegates)
2. `Renderer` (`ZSurfaceViewEventHandler` + `ZRenderer` + rendering context)
3. `ZObject` tree (scene graph)
4. `ZComponents` and renderers (GPU resources)

---

## Problem

Observed current behavior:

1. `ZernikalosBase.dispose()` only calls `surfaceView.dispose()`.
2. `ZSurfaceViewEventHandlerImpl` has no disposed state and can still process pending `onRender`/`onResize` callbacks.
3. `ZObject` has initialize/render/resize lifecycle but no teardown lifecycle.
4. `ZComponentRenderer` has initialize/bind/unbind/render but no release lifecycle.
5. WebGPU wrappers declare no explicit `destroy()` for `GPUBuffer`/`GPUTexture`, so resources cannot be deterministically freed.

Practical consequences:

- One last frame can execute while surface/canvas size is `0x0`.
- Render pass creation fails and cascades into invalid texture/view/command buffer errors.
- Reopening a view/window may reuse stale graph/renderer state.
- Memory/resource pressure accumulates in long-running sessions.

---

## Goals

- Provide a deterministic, idempotent dispose flow from engine root to GPU handles.
- Prevent rendering and resize work after dispose starts.
- Define clear ownership and release responsibility per layer.
- Keep compatibility with existing scene and component APIs during migration.

---

## Non-goals

- Redesigning scene update/render semantics.
- Introducing automatic reference counting for all resources.
- Replacing platform-specific surface implementations.

---

## Proposed Direction

### 1. Introduce Concrete Lifecycle Fields and Hooks (No Shared Interfaces)

Instead of introducing new common interfaces, lifecycle control is implemented directly in the classes that own resources (`ZernikalosBase`, `ZSurfaceViewEventHandlerImpl`, `ZObject`, `ZComponentRenderer`, and rendering contexts).

Design constraints:

- `dispose()` must be idempotent.
- `dispose()` must be safe to call from host lifecycle callbacks.
- Objects in `isDisposing`/`isDisposed` state must ignore render/update/resize work.

### 2. Define Ownership and Dispose Order

Ownership graph:

1. `ZernikalosBase` owns: `surfaceView`, event handler, `context`, scene root.
2. `ZSurfaceViewEventHandlerImpl` owns: render loop scheduling guards.
3. `ZContext` owns: `sceneContext`, `renderingContext`, event queue.
4. `ZScene` (`ZObject` root) owns: children + components.
5. Components own their renderer internals and GPU handles.

Required dispose order:

1. Stop frame production (UI loop / delegate / interval / animation frame).
2. Stop frame consumption (`ZSurfaceViewEventHandlerImpl` gates).
3. Dispose scene graph (`ZObject` recursive teardown).
4. Dispose rendering resources (component renderers, viewport/depth, context).
5. Dispose input/event systems.
6. Null out references for GC friendliness.

### 3. Extend Core Types with Teardown Hooks

#### `ZSurfaceViewEventHandler`

Add control method:

```kotlin
interface ZSurfaceViewEventHandler {
    fun onReady()
    fun onRender()
    fun onResize(width: Int, height: Int)
    fun dispose()
}
```

`ZSurfaceViewEventHandlerImpl`:

- Add `isDisposed` and `isDisposing` flags.
- Short-circuit `onRender()` and `onResize()` when disposing/disposed.
- Ensure pending callbacks (`onUpdate`/`onRender` done lambdas) cannot re-enter render after dispose.

#### `ZObject`

Add protected hook plus recursive public method:

```kotlin
fun dispose(ctx: ZContext) {
    internalDispose(ctx)
    children.forEach { it.dispose(ctx) }
}

protected open fun internalDispose(ctx: ZContext) {}
```

This gives a deterministic place to release object-owned resources.

#### `ZComponentRenderer`

Add teardown method:

```kotlin
abstract class ZComponentRenderer(...) {
    abstract fun initialize()
    open fun dispose() {}
}
```

Component renderers release backend resources here.

### 4. Backend Resource Release Requirements (All Backends)

Resource teardown must be explicit for every rendering backend, not only WebGPU.

WebGPU minimum cleanup targets:

- `ZViewportRenderer`: destroy old depth texture on resize and on dispose.
- `ZUniformRenderer`: destroy `uniformBuffer`.
- `ZBufferContentRenderer`: destroy `wgpuBuffer`.
- `ZTextureRenderer`: destroy `texture`; clear bind group refs/sampler refs.
- `ZModelRenderer`: clear `pipeline` and `bindGroup` refs.
- `ZWebGPURenderingContext`: clear command encoder/render pass references and invalidate device-bound temporary state.

Android/OpenGL minimum cleanup targets:

- View-level: ensure render callbacks are fully detached before GL thread pauses (`AndroidNativeRenderer.dispose()` + view pause path).
- Renderer-level: release GL-owned resources (buffers, textures, programs, framebuffers) in renderer/component disposal hooks.
- Context-level: clear references tied to the current GL context so re-attach does not reuse stale handles.

Metal minimum cleanup targets:

- View/delegate-level: stop `MTKView` draw callbacks before scene/resource teardown.
- Renderer-level: release Metal resource references (pipeline state, buffers, textures, depth targets) in disposal hooks.
- Context-level: clear command-encoding temporary state and detach drawable-dependent references.

### 5. Engine-level Dispose Entry Point

Upgrade `ZernikalosBase.dispose()` to orchestrate full teardown:

1. Mark engine disposing.
2. Dispose surface event handler first (stop callbacks).
3. Dispose scene root if present.
4. Dispose rendering context + scene context.
5. Dispose `surfaceView`.
6. Mark disposed.

If the API must remain source-compatible, keep current signature and only strengthen behavior.

---

## Compatibility and Migration

Phase 1 (safe scaffolding):

- Add no-op `dispose` hooks on core classes and renderer base types.
- Add dispose gating in `ZSurfaceViewEventHandlerImpl`.

Phase 2 (resource migration):

- Implement renderer-specific disposals for WebGPU, Android/OpenGL, and Metal.
- Add depth texture replacement cleanup on viewport resize.

Phase 3 (scene graph migration):

- Add `ZObject.dispose(ctx)` and migrate object/component classes that own GPU resources.

Phase 4 (strict mode):

- Add debug assertions/logging when render/update is attempted after dispose.

---

## Validation Plan

1. Close/reopen window repeatedly: no backend-specific invalid resource/command errors (WebGPU, OpenGL, Metal).
2. Force resize-to-zero then dispose: no render pass creation attempts.
3. Recreate scene after dispose: resources initialize once, no stale bind groups.
4. Stress test with frequent scene swap + texture-heavy models: stable memory trend.

---

## Open Questions

- Should `ZSceneStateHandler` receive an explicit `onDispose(context, done)` hook?
- Should `ZRenderingContext` expose a common `dispose()` method immediately, or in a second pass?
- Should component teardown run in reverse initialization order for stricter dependency safety?
