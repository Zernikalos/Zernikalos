# Lights Creation API

## Overview

The Zernikalos lighting system supports four light types through the [`ZLight`](../../engine/src/commonMain/kotlin/zernikalos/objects/ZLight.kt) class. This document explains how to create and configure lights programmatically.

## Light Types

### Ambient Light

Global non-directional illumination that affects all surfaces equally. Does not cast shadows.

```kotlin
import zernikalos.objects.ZLight

// Factory method
val ambient = ZLight.createAmbientLight()

// Or access the default (white color, intensity 1.0)
val defaultAmbient = ZLight.DefaultAmbientLight
```

**Best for:** Base illumination, preventing completely dark shadows, global mood.

### Directional Light

Parallel rays simulating distant light sources (sun, moon). Affects entire scene based on direction.

```kotlin
import zernikalos.objects.ZLight

val sun = ZLight.createDirectionalLight()
sun.color = ZColor(1.0f, 0.95f, 0.8f)  // Warm sunlight
sun.intensity = 1.5f

// Set direction via rotation
sun.transform.rotation = ZEulerAngles(
    pitch = -45f,  // Elevation
    yaw = 30f,     // Azimuth
    roll = 0f
)
```

**Best for:** Sun/moon, large area lighting, casting shadows (future feature).

### Point Light

Omni-directional light emanating from a point in space. Intensity falls off with distance based on range and decay.

```kotlin
import zernikalos.objects.ZLight
import zernikalos.components.light.ZPointLamp

val lamp = ZLight.createPointLight()
lamp.color = ZColor(1.0f, 0.8f, 0.6f)  // Warm lamp
lamp.intensity = 2.0f

// Position in scene
lamp.transform.position = ZVector3(2f, 3f, 2f)

// Configure point light properties
val pointLamp = lamp.lamp as ZPointLamp
pointLamp.range = 10f    // Affects distance of influence
pointLamp.decay = 2f     // Attenuation factor (1=linear, 2=quadratic)
```

**Best for:** Lamps, torches, glowing objects, local illumination.

### Spot Light

Cone-shaped light with inner and outer cone angles. Combines point light attenuation with directional cone.

```kotlin
import zernikalos.objects.ZLight
import zernikalos.components.light.ZSpotLamp

val spot = ZLight.createSpotLight()
spot.color = ZColor(1.0f, 1.0f, 0.9f)
spot.intensity = 3.0f

// Position and aim
spot.transform.position = ZVector3(0f, 5f, 0f)
spot.transform.rotation = ZEulerAngles(pitch = -90f, yaw = 0f, roll = 0f)

// Configure spot properties
val spotLamp = spot.lamp as ZSpotLamp
spotLamp.range = 15f
spotLamp.decay = 2f
spotLamp.innerAngle = 30f   // Full intensity cone (degrees)
spotLamp.outerAngle = 45f   // Falloff edge (degrees)
```

**Best for:** Flashlights, street lamps, stage lighting, car headlights.

## Common Properties

All lights share these properties:

```kotlin
// Color (RGB)
light.color = ZColor(r, g, b)

// Intensity multiplier
light.intensity = 1.5f

// Enable/disable (affects rendering performance)
light.isEnabled = true

// Name for identification
light.name = "MainSun"

// Transform (position/rotation/scale)
light.transform.position = ZVector3(x, y, z)
light.transform.rotation = ZEulerAngles(pitch, yaw, roll)
```

## Light Limits

The engine enforces GPU limits for performance:

| Limit | Value | Description |
|-------|-------|-------------|
| `MAX_DIRECT_LIGHTS` | 4 | Maximum directional, point, and spot lights combined |
| Ambient | 1 | Only one ambient light (first enabled one used) |

Excess lights are automatically culled based on discovery order (depth-first scene traversal).

## Creating Light Setups

### Three-Point Lighting (Classic)

