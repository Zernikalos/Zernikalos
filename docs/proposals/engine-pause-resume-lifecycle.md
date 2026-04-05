# Proposal: Engine Pause/Resume Lifecycle on Top of Deterministic Loop Control

**Status**: Draft
**Created**: 2026
**Related**: [Deterministic Dispose Lifecycle for Engine, Scene, and Render Resources](./deterministic-dispose-lifecycle.md), [Zernikalos Initialization Architecture](../architecture/zernikalos-initialization-architecture.md), [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md)

---

## Summary

After dispose semantics are explicit, pause can be modeled as a first-class engine state instead of ad-hoc platform behavior. This proposal adds a unified pause/resume lifecycle so the engine can temporarily stop frame processing without destroying the scene or GPU resources.

Pause should:

- Stop update/render callbacks immediately.
- Keep scene and GPU state intact.
- Resume deterministically from the same state.
- Work consistently across WebGPU, Android, and Metal.

---

## Problem

Current platform behavior is uneven:

- Android has native `onPause()`/`onResume()` paths on `GLSurfaceView`, but engine core has no unified pause API.
- WebGPU loop in `ZJsSurfaceView` keeps ticking unless disposed.
- Metal delegate keeps rendering unless host disables draw loop.
- `ZSurfaceViewEventHandlerImpl` has no paused state, so frame consumption cannot be centrally gated.

Result: host apps cannot reliably pause simulation/rendering without disposing the whole engine.

---

## Goals

- Add engine-level `pause()` and `resume()` APIs.
- Gate update/render at a single point in core lifecycle.
- Let each `ZSurfaceView` implementation map pause/resume to platform-native mechanisms.
- Avoid resource destruction/recreation during pause.

---

## Non-goals

- Full simulation time-scaling/time-dilation system.
- Background asset streaming policy.
- Automatic mobile lifecycle integration (host still decides when to pause/resume).

---

## Proposed Direction

### 1. Engine Lifecycle State Machine

Introduce explicit engine states:

```kotlin
enum class ZEngineState {
    CREATED,
    INITIALIZING,
    RUNNING,
    PAUSED,
    DISPOSING,
    DISPOSED
}
```

Valid transitions:

- `CREATED -> INITIALIZING -> RUNNING`
- `RUNNING -> PAUSED -> RUNNING`
- `RUNNING|PAUSED -> DISPOSING -> DISPOSED`

### 2. Public Engine Control API

In `ZernikalosBase`:

```kotlin
fun pause()
fun resume()
val state: ZEngineState
```

Rules:

- `pause()` is idempotent.
- `resume()` is no-op unless `state == PAUSED`.
- `resume()` must fail fast or no-op when disposing/disposed.

### 3. Surface Contract Additions

Extend `ZSurfaceView` with optional pause controls:

```kotlin
interface ZSurfaceView {
    fun pause()
    fun resume()
}
```

Default compatibility path:

- If a platform cannot truly stop the native loop, it still must stop calling `eventHandler.onRender()`.

Platform mapping:

- WebGPU (`ZJsSurfaceView`): clear interval / cancel animation frame on pause; restart on resume.
- Android (`ZAndroidSurfaceView`, `ZernikalosView`): use `nativeSurfaceView.onPause()` and `onResume()`.
- Metal (`ZMtlSurfaceView`): toggle `MTKView.paused` (or equivalent draw disabling) and keep delegate attached.

### 4. Core Render Gate

Add a gate in `ZSurfaceViewEventHandlerImpl`:

- If `paused`, ignore `onRender()`.
- Continue accepting `onResize()` and updating `context.screenWidth/screenHeight`.
- Do not call `stateHandler.onUpdate()` or `stateHandler.onRender()` while paused.

This central gate guarantees consistent behavior even if a platform emits a stray render callback.

### 5. Optional Scene Hooks

To support game/app-specific behavior, add optional hooks to `ZSceneStateHandler`:

```kotlin
fun onPause(context: ZContext, done: () -> Unit): Unit = done()
fun onResume(context: ZContext, done: () -> Unit): Unit = done()
```

Typical use:

- Pause: stop audio, stop action players, freeze external systems.
- Resume: re-sync timers and restart systems.

---

## Interaction with Dispose

Pause and dispose must compose safely:

1. If `dispose()` is called while paused, engine transitions to `DISPOSING` directly.
2. `resume()` after dispose start is ignored.
3. Dispose always wins over pause.

This avoids accidental frame restart during teardown.

---

## Implementation Plan

1. Add `ZEngineState` and state transitions in `ZernikalosBase`.
2. Add `pause()/resume()` to `ZSurfaceView` and implement per platform.
3. Add `paused` gate to `ZSurfaceViewEventHandlerImpl`.
4. Optionally add `ZSceneStateHandler.onPause/onResume` with defaults.
5. Add tests and debug logs for illegal transitions.

---

## Validation Plan

1. Web: pause window with active animation; verify no new queue submit calls.
2. Android: `Activity.onPause/onResume` wiring; verify rendering halts and resumes without reinitialization.
3. Metal: verify drawable callbacks stop in pause state and restart cleanly.
4. Pause/resume stress loop (100+ cycles): no invalid WebGPU errors, no duplicate initialization logs.

---

## Open Questions

- Should paused mode still process input queue, or freeze input dispatch completely?
- Should we expose a `renderOneFrame()` API for editor-style paused stepping?
- Should action/animation systems auto-pause by default, or remain app-controlled via `onPause/onResume` hooks?
