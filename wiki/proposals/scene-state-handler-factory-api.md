# Proposal: Scene State Handler Factory API

**Status**: Draft  
**Created**: 2025  
**Related**: [Zernikalos Initialization Architecture](../architecture/zernikalos-initialization-architecture.md), [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md)

---

## Summary

`ZSceneStateHandler` is an interface that apps implement to hook into the engine's lifecycle (ready, resize, update, render). Methods use async-style callbacks (`done: () -> Unit`) to signal completion. When this interface is exported to Swift (Kotlin/Native) or JavaScript, implementors often **do not call** the `done` callback, causing the engine to hang or skip the render pipeline. This proposal introduces a **factory-based API with closures** and **sync/async-aware wrapper types**, so Swift/JS consumers never implement the protocol directly and can use simpler sync signatures where appropriate, without callback pitfalls.

---

## Problem

1. **Protocol export to Swift/JS**  
   `ZSceneStateHandler` becomes a Swift protocol or JS interface. Native implementors must implement `onUpdate(context, done)` and `onRender(context, done)`, and are responsible for calling `done()` when finished. In practice, implementors frequently forget or omit the callback, leading to:
   - `renderer.render()` never running (the engine waits for `done()`)
   - `isRendering` never reset, blocking subsequent frames

2. **Callback semantics are easy to ignore**  
   Swift/JS developers expect simple function signatures. A `done` callback that must be invoked is not idiomatic and is easy to overlook, especially when the handler's work is synchronous.

3. **Mix of sync and async needs**  
   Most handlers are synchronous (onUpdate, onRender: do work, return). Some (e.g. onReady for asset loading) are genuinely async. Adding more protocol methods (sync vs async variants) would **grow the protocol** and force Swift/JS to implement more methods, making the situation worse.

---

## Goals

- **No protocol implementation from Swift/JS**: Apps use a factory that accepts closures instead of implementing `ZSceneStateHandler`.
- **Flexible sync/async per hook**: Each hook (onUpdate, onRender, onResize) can be either `(context) -> Unit` (sync) or `(context, done) -> Unit` (async), without expanding the protocol.
- **Backward compatible**: Existing Kotlin code that implements `ZSceneStateHandler` should continue to work; the factory becomes an alternative entry point, not a replacement for the interface itself.

---

## Proposed Direction: Factory + Sync/Async Wrapper Types

### 1. Sync/Async Wrapper (e.g. `OnUpdateHandler`)

For each hook that can be sync or async, introduce a wrapper type with overloaded `invoke` in a companion object:

```kotlin
class OnUpdateHandler private constructor(
    private val fn: (ZContext, () -> Unit) -> Unit
) {
    companion object {
        operator fun invoke(handler: (ZContext) -> Unit) = OnUpdateHandler { ctx, done ->
            handler(ctx)
            done()
        }
        operator fun invoke(handler: (ZContext, () -> Unit) -> Unit) = OnUpdateHandler(handler)
    }
    
    fun invoke(context: ZContext, done: () -> Unit) = fn(context, done)
}
```

The compiler selects the correct overload from the lambda passed by the user. Internally, both forms are normalized to `(ZContext, () -> Unit) -> Unit`, which matches `ZSceneStateHandler.onUpdate(context, done)`.

### 2. Factory API

```kotlin
fun createSceneStateHandler(
    onReady: (ZContext, () -> Unit) -> Unit,
    onUpdate: OnUpdateHandler? = null,
    onRender: OnUpdateHandler? = null,
    onResize: OnResizeHandler? = null,  // same pattern, with width/height
): ZSceneStateHandler
```

- `onReady` stays async-only (typically used for loading).
- `onUpdate`, `onRender`, `onResize` use the wrapper and are optional; if omitted, default behaviour is no-op then `done()`.

### 3. Internal Implementation

The factory returns a `ZSceneStateHandler` that delegates to the closures:

```kotlin
private class ZSceneStateHandlerImpl(
    private val onReady: (ZContext, () -> Unit) -> Unit,
    private val onUpdate: OnUpdateHandler?,
    private val onRender: OnUpdateHandler?,
    private val onResize: OnResizeHandler?,
) : ZSceneStateHandler {

    override fun onReady(context: ZContext, done: () -> Unit) = onReady(context, done)

    override fun onUpdate(context: ZContext, done: () -> Unit) {
        onUpdate?.invoke(context, done) ?: done()
    }

    override fun onRender(context: ZContext, done: () -> Unit) {
        onRender?.invoke(context, done) ?: done()
    }

    override fun onResize(context: ZContext, width: Int, height: Int, done: () -> Unit) {
        onResize?.invoke(context, width, height, done) ?: done()
    }
}
```

### 4. Usage from Kotlin

```kotlin
createSceneStateHandler(
    onReady = { ctx, done -> /* init... */ done() },
    onUpdate = OnUpdateHandler { ctx -> /* sync */ },
    onRender = OnUpdateHandler { ctx, done -> /* async */ done() },
)
```

### 5. Usage from Swift / JS

- Swift/JS use the factory; they pass closures, not a protocol implementation.
- Sync handlers: `OnUpdateHandler { ctx in ... }` — no `done` in sight.
- Async handlers: `OnUpdateHandler { ctx, done in ...; done() }` when explicitly needed.
- `ZSceneStateHandler` remains for Kotlin implementors who prefer the interface; the engine accepts either the interface or the factory-created implementation.

---

## Wrapper Types to Introduce

| Wrapper           | Sync signature                          | Async signature                                      |
|------------------|------------------------------------------|-----------------------------------------------------|
| `OnUpdateHandler` | `(ZContext) -> Unit`                     | `(ZContext, () -> Unit) -> Unit`                    |
| `OnRenderHandler` | `(ZContext) -> Unit`                     | `(ZContext, () -> Unit) -> Unit`                    |
| `OnResizeHandler` | `(ZContext, Int, Int) -> Unit`           | `(ZContext, Int, Int, () -> Unit) -> Unit`          |

---

## Recommended Path

1. **Introduce wrapper types** (`OnUpdateHandler`, `OnRenderHandler`, `OnResizeHandler`) in a new package or alongside `ZSceneStateHandler`.
2. **Add `createSceneStateHandler(...)`** factory that accepts `onReady` and optional wrappers, returns `ZSceneStateHandler`.
3. **Update samples and docs** to demonstrate the factory as the recommended approach for Swift/JS consumers.
4. **Keep `ZSceneStateHandler`** as the internal contract; no breaking changes for existing Kotlin implementors.

---

## Open Questions

- Should `createSceneStateHandler` be the **only** supported way to provide a handler from Swift/JS in the future, or should we continue to allow direct protocol conformance for advanced use cases?
- Should `onReady` also support a sync variant via a wrapper, or remain async-only given its typical loading semantics?
- Naming: `OnUpdateHandler` vs `SceneUpdateHandler` vs `UpdateHandler` — prefer consistency with existing `onX` naming?

---

## Non-goals (out of scope)

- Changing the internal flow in `ZCreateSurfaceViewEventHandler` or the engine's expectation that `done()` is called; the factory-created implementation guarantees correct callback invocation.
- Deprecating or removing `ZSceneStateHandler`; it stays as the internal interface and as an option for Kotlin implementors.
- Supporting cancellable or timeout-based `done` semantics; the proposal assumes the handler either calls `done` synchronously or asynchronously, with no engine-side fallback if it is never called (the factory wrappers ensure sync handlers always invoke `done`).
