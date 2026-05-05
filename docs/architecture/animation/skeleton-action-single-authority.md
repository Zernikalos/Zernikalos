# Proposal: Single Authority for Skeleton Pose vs Skeletal Actions

**Status**: Implemented  
**Created**: 2026  
**Related**: [Implementation plan (detailed)](./skeleton-action-single-authority-implementation.md) · `engine/src/commonMain/kotlin/zernikalos/action/`, `engine/src/commonMain/kotlin/zernikalos/components/skeleton/`, `engine/src/commonMain/kotlin/zernikalos/objects/ZModel.kt`, `engine/src/commonMain/kotlin/zernikalos/objects/ZSkeleton.kt`

---

## Summary

The skeletal animation stack today spreads responsibility across `ZSkeletalAction`, `ZActionPlayer`, `ZBone`, `ZModel`, and skinning data. Playback time, clip sampling, rest-pose merging, and final `poseMatrix` writes are not owned by one clear subsystem. **`ZActionPlayer` is currently wired to `ZModel`**, but skeletal playback only needs a **skeleton** (`ZSkeleton`): a model aggregates mesh, materials, and optional skinning—those are not the animation clock’s concern. This proposal records that problem and outlines a reimplementation where **each concern has a single source of truth**, with explicit boundaries between *asset data*, *runtime pose*, and *playback clock*, and where **the player’s binding target is the skeleton**, not the model.

---

## Problem

### Animation player attached to `ZModel` instead of `ZSkeleton`

- `ZActionPlayer.setAction(obj: ZModel, action: ZSkeletalAction)` couples playback to the **model** aggregate. Clips drive **bone pose** under `ZSkeleton` (`root` / `ZBone` hierarchy); mesh, shader, and `ZSkinning` are separate concerns.
- The player then reaches the skeleton indirectly via `obj.skeleton` (e.g. in `getCurrentKeyFrame`). That forces every caller to have a `ZModel` even when only posing a skeleton, and blurs ownership if multiple render objects ever share one skeleton or if a skeleton is manipulated without a model.
- **Target rule**: the playback/controller type should hold a **`ZSkeleton` (or a dedicated skeleton runtime)** reference; `ZModel` may *use* that skeleton for skinning, but must not be the primary handle for the animation clock.

### Mixed ownership of “what is playing”

- `ZActionPlayer` keeps `currentAction` and advances `currentTime`, but `setAction` also assigns `obj.action = action` on the `ZModel` (`ZActionPlayer.kt`). The model therefore mirrors clip state that the player already holds, which can drift if callers mutate `model.action` without going through the player—another symptom of anchoring the player to the wrong aggregate.

### Mixed ownership of “current pose”

- `ZSkeletalAction.getKeyFrame(time)` evaluates tracks and builds a `ZKeyFrame` (sampled pose in a map keyed by bone id). That is the **authority for clip-relative sampled transforms** at a time `t`.
- `ZBone.computePoseFromKeyFrame` merges **rest/bind transform** from each bone’s `ZTransform` with optional overrides from the keyframe, then writes **`poseMatrix`** down the hierarchy (`ZBone.kt`). So the **authority for the final animated local pose** is split: part from skeleton asset (`transform`), part from action (`ZKeyFrame`).
- `ZActionPlayer.getCurrentKeyFrame()` both returns the keyframe **and** calls `obj?.skeleton?.root?.computePoseFromKeyFrame(...)`. A getter-shaped API performs a full skeleton side effect, which blurs “query sampled clip” vs “commit pose to skeleton”. If the player owned `ZSkeleton` directly, this path would at least skip the irrelevant `ZModel` hop.

### Skin / bind data in two places

- Per-bone `inverseBindMatrix` is computed and stored on `ZBone` during initialization.
- `ZSkinning` carries another ordered list of bone ids and inverse bind matrices for mesh influence (`ZSkinning.kt`), aligned with glTF-style skin data.

Loaders and render paths must agree on which matrices are authoritative for GPU skinning vs hierarchy. Today that relationship is implicit rather than named in a single module.

### Ephemeral vs persistent pose buffers

- Each `getKeyFrame` builds a new `ZKeyFrame` instance. That is fine for sampling, but there is no dedicated **runtime pose object** (e.g. “current local transforms” or “palette for shaders”) separate from both the serializable clip and the serializable bone tree.

---

## Goals

1. **Skeleton-scoped player**: Playback state (time, speed, loop, active clip) is owned by a type that references **`ZSkeleton`** (or a skeleton-pose runtime), not `ZModel`. Optional helpers may accept a model only to find `model.skeleton`, but the canonical API must not require `ZModel` as the animation subject.

2. **One clock owner**: That same component owns playback time, speed, loop, and seek—without duplicating “current clip” on `ZModel` unless that duplication is a deliberate, documented view for serialization only.

3. **One evaluator boundary**: Given `(clip, time) → pose sample`, the engine has a single function or type responsible for producing a **pose sample** (local TRS per bone or equivalent), without writing `poseMatrix` on bones unless that is explicitly requested.

