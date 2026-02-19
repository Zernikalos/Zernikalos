# Adding Uniforms to Zernikalos Engine

This document outlines the general process for adding new uniforms to the Zernikalos engine. The process involves modifying several files across different layers of the engine architecture.

## Overview

Adding a uniform to the engine requires changes in multiple locations to ensure proper integration with the shader generation system, material system, and rendering pipeline.

## Current Limitations

**Important**: The engine currently suffers from a limitation in the shader generators where they cannot automatically generate complete uniform blocks. This means that **every individual uniform must be manually constructed** when building uniform blocks.

## Required Modifications

### 1. Uniform Descriptors (`ZUniformDescriptor.kt`)

File: `engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt`.

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

Create the uniform block (type is `ZUniform`, not `ZUniformBlock`):

```kotlin
val ZNewMaterialBlock: ZUniform
    get() = ZUniform(UNIFORM_IDS.BLOCK_NEW_MATERIAL, "u_newMaterialBlock", listOf(
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

File: `engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderProgramParameters.kt`.

Add the new uniform flag (it is a `class`, not a `data class`):

```kotlin
class ZShaderProgramParameters() {
    // ... existing properties ...
    var useNewUniform: Boolean = false
}
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

File: `engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderGenerator.kt`.

Update the uniform addition logic. Use `addUniform` (not `addUniformBlock`); the first argument is a logical name for the uniform/block:

```kotlin
private fun addRequiredUniforms(params: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
    // ... existing uniforms ...
    if (params.useNewUniform) {
        shaderProgram.addUniform("NewUniform", ZNewMaterialBlock)
    }
}
```

### 4. Uniform Generators

#### 4.1 Create Individual Generators

Create one generator **per uniform in the block** in `engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/`. `ZUniformGenerator` is a type alias: `(ZSceneContext, ZObject) -> ZAlgebraObject`. Use a lambda or top-level function:

```kotlin
val ZNewUniformGenerator: ZUniformGenerator = { sceneContext, obj ->
    // Return the appropriate value (e.g. ZMatrix4, ZAlgebraObjectCollection, etc.)
    computeUniformValue(sceneContext, obj)
}
```

#### 4.2 Register Generators in Context

