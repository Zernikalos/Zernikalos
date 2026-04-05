# Proposal: Simplify Uniform Registration

**Status**: Draft  
**Created**: 2025  
**Related**: [Adding Uniforms (architecture)](../architecture/adding-uniforms.md)

---

## Summary

Adding a new uniform to the Zernikalos engine currently requires editing **six or more** places across descriptors, shader parameters, generators, scene context, and each platform’s shader source. This proposal suggests a **declarative, single-source** way to define uniforms so that descriptor, generator registration, and (where possible) shader wiring are derived from one definition, reducing mistakes and duplication.

---

## Problem

The current flow for adding a uniform (or a uniform block) has several pain points:

1. **Many touch points**  
   Developers must edit: `ZUniformDescriptor.kt`, `ZShaderProgramParameters.kt`, component logic (e.g. `ZModel.buildShaderParameters()`), `ZShaderGenerator.kt`, one generator file per block member, `ZSceneContext.kt`, and **three** shader files (Android, Metal, WebGPU). It is easy to miss a step or mis-type a name.

2. **Fragile naming contract**  
   Generator lookup uses the **block member name** (e.g. `UNIFORM_NAMES.BONES`). The same name must appear in the block definition and in `addUniformGenerator(...)`. There is no compile-time or tooling check that they stay in sync.

3. **Duplicate block layout**  
   Block layout (names, types, counts) is defined in Kotlin in `ZUniformDescriptor.kt` and again in each platform’s shader (GLSL, MSL, WGSL). Adding a member forces parallel edits in multiple languages.

4. **Boilerplate**  
   Every new uniform repeats the same pattern: add name/ID, add descriptor, add flag, add generator, register in context. There is no reuse or composition.

5. **Scattered discovery**  
   To understand “what uniforms does skinning use?” you must open several files. There is no single place that lists a feature’s uniforms and how they are filled.

---

## Goals

- **Fewer edits**: Adding a simple uniform or block should require changing as few places as possible (ideally one or two).
- **Single source of truth**: Block structure (member names, types, counts) should be defined once and reused for descriptors and, where feasible, for shader generation or validation.
- **Safe by default**: Wrong or missing generator registration should be detectable (e.g. at init or via tests), not only at runtime when a uniform is null.
- **Backward compatible**: Existing uniforms and render path should keep working during and after the change; no big-bang rewrite.

---

## Proposed Direction: Declarative Uniform Definitions

### Idea

Introduce a **declarative definition** of uniform blocks (and optionally single uniforms) that:

1. Describes the block once: name, GLSL/API block name, list of members (name, type, count).
2. Is used to generate or populate:
   - `UNIFORM_NAMES` / `UNIFORM_IDS` and `ZUniform` block in `ZUniformDescriptor.kt` (or equivalent),
   - Generator registration in `ZSceneContext` (so each member is bound to its generator by name),
   - Optional: a small shader snippet or metadata so platform shaders can include the block layout from a single spec.

3. Keeps **generator implementations** as they are (lambdas `(ZSceneContext, ZObject) -> ZAlgebraObject`) so behaviour stays in code; only **wiring** is simplified.

### Option A: Kotlin-first registry (no codegen)

- **Single module or file**: e.g. `UniformRegistry.kt` or `engine/.../uniforms/`.
- **Define blocks in Kotlin** with a small DSL or data structure:

```kotlin
// Conceptual
object SkinningUniforms : UniformBlockDef(
    blockId = UNIFORM_IDS.BLOCK_SKINNING_MATRIX,
    glslName = "u_skinningMatrixBlock",
    members = listOf(
        UniformMember("Bones", ZTypes.MAT4F, count = 100),
        UniformMember("InverseBindMatrix", ZTypes.MAT4F, count = 100)
    ),
    generators = mapOf(
        "Bones" to ZBoneMatrixGenerator,
        "InverseBindMatrix" to ZInverseBindMatrixGenerator
    )
)
```

- At **init** (or in a test), the engine (or a test) walks all registered blocks and:
  - Builds the `ZUniform` instances used by the shader program,
  - Registers generators in `ZSceneContext` by member name.
- `ZShaderGenerator.addRequiredUniforms()` and `ZShaderProgramParameters` still turn features (e.g. skinning) on/off, but they refer to the block by id or name (e.g. `SkinningUniforms`) instead of manually listing descriptors and generators in multiple files.

**Pros**: No new build step; everything stays in Kotlin; gradual migration.  
**Cons**: Shader source (GLSL/MSL/WGSL) still has to be kept in sync by hand unless we add a second step (e.g. emit a JSON/table that the shader build uses).

### Option B: Single source + optional codegen for shaders

- Same as Option A, but the **block definition** is the only place that lists member names and types.
- A small **codegen script** (e.g. Kotlin script or Gradle task) reads that definition and:
  - Generates the Kotlin descriptor/registry code, and/or
  - Emits a neutral block layout (e.g. JSON or a small DSL file) that the Android/Metal/WebGPU shader build can use to generate `uniform ... { ... }` (or equivalent) so that layout is not hand-written in three languages.

**Pros**: One source of truth for layout; fewer copy-paste errors in shaders.  
**Cons**: Requires a codegen step and agreement on how each platform consumes the emitted layout.

### Option C: Feature-based uniform bundles

- Group uniforms by **feature** (e.g. “skin”, “pbr”, “phong”) instead of by block.
- A “feature” declares: which block(s) it needs, which flags in `ZShaderProgramParameters` enable it, and which generators fill which members.
- When a feature is enabled (e.g. skinning on a model), the engine adds the right blocks and registers the right generators from that feature’s declaration, so `ZShaderGenerator` and `ZSceneContext` no longer hard-code every block/member.

**Pros**: Adding a new feature (e.g. a new material type) becomes “add one feature bundle” instead of touching six files.  
**Cons**: Slightly more abstraction; need a clear rule for features that share blocks (e.g. scene matrix).

---

## Recommended Path

1. **Short term**: Introduce a **Kotlin-only registry** (Option A) for **new** uniform blocks. New blocks are defined in one place (name, members, generators); existing blocks stay as they are until migrated. This reduces new boilerplate and establishes the “single definition” pattern without a codegen step.
2. **Medium term**: Migrate existing blocks (scene matrix, skinning, PBR, Phong) into the registry and remove duplicate wiring from `ZSceneContext` and (where possible) `ZShaderGenerator`.
3. **Later**: If needed, add optional codegen (Option B) for shader block layout so GLSL/MSL/WGSL stay in sync with the registry, and/or introduce feature bundles (Option C) for clearer feature–uniform mapping.

---

## Open Questions

- **IDs**: Today `UNIFORM_IDS` are manual. Should block and member IDs be assigned by the registry (e.g. sequential or from an enum) to avoid collisions?
- **Platform shaders**: How far can we go without codegen? For example, could a shared “block layout” file be included by all three shader variants so that adding a member is one file change?
- **ZKBuilder / protos**: Should the declarative definition also drive or validate ZKBuilder proto usage (e.g. which uniforms are serialized), or stay limited to the engine runtime?

---

## Non-goals (out of scope for this proposal)

- Changing how uniform **values** are computed (generators stay as they are).
- Replacing the platform-specific shader **generators** (Android/Metal/WebGPU); only simplifying how uniforms are **declared** and **wired**.
- A full material/shader graph system; the proposal stays focused on “add a uniform / block with less friction”.
