# Texture System

## Overview

The texture system uses a **compositional architecture** instead of monolithic format enums. Texture formats are defined by combining four independent components: pixel type, channel layout, color space, and normalization. This design provides maximum flexibility and extensibility across all rendering backends.

## Architecture

### Compositional Design

The texture format is defined by **four independent components**:

1. **Pixel Type** (`ZBaseType`) - The data type of each pixel component
2. **Channels** (`ZTextureChannels`) - The channel layout
3. **Color Space** (`ZTextureColorSpace`) - Linear or sRGB
4. **Normalized** (`Boolean`) - Whether integer values map to [0.0, 1.0]

This design provides maximum flexibility and makes it easy to add new format combinations without modifying the core system.

### Core Components

#### ZBaseType (Pixel Type)

Reuses the existing `ZBaseType` enum for consistency:

```kotlin
enum class ZBaseType {
    UNSIGNED_BYTE,    // 8-bit unsigned integer
    BYTE,             // 8-bit signed integer
    UNSIGNED_SHORT,   // 16-bit unsigned integer
    SHORT,            // 16-bit signed integer
    UNSIGNED_INT,     // 32-bit unsigned integer
    INT,              // 32-bit signed integer
    FLOAT,            // 32-bit floating point
    DOUBLE            // 64-bit floating point
}
```

#### ZTextureChannels

Defines the channel layout:

```kotlin
enum class ZTextureChannels {
    R,      // Single channel (grayscale/red)
    RG,     // Two channels (red, green)
    RGB,    // Three channels (red, green, blue)
    RGBA,   // Four channels (red, green, blue, alpha)
    BGRA    // Four channels (blue, green, red, alpha) - platform-specific
}
```

#### ZTextureColorSpace

Defines the color space interpretation:

```kotlin
enum class ZTextureColorSpace {
    LINEAR,  // Linear color space (for calculations)
    SRGB     // sRGB color space (gamma-corrected, for display)
}
```

#### Normalized Flag

- **`true`**: Integer values are normalized to [0.0, 1.0] when read in shaders
  - Example: `UNSIGNED_BYTE` value 255 → 1.0
- **`false`**: Integer values are read as-is
  - Example: `UNSIGNED_BYTE` value 255 → 255.0

## ZTextureData Structure

```kotlin
@Serializable
data class ZTextureData(
    @ProtoNumber(1) var id: String,
    @ProtoNumber(2) var width: Int,
    @ProtoNumber(3) var height: Int,
    @ProtoNumber(4) var flipX: Boolean,
    @ProtoNumber(5) var flipY: Boolean,
    @ProtoNumber(6) var minFilter: ZTextureFilterMode,
    @ProtoNumber(7) var magFilter: ZTextureFilterMode,
    @ProtoNumber(8) var wrapModeU: ZTextureWrapMode,
    @ProtoNumber(9) var wrapModeV: ZTextureWrapMode,
    @ProtoNumber(10) var pixelType: ZBaseType,
    @ProtoNumber(11) var channels: ZTextureChannels,
    @ProtoNumber(12) var colorSpace: ZTextureColorSpace,
    @ProtoNumber(13) var normalized: Boolean,
    @ProtoNumber(100) var dataArray: ByteArray
)
```

## Common Texture Formats

### Standard Image Textures

| Format Name | Pixel Type | Channels | Color Space | Normalized | Use Case |
|-------------|-----------|----------|-------------|------------|----------|
| RGBA8 | UNSIGNED_BYTE | RGBA | SRGB | true | Standard color images |
| RGBA8 Linear | UNSIGNED_BYTE | RGBA | LINEAR | true | Albedo maps (PBR) |
| RGB8 | UNSIGNED_BYTE | RGB | SRGB | true | Color without alpha |
| R8 | UNSIGNED_BYTE | R | LINEAR | true | Grayscale, masks |
| RG8 | UNSIGNED_BYTE | RG | LINEAR | true | Normal maps (2-channel) |

### HDR Textures

