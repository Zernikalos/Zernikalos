# ZSurfaceView Platform Implementations

This document describes the architecture and implementations of `ZSurfaceView` across different platforms in the Zernikalos Engine. The `ZSurfaceView` interface provides a unified abstraction for rendering surfaces across WebGPU (JavaScript), OpenGL ES (Android), and Metal (iOS) platforms.

## Architecture Overview

The `ZSurfaceView` interface serves as a common contract for all platform-specific surface implementations:

```kotlin
interface ZSurfaceView {
    val surfaceWidth: Int
    val surfaceHeight: Int
    var eventHandler: ZSurfaceViewEventHandler?
}
```

Each platform implementation handles the specific rendering API and lifecycle management while maintaining the same interface contract.

## Platform Implementations

### 1. WebGPU/JavaScript Implementation (`ZJsSurfaceView`)

**File:** `src/webgpuMain/kotlin/zernikalos/ui/ZSurfaceView.kt`

**Purpose:** Provides WebGPU rendering surface for web browsers using HTML5 Canvas.

**Key Features:**
- **ResizeObserver Integration:** Automatically handles canvas resizing using modern ResizeObserver API
- **Device Pixel Ratio Support:** Scales canvas resolution for high-DPI displays
- **RequestAnimationFrame:** Synchronizes rendering with browser's refresh rate (60 FPS)
- **Throttled Resize Events:** Prevents excessive resize callbacks using pending flag

**Implementation Details:**
```kotlin
class ZJsSurfaceView(val canvas: HTMLCanvasElement): ZSurfaceView {
    private var pendingResize = false
    private val resizeObserver = ResizeObserver { entries ->
        handleResize(entries)
    }
    
    private fun handleResize(entries: Array<ResizeObserverEntry>) {
        // Updates canvas dimensions with device pixel ratio scaling
        // Notifies event handler of size changes
    }
}
```

**Canvas Management:**
- Automatically updates `canvas.width` and `canvas.height` based on visual size
- Applies device pixel ratio for crisp rendering on high-DPI screens
- Uses `requestAnimationFrame` for smooth 60 FPS rendering loop

### 2. OpenGL ES/Android Implementation (`ZAndroidSurfaceView`)

**File:** `src/androidMain/kotlin/zernikalos/ui/ZSurfaceView.android.kt`

**Purpose:** Provides OpenGL ES rendering surface for Android devices using GLSurfaceView.

**Key Features:**
- **GLSurfaceView Integration:** Wraps Android's GLSurfaceView for OpenGL ES rendering
- **EGL Context Management:** Handles OpenGL ES 3.0 context creation and management
- **Continuous Rendering:** Uses `RENDERMODE_CONTINUOUSLY` for smooth frame updates
- **Context Preservation:** Maintains EGL context across pause/resume cycles

**Implementation Details:**
```kotlin
class ZAndroidSurfaceView(view: GLSurfaceView): ZSurfaceView {
    var nativeSurfaceView: GLSurfaceView
    private val nativeRenderer: AndroidNativeRenderer = AndroidNativeRenderer()
    
    private fun setNativeRenderer() {
        nativeSurfaceView.setEGLContextClientVersion(3)
        nativeSurfaceView.setRenderer(nativeRenderer)
        nativeSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        nativeSurfaceView.preserveEGLContextOnPause = true
    }
}
```

**Renderer Implementation:**
```kotlin
class AndroidNativeRenderer: GLSurfaceView.Renderer {
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        eventHandler?.onReady()
    }
    
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        eventHandler?.onResize(width, height)
    }
    
    override fun onDrawFrame(p0: GL10?) {
        eventHandler?.onRender()
    }
}
```

**Alternative Implementation:** `ZernikalosView` extends `GLSurfaceView` directly, providing a more integrated approach for Android applications.

### 3. Metal/iOS Implementation (`ZMtlSurfaceView`)

**File:** `src/metalMain/kotlin/zernikalos/ui/ZSurfaceView.metal.kt`

**Purpose:** Provides Metal rendering surface for iOS devices using MTKView.

**Key Features:**
- **MTKView Integration:** Wraps Apple's MetalKit view for Metal rendering
- **Delegate Pattern:** Uses `ZMtkViewDelegate` to handle Metal-specific callbacks
- **Drawable Size Management:** Automatically handles drawable size changes
- **Semaphore-based Synchronization:** Uses dispatch semaphores for frame synchronization

**Implementation Details:**
```kotlin
class ZMtlSurfaceView(view: MTKView): ZSurfaceView {
    var nativeView: MTKView
    private val viewDelegate: ZMtkViewDelegate = ZMtkViewDelegate()
    
    override val surfaceWidth: Int
        get() = nativeView.drawableSize().useContents { this.width.toInt() }
        
    override val surfaceHeight: Int
        get() = nativeView.drawableSize.useContents { this.height.toInt() }
}
```

**Delegate Implementation:**
```kotlin
class ZMtkViewDelegate() : NSObject(), MTKViewDelegateProtocol {
    private val inFlightSemaphore: dispatch_semaphore_t = dispatch_semaphore_create(3)
    
    override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) {
        drawableSizeWillChange.useContents {
            eventHandler?.onResize(this.width.toInt(), this.height.toInt())
        }
    }
    
    override fun drawInMTKView(view: MTKView) {
        eventHandler?.onRender()
    }
}
```

## Event Handling

All implementations use the `ZSurfaceViewEventHandler` interface to communicate lifecycle and rendering events:

```kotlin
interface ZSurfaceViewEventHandler {
    fun onReady()           // Surface is ready for rendering
    fun onRender()          // Render frame
    fun onResize(width: Int, height: Int)  // Surface size changed
}
```

### Event Flow

1. **onReady():** Called when the rendering surface is initialized and ready
2. **onResize():** Called when surface dimensions change (window resize, orientation change, etc.)
3. **onRender():** Called continuously for each frame rendering

## Platform-Specific Considerations

### WebGPU/JavaScript
- **ResizeObserver:** Modern API for efficient resize detection
- **Device Pixel Ratio:** Critical for high-DPI display support
- **RequestAnimationFrame:** Ensures smooth 60 FPS rendering
- **Canvas Context:** May be reset when canvas dimensions change

### OpenGL ES/Android
- **EGL Context:** Requires proper context management
- **Surface Lifecycle:** Handles Android activity lifecycle (pause/resume)
- **Continuous Rendering:** Uses GLSurfaceView's built-in render loop
- **OpenGL ES 3.0:** Requires minimum API level 18

### Metal/iOS
- **MTKView:** Apple's optimized Metal rendering view
- **Drawable Management:** Automatic drawable size and format handling
- **Semaphore Synchronization:** Prevents frame buffer overruns
- **Delegate Pattern:** Follows iOS/MacOS design patterns


## Performance Considerations

- **WebGPU:** ResizeObserver provides efficient resize detection without polling
- **Android:** GLSurfaceView handles continuous rendering with minimal overhead
- **iOS:** MTKView provides optimized Metal rendering with automatic resource management

## Future Considerations

- **Vulkan Support:** Potential future implementation for Android and desktop platforms
- **DirectX Support:** Potential Windows platform implementation
- **Unified Shader Language:** Cross-platform shader compilation and optimization