Register each generator in **`ZSceneContext.kt`** (`engine/src/commonMain/kotlin/zernikalos/context/ZSceneContext.kt`) inside `ZSceneContextDefault` (the class that extends `ZSceneContext`). The **key must match the uniform name used in the block**: use `UNIFORM_NAMES.XXX` (the same name as in the block's `listOf(UNIFORM_NAMES.XXX to ZUniform...)`). At render time, the engine looks up generators by these member names to fill each slot in the block.

```kotlin
addUniformGenerator(UNIFORM_NAMES.NEW_UNIFORM_NAME, ZNewUniformGenerator)
```

### 5. Shader Source Files

**Critical**: You must update shader source files for **ALL** supported platforms. Paths are under `engine/src/<platform>Main/kotlin/zernikalos/generators/shadergenerator/libs/`:

- **Android/OpenGL**: `androidMain/.../libs/ZDefaultShader.android.kt`
- **Metal**: `metalMain/.../libs/ZDefaultShader.metal.kt`
- **WebGPU**: `webgpuMain/.../libs/ZDefaultShader.wgpu.kt`

Each platform requires:
- Uniform block definitions (matching the block name and members from `ZUniformDescriptor.kt`)
- Shader logic implementation
- Proper preprocessor directives (e.g. `#ifdef USE_SKINNING`)

The platform-specific **shader generator** (e.g. `ZDefaultShaderGenerator.android.kt`) builds the final shader source from these libs and the `ZShaderProgramParameters` flags; ensure any new `#ifdef` is driven from a flag in `ZShaderProgramParameters` and from `buildShaderSource` / preprocessor on each platform.

### 6. Proto Definitions (ZKBuilder)

If the uniform is used in the ZKBuilder pipeline, update the proto definitions under **`ZKBuilder/packages/zkbuilder/proto/`** (e.g. `material.proto`, `model.proto`, etc.):

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

- [ ] Add uniform names to `UNIFORM_NAMES` and IDs to `UNIFORM_IDS` in `engine/.../components/shader/ZUniformDescriptor.kt`
- [ ] Create individual `ZUniformData` (or array helpers like `ZBonesMatrixArray`) in `ZUniformDescriptor.kt`
- [ ] Create the uniform block with `ZUniform(...)` in `ZUniformDescriptor.kt`
- [ ] Add a flag (e.g. `useNewUniform`) in `engine/.../generators/shadergenerator/ZShaderProgramParameters.kt`
- [ ] Set that flag in component logic (e.g. `ZModel.buildShaderParameters()`) when the uniform is needed
- [ ] In `ZShaderGenerator.addRequiredUniforms()`, call `shaderProgram.addUniform("LogicalName", ZNewMaterialBlock)` when the flag is true
- [ ] Create one uniform generator per block member in `engine/.../generators/uniformgenerator/` (lambda returning `ZAlgebraObject`)
- [ ] In `ZSceneContext.kt`, inside `ZSceneContextDefault`, register each generator with `addUniformGenerator(UNIFORM_NAMES.XXX, ZXxxGenerator)` (key = name used in the block)
- [ ] Update Android shader in `engine/.../androidMain/.../libs/ZDefaultShader.android.kt` (block layout + logic)
- [ ] Update Metal shader in `engine/.../metalMain/.../libs/ZDefaultShader.metal.kt`
- [ ] Update WebGPU shader in `engine/.../webgpuMain/.../libs/ZDefaultShader.wgpu.kt`
- [ ] Update proto definitions (if applicable) in `ZKBuilder/packages/zkbuilder/proto/`
- [ ] Test uniform binding and rendering
- [ ] Verify shader compilation on all platforms

## Important Notes

1. **Unique IDs**: Ensure all `UNIFORM_IDS` and block IDs are unique across the system.
2. **Generator key = block member name**: When registering generators in `ZSceneContextDefault`, the key must be the same as the name used in the block (e.g. `UNIFORM_NAMES.BONES`). The render loop iterates over uniform entries and looks up `getUniform(name)`; for blocks, members are also stored by their names, so each member is filled by the generator registered under that name.
3. **Proto Numbers**: Choose unique proto numbers for serialization.
4. **Platform consistency**: Uniform block layout and names in shaders must match the engine’s block definition.
5. **Manual construction**: Uniform block content is filled per member via generators; there is no single “block generator” unless the block has only one member.
6. **Testing**: Test on all target platforms to ensure compatibility.

## Example Implementation

**Component-specific uniforms (e.g. material):**
- Phong: `ZUniformPhongMaterialBlock` and `ZUniformPhongAmbient`, etc., in `ZUniformDescriptor.kt`
- `ZShaderProgramParameters.usePhongMaterial` and `addUniform("PhongMaterial", ZUniformPhongMaterialBlock)` in `ZShaderGenerator.kt`
- `addUniformGenerator(UNIFORM_NAMES.PHONG_AMBIENT, ZPhongAmbientGenerator)` (and similar) in `ZSceneContext.kt`
- Phong shader logic in `ZDefaultShader.android.kt` (and Metal/WebGPU)

**Block with multiple members (scene matrix):**
- `ZModelViewProjectionMatrixBlock` in `ZUniformDescriptor.kt` with members `ProjectionMatrix`, `ViewMatrix`, `ModelViewProjectionMatrix`
- Generators registered as `UNIFORM_NAMES.MODEL_VIEW_PROJECTION_MATRIX`, `UNIFORM_NAMES.VIEW_MATRIX`, `UNIFORM_NAMES.PROJECTION_MATRIX` in `ZSceneContextDefault`

**Skinning (block with arrays):**
- `ZSkinningMatrixBlock` with `BONES` and `INVERSE_BIND_MATRIX`; generators `ZBoneMatrixGenerator`, `ZInverseBindMatrixGenerator` registered under those `UNIFORM_NAMES`
- Optional `ZModelSkinningMatrixBlock` and `ZModelSkinningMatrixGenerator` / `ZInverseModelSkinningMatrixGenerator` for mesh-level bind matrix
