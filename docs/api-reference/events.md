# Events API Reference

## Overview

Zernikalos exposes a small, explicit **user input event model** built around three event families:

- **Touch** (`ZTouchEvent`)
- **Mouse** (`ZMouseEvent`)
- **Keyboard** (`ZKeyboardEvent`)

Events are **captured asynchronously** by the platform layer (Android / Web), **enqueued** into a per-context queue, and then **processed synchronously** during the frame update phase.

Key building blocks:

- [`ZContext.eventQueue`](../../engine/src/commonMain/kotlin/zernikalos/context/ZContext.kt): the queue instance bound to a context
- [`ZEventQueue`](../../engine/src/commonMain/kotlin/zernikalos/events/ZEventQueue.kt): accumulates and distributes events
- [`ZEventManager`](../../engine/src/commonMain/kotlin/zernikalos/events/ZEventManager.kt): owned by each [`ZObject`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZObject.kt) and manages listeners
- [`ZInputState`](../../engine/src/commonMain/kotlin/zernikalos/events/ZInputState.kt): current input state (keyboard currently supported)

## Delivery model (important semantics)

### Queued, frame-synchronous processing

The platform enqueues input events at any time, but distribution happens when the engine calls:

- `ZEventQueue.processAll()`

This provides a deterministic "input is processed during the frame" behavior similar to Unity / Unreal / Godot.

### Broadcast to scene objects (no hit testing)

When processing, the queue traverses the scene graph and **broadcasts** each event to **every `ZObject` that has listeners** for that event family.

Implications:

- There is currently **no built-in pointer hit-testing** or "event target" concept.
- If you need object-level filtering, do it in your listener (e.g. based on your own picking system / UI logic).

### Keyboard state is updated before callbacks

Before dispatching keyboard events to listeners, `ZEventQueue.processAll()` updates:

- `context.input.keyboard` (a [`ZKeyboardState`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZKeyboardState.kt))

So your listeners can safely query *current* key state while handling events.

Notes:

- `KEY_DOWN` marks the key pressed
- `KEY_UP` marks the key released
- `KEY_PRESS` does **not** modify state (it is considered a repeat / typed character event)

## Attaching listeners to a `ZObject`

Every `ZObject` owns an event manager:

- `obj.events: ZEventManager`

You can register listeners either as interfaces or as lambdas (the manager wraps lambdas into listener interfaces).

### Touch listeners

Interfaces:

- [`ZObjectTouchListener`](../../engine/src/commonMain/kotlin/zernikalos/events/touch/ZObjectTouchListener.kt)

Event type:

- [`ZTouchEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/touch/ZTouchEvent.kt)

Register:

```kotlin
import zernikalos.events.touch.ZObjectTouchListener
import zernikalos.events.touch.ZTouchEvent
import zernikalos.objects.ZObject

obj.events.addTouchListener(object : ZObjectTouchListener {
    override fun onTouchEvent(obj: ZObject, event: ZTouchEvent) {
        // Handle touch input
    }
})

// Or as a lambda
obj.events.addTouchListener { o, e ->
    // Handle touch input
}
```

### Mouse listeners

Interfaces:

- [`ZObjectMouseListener`](../../engine/src/commonMain/kotlin/zernikalos/events/mouse/ZObjectMouseListener.kt)

Event type:

- [`ZMouseEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/mouse/ZMouseEvent.kt)

Register:

```kotlin
import zernikalos.events.mouse.ZMouseEvent

obj.events.addMouseListener { _, event: ZMouseEvent ->
    // Handle mouse input
}
```

### Keyboard listeners

Interfaces:

- [`ZObjectKeyboardListener`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZObjectKeyboardListener.kt)

Event type:

- [`ZKeyboardEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZKeyboardEvent.kt)

Register:

```kotlin
import zernikalos.events.keyboard.ZKeyboardEvent
import zernikalos.events.keyboard.ZKeyCode
import zernikalos.events.keyboard.ZKeyboardEventType

obj.events.addKeyboardListener { _, event: ZKeyboardEvent ->
    if (event.type == ZKeyboardEventType.KEY_DOWN && event.keyCode == ZKeyCode.Space) {
        // Handle Space down
    }
}
```

### Removing listeners

You can remove one listener instance, or clear all listeners of a given family:

- `removeTouchListener(listener)` / `removeAllTouchListeners()`
- `removeMouseListener(listener)` / `removeAllMouseListeners()`
- `removeKeyboardListener(listener)` / `removeAllKeyboardListeners()`

## Event data model

### Touch events

Type:

- [`ZTouchEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/touch/ZTouchEvent.kt)

