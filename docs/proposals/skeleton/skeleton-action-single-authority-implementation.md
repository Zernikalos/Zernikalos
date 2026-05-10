# Implementation Plan: Single Authority for Skeleton Pose vs Skeletal Actions

**Status**: Draft  
**Created**: 2026  
**Parent proposal**: [skeleton-action-single-authority.md](./skeleton-action-single-authority.md)  
**Related**: [z-action-player-update-apply-unification.md](./z-action-player-update-apply-unification.md) — `ZActionPlayer.update` / `update(dt)` bundles clock + pose commit; sampling/commit helpers are private

This document expands the parent proposal into **concrete types, call sequences, phased rollout, and verification**. It does not repeat the full problem statement; read the proposal first.

---

## 1. Objectives of this plan

- Land the **skeleton-scoped player** and **explicit sample → commit** pipeline without blocking on animation graphs or proto migrations.
- Preserve **incremental migration**: demos and hosts can move call sites in small steps.
- Keep **Kotlin/JS `@JsExport`** surfaces predictable: thin facades where needed, core APIs on `ZSkeleton` or dedicated runtime types.

---

## 2. Target responsibility map (after refactor)

| Responsibility | Owner (target) | Must not |
|----------------|----------------|----------|
| Clip data, duration, tracks | `ZSkeletalAction` (or renamed `SkeletalClip`) | Hold `ZModel`, advance wall time, write `poseMatrix` |
| Clock: `time`, speed, loop, play/pause, seek | `ZActionPlayer` (or `AnimationClock` + thin player) | Call `computePoseFromKeyFrame` inside APIs named like pure getters |
| `(clip, time) → neutral sample` | `ZSkeletalAction.sample(time)` or free function | Mutate `ZBone` |
| Merge sample + rest + hierarchy → `poseMatrix` | `SkeletonPoseApplier` / `ZSkeletonRuntime.commit(...)` | Advance playback time |
| GPU joint palette / skin prep | Existing generators + documented bind contract | Implicitly assume `ZModel.action` is source of truth |
| Aggregate for render | `ZModel` | Own animation clock or require `setAction(model, …)` as the only path |

---

## 3. Recommended package and type layout

All names are **suggested**; align with existing Zernikalos naming on implementation.

```
zernikalos.action/
  ZSkeletalAction.kt          // clip + evaluation only (see §4)
  ZKeyFrame.kt                // remains evaluator output DTO unless renamed
  ZActionPlayer.kt            // clock + skeleton ref; no pose side effects in getters

zernikalos.components.skeleton/   // or zernikalos.skeleton.runtime/
  ZBone.kt                    // asset node; poseMatrix fields stay
  SkeletonPoseApplier.kt      // new: apply ZKeyFrame/PoseSample → bones
```

Optional split if the team prefers smaller files:

- `SkeletalClipEvaluator.kt` — pure `fun evaluate(clip, time): ZKeyFrame` (wrapper over current `getKeyFrame`).
- `ZSkeletonPlayback.kt` — holds `ZSkeleton` + `ZActionPlayer` as a single façade for demos (not required for core design).

---

## 4. `PoseSample` vs `ZKeyFrame`

**Minimal-change path**: Treat **`ZKeyFrame` as the neutral sample type** (map bone id → partial local override). Add:

- `fun ZSkeletalAction.sampleAt(time: Float): ZKeyFrame` as an alias or replacement for `getKeyFrame`, documented as **pure** (no I/O, no bone mutation).
- Deprecate or narrow `getKeyFrame` if its name implies “full pose”; prefer `sampleAt` for new code.

**Richer path** (if you want a stronger type boundary later):

- Introduce `PoseSample` as a thin wrapper or data class mirroring `ZKeyFrame` with a stable binary layout for future blending.
- Map `ZKeyFrame` → `PoseSample` at the evaluator boundary only once blending exists.

**Rule**: Any function that returns a sample and is used in tests **must not** touch `ZBone.poseMatrix`.

---

## 5. `ZActionPlayer` API shape (concrete)