4. **One commit path**: Applying a pose sample to the skeleton (filling `poseMatrix`, propagating hierarchy, preparing shader bone matrices) lives in **one place** (e.g. skeleton runtime, pose writer, or skinning prep)—not inside a method named like a pure query.

5. **Clear asset vs runtime split**:

   - **Asset**: skeleton hierarchy, rest transforms, skin tables, clips/tracks.
   - **Runtime**: current time(s), blended clips (future), resolved pose, GPU-ready palette.

6. **Documented skinning authority**: Specify whether `ZSkinning.inverseBindMatrices` or per-bone `inverseBindMatrix` drives final skinning math, or how they compose, so loaders do not encode the same fact twice without a contract.

---

## Non-goals (for this proposal iteration)

- Full animation graph / blending / state machines (only note extension points).
- Changing serialization wire format of existing protos unless a follow-up migration proposal is opened.
- Rewriting all loaders in one step; migration can be incremental behind new APIs.

---

## Comparison with major engines (Unity, Godot 4, Unreal)

This section situates the proposal against common commercial engine architecture. It is **informal** (APIs and names evolve); use it as mental anchoring, not as vendor documentation.

**In one line:** mature stacks separate **clip data**, **playback / graph state**, **evaluated pose** (often queryable or pipeline-stage), and **commit** to bone transforms / GPU matrices, with **skeleton (or skeletal component)** as the pose subject—not the visual mesh aggregate alone.

### Unity

- **Similarities:** Clips are data; **Animator** owns layer state, parameters, and current state; pose ends up on **Transform** in the bone hierarchy. **SkinnedMeshRenderer** ties mesh, weights, and bone references.
- **Differences:** The usual “subject” is a **GameObject** hierarchy, not a single named skeleton type: Animator often lives on the character root while bones are child transforms. That can resemble coupling to an aggregate rather than to a minimal pose target, even though Animator—not the mesh renderer—is the animation authority in modern setups.
- **Sample vs commit:** Default workflows evaluate and write transforms together. **Playables**, **AnimationStream**, and rigging packages move toward explicit evaluate / apply phases, closer to this proposal’s boundaries—but that is not the default beginner path.
- **Skinning:** Bind data and conventions are documented, but mental model remains “renderer + bones” rather than always a single named **runtime pose** module.

### Godot 4

- **Similarities:** **`Skeleton3D`** is a first-class node: bone hierarchy and pose live there. **`AnimationPlayer`** (or animation tree) drives clips via **property paths**; playback state lives on the player, not on `MeshInstance3D`.
- **Alignment:** Strong match to **skeleton-scoped animation**: you animate the skeleton; the mesh consumes the same hierarchy. No need for a “model aggregate” as the clock handle.
- **Sample vs commit:** The main loop still applies tracks by writing properties; there is not always a public “pure sample” API, but the **conceptual** split between skeleton pose and mesh is clear.
- **Skinning:** Weights on the mesh; pose authority on **`Skeleton3D`**, consistent with binding the player to skeleton rather than to the render aggregate.

### Unreal Engine

- **Similarities:** **`USkeletalMeshComponent`** bundles skeletal mesh and instance skeleton; **`UAnimInstance`** (graph, montages, layers) is the **runtime animation authority** for that component—time, blending, and node output feed bone transforms, then skinning uses ref skeleton and mesh data.
- **Alignment:** Internally, the engine keeps a relatively explicit line between **evaluating the anim graph** and **refreshing / committing** bone transforms. The clock and blend state live on **AnimInstance**, not on the mesh asset as the owner of playback.
- **Model vs skeleton:** Everything still hangs off one **skeletal component** (mesh + anim together), but animation authority is not “the static mesh”; it matches the idea that **`ZModel` does not own the player** while sharing the same skeleton for rendering.
- **Skinning:** Reference skeleton, bone map, and LOD data form a **tight contract**—closer to this proposal’s goal of a single documented bind / IBM story than ad hoc duplication.

### Summary table

| Proposal theme | Unity | Godot 4 | Unreal |
|----------------|-------|---------|--------|
| Player bound to **skeleton**, not mesh aggregate | Partial: hierarchy of transforms; often “character root” mental model | **Strong** (`Skeleton3D` + `AnimationPlayer`) | **Strong in practice** (`AnimInstance` + skeletal component) |
| Single **clock** owner (no silent mirror elsewhere) | Animator centralizes; legacy paths once duplicated concepts | `AnimationPlayer` / mixer | **Very explicit** (`AnimInstance`) |
| **Sample** without side effects + explicit **commit** | Mixed by default; Playables / rigs approach this | Tracks write properties; less “pure sample” API | Pipeline stages are explicit internally |
| **Runtime pose** vs clip / asset | Less explicit at high-level API | Medium: pose on skeleton, clips separate | Strong separation inside the engine |
| **Skinning / bind** contract | Conventions exist; sometimes split across types | Reasonably clear | Highly formalized |

