# Resource Disposal Lifecycle

## Overview

The Zernikalos engine implements a deterministic, ownership-driven disposal architecture ensuring GPU resources and scene graph objects are properly released across OpenGL ES, Metal, and WebGPU backends.

## Design Principles

1. **Idempotent disposal**: Multiple calls to `dispose()` are safe
2. **Ownership hierarchy**: Resources are released from children upward
3. **Deterministic ordering**: GPU resources disposed before context teardown
4. **Safe callbacks**: Disposed objects reject render/update work

## Component Hierarchy

```
ZernikalosBase
├── ZSurfaceView (UI)
├── ZSurfaceViewEventHandler
│   └── ZRenderer
├── ZContext
│   ├── ZSceneContext
│   │   └── ZScene (ZObject tree)
│   │       └── ZComponents
│   │           └── ZComponentRenderer
│   └── ZRenderingContext
└── Event queue / input systems
```

## Disposal Order

Required teardown sequence:

1. **Stop frame production** - UI loop/delegate/interval stopped
2. **Stop frame consumption** - Event handler gates block callbacks
3. **Dispose scene graph** - Recursive `ZObject.dispose(ctx)`
4. **Dispose rendering resources** - Component renderers, viewport, context
5. **Dispose input/event systems**
6. **Null references** - GC friendliness

## Core Types

### ZObject (Scene Graph)

```kotlin
abstract class ZObject {
    fun dispose(ctx: ZContext) {
        if (isDisposed) return
        isDisposing = true
        internalDispose(ctx)
        children.forEach { it.dispose(ctx) }
        isDisposed = true
        isDisposing = false
    }

    protected abstract fun internalDispose(ctx: ZContext)
}
```

State flags:
- `isDisposing`: Currently in disposal (blocks concurrent operations)
- `isDisposed`: Disposal complete (prevents reuse)

### ZComponent

```kotlin
abstract class ZComponent {
    var isDisposed: Boolean = false
        private set

    var isDisposing: Boolean = false
        private set

    fun dispose(ctx: ZContext) {
        if (isDisposed || isDisposing) return
        isDisposing = true
        internalDispose(ctx)
        renderer.dispose()
        isDisposed = true
        isDisposing = false
    }

    protected open fun internalDispose(ctx: ZContext) {}
}
```

### ZComponentRenderer

```kotlin
abstract class ZComponentRenderer {
    abstract fun initialize()
    open fun dispose() {}  // Override in backend renderers
}
```

Backend implementations override `dispose()` to release GPU resources.

## Backend-Specific Disposal

### OpenGL ES (Android)

| Resource | Release Call |
|----------|--------------|
| VertexArray | `glDeleteVertexArrays()` |
| BufferContent | `glDeleteBuffers()` |
| Texture | `glDeleteTextures()` |
| Shader | `glDeleteShader()` |
| ShaderProgram | `glDeleteProgram()` (after detach) |
| UniformBuffer | `glDeleteBuffers()` + binding registry cleanup |

Implemented in:
- `ZVertexArray.kt`
- `ZBufferContentRenderer.android.kt`
- `ZTextureRenderer.android.kt`
- `ZShaderRenderer.android.kt`
- `ZShaderProgramRenderer.android.kt`
- `ZUniformBlockRenderer.android.kt`

### Metal (Apple)

Metal uses ARC (Automatic Reference Counting) with explicit nulling:

| Resource | Release Pattern |
|----------|-----------------|
| Texture | `texture = null` |
| Sampler | `samplerState = null` |
| Buffer | `buffer = null` |
| PipelineState | `pipelineState = null` |
| Library | `library = null` |

Implemented in respective `*Renderer.metal.kt` files.

### WebGPU (Web)

WebGPU requires explicit `destroy()` calls:

| Resource | Release Call |
|----------|--------------|
| Texture | `texture.destroy()` |
| Buffer | `wgpuBuffer.destroy()` |
| Sampler | Clear ref (no explicit destroy needed) |
| Pipeline | Clear refs |
| BindGroup | Clear refs |

Implemented in respective `*Renderer.wgpu.kt` files.

## Shared Resource Safety

When multiple `ZModel`s share the same `ZMesh`, `ZTexture`, or `ZShaderProgram`:

- **Component controls disposal**: Each owner component decides when to dispose
- **No renderer idempotency**: Backend renderers don't guard against double-release
- **Caller responsibility**: Ensure single disposal via component lifecycle

Pattern for shared components:
```kotlin
// Each component tracks its own disposal state
// The shared resource's renderer.dispose() is called once per owner
// Reference counting is not implemented - coordination via scene graph
```

## Event Handler Disposal

`ZSurfaceViewEventHandlerImpl` gates callbacks:

```kotlin
override fun onRender() {
    if (isDisposed || isDisposing) return
    // ... render logic
}

override fun onResize(width: Int, height: Int) {
    if (isDisposed || isDisposing) return
    // ... resize logic
}
```

This prevents stray callbacks during/after disposal.

## Disposal Triggers

1. **Engine shutdown**: `ZernikalosBase.dispose()` → full teardown
2. **Surface destruction**: Platform surface callbacks trigger handler disposal
3. **Scene change**: Old scene disposed before new scene attached
4. **Component removal**: Object removal disposes its components

## Testing Recommendations

- Close/reopen window repeatedly: Verify no backend resource errors
- Resize-to-zero then dispose: Confirm no render pass creation attempts
- Scene swap with heavy textures: Monitor memory trend stability
- Multi-dispose calls: Verify idempotency (no crashes)

## Threading Considerations

- Disposal runs on the **main/UI thread**
- Render thread must complete current frame before disposal proceeds
- No explicit synchronization - caller must ensure proper ordering
- GPU command buffers complete before resource destruction (driver handles)

## Known Limitations

- **Threading**: Disposal from non-UI thread requires manual synchronization
- **Shared resources**: No built-in reference counting
- **Double-dispose**: Safe but logs debug warning in development builds
- **Partial failures**: Backend-specific; check platform logs

## Related Documentation

- [Zernikalos Initialization Architecture](./zernikalos-initialization-architecture.md) - Startup counterpart to disposal
- [ZSurfaceView Implementations](../ui/zsurfaceview-implementations.md) - Platform surface disposal
- [Components Architecture](./components/components-architecture.md) - Component/renderer relationships