| Format Name | Pixel Type | Channels | Color Space | Normalized | Use Case |
|-------------|-----------|----------|-------------|------------|----------|
| RGBA32F | FLOAT | RGBA | LINEAR | false | HDR images, render targets |
| RGBA16F | FLOAT* | RGBA | LINEAR | false | HDR (half precision) |
| R32F | FLOAT | R | LINEAR | false | Height maps, data textures |
| RG32F | FLOAT | RG | LINEAR | false | Flow maps, vector fields |

*Note: Half-float (16-bit) maps to `ZBaseType.FLOAT` as hardware handles precision internally.

### Data Textures

| Format Name | Pixel Type | Channels | Color Space | Normalized | Use Case |
|-------------|-----------|----------|-------------|------------|----------|
| R32UI | UNSIGNED_INT | R | LINEAR | false | Object IDs, indices |
| RGBA8UI | UNSIGNED_BYTE | RGBA | LINEAR | false | Packed data (4 bytes) |
| R16 | UNSIGNED_SHORT | R | LINEAR | true | High-precision masks |

### Special Purpose

| Format Name | Pixel Type | Channels | Color Space | Normalized | Use Case |
|-------------|-----------|----------|-------------|------------|----------|
| BGRA8 | UNSIGNED_BYTE | BGRA | SRGB | true | Platform-specific (Metal) |
| RGB8 Normal | UNSIGNED_BYTE | RGB | LINEAR | true | Normal maps (tangent space) |

## Texture Filtering

```kotlin
enum class ZTextureFilterMode {
    NEAREST,  // Point sampling (pixelated)
    LINEAR    // Bilinear filtering (smooth)
}
```

### Usage Guidelines

- **NEAREST**: Pixel art, UI elements, data lookups
- **LINEAR**: Photographic textures, smooth gradients

**Note**: Mipmapping is currently handled automatically by the backend.

## Texture Wrapping

```kotlin
enum class ZTextureWrapMode {
    REPEAT,         // Tile the texture
    CLAMP_TO_EDGE,  // Clamp to edge pixels
    MIRROR_REPEAT   // Mirror on each repetition
}
```

### Usage Guidelines

- **REPEAT**: Seamless tiling textures (ground, walls)
- **CLAMP_TO_EDGE**: UI elements, single images
- **MIRROR_REPEAT**: Patterns that benefit from mirroring

## Platform-Specific Implementation

### WebGPU

**File**: `src/webgpuMain/kotlin/zernikalos/components/material/ZTexture.wgpu.kt`

Maps compositional format to WebGPU format strings:

```kotlin
private fun mapTextureFormat(data: ZTextureData): String {
    return when {
        data.channels == ZTextureChannels.RGBA && 
        data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && 
        data.colorSpace == ZTextureColorSpace.LINEAR ->
            GPUTextureFormat.RGBA8Unorm
        
        data.channels == ZTextureChannels.RGBA && 
        data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && 
        data.colorSpace == ZTextureColorSpace.SRGB ->
            GPUTextureFormat.RGBA8UnormSRGB
        
        // ... more mappings
    }
}
```

### Metal (iOS/macOS)

**File**: `src/metalMain/kotlin/zernikalos/components/material/ZTextureRenderer.metal.kt`

Maps to Metal pixel formats:

```kotlin
private fun mapTextureFormat(data: ZTextureData): ULong {
    return when {
        data.channels == ZTextureChannels.RGBA && 
        data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && 
        data.colorSpace == ZTextureColorSpace.LINEAR ->
            MTLPixelFormatRGBA8Unorm
        
        data.channels == ZTextureChannels.BGRA && 
        data.pixelType == ZBaseType.UNSIGNED_BYTE &&
        data.normalized && 
        data.colorSpace == ZTextureColorSpace.SRGB ->
            MTLPixelFormatBGRA8Unorm_sRGB
        
        // ... more mappings
    }
}
```

### Android (OpenGL ES)

**File**: `src/androidMain/kotlin/zernikalos/components/material/ZTexture.android.kt`

Maps to OpenGL format and internal format:

