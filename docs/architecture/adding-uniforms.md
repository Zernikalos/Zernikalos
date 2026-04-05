# Adding Uniforms to Zernikalos Engine

This document outlines the general process for adding new uniforms to the Zernikalos engine. The process involves modifying several files across different layers of the engine architecture.

## Overview

Adding a uniform to the engine requires changes in multiple locations to ensure proper integration with the shader generation system, material system, and rendering pipeline. The engine uses a **declarative, single-source** definition per block via `UniformBlockDef`, which unifies layout, generators, and block keys.

## Architecture Summary

- **UniformKey**: `data class UniformKey(val id: Int, val name: String)` — single source for both identifier and logical name.
- **UNIFORM_KEYS**: Object holding all `UniformKey` constants (block and member keys).
- **UniformMember**: Defines one member with `key: UniformKey`, `dataType`, `count`, and `glslName`.
- **UniformBlockDef**: Declarative definition of a block: `blockKey`, `glslName`, `members`, and `generators`. Produces `ZUniform` via `toZUniform()`.
- **ZShaderGenerator**: Adds blocks to the shader program using `UNIFORM_KEYS.BLOCK_*.name` as the map key.

The render loop iterates over `shaderProgram.uniforms.blocks` and calls `uniform.computeValue(ctx.sceneContext, model)` for each block. Generators are embedded in each `ZUniform` via `addGenerators()` in `toZUniform()`.

## Current Limitations

**Important**: The engine currently suffers from a limitation in the shader generators where they cannot automatically generate complete uniform blocks. This means that **every individual uniform must be manually constructed** when building uniform blocks, and platform-specific shader sources (GLSL, WGSL, Metal) must be kept in sync with the block definitions.

## Required Modifications

### 1. Uniform Descriptors (`ZUniformDescriptor.kt`)

File: `engine/src/commonMain/kotlin/zernikalos/components/shader/ZUniformDescriptor.kt`.

#### 1.1 Add new keys to `UNIFORM_KEYS`

Add the new uniform key (for members) or block key:

```kotlin
object UNIFORM_KEYS {
    // ... existing keys ...
    // For a new member in an existing block:
    val NEW_MEMBER = UniformKey(20, "NewMember")

    // For a new block:
    val BLOCK_NEW_MATERIAL = UniformKey(104, "NewMaterial")
}
```

Choose unique IDs. Block IDs are typically higher (e.g. 14+) to avoid clashes with member IDs. Member IDs start at 0.

#### 1.2 Create or extend a `UniformBlockDef`

**New block:**

```kotlin
object NewMaterialUniforms : UniformBlockDef(
    blockKey = UNIFORM_KEYS.BLOCK_NEW_MATERIAL,
    glslName = "u_newMaterialBlock",
    members = listOf(
        UniformMember(UNIFORM_KEYS.NEW_MEMBER, ZTypes.VEC4F, count = 1, glslName = "u_newMember")
    ),
    generators = mapOf(
        UNIFORM_KEYS.NEW_MEMBER.name to ZNewMemberGenerator
    )
)

val ZNewMaterialBlock: ZUniform
    get() = NewMaterialUniforms.toZUniform()
```

**Extending an existing block:** add a new `UniformMember` and a generator entry to the existing `UniformBlockDef` object.

### 2. Shader Parameters (`ZShaderProgramParameters.kt`)

File: `engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderProgramParameters.kt`.

Add a flag for when the uniform block is needed:

```kotlin
class ZShaderProgramParameters() {
    // ... existing properties ...
    var useNewMaterial: Boolean = false
}
```

### 3. Component Logic

If the uniform is used by specific components (e.g. material), set the flag in `buildShaderParameters()`:

```kotlin
// Example: in ZModel.buildShaderParameters()
if (shouldUseNewMaterial) {
    shaderParameters.useNewMaterial = true
}
```

### 4. Shader Generator (`ZShaderGenerator.kt`)

File: `engine/src/commonMain/kotlin/zernikalos/generators/shadergenerator/ZShaderGenerator.kt`.

Add the block when the flag is true. **Always use `UNIFORM_KEYS.BLOCK_*.name`** to avoid hardcoded strings:

```kotlin
private fun addRequiredUniforms(params: ZShaderProgramParameters, shaderProgram: ZShaderProgram) {
    // ... existing uniforms ...
    if (params.useNewMaterial) {
        shaderProgram.addUniform(UNIFORM_KEYS.BLOCK_NEW_MATERIAL.name, ZNewMaterialBlock)
    }
}
```

### 5. Uniform Generators

Create one generator per member in `engine/src/commonMain/kotlin/zernikalos/generators/uniformgenerator/`. `ZUniformGenerator` is a type alias: `(ZSceneContext, ZObject) -> ZAlgebraObject`.