**Takeaway:** The direction in this proposal (clock on skeleton scope, evaluator output vs commit, documented skinning authority) is **closest in spirit to Godot 4 and Unreal**; **Unity** converges toward similar separation in advanced APIs, while the default Transform-driven workflow is less explicitly layered.

---

## Current architecture (reference)

| Concern | Where it lives today |
|--------|----------------------|
| Clip samples (tracks, duration) | `ZSkeletalAction` |
| Playback state | `ZActionPlayer` (bound to `ZModel` today; should be `ZSkeleton`) + `ZModel.action` mirror |
| Sample at time → keyframe map | `ZSkeletalAction.getKeyFrame` |
| Rest pose + merge + `poseMatrix` | `ZBone.computePoseFromKeyFrame` |
| Skeleton object | `ZSkeleton` / `ZModel.skeleton` |
| Skin joint list + IBM list | `ZSkinning` on `ZModel` |

---

## Proposed direction

The following is a **target shape** for a reimplementation; exact naming can follow existing Zernikalos conventions.

### 1. `SkeletalClip` (or keep `ZSkeletalAction` as the data type)

Owns only **serialized / loaded clip data** and **evaluation** to a neutral output:

- `fun sample(time: Float): PoseSample` where `PoseSample` is a small runtime struct (bone id → partial local TRS deltas or absolute locals—pick one convention and document it).

No knowledge of `ZBone` instances or GPU.

### 2. `AnimationClock` or embedded in `ZActionPlayer` (refined)

Owns `time`, `speed`, `loop`, `isPlaying`, `update(delta)`. Holds a stable reference to **`ZSkeleton`** (the bone hierarchy that receives posed matrices), not to `ZModel`. Emits **time** or **clip + time** to evaluators. Does **not** write bone matrices until an explicit apply/commit step.

### 3. `SkeletonPose` / `ZSkeletonRuntime`

Owns:

- Optional cache of **current local pose** per bone after merges.
- **Commit** from `PoseSample` + skeleton rest/bind rules (today’s merge logic moves here from `ZBone` or wraps it).
- Propagation to `poseMatrix` and any flat bone array needed by uniforms.

`ZBone` remains the scene/asset node; the runtime object is the place that applies animation results.

### 4. `ZModel` vs skeleton at the API edge

- **`ZModel`** continues to reference `skeleton` (and `skinning`) for rendering a skinned mesh, but **does not own** the animation player. Host code (or a thin façade) composes: `val player = ZActionPlayer(skeleton)` / `player.setAction(clip)` and the model simply **shares** the same `ZSkeleton` instance the player drives.
- Prefer removing `ZModel.action` from the runtime path; if retained for serialization or editor round-trip, treat it as non-authoritative metadata unless explicitly synced.
- Render/update calls (pseudocode): `skeletalPlayer.update(dt)` then `skeletonPose.commit(skeletalPlayer.currentSample())`—two explicit steps instead of hidden inside `getCurrentKeyFrame`, with no `ZModel` in the player signature.
- Optional convenience: `fun ZModel.createSkeletalPlayer(): ZActionPlayer` that delegates to `skeleton ?: error(...)` for demos only—**not** the core type design.

### 5. Skinning contract

Add a short **contract** section in code or docs: for a bound `ZModel`, which inverse bind matrices are consumed by `ZModelSkinningMatrixGenerator` and how they relate to `ZBone.inverseBindMatrix`. Eliminate silent duplication or document the composition order (mesh node vs joint space).

---

## Migration sketch

1. Change `ZActionPlayer` (or successor) to take **`ZSkeleton`** in `setAction` / constructor; apply pose via `skeleton.root`, not via `ZModel`.
2. Introduce `PoseSample` (or reuse `ZKeyFrame` strictly as the evaluator output type) and move **no side effects** out of “sample” APIs.
3. Extract `computePoseFromKeyFrame` logic into a `SkeletonPoseApplier` (or rename method on a non–getter path, e.g. `applyKeyFrameToSkeleton`).
4. Stop writing `model.action` from `setAction` unless required for backward compatibility; deprecate with KDoc.
5. Update call sites (e.g. demos) to obtain the player from the skeleton (or from `model.skeleton!!` once at setup), not `setAction(model, …)`.
6. Align skinning generators with the documented bind authority.

---

## Open questions

- Should partial overrides in clips be **additive** vs **replace** rest pose (current behavior is replace-per-channel when present)? Formalize for future blending.
- Do we want **bone index** stable arrays for GPU in addition to id-keyed maps for evaluation?
- JS export: keep flat APIs for hosts that only need “play this clip on this model” via a façade that resolves `model.skeleton` and forwards to the skeleton-scoped player—without making `ZModel` the core dependency of `ZActionPlayer`.

---

## Success criteria

- No public API that both **returns** sampled animation data and **mutates** full skeleton pose unless named accordingly (`apply…`, `updatePose…`).
- Callers can unit-test clip sampling without constructing a full `ZRenderingContext`.
- One documented path from clip time → GPU skinning matrices.
- Public skeletal playback APIs do not require `ZModel`; they require **`ZSkeleton`** (or a façade that is clearly labeled as sugar).