Event type enum:

- [`ZTouchEventType`](../../engine/src/commonMain/kotlin/zernikalos/events/touch/ZTouchEventType.kt): `DOWN`, `UP`, `MOVE`, `CANCEL`

Fields:

- `x`, `y`: current pointer position in pixels
- `prevX`, `prevY`: previous pointer position in pixels
- `deltaX`, `deltaY`: difference since last event
- `velocityX`, `velocityY`: pixels per second
- `accelerationX`, `accelerationY`: pixels per second squared
- `type`: `ZTouchEventType`
- `timestamp`: milliseconds
- `pointerId`: multi-touch pointer identifier

### Mouse events

Type:

- [`ZMouseEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/mouse/ZMouseEvent.kt)

Event type enum:

- [`ZMouseEventType`](../../engine/src/commonMain/kotlin/zernikalos/events/mouse/ZMouseEventType.kt): `DOWN`, `UP`, `MOVE`

Fields:

- `x`, `y`, `prevX`, `prevY`, `deltaX`, `deltaY`
- `velocityX`, `velocityY`, `accelerationX`, `accelerationY`
- `type`: `ZMouseEventType`
- `timestamp`: milliseconds
- `button`: pressed button (`0` left, `1` middle, `2` right)
- `buttons`: bitmask of currently pressed buttons

### Keyboard events

Type:

- [`ZKeyboardEvent`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZKeyboardEvent.kt)

Event type enum:

- [`ZKeyboardEventType`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZKeyboardEventType.kt): `KEY_DOWN`, `KEY_UP`, `KEY_PRESS`

Key code enum:

- [`ZKeyCode`](../../engine/src/commonMain/kotlin/zernikalos/events/keyboard/ZKeyCode.kt)

Fields:

- `key`: logical key value (e.g. `"a"`, `"Enter"`, `"ArrowLeft"`)
- `code`: physical key code (e.g. `"KeyA"`, `"Enter"`, `"ArrowLeft"`)
- `keyCode`: type-safe `ZKeyCode` mapping
- `char`: typed character (typically non-null for `KEY_PRESS`, null otherwise)
- `type`: `ZKeyboardEventType`
- `timestamp`: milliseconds
- `ctrlKey`, `shiftKey`, `altKey`, `metaKey`: modifier state
- `repeat`: whether this is a repeat event (key held down)

## Querying input state (keyboard)

`ZContext` exposes an input state container:

- `context.input: ZInputState`

Keyboard is currently supported via:

- `context.input.keyboard: ZKeyboardState`

Example:

```kotlin
import zernikalos.events.keyboard.ZKeyCode

if (context.input.keyboard.isKeyPressed(ZKeyCode.W)) {
    // "W" is currently held down
}
```

## Platform support and event sources

This section documents where input events originate per target.

### Android

Touch is currently captured from the platform surface/view and converted to `ZTouchEvent` instances:

- [`ZAndroidSurfaceView`](../../engine/src/androidMain/kotlin/zernikalos/ui/ZSurfaceView.android.kt) uses `setOnTouchListener(...)` and enqueues converted touch events
- [`ZernikalosView`](../../engine/src/androidMain/kotlin/zernikalos/ui/ZernikalosView.kt) overrides `onTouchEvent(...)` and enqueues converted touch events

### Web (WebGPU)

Mouse and keyboard events are captured from the HTML canvas and enqueued:

- [`WebInputEventManager`](../../engine/src/webgpuMain/kotlin/zernikalos/events/WebInputEventManager.kt)

Notes:

- The canvas is focused on mouse down so it can receive keyboard events.
- The current implementation wires `mousedown/up/move` + `keydown/up/keypress`.
- Touch input for web is not currently handled by `WebInputEventManager`.

## Best practices

- Prefer **input-state querying** (`context.input.keyboard`) for "is being held" checks, and events for "edges" (down/up, typed characters).
- Keep listeners **cheap**: events are broadcast to every object with listeners, so heavy per-object work may scale poorly.
- If you need per-object pointer targeting, implement a picking layer and filter inside listeners.

## Related documentation

- [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md)
- [Zernikalos Initialization Architecture](../architecture/zernikalos-initialization-architecture.md)