```kotlin
private fun mapTextureFormat(data: ZTextureData): Int {
    return when(data.channels) {
        ZTextureChannels.RGBA, ZTextureChannels.BGRA -> GLES30.GL_RGBA
        ZTextureChannels.RGB -> GLES30.GL_RGB
        ZTextureChannels.RG -> GLES30.GL_RG
        ZTextureChannels.R -> GLES30.GL_RED
    }
}

private fun mapTextureInternalFormat(data: ZTextureData): Int {
    return when {
        data.channels == ZTextureChannels.RGBA && 
        data.colorSpace == ZTextureColorSpace.SRGB ->
            GLES30.GL_SRGB8_ALPHA8
        
        data.channels == ZTextureChannels.RGBA ->
            GLES30.GL_RGBA8
        
        // ... more mappings
    }
}
```

## Three.js Integration (ZKBuilder)

The texture parser in ZKBuilder automatically maps Three.js textures to Zernikalos format:

**File**: `ZKBuilder/packages/zkbuilder/src/parsers/parseTexture.ts`

### Mapping Functions

#### Pixel Type Mapping

```typescript
function mapThreeTypeToZPixelType(threeType: number): ZBaseType {
    switch(threeType) {
        case THREE.UnsignedByteType:
            return ZBaseType.UNSIGNED_BYTE;
        case THREE.FloatType:
        case THREE.HalfFloatType:
            return ZBaseType.FLOAT;
        // ... more mappings
    }
}
```

#### Channel Mapping

```typescript
function mapThreeFormatToZChannels(threeFormat: number): ZTextureChannels {
    if (threeFormat === THREE.RGBAFormat) return ZTextureChannels.RGBA;
    if (threeFormat === THREE.RGBFormat) return ZTextureChannels.RGB;
    if (threeFormat === THREE.RedFormat) return ZTextureChannels.R;
    if (threeFormat === THREE.RGFormat) return ZTextureChannels.RG;
    return ZTextureChannels.RGBA; // default
}
```

#### Color Space Mapping

```typescript
function mapThreeColorSpaceToZColorSpace(colorSpace?: string): ZTextureColorSpace {
    const isSRGB = colorSpace === THREE.SRGBColorSpace || 
                   colorSpace === THREE.LinearSRGBColorSpace;
    return isSRGB ? ZTextureColorSpace.SRGB : ZTextureColorSpace.LINEAR;
}
```

#### Normalized Mapping

```typescript
function mapThreeTypeToNormalized(threeType: number): boolean {
    switch(threeType) {
        case THREE.FloatType:
        case THREE.HalfFloatType:
            return false; // Float types are not normalized
        case THREE.UnsignedByteType:
        case THREE.ByteType:
        case THREE.ShortType:
        case THREE.UnsignedShortType:
            return true; // Standard integer types for images
        case THREE.IntType:
        case THREE.UnsignedIntType:
            return false; // Large ints for data/indices
        default:
            return true;
    }
}
```

## Usage Examples

### Creating a Standard Texture

```kotlin
val texture = ZTexture().apply {
    id = "myTexture"
    width = 512
    height = 512
    
    // Format components
    pixelType = ZBaseType.UNSIGNED_BYTE
    channels = ZTextureChannels.RGBA
    colorSpace = ZTextureColorSpace.SRGB
    normalized = true
    
    // Filtering and wrapping
    minFilter = ZTextureFilterMode.LINEAR
    magFilter = ZTextureFilterMode.LINEAR
    wrapModeU = ZTextureWrapMode.REPEAT
    wrapModeV = ZTextureWrapMode.REPEAT
    
    // Data
    dataArray = imageBytes
}
```

### Creating an HDR Texture

```kotlin
val hdrTexture = ZTexture().apply {
    id = "hdrEnvironment"
    width = 2048
    height = 1024
    
    // HDR format
    pixelType = ZBaseType.FLOAT
    channels = ZTextureChannels.RGBA
    colorSpace = ZTextureColorSpace.LINEAR
    normalized = false  // Float values not normalized
    
    minFilter = ZTextureFilterMode.LINEAR
    magFilter = ZTextureFilterMode.LINEAR
    wrapModeU = ZTextureWrapMode.CLAMP_TO_EDGE
    wrapModeV = ZTextureWrapMode.CLAMP_TO_EDGE
    
    dataArray = hdrFloatBytes
}
```

