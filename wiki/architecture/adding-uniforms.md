# Adding Uniforms to Zernikalos Engine

This document outlines the general process for adding new uniforms to the Zernikalos engine. The process involves modifying several files across different layers of the engine architecture.

## Overview

Adding a uniform to the engine requires changes in multiple locations to ensure proper integration with the shader generation system, material system, and rendering pipeline.

## Current Limitations

**Important**: The engine currently suffers from a limitation in the shader generators where they cannot automatically generate complete uniform blocks. This means that **every individual uniform must be manually constructed** when building uniform blocks.

## Required Modifications

### 1. Uniform Descriptors (`ZUniformDescriptor.kt`)

Add the new uniform identifiers and names:

```kotlin
object UNIFORM_NAMES {
    // ... existing names ...
    const val NEW_UNIFORM_NAME = "NewUniformName"
}

object UNIFORM_IDS {
    // ... existing IDs ...
    const val NEW_UNIFORM_ID = 20 // Choose unique ID
    const val BLOCK_NEW_MATERIAL = 104 // Choose unique block ID
}
```

Create individual uniform data objects:

```kotlin
val ZUniformNewUniform: ZUniformData
    get() = ZUniformData(UNIFORM_IDS.NEW_UNIFORM_ID, "u_newUniform", 1, ZTypes.VEC4F)
```

Create the uniform block:

```kotlin
val ZNewMaterialBlock: ZUniformBlock
    get() = ZUniformBlock(UNIFORM_IDS.BLOCK_NEW_MATERIAL, "u_newMaterialBlock", listOf(
        UNIFORM_NAMES.NEW_UNIFORM_NAME to ZUniformNewUniform
        // Add all other uniforms in the block
    ))
```

### 2. Component Integration (if applicable)

If the uniform is related to a specific component (like materials, lights, etc.), you may need to:

- Add the uniform data to the component's data structure
- Update the component class to expose the uniform
- Modify the component's serialization if needed

**Note**: This step is only required if the uniform is tied to a specific component. For standalone uniforms, you can skip this step.

### 3. Shader Parameters

#### 3.1 Adding the New Flag (`ZShaderProgramParameters.kt`)

Add the new uniform flag:

```kotlin
data class ZShaderProgramParameters(
    // ... existing properties ...
    var useNewUniform: Boolean = false
)
```

#### 3.2 Integration in Component Logic

If the uniform is used by specific components, update their logic:

```kotlin
// Example: Model integration
private fun buildShaderParameters(): ZShaderProgramParameters {
    // ... existing logic ...
    if (shouldUseNewUniform) {
        shaderParameters.useNewUniform = true
    }
    return shaderParameters
}
```

#### 3.3 Add to Shader Generator (`ZShaderGenerator.kt`)

Update the uniform addition logic:

```kotlin
private fun addRequiredUniforms(params: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
    // ... existing uniforms ...
    if (params.useNewUniform) {
        shaderProgram.addUniformBlock("NewUniform", ZNewUniformBlock)
    }
}
```

### 4. Uniform Generators

#### 4.1 Create Individual Generators

Create individual uniform generators for each uniform in the block in the `generators/uniformgenerator/` folder:

```kotlin
class ZNewUniformGenerator: ZUniformGenerator {
    override fun compute(
        sceneContext: ZSceneContext,
        obj: ZObject
    ): ZAlgebraObject {
        // Return the appropriate value for this uniform
        return computeUniformValue(sceneContext, obj)
    }
}
```

#### 4.2 Add Generator to Context

Register the new uniform generators in the rendering context `ZSceneContext.kt`. This typically involves updating the context initialization to recognize and use the new generators.

### 5. Shader Source Files

**Critical**: You must update shader source files for **ALL** supported platforms:

- **Android/OpenGL**: `ZDefaultShader.android.kt`
- **Metal**: `ZDefaultShader.metal.kt` 
- **WebGPU**: `ZDefaultShader.wgpu.kt`

Each platform requires:
- Uniform block definitions
- Shader logic implementation
- Proper preprocessor directives

**Note**: Currently, Metal and WebGPU shaders are incomplete and need to be updated to match the Android implementation.

### 6. Proto Definitions (ZKBuilder)

If the uniform is used in the ZKBuilder pipeline, update the proto definitions:

```protobuf
message ZkNewUniform {
    required ZkColor property1 = 1;
    required float property2 = 2;
}

message ZkComponent {
    // ... existing fields ...
    optional ZkNewUniform newUniform = 11;
}
```

## Implementation Checklist

- [ ] Add uniform names and IDs to `ZUniformDescriptor.kt`
- [ ] Create individual uniform data objects in `ZUniformDescriptor.kt`
- [ ] Create uniform block definition in `ZUniformDescriptor.kt`
- [ ] Add uniform flag to shader parameters in `ZShaderProgramParameters.kt`
- [ ] Update component integration logic (if applicable) - varies by component
- [ ] Add to shader generator in `ZShaderGenerator.kt`
- [ ] Create uniform generators for each uniform in `generators/uniformgenerator/` folder
- [ ] Add generators to the rendering context in `ZSceneContext.kt`
- [ ] Update Android shader source files in `ZDefaultShader.android.kt`
- [ ] Update Metal shader source files in `ZDefaultShader.metal.kt`
- [ ] Update WebGPU shader source files in `ZDefaultShader.wgpu.kt`
- [ ] Update proto definitions (if applicable) in `ZKBuilder/packages/zkbuilder/proto/`
- [ ] Test uniform binding and rendering
- [ ] Verify shader compilation on all platforms

## Important Notes

1. **Unique IDs**: Ensure all uniform IDs and block IDs are unique across the system
2. **Proto Numbers**: Choose unique proto numbers for serialization
3. **Platform Consistency**: All shader implementations must be consistent across platforms
4. **Manual Construction**: Remember that uniform blocks must be manually constructed due to generator limitations
5. **Testing**: Test on all target platforms to ensure compatibility

## Example Implementation

See the Phong material implementation as a reference for component-specific uniforms:
- `ZPhongMaterialData` in `ZMaterial.kt`
- `ZUniformPhongMaterialBlock` in `ZUniformDescriptor.kt`
- Phong shader logic in shader source files
- `ZPhongDiffuseGenerator` and related generators

For standalone uniforms, see the scene matrix uniforms as reference:
- `ZModelViewProjectionMatrixBlock` in `ZUniformDescriptor.kt`
- Scene matrix handling in shader source files
