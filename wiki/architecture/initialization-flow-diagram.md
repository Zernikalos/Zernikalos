# Zernikalos Initialization Flow Diagram

## Main Diagram (Mermaid)

```mermaid
graph TD
    A[User calls Zernikalos.initialize] --> B[Platform-specific Zernikalos]
    B --> C[ZernikalosBase.internalInitialize]
    
    C --> D[Create ZSurfaceView]
    C --> E[Create ZContextCreator]
    C --> F[Create ZSceneStateHandler]
    
    E --> G[contextCreator.createContext]
    G --> H[Create ZSceneContext]
    G --> I[Create ZRenderingContext]
    
    H --> J[ZContext]
    I --> J
    
    C --> K[createSurfaceViewEventHandler]
    K --> L[ZSurfaceViewEventHandlerImpl]
    L --> M[InitializationProcess]
    
    M --> N{Current state?}
    N -->|NOT_STARTED| O[SCENE_HANDLER_INITIALIZING]
    N -->|SCENE_HANDLER_INITIALIZING| P[RENDERER_INITIALIZING]
    N -->|RENDERER_INITIALIZING| Q[READY]
    N -->|READY| R[Operating system]
    
    O --> S[stateHandler.onReady]
    S --> P
    
    P --> T[renderer.initialize]
    T --> Q
    
    Q --> U[Rendering events]
    U --> V[onRender, onResize]
    
    V --> W[performRender]
    V --> X[applyResize]
    
    W --> Y[stateHandler.onRender]
    Y --> Z[renderer.render]
    
    X --> AA[stateHandler.onResize]
    AA --> BB[renderer.onViewportResize]
```

## Initialization States Diagram

```mermaid
stateDiagram-v2
    [*] --> NOT_STARTED
    NOT_STARTED --> SCENE_HANDLER_INITIALIZING : onReady() called
    SCENE_HANDLER_INITIALIZING --> RENDERER_INITIALIZING : stateHandler.onReady() completed
    RENDERER_INITIALIZING --> READY : renderer.initialize() completed
    READY --> READY : Rendering events
    READY --> READY : Resize events
```

## Components and Dependencies Diagram

```mermaid
graph LR
    subgraph "Platform Specific"
        A[Zernikalos Android]
        B[Zernikalos iOS]
        C[Zernikalos WebGPU]
        D[Zernikalos Metal]
        E[Zernikalos OpenGL]
    end
    
    subgraph "Common Core"
        F[ZernikalosBase]
        G[ZContext]
        H[ZSurfaceView]
        I[ZSceneStateHandler]
    end
    
    subgraph "Contexts"
        J[ZSceneContext]
        K[ZRenderingContext]
        L[ZContextCreator]
    end
    
    subgraph "Events and States"
        M[ZSurfaceViewEventHandler]
        N[InitializationProcess]
        O[ZRenderer]
    end
    
    A --> F
    B --> F
    C --> F
    D --> F
    E --> F
    
    F --> G
    F --> H
    F --> I
    
    G --> J
    G --> K
    
    L --> J
    L --> K
    
    F --> M
    M --> N
    M --> O
    
    I --> M
    G --> M
```

## Detailed Event Flow

```mermaid
sequenceDiagram
    participant U as User
    participant Z as Zernikalos
    participant ZB as ZernikalosBase
    participant CC as ZContextCreator
    participant ZC as ZContext
    participant EH as EventHandler
    participant IP as InitProcess
    participant SH as StateHandler
    participant R as Renderer
    
    U->>Z: initialize(view, stateHandler)
    Z->>ZB: internalInitialize(surfaceView, contextCreator, stateHandler)
    
    ZB->>CC: createContext(surfaceView)
    CC->>ZC: new ZContext(sceneContext, renderingContext)
    CC-->>ZB: context
    
    ZB->>EH: createSurfaceViewEventHandler(context, stateHandler)
    ZB->>EH: surfaceView.eventHandler = eventHandler
    
    Note over EH: EventHandler configured
    
    EH->>IP: NOT_STARTED state
    
    Note over EH: onReady() event received
    EH->>IP: goToStateSceneHandlerInitialization()
    IP->>IP: SCENE_HANDLER_INITIALIZING state
    
    EH->>SH: onReady(context)
    SH-->>EH: Callback completed
    EH->>IP: goToStateRendererInitialization()
    IP->>IP: RENDERER_INITIALIZING state
    
    EH->>R: initialize()
    R-->>EH: Renderer initialized
    EH->>IP: goToStateReady()
    IP->>IP: READY state
    
    Note over EH: System ready for rendering
    
    loop Rendering events
        EH->>SH: onRender(context)
        SH-->>EH: Callback completed
        EH->>R: render()
    end
```

## File Structure and Responsibilities

```mermaid
graph TD
    subgraph "Zernikalos/src/commonMain/kotlin/zernikalos"
        A[ZernikalosBase.kt]
        B[context/]
        C[ui/]
        D[scenestatehandler/]
        E[statehandler/]
    end
    
    subgraph "Zernikalos/src/androidMain/kotlin/zernikalos"
        F[Zernikalos.kt]
        G[ui/ZAndroidSurfaceView.kt]
    end
    
    subgraph "Zernikalos/src/webgpuMain/kotlin/zernikalos"
        H[Zernikalos.kt]
        I[context/ZContextCreator.kt]
    end
    
    A --> B
    A --> C
    A --> D
    A --> E
    
    F --> A
    G --> C
    
    H --> A
    I --> B
```

## Platform Entry Points

| Platform | Responsibility |
|----------|----------------|
| Android | Adapts `GLSurfaceView` to `ZSurfaceView` |
| iOS | Adapts native iOS view |
| WebGPU | Configures WebGPU context |
| Metal | Configures Metal context |
| OpenGL | Configures OpenGL context |

## Architecture Summary

Zernikalos architecture follows a **Template Method** pattern where:

1. **ZernikalosBase** defines the initialization skeleton
2. **Platform-specific implementations** provide concrete details
3. **Asynchronous event system** handles gradual initialization
4. **State machine** controls initialization flow
5. **Separate contexts** for scene and rendering allow flexibility

This architecture allows the engine to work on multiple platforms while maintaining a common core, but the complexity of the initialization flow can make debugging and maintenance difficult.