```kotlin
import zernikalos.objects.ZLight
import zernikalos.math.ZColor
import zernikalos.math.ZVector3

fun createThreePointLighting(scene: ZScene) {
    // Key light (main illumination)
    val keyLight = ZLight.createDirectionalLight()
    keyLight.name = "KeyLight"
    keyLight.color = ZColor(1.0f, 0.95f, 0.9f)
    keyLight.intensity = 1.0f
    keyLight.transform.rotation = ZEulerAngles(-30f, 45f, 0f)
    scene.addChild(keyLight)
    
    // Fill light (soften shadows)
    val fillLight = ZLight.createDirectionalLight()
    fillLight.name = "FillLight"
    fillLight.color = ZColor(0.6f, 0.7f, 0.8f)
    fillLight.intensity = 0.4f
    fillLight.transform.rotation = ZEulerAngles(-10f, -45f, 0f)
    scene.addChild(fillLight)
    
    // Rim/Back light (separate from background)
    val rimLight = ZLight.createDirectionalLight()
    rimLight.name = "RimLight"
    rimLight.color = ZColor(0.8f, 0.9f, 1.0f)
    rimLight.intensity = 0.6f
    rimLight.transform.rotation = ZEulerAngles(-10f, 180f, 0f)
    scene.addChild(rimLight)
    
    // Ambient base
    val ambient = ZLight.createAmbientLight()
    ambient.intensity = 0.2f
    scene.addChild(ambient)
}
```

### Indoor Lighting

```kotlin
import zernikalos.objects.ZLight
import zernikalos.components.light.ZPointLamp

fun createIndoorLighting(scene: ZScene) {
    // Ambient for base visibility
    val ambient = ZLight.createAmbientLight()
    ambient.intensity = 0.3f
    scene.addChild(ambient)
    
    // Ceiling lamp
    val ceilingLamp = ZLight.createPointLight()
    ceilingLamp.name = "CeilingLamp"
    ceilingLamp.color = ZColor(1.0f, 0.95f, 0.8f)
    ceilingLamp.intensity = 1.5f
    ceilingLamp.transform.position = ZVector3(0f, 3f, 0f)
    (ceilingLamp.lamp as ZPointLamp).range = 8f
    scene.addChild(ceilingLamp)
    
    // Warm table lamp
    val tableLamp = ZLight.createPointLight()
    tableLamp.name = "TableLamp"
    tableLamp.color = ZColor(1.0f, 0.7f, 0.4f)
    tableLamp.intensity = 1.0f
    tableLamp.transform.position = ZVector3(2f, 1f, 1f)
    (tableLamp.lamp as ZPointLamp).range = 4f
    scene.addChild(tableLamp)
}
```

### Outdoor/Environment

```kotlin
import zernikalos.objects.ZLight

fun createOutdoorLighting(scene: ZScene, isDay: Boolean = true) {
    if (isDay) {
        // Sun
        val sun = ZLight.createDirectionalLight()
        sun.name = "Sun"
        sun.color = ZColor(1.0f, 0.95f, 0.8f)
        sun.intensity = 1.2f
        sun.transform.rotation = ZEulerAngles(-45f, 30f, 0f)
        scene.addChild(sun)
        
        // Sky fill
        val ambient = ZLight.createAmbientLight()
        ambient.color = ZColor(0.4f, 0.6f, 0.8f)
        ambient.intensity = 0.4f
        scene.addChild(ambient)
    } else {
        // Moon
        val moon = ZLight.createDirectionalLight()
        moon.name = "Moon"
        moon.color = ZColor(0.7f, 0.8f, 1.0f)
        moon.intensity = 0.4f
        moon.transform.rotation = ZEulerAngles(-60f, -30f, 0f)
        scene.addChild(moon)
        
        // Night ambient
        val ambient = ZLight.createAmbientLight()
        ambient.color = ZColor(0.1f, 0.1f, 0.2f)
        ambient.intensity = 0.2f
        scene.addChild(ambient)
    }
}
```

## Complete Examples

### Kotlin

