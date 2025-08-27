# Material System in Zernikalos Engine

This document describes the current material system implementation in the Zernikalos engine, covering both the Physically Based Rendering (PBR) and Phong lighting models.

## Overview

The Zernikalos engine supports two main material systems:
1. **PBR (Physically Based Rendering)** - Modern, physically accurate rendering
2. **Phong** - Traditional lighting model with Blinn-Phong specular highlights

Both systems are integrated into the same material framework and can be used interchangeably based on the rendering requirements.

## Current Limitations

**Important**: The engine currently suffers from a limitation where **lighting must be hardcoded** in the shaders. This means:
- Light positions are fixed in shader code
- Light colors and intensities are hardcoded
- No dynamic lighting system is currently implemented
- Materials cannot respond to scene lighting changes

## PBR Material System

### Structure

The PBR material is defined in `ZPbrMaterialData`:

```kotlin
@Serializable
@JsExport
data class ZPbrMaterialData(
    @ProtoNumber(1)
    var color: ZColor,                    // Base albedo color
    @ProtoNumber(2)
    var emissive: ZColor,                 // Self-illumination color
    @ProtoNumber(3)
    private var _emissiveIntensity: Float, // Emissive strength
    @ProtoNumber(4)
    private var _metalness: Float,         // Metalness (0-1)
    @ProtoNumber(5)
    private var _roughness: Float          // Surface roughness (0-1)
)
```

### Properties

- **Color**: Base surface color (albedo)
- **Emissive**: Self-illuminating color
- **Emissive Intensity**: Strength of self-illumination (≥0, supports HDR)
- **Metalness**: How metallic the surface is (0 = dielectric, 1 = metal)
- **Roughness**: Surface smoothness (0 = mirror-like, 1 = completely rough)

### Shader Implementation

The PBR system uses the **Cook-Torrance BRDF** with:
- **Distribution**: GGX/Trowbridge-Reitz for microfacet distribution
- **Geometry**: Smith function for geometric shadowing
- **Fresnel**: Schlick approximation for reflection/refraction ratio

```glsl
// PBR calculation in shaders
float NDF = DistributionGGX(N, H, roughness);
float G = GeometrySmith(N, V, L, roughness);
vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

vec3 specular = (NDF * G * F) / (4.0 * NdotV * NdotL + 0.0001);
```

### Current Shader Support

- ✅ **Android/OpenGL**: Fully implemented
- ✅ **Metal**: Fully implemented (PBR + Phong)
- ⚠️ **WebGPU**: Partially implemented (PBR + partial Phong support)

## Phong Material System

### Structure

The Phong material is defined in `ZPhongMaterialData`:

```kotlin
@Serializable
@JsExport
data class ZPhongMaterialData(
    @ProtoNumber(1)
    var diffuse: ZColor,      // Diffuse surface color
    @ProtoNumber(2)
    var ambient: ZColor,      // Ambient lighting color
    @ProtoNumber(3)
    var specular: ZColor,     // Specular highlight color
    @ProtoNumber(4)
    var shininess: Float      // Specular highlight sharpness
)
```

### Properties

- **Diffuse**: Main surface color that responds to direct lighting
- **Ambient**: Color under indirect/ambient lighting
- **Specular**: Color of reflective highlights
- **Shininess**: Controls how focused the specular highlights are (higher = more focused)

### Shader Implementation

The Phong system uses the **Blinn-Phong lighting model**:

```glsl
// Blinn-Phong calculation in shaders
vec3 H = normalize(V + L); // Halfway vector
float NdotH = max(dot(N, H), 0.0);
vec3 specularComponent = specular * lightColor * pow(NdotH, shininess);

vec3 finalColor = ambientComponent + diffuseComponent + specularComponent;
```

### Lighting Components

1. **Ambient**: `ambient * baseColor` - constant lighting
2. **Diffuse**: `diffuse * lightColor * (N·L)` - surface scattering
3. **Specular**: `specular * lightColor * pow(N·H, shininess)` - reflective highlights