### Creating a Data Texture

```kotlin
val dataTexture = ZTexture().apply {
    id = "objectIDs"
    width = 1024
    height = 1024
    
    // Integer data format
    pixelType = ZBaseType.UNSIGNED_INT
    channels = ZTextureChannels.R
    colorSpace = ZTextureColorSpace.LINEAR
    normalized = false  // Read as integer values
    
    minFilter = ZTextureFilterMode.NEAREST  // No filtering
    magFilter = ZTextureFilterMode.NEAREST
    wrapModeU = ZTextureWrapMode.CLAMP_TO_EDGE
    wrapModeV = ZTextureWrapMode.CLAMP_TO_EDGE
    
    dataArray = idBytes
}
```

## Helper Functions

### Format String Generation

A helper function is provided for debugging and logging:

```kotlin
fun ZTextureData.getFormatString(): String {
    val channelStr = when(channels) {
        ZTextureChannels.R -> "r"
        ZTextureChannels.RG -> "rg"
        ZTextureChannels.RGB -> "rgb"
        ZTextureChannels.RGBA -> "rgba"
        ZTextureChannels.BGRA -> "bgra"
    }
    val typeStr = when(pixelType) {
        ZBaseType.UNSIGNED_BYTE -> "8"
        ZBaseType.UNSIGNED_SHORT -> "16"
        ZBaseType.FLOAT -> "32float"
        else -> "8"
    }
    val normStr = if(normalized) "unorm" else "uint"
    val csStr = when(colorSpace) {
        ZTextureColorSpace.SRGB -> "-srgb"
        ZTextureColorSpace.LINEAR -> ""
    }
    return "$channelStr$typeStr$normStr$csStr"
}
```

Example output: `"rgba8unorm-srgb"`, `"r32float"`, `"rg16unorm"`

## Future Extensions

The compositional design makes it easy to add support for:

### Compressed Formats

Add new channel types:
- `BC7` (DirectX texture compression)
- `ASTC` (ARM texture compression)
- `ETC2` (OpenGL ES compression)

### Additional Bit Depths

Extend `ZBaseType` with:
- 10-bit per channel formats
- 11-11-10 packed float formats

### Additional Color Spaces

Extend `ZTextureColorSpace` with:
- Display P3
- Rec.2020
- ACES

## Version History

- **v0.15.0**: Compositional texture system introduced, replacing monolithic `ZTextureFormat` enum
- **v0.14.0**: Original texture format system

## Related Documentation

- [Material System](material-system.md) - How textures integrate with materials
- [Component Architecture](components/components-architecture.md) - Overall component design

## Performance Considerations

### Color Space

- **sRGB textures**: Hardware performs automatic gamma correction, no shader cost
- **Linear textures**: Direct values, used for calculations (normals, masks, data)

### Normalized vs Non-Normalized

- **Normalized**: Standard for images, uses less bandwidth (8-bit → float in shader)
- **Non-normalized**: Required for data textures, preserves exact integer values

### Channel Selection

- Use minimal channels needed:
  - Single-channel (R) saves 75% memory vs RGBA
  - Dual-channel (RG) saves 50% memory vs RGBA

### Filtering

- **LINEAR**: Slightly more expensive but smoother results
- **NEAREST**: Faster, use for pixel art or data lookups

## Troubleshooting

### Common Issues

**Issue**: Texture appears too dark or washed out
- **Solution**: Check color space. Photos should use `SRGB`, computed data should use `LINEAR`

**Issue**: Integer texture values not reading correctly in shader
- **Solution**: Ensure `normalized = false` for data textures

**Issue**: HDR texture clamped to [0, 1]
- **Solution**: Use `pixelType = FLOAT` with `normalized = false`

**Issue**: Texture format not supported on platform
- **Solution**: Check platform-specific mapper functions for supported combinations

## Conclusion

The Zernikalos texture system provides a modern, flexible foundation for handling diverse texture use cases across multiple platforms. The compositional design ensures extensibility while maintaining clean, understandable code.