```kotlin
import zernikalos.objects.ZScene
import zernikalos.objects.ZLight
import zernikalos.components.light.ZPointLamp
import zernikalos.components.light.ZSpotLamp
import zernikalos.math.ZColor
import zernikalos.math.ZVector3
import zernikalos.math.ZEulerAngles

fun createLitScene(): ZScene {
    val scene = ZScene.defaultScene()
    
    // Add point light
    val lamp = ZLight.createPointLight()
    lamp.name = "TableLamp"
    lamp.color = ZColor(1.0f, 0.85f, 0.6f)
    lamp.intensity = 2.0f
    lamp.transform.position = ZVector3(2f, 2.5f, 1f)
    
    val pointLamp = lamp.lamp as ZPointLamp
    pointLamp.range = 8f
    pointLamp.decay = 2f
    
    scene.addChild(lamp)
    
    // Add spot light
    val spot = ZLight.createSpotLight()
    spot.name = "Spotlight"
    spot.color = ZColor(0.9f, 0.95f, 1.0f)
    spot.intensity = 3.0f
    spot.transform.position = ZVector3(-2f, 4f, 0f)
    spot.transform.lookAt(ZVector3(0f, 0f, 0f))
    
    val spotLamp = spot.lamp as ZSpotLamp
    spotLamp.range = 10f
    spotLamp.innerAngle = 20f
    spotLamp.outerAngle = 35f
    
    scene.addChild(spot)
    
    return scene
}
```

### JavaScript

```javascript
import { 
    ZScene, 
    ZLight, 
    ZColor, 
    ZVector3,
    ZEulerAngles 
} from "zernikalos";

function createLitScene() {
    const scene = ZScene.defaultScene();
    
    // Configure default ambient
    const ambient = scene.findFirstLight();
    if (ambient) {
        ambient.intensity = 0.3;
    }
    
    // Add warm point light
    const lamp = ZLight.createPointLight();
    lamp.name = "TableLamp";
    lamp.color = new ZColor(1.0, 0.85, 0.6, 1.0);
    lamp.intensity = 2.0;
    lamp.transform.position = new ZVector3(2, 2.5, 1);
    
    // Configure point lamp properties
    const pointLamp = lamp.lamp;
    pointLamp.range = 8;
    pointLamp.decay = 2;
    
    scene.addChild(lamp);
    
    // Add directional sun
    const sun = ZLight.createDirectionalLight();
    sun.name = "Sun";
    sun.color = new ZColor(1.0, 0.98, 0.9, 1.0);
    sun.intensity = 1.2;
    sun.transform.rotation = new ZEulerAngles(-45, 30, 0);
    
    scene.addChild(sun);
    
    return scene;
}
```

### Swift

```swift
import Zernikalos

func createLitScene() -> ZScene {
    let scene = ZScene.defaultScene()
    
    // Add point light
    let lamp = ZLight.createPointLight()
    lamp.name = "TableLamp"
    lamp.color = ZColor(r: 1, g: 0.85, b: 0.6, a: 1)
    lamp.intensity = 2
    lamp.transform.position = ZVector3(x: 2, y: 2.5, z: 1)
    
    if let pointLamp = lamp.lamp as? ZPointLamp {
        pointLamp.range = 8
        pointLamp.decay = 2
    }
    
    scene.addChild(lamp)
    
    // Add directional sun
    let sun = ZLight.createDirectionalLight()
    sun.name = "Sun"
    sun.color = ZColor(r: 1, g: 0.98, b: 0.9, a: 1)
    sun.intensity = 1.2
    sun.transform.rotation = ZEulerAngles(pitch: -45, yaw: 30, roll: 0)
    
    scene.addChild(sun)
    
    return scene
}
```

## Best Practices

1. **Always include ambient light** - Prevents completely black shadows
2. **Use intensity < 1 for fill/ambient** - Keeps contrast and depth
3. **Position point lights strategically** - Remember the 4-light limit
4. **Name your lights** - Easier to debug and find in scene graph
5. **Use `isEnabled` to toggle** - Cheaper than adding/removing from scene
6. **Check `lampType`** - Verify you're configuring the right lamp type

## Light Discovery

The engine finds lights automatically each frame using [`ZFinder`](../../engine/src/commonMain/kotlin/zernikalos/search/ZFinder.kt):

```kotlin
// All lights in scene (depth-first order)
val allLights = findAllLights(scene)

// Only enabled direct lights (directional/point/spot)
val directLights = findAllDirectLights(scene)

// First enabled ambient light
val ambient = findAmbientLight(scene)
```

## Related Documentation

- [Lighting System Architecture](../architecture/lighting-system.md)
- [Scene Creation API](./scene-creation.md)
- [ZLight API](../../engine/src/commonMain/kotlin/zernikalos/objects/ZLight.kt)
- [ZLamp Types](../../engine/src/commonMain/kotlin/zernikalos/components/light/ZLamp.kt)