### Current Shader Support

- ✅ **Android/OpenGL**: Fully implemented with Blinn-Phong
- ✅ **Metal**: Fully implemented with Blinn-Phong
- ⚠️ **WebGPU**: Partially implemented (needs complete Phong support)

## Material Integration

### Material Class

The main `ZMaterial` class supports both systems:

```kotlin
@JsExport
@Serializable(with = ZMaterialSerializer::class)
class ZMaterial {
    var pbr: ZPbrMaterialData? by data::pbr
    var phong: ZPhongMaterialData? by data::phong
    
    val usesPbr: Boolean get() = pbr != null
    val usesPhong: Boolean get() = phong != null
}
```

### Shader Selection

Materials automatically select the appropriate shader path:

```kotlin
#if defined(USE_PBR_MATERIAL)
    // PBR rendering path
#elif defined(USE_PHONG_MATERIAL)
    // Phong rendering path
#else
    // Default rendering path
#endif
```

### Uniform Blocks

Each material system has its own uniform block:

- **PBR**: `u_pbrMaterialBlock` (ID: 102)
- **Phong**: `u_phongMaterialBlock` (ID: 103)

## Shader Generation

### Preprocessor Directives

The shader system uses conditional compilation:

```glsl
#ifdef USE_PBR_MATERIAL
    // PBR-specific code
#endif

#ifdef USE_PHONG_MATERIAL
    // Phong-specific code
#endif
```

### Attribute Requirements

- **PBR**: Requires normals for proper lighting calculation
- **Phong**: Requires normals for diffuse and specular calculations
- **Both**: Can work with textures, colors, and other attributes

## Future Improvements

### Planned Enhancements

1. **Dynamic Lighting System**: Replace hardcoded lights with scene-based lighting
2. **Light Types**: Support for point, directional, and spot lights
3. **Shadow Mapping**: Real-time shadow support
4. **Global Illumination**: Ambient occlusion and indirect lighting
5. **Material Blending**: Support for multiple material types on single objects

### Current Work Needed

1. **WebGPU Shader Support**: Complete Phong materials implementation in WebGPU shaders
2. **Lighting System**: Design and implement dynamic lighting architecture
3. **Performance Optimization**: Optimize shader performance across platforms

## Usage Examples

### Creating PBR Materials

```kotlin
val pbrMaterial = ZMaterial()
pbrMaterial.pbr = ZPbrMaterialData(
    color = ZColor(0.8f, 0.2f, 0.2f),      // Red
    emissive = ZColor(0.1f, 0.0f, 0.0f),   // Dark red glow
    emissiveIntensity = 0.5f,
    metalness = 0.0f,                       // Non-metallic
    roughness = 0.3f                         // Semi-smooth
)
```

### Creating Phong Materials

```kotlin
val phongMaterial = ZMaterial()
phongMaterial.phong = ZPhongMaterialData(
    diffuse = ZColor(0.8f, 0.8f, 0.8f),    // Light gray
    ambient = ZColor(0.2f, 0.2f, 0.2f),    // Dark gray
    specular = ZColor(1.0f, 1.0f, 1.0f),   // White highlights
    shininess = 32.0f                       // Medium sharpness
)
```

## Technical Details

### Serialization

Both material systems support:
- **Kotlinx Serialization**: For runtime material creation
- **Protocol Buffers**: For asset pipeline integration
- **JSON Export**: For web platform compatibility

### Memory Layout

Uniform blocks are packed efficiently:
- **PBR Block**: 20 bytes (4 vec4 + 4 float)
- **Phong Block**: 16 bytes (3 vec4 + 1 float)

### Performance Considerations

- **PBR**: More computationally expensive due to complex BRDF calculations
- **Phong**: Lighter weight, suitable for mobile/performance-critical applications
- **Conditional Compilation**: Unused material code is excluded from final shaders
