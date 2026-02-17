# Zernikalos Engine Initialization Architecture

## Overview

Zernikalos uses a modular and asynchronous architecture for rendering engine initialization. The flow is designed to handle different platforms (Android, iOS, WebGPU, Metal, OpenGL) in a unified way through common abstractions.

## Main Flow Diagram

```
User → Zernikalos.initialize() → ZernikalosBase.internalInitialize() → Operating System
```

## Main Components

### 1. Zernikalos (Platform-specific implementation)
- **Responsibility**: Platform-specific entry point
- **Platforms**: Android, iOS, WebGPU, Metal, OpenGL
- **Function**: Adapts native view to `ZSurfaceView` and creates `ZContextCreator`

### 2. ZernikalosBase (Common base class)
- **Responsibility**: Central engine coordination
- **Internal components**:
  - `surfaceView`: Rendering view
  - `context`: Main context (scene + rendering)
  - `stateHandler`: Scene state handler
  - `settings`: Engine settings
  - `stats`: Platform statistics

### 3. ZSurfaceView (View interface)
- **Responsibility**: Rendering surface abstraction
- **Properties**:
  - `surfaceWidth` / `surfaceHeight`: Dimensions
  - `eventHandler`: Event handler

## Detailed Initialization Flow

### Phase 1: Context Creation
```
Zernikalos.initialize()
    ↓
ZernikalosBase.internalInitialize()
    ↓
contextCreator.createContext(surfaceView)
    ↓
ZContext(
    sceneContext: ZSceneContext,
    renderingContext: ZRenderingContext
)
```

### Phase 2: Event Handler Configuration
```
createSurfaceViewEventHandler(context, stateHandler)
    ↓
ZSurfaceViewEventHandlerImpl (internal InitState)
```

### Phase 3: Asynchronous Initialization Process

#### InitState (4 states):
1. **NOT_STARTED** → Initial state
2. **SCENE_INIT** → Waiting for stateHandler.onReady() callback
3. **RENDERER_INIT** → Renderer initialization on next progressInitialization
4. **READY** → System ready for rendering

#### Event Flow:
```
onReady() / onRender() → progressInitialization()
    ↓
stateHandler.onReady() callback → renderer.initialize() → READY
    ↓
System ready for rendering
```

## Context Architecture

### ZContext (Main Context)
```kotlin
class ZContext(
    val sceneContext: ZSceneContext,
    val renderingContext: ZRenderingContext
) {
    var screenWidth: Int
    var screenHeight: Int
    var scene: ZScene?
    var activeCamera: ZCamera?
    val isInitialized: Boolean
}
```

### ZSceneContext (Scene Context)
- **Responsibility**: Scene, camera and uniform generators management
- **Components**:
  - `scene`: Active scene
  - `activeCamera`: Active camera
  - `uniformsGeneratorMap`: Uniform generators for shaders

### ZRenderingContext (Rendering Context)
- **Responsibility**: Platform-specific rendering context abstraction
- **Implementations**: WebGPU, Metal, OpenGL, Android

## Design Patterns Used

### 1. Factory Pattern
- `ZContextCreator.createContext()` creates platform-specific contexts
- `createDefaultContextCreator()` provides default implementation

### 2. State Machine Pattern
- `ZSurfaceViewEventHandlerImpl` uses an internal `InitState` enum for sequential initialization
- States can only progress forward, never backward

### 3. Strategy Pattern
- Different `ZContextCreator` implementations for each platform
- `ZSceneStateHandler` allows different state handling strategies

### 4. Observer Pattern
- `ZSurfaceViewEventHandler` responds to view events
- `ZSceneStateHandler` notifies state changes

## Resize Handling During Initialization

```kotlin
// If resize occurs during initialization, applied in stateHandler.onReady callback
if (pendingResize) {
    applyResize(context.screenWidth, context.screenHeight)
    pendingResize = false
}
```

## Architecture Advantages

1. **Modularity**: Clear separation of responsibilities
2. **Platform-agnostic**: Common abstractions for different platforms
3. **Asynchronous**: Non-blocking initialization
4. **Extensible**: Easy to add new platforms
5. **Robust state handling**: Controlled initialization process

## Identified Improvement Points

1. **Flow complexity**: The initialization process can be confusing
2. **Coupling**: Some components are tightly coupled
3. **Error handling**: Lacks robust failure handling during initialization
4. **Documentation**: The flow is not well documented for developers

## Refactoring Recommendations

1. ~~**Simplify InitializationProcess**: Reduce number of states~~ ✓ Done
2. **Add error handling**: Rollback in case of failure
3. **Improve logging**: More information during the process
4. **Create sequence diagrams**: For each specific platform
5. **Implement unit tests**: For each initialization phase
