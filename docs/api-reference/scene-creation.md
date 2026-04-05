# Scene Creation API

## Overview

The Zernikalos engine provides a hierarchical scene graph through the [`ZScene`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZScene.kt) and [`ZObject`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZObject.kt) classes. This document explains how to create and configure scenes programmatically.

## Creating a Scene

### Basic Scene

```kotlin
import zernikalos.objects.ZScene

val scene = ZScene()
scene.name = "MyScene"
```

### Default Scene (Recommended)

Use the factory method for a scene pre-configured with essential objects:

```kotlin
import zernikalos.objects.ZScene

val scene = ZScene.defaultScene()
```

The default scene includes:

| Object | Type | Description |
|--------|------|-------------|
| `DefaultPerspectiveCamera` | [`ZCamera`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZCamera.kt) | Perspective camera at origin, looking at center, up vector (0,1,0) |
| `DefaultAmbientLight` | [`ZLight`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZLight.kt) | Ambient light with white color and intensity 1.0 |

## Scene Structure

### Scene Hierarchy

Scenes follow a tree structure where each [`ZObject`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZObject.kt) can have children:

```
ZScene (root)
├── ZCamera (DefaultPerspectiveCamera)
├── ZLight (DefaultAmbientLight)
├── ZGroup (optional organizational container)
│   ├── ZModel (3D model with mesh + material)
│   └── ZLight (additional light)
└── ZModel (another model)
```

### Adding Children

```kotlin
// Add objects to the scene
val model = ZModel()
scene.addChild(model)

// Add to a specific group
val group = ZGroup()
group.addChild(model)
scene.addChild(group)
```

### Scene Context

Scenes operate within a [`ZSceneContext`](../../engine/src/commonMain/kotlin/zernikalos/context/ZSceneContext.kt):

```kotlin
import zernikalos.context.createDefaultSceneContext
import zernikalos.context.createSceneContext

// Create default context
val context = createDefaultSceneContext()
context.scene = scene

// Or create custom context
val customContext = createSceneContext()
customContext.scene = scene
```

## Scene Configuration

### Viewport

Every scene has a [`ZViewport`](../../engine/src/commonMain/kotlin/zernikalos/components/ZViewport.kt):

```kotlin
// Access the scene's viewport
val viewport = scene.viewport

// Configure viewport (optional)
viewport.clearColor = ZColor(0.2f, 0.2f, 0.2f, 1.0f)
```

### Camera Setup

```kotlin
// Access the default camera
val camera = scene.findFirstCamera() as ZCamera?

// Or create custom camera
val myCamera = ZCamera(
    lookAt = ZVector3(0f, 0f, 0f),
    up = ZVector3(0f, 1f, 0f)
)
myCamera.lens.near = 0.1f
myCamera.lens.far = 1000f
myCamera.lens.fov = 60f

// Position the camera
myCamera.transform.position = ZVector3(0f, 5f, 10f)

// Set as active camera (optional)
sceneContext.activeCamera = myCamera
```

## Complete Example

### Kotlin

```kotlin
import zernikalos.objects.*
import zernikalos.context.createDefaultSceneContext
import zernikalos.math.ZVector3
import zernikalos.components.camera.ZPerspectiveLens

fun createCustomScene(): ZScene {
    // Create empty scene
    val scene = ZScene()
    scene.name = "CustomScene"
    
    // Add custom camera
    val camera = ZCamera()
    camera.name = "MainCamera"
    camera.transform.position = ZVector3(0f, 2f, 5f)
    camera.lens = ZPerspectiveLens(
        near = 0.1f,
        far = 100.0f,
        fov = 45.0f
    )
    scene.addChild(camera)
    
    // Add ambient light
    val ambientLight = ZLight.createAmbientLight()
    ambientLight.intensity = 0.3f
    scene.addChild(ambientLight)
    
    // Add directional light
    val sunLight = ZLight.createDirectionalLight()
    sunLight.color = ZColor(1.0f, 0.95f, 0.8f)
    sunLight.intensity = 1.0f
    sunLight.transform.rotation = ZEulerAngles(
        pitch = -45f,
        yaw = 30f,
        roll = 0f
    )
    scene.addChild(sunLight)
    
    return scene
}

// Usage with engine
val context = createDefaultSceneContext()
context.scene = createCustomScene()
```

### JavaScript

```javascript
import { ZScene, ZCamera, ZLight, ZVector3, ZColor } from "zernikalos";

function createScene() {
    // Create default scene (recommended)
    const scene = ZScene.defaultScene();
    
    // Or create empty scene
    // const scene = new ZScene();
    
    // Position camera
    const camera = scene.findFirstCamera();
    camera.transform.position = new ZVector3(0, 2, 5);
    
    // Add additional light
    const pointLight = ZLight.createPointLight();
    pointLight.color = new ZColor(1, 0.5, 0.5, 1);
    pointLight.intensity = 0.8;
    pointLight.transform.position = new ZVector3(2, 3, 2);
    scene.addChild(pointLight);
    
    return scene;
}
```

### Swift

```swift
import Zernikalos

func createScene() -> ZScene {
    let scene = ZScene.defaultScene()
    
    // Configure camera position
    if let camera = scene.findFirstCamera() as? ZCamera {
        camera.transform.position = ZVector3(x: 0, y: 2, z: 5)
    }
    
    // Add directional light
    let sun = ZLight.createDirectionalLight()
    sun.color = ZColor(r: 1, g: 0.95, b: 0.8, a: 1)
    sun.intensity = 1.2
    scene.addChild(sun)
    
    return scene
}
```

## Lifecycle Methods

Scenes automatically handle initialization, rendering, and disposal:

| Method | Called When | Override |
|--------|-------------|----------|
| `initialize()` | Before first frame | `internalInitialize()` |
| `render()` | Each frame | `internalRender()` |
| `onViewportResize()` | Surface resize | `internalOnViewportResize()` |
| `dispose()` | Engine shutdown | `internalDispose()` |

## Best Practices

1. **Use `ZScene.defaultScene()`** for quick prototyping - it provides a working camera and ambient light
2. **Name your objects** - Unnamed objects get auto-generated names like `model_a1b2c3`
3. **Set up cameras early** - The renderer needs an active camera from the first frame
4. **Add lights before models** - Ensures materials receive lighting calculations
5. **Use groups for organization** - [`ZGroup`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZGroup.kt) helps structure complex scenes

## Related Documentation

- [Zernikalos Initialization Architecture](../architecture/zernikalos-initialization-architecture.md)
- [Components Architecture](../architecture/components/components-architecture.md)
- [Lighting System](../architecture/lighting-system.md)
