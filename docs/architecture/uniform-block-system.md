# Uniform Block Definition System

## Overview

The uniform block definition system provides a declarative, centralized way to define uniform blocks in the Zernikalos engine. This reduces the number of touch points required when adding new uniforms and establishes a single source of truth for block layout and member types.

## Core Types

### UniformMember

Defined in [`UniformBlockDef.kt`](../../engine/src/commonMain/kotlin/zernikalos/components/shader/UniformBlockDef.kt):

```kotlin
data class UniformMember(
    val key: UniformKey,           // Unified id + name
    val dataType: ZDataType,      // Shader data type
    val count: Int = 1,           // Array count (e.g., 100 for bone matrices)
    val glslName: String          // Shader variable name
)
```

A `UniformMember` describes a single field inside a uniform block:
- `key`: Combines numeric ID and string name from `UNIFORM_KEYS`/`UNIFORM_NAMES`
- `dataType`: Type information including byte size
- `count`: Element count for arrays (default 1 for scalars/vectors)
- `glslName`: Name used in shader code (auto-generated from key if not specified)

### UniformBlockDef

Abstract base class for defining uniform blocks:

```kotlin
abstract class UniformBlockDef(
    val blockKey: UniformKey,                    // Block identifier
    val glslName: String,                        // Block name in shaders (e.g., "u_sceneMatrixBlock")
    val members: List<UniformMember>,            // Block layout (order matters)
    val generators: Map<String, ZUniformGenerator> // Member name → generator function
) {
    /** Total byte size for GPU buffer allocation */
    val byteSize: Int
        get() = members.sumOf { it.dataType.byteSize * it.count }

    /** Build ZUniform with embedded generators for shader program */
    fun toZUniform(): ZUniform

    /** Register per-member generators in scene context */
    fun registerGenerators(context: ZSceneContext)
}
```

## Usage Pattern

### 1. Define Block Subclass

```kotlin
object SceneMatrixBlock : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SCENE_MATRIX,  // id + name
    glslName = "u_sceneMatrixBlock",
    members = listOf(
        UniformMember(
            key = UNIFORM_KEYS.MODEL_MATRIX,
            dataType = ZTypes.MAT4F,
            glslName = "u_modelMatrix"
        ),
        UniformMember(
            key = UNIFORM_KEYS.VIEW_MATRIX,
            dataType = ZTypes.MAT4F
        ),
        UniformMember(
            key = UNIFORM_KEYS.PROJECTION_MATRIX,
            dataType = ZTypes.MAT4F
        )
    ),
    generators = mapOf(
        "MODEL_MATRIX" to ZModelMatrixGenerator,
        "VIEW_MATRIX" to ZViewMatrixGenerator,
        "PROJECTION_MATRIX" to ZProjectionMatrixGenerator
    )
)
```

### 2. Register at Context Initialization

```kotlin
override fun internalInitialize(ctx: ZContext) {
    // Build uniform with embedded generators
    val uniform = SceneMatrixBlock.toZUniform()

    // Register generators so computeValue() finds them
    SceneMatrixBlock.registerGenerators(sceneContext)

    // Add uniform to program
    shaderProgram.addUniform(uniform)
}
```

### 3. Shader Integration

The GLSL block must match the Kotlin definition:

```glsl
uniform u_sceneMatrixBlock {
    mat4 u_modelMatrix;
    mat4 u_viewMatrix;
    mat4 u_projectionMatrix;
};
```

## Benefits

### Before (Manual Registration)

Multiple touch points required:
1. Add to `UNIFORM_KEYS` / `UNIFORM_IDS`
2. Define in `ZUniformDescriptor.kt`
3. Add generator in `ZSceneContext`
4. Manually create `ZUniform` with `ZUniformData` pairs
5. Register in `ZShaderGenerator.kt`
6. Define in GLSL shader
7. Define in MSL shader
8. Define in WGSL shader

### After (Declarative Block)

Reduced touch points:
1. Add to `UNIFORM_KEYS` (for key.id and key.name)
2. Define `UniformBlockDef` subclass with members and generators
3. Call `toZUniform()` and `registerGenerators()` at init
4. Define layout once in shaders (manual - still required)

## Current Implementation Status

### Implemented

- ✅ `UniformMember` data class
- ✅ `UniformBlockDef` abstract base class
- ✅ `toZUniform()` builder method
- ✅ `registerGenerators()` registration helper
- ✅ `byteSize` calculation
- ✅ Generator embedding in `ZUniform`

### Not Implemented

- ❌ Codegen for shader layout (GLSL/MSL/WGSL generation from Kotlin definition)
- ❌ Validation that Kotlin layout matches shader layout
- ❌ Feature-based uniform bundles (skinning, PBR, lighting as unified features)
- ❌ Auto-registration discovery (blocks must be manually registered)

## Future Directions

### Option A: Shader Layout Codegen

Generate shader snippets from `UniformBlockDef`:

```kotlin
// Kotlin definition (single source)
object SkinningBlock : UniformBlockDef(...)

// Generated → skinning_block.glsl
layout(std140) uniform SkinningBlock {
    mat4 bones[100];
    mat4 inverseBindMatrices[100];
};
```

Requires: Build-time Kotlin script or Gradle task to emit shader includes.

### Option B: Feature-Based Bundles

Group related uniforms by engine feature:

```kotlin
object SkinningFeature : UniformFeatureBundle(
    block = SkinningBlock,
    shaderFlag = "USE_SKINNING",
    parametersFlag = ShaderProgramParameters::useSkinning
)

// Auto-enable block when feature is enabled
shaderProgram.enableFeature(SkinningFeature)
```

### Option C: Runtime Validation

Debug builds validate layout consistency:

```kotlin
// Compare Kotlin byte offsets with shader introspection
val reflection = shaderProgram.reflectUniformBlock("SkinningBlock")
SkinningBlock.validateAgainst(reflection)  // Throws on mismatch
```

## Migration Guide

Existing blocks can be migrated incrementally:

1. **Keep existing registration** for backward compatibility
2. **Add UniformBlockDef** definition alongside
3. **Switch to `toZUniform()`** once validated
4. **Remove manual `ZUniformData` construction**

Example migration:

```kotlin
// Old manual way (still works)
val uniform = ZUniform(UNIFORM_IDS.BLOCK_SKINNING, "u_skinningBlock", listOf(
    "BONES" to ZUniformData(UNIFORM_IDS.BONES, "u_bones", 100, ZTypes.MAT4F),
    ...
))
sceneContext.addUniformGenerator("BONES", ZBoneGenerator)

// New declarative way
object SkinningBlock : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_SKINNING,
    glslName = "u_skinningBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.BONES, ZTypes.MAT4F, 100),
        ...
    ),
    generators = mapOf("BONES" to ZBoneGenerator, ...)
)

// Usage
val uniform = SkinningBlock.toZUniform()
SkinningBlock.registerGenerators(sceneContext)
```

## Related Documentation

- [Adding Uniforms](./adding-uniforms.md) - Legacy manual uniform registration
- [ZUniformDescriptor.kt reference](../../engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt)
- [ZSceneContext generators](../../engine/src/commonMain/kotlin/zernikalos/context/ZSceneContext.kt)