### 5.1 State the player owns

- `currentAction: ZSkeletalAction?`
- `currentTime: Float`
- `playbackSpeed`, `isLooping`, `isPlaying`
- `lastUpdateTimeMs` (or injectable clock for testability — see §10)
- **`skeleton: ZSkeleton?`** (replace `obj: ZModel?`)

### 5.2 Public methods (target)

| Method | Behavior |
|--------|----------|
| `setAction(skeleton: ZSkeleton, action: ZSkeletalAction)` | Assign skeleton + clip; reset time; **do not** set `ZModel.action`. |
| `update()` / `update(deltaTimeSeconds)` | Advance `currentTime`, then commit sampled pose to `skeleton` (single host call per frame). |
| `stepPlaybackClock`, `sampleCurrent`, `applyCurrentPose` | **Private** implementation detail (conceptual sample → commit split remains inside the player). |
| `seek(time)`, `play`, `pause`, `stop` | Unchanged semantics (each applies pose where relevant). |

### 5.3 Deprecation of model-centric API

- Keep `setAction(model: ZModel, action: ZSkeletalAction)` as **`@Deprecated`** forwarding to `model.skeleton ?: error(...)` + `setAction(skeleton, action)` for one release cycle, with KDoc pointing to skeleton API.
- `getCurrentKeyFrame()`: split into `sampleCurrent()` + `applyCurrentPose()`, deprecate the combined behavior with **ReplaceWith** or migration KDoc.

---

## 6. Extract pose application: `SkeletonPoseApplier`

Move the body of `ZBone.computePoseFromKeyFrame` into a dedicated type or top-level functions:

```kotlin
// Pseudocode — actual signatures should match ZBone tree APIs
object SkeletonPoseApplier {
    fun apply(root: ZBone, keyFrame: ZKeyFrame, parentWorld: ZMatrix4)
}
```

- `ZBone.computePoseFromKeyFrame` can become a **one-line** delegate to `SkeletonPoseApplier.apply(this, …)` for backward compatibility, or be deprecated in favor of `SkeletonPoseApplier.apply(root, …)` only.
- **Single commit path** for engine code: render loop and tests call the applier, not scattered `computePoseFromKeyFrame` on getters.

---

## 7. Per-frame update sequence (host / engine integration)

Preferred explicit order:

1. **Input / game logic** — optional `seek` on events.
2. **`player.update()`** (wall clock) **or** **`player.update(deltaTimeSeconds)`** (fixed step) — advances clock **and** writes `poseMatrix` via internal sample/commit.
3. **Skinning / uniform generators** — read `poseMatrix` + bind data per documented contract (§8).
4. **Render** — unchanged.

---

## 8. Skinning contract (implementation checklist)

Add a short **KDoc block** on one canonical type (e.g. `ZSkinning` or `ZModelSkinningMatrixGenerator`) stating:

1. **Authoritative joint order** for the mesh: `ZSkinning` joint list vs traversal order of `ZSkeleton.bones`.
2. **Which inverse bind matrix** feeds the final \(jointMatrix\) passed to the shader: per-bone `inverseBindMatrix`, `ZSkinning.inverseBindMatrices`, or product with node hierarchy.
3. **Composition order** (column-vector convention): e.g. `skinMatrix = globalJointPose * inverseBind` (verify against current generator).

Then:

- Audit `ZModelSkinningMatrixGenerator` (or equivalent) to match the doc **exactly**.
- If loaders populate both `ZBone.inverseBindMatrix` and `ZSkinning` lists, document whether one is derived from the other or both must stay in sync.

---

## 9. Phased rollout

### Phase A — Introduce APIs without breaking behavior

1. Add `ZSkeletalAction.sampleAt(time)` (implementation = current `getKeyFrame` logic).
2. Add `SkeletonPoseApplier` + wire `ZBone.computePoseFromKeyFrame` to delegate.
3. Add `ZActionPlayer.setAction(skeleton, action)` overload; implementation sets `skeleton` ref; keep model overload calling through.