```kotlin
val ZNewMemberGenerator: ZUniformGenerator = { sceneContext, obj ->
    // Return the appropriate value (e.g. ZMatrix4, ZColor, etc.)
    computeValue(sceneContext, obj)
}
```

Generators are registered inside the `UniformBlockDef.generators` map. The key must be `UNIFORM_KEYS.XXX.name` (the member name). No separate registration in `ZSceneContext` is required for the blocks flow — the block embeds them via `toZUniform()`.

**Optional:** If you use the alternative "entries" flow (lookup via `context.getUniform(name)`), call `blockDef.registerGenerators(context)` to register per-member generators in the scene context.

### 6. Shader Source Files

**Critical**: You must update shader source files for **ALL** supported platforms. Paths are under `engine/src/<platform>Main/kotlin/zernikalos/generators/shadergenerator/libs/`:

- **Android/OpenGL**: `androidMain/.../libs/ZDefaultShader.android.kt`
- **Metal**: `metalMain/.../libs/ZDefaultShader.metal.kt`
- **WebGPU**: `webgpuMain/.../libs/ZSkinningShader.wgpu.kt`, `ZSkinningNoPbrShader.wgpu.kt`

Each platform requires:
- Uniform block definitions (matching the `glslName` and member layout from `UniformBlockDef`)
- Shader logic implementation
- Proper preprocessor directives (e.g. `#ifdef USE_SKINNING`)

The platform-specific shader generator builds the final shader source from these libs and the `ZShaderProgramParameters` flags. Ensure any new `#ifdef` is driven from a flag and from `buildShaderSource` on each platform.

### 7. Proto Definitions (ZKBuilder)

If the uniform is used in the ZKBuilder pipeline, update the proto definitions under **`ZKBuilder/packages/zkbuilder/proto/`** (e.g. `material.proto`, `model.proto`).

## Implementation Checklist

- [ ] Add uniform key(s) to `UNIFORM_KEYS` in `ZUniformDescriptor.kt`
- [ ] Create or extend a `UniformBlockDef` with members and generators
- [ ] Add `val ZXxxBlock = XxxUniforms.toZUniform()` for the block
- [ ] Add a flag (e.g. `useNewMaterial`) in `ZShaderProgramParameters.kt`
- [ ] Set that flag in component logic when the uniform is needed
- [ ] In `ZShaderGenerator.addRequiredUniforms()`, add `shaderProgram.addUniform(UNIFORM_KEYS.BLOCK_*.name, ZXxxBlock)` when the flag is true
- [ ] Create one generator per block member in `engine/.../generators/uniformgenerator/`
- [ ] Update Android shader in `ZDefaultShader.android.kt` (block layout and logic)
- [ ] Update Metal shader in `ZDefaultShader.metal.kt`
- [ ] Update WebGPU shaders as needed
- [ ] Update proto definitions if applicable
- [ ] Test uniform binding and rendering
- [ ] Verify shader compilation on all platforms

## Important Notes

1. **Unique IDs**: Ensure all member and block IDs in `UNIFORM_KEYS` are unique. Block IDs typically use higher numbers (13–17, etc.).
2. **UNIFORM_IDS**: The object `UNIFORM_IDS` is deprecated and delegates to `UNIFORM_KEYS.BLOCK_*.id`. It is kept for shader binding references (WGSL/Metal). Prefer `UNIFORM_KEYS` for new code.
3. **Generator key = member name**: In `UniformBlockDef.generators`, the map key must be `UNIFORM_KEYS.XXX.name` (the logical member name).
4. **Platform consistency**: Uniform block layout and `glslName` in shaders must match the engine's block definition.
5. **Testing**: Test on all target platforms to ensure compatibility.

## Example Implementations

**Scene matrix block:**
- `SceneMatrixUniforms` with members `ProjectionMatrix`, `ViewMatrix`, `ModelViewProjectionMatrix`
- `ZModelViewProjectionMatrixBlock = SceneMatrixUniforms.toZUniform()`
- Added as `UNIFORM_KEYS.BLOCK_SCENE_MATRIX.name` in `ZShaderGenerator`

**Skinning block:**
- `SkinningUniforms` with `BONES` and `INVERSE_BIND_MATRIX`
- `ZSkinningMatrixBlock`, `ZModelSkinningMatrixBlock`
- Added when `params.useSkinning` is true

**PBR / Phong materials:**
- `PbrMaterialUniforms`, `PhongMaterialUniforms`
- Added when `params.usePbrMaterial` or `params.usePhongMaterial` is true
- `UNIFORM_KEYS.BLOCK_PBR_MATERIAL.name`, `UNIFORM_KEYS.BLOCK_PHONG_MATERIAL.name`