### Phase B — Separate clock from pose commit

1. Refactor clock advance (today `stepPlaybackClock` / formerly delta-time step) to **not** call `getCurrentKeyFrame()`.
2. Add `sampleCurrent()` and `applyCurrentPose()`; implement old `getCurrentKeyFrame` as deprecated wrapper that samples + applies (temporary).

### Phase C — Remove authority from `ZModel`

1. Stop assigning `obj.action = action` in `setAction`; gate behind deprecation or remove after one cycle.
2. Update **engine demos**, **DemoApps**, and **Samples** to construct player with `model.skeleton!!` once at setup.
3. Mark `ZModel.action` as legacy in KDoc if the field remains for serialization/editor.

### Phase D — Cleanup

1. Remove deprecated overloads if semver allows.
2. Add focused unit tests (§10).

---

## 10. Testing strategy

| Test | Goal |
|------|------|
| `sampleAt` on a tiny clip with 2–3 bones | Deterministic map contents at `t = 0`, mid, end; **no** `ZBone` in fixture or assert bones untouched. |
| `SkeletonPoseApplier` with fixed `ZKeyFrame` | Known `poseMatrix` on a trivial hierarchy (hand-calculated or golden). |
| `ZActionPlayer` with injected dt | `update(deltaTimeSeconds)` advances time and commits pose in one call. |
| Integration smoke | One skinned model frame: same pixel or matrix hash as pre-refactor baseline (optional snapshot). |

**Fake clock**: Use **`update(deltaTimeSeconds)`** for deterministic tests; wall-clock **`update()`** for production frame loops.

---

## 11. Call-site migration (reference)

**Before** (conceptual):

```kotlin
player.setAction(model, action)
player.update()
// pose updated as side effect of getCurrentKeyFrame inside update
```

**After**:

```kotlin
val skeleton = model.skeleton ?: error("Skinned model requires skeleton")
player.setAction(skeleton, action)
player.update()
```

Optional convenience (explicitly “sugar”):

```kotlin
fun ZModel.skeletalPlayer(): ZActionPlayer =
    ZActionPlayer().also { /* bind skeleton once */ }
```

---

## 12. JS export and host facades

- Export **`setAction(skeleton, action)`** and **`update` / `update(deltaTimeSeconds)`** on `@JsExport` types (pose commit is internal to `update`).
- If TypeScript hosts still want “model + clip”, provide a **non-core** helper:

  `fun playSkeletalOnModel(model: ZModel, player: ZActionPlayer, action: ZSkeletalAction)`  
  that resolves `model.skeleton` and forwards — implemented in a `facade` file so `ZActionPlayer` stays skeleton-centric.

---

## 13. Risks and mitigations

| Risk | Mitigation |
|------|------------|
| Callers forget pose after clock | Addressed by **`update`** performing commit; document one call per frame. |
| `ZModel.action` used for UI | Sync from player in editor layer only, or read `player.getCurrentAction()` once exposed. |
| Semver / binary compatibility | Deprecation cycle on JVM/JS; changelog entry. |

---

## 14. Definition of done (extends proposal success criteria)

- [ ] `ZActionPlayer` holds `ZSkeleton?`, not `ZModel?`, in the primary constructor/state.
- [ ] No getter named `get…` performs full skeleton pose writes without `apply` in the name.
- [ ] `ZSkeletalAction` exposes a **documented pure** sample API used by tests without `ZRenderingContext`.
- [ ] Skinning path has **one KDoc contract** aligned with generator code.
- [ ] All in-repo skeletal demos updated to a **single `update()`** (or `update(dt)`) per frame.

---

## 15. Open implementation decisions (resolve during Phase A)

- **Stable bone index arrays**: If GPU path requires contiguous indices, add `ZSkeleton.boneIndexById` built at init time vs rebuilding each frame.
- **Threading**: Confirm animation updates run on the same thread as render (likely yes on mobile); document if not.
- **Partial channel overrides**: Encode current replace-per-channel behavior in `SkeletonPoseApplier` KDoc for future blending work.
