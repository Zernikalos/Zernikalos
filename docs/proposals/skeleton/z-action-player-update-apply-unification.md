# Analysis: Unifying `ZActionPlayer` clock update and pose commit

**Status**: Implemented in engine (single public `update` entry points; internals private)  
**Created**: 2026  
**Related**: [skeleton-action-single-authority-implementation.md](./skeleton-action-single-authority-implementation.md) (§5.2, §7, §13) · `engine/src/commonMain/kotlin/zernikalos/action/ZActionPlayer.kt` · `engine/src/commonTest/kotlin/zernikalos/action/ZActionPlayerTest.kt`

---

## 1. Motivation (historical)

Previously `ZActionPlayer` split **clock advance** from **pose commit** (two public calls per frame). Hosts had to call both every frame, which was easy to forget and mismatched the intuition that “update the player” leaves the skeleton ready for skinning.

The sections below keep the original trade-space analysis. **Current engine behavior** is summarized in §2.

---

## 2. Current behavior (implemented)

| API | Mutates `currentTime` | Mutates skeleton pose |
|-----|----------------------|------------------------|
| `setAction`, `play`, `stop`, `seek` | Yes (where relevant) | Yes — each ends with an internal pose commit |
| `update()` (wall clock) | Yes (when playing) | Yes — after clock step |
| `update(deltaTimeSeconds)` | Yes (when playing) | Yes — after clock step |
| `stepPlaybackClock`, `sampleCurrent`, `applyCurrentPose` | (internal) | (internal) — **private** |

**Invariant**: After `update()` / `update(dt)`, the skeleton matches the player clock for that call (same frame), assuming a clip and skeleton are bound.

---

## 3. Goals of the refactor (original)

1. **Reduce forgotten `applyCurrentPose()`** at call sites.
2. **Preserve** clock-only updates for tests / future graphs — superseded for public API by private internals; deterministic hosts use **`update(deltaTimeSeconds)`**.
3. **`@JsExport`**: public surface is `update` overloads; private helpers are not exported.
4. **Naming**: shipped as **`update` / `update(dt)`** (no separate `tick` name).

---

## 4. Options (ranked by invasiveness; pre-ship analysis)

### Option A — Additive convenience API (recommended first step)

Introduce one or both:

- `fun tick()` — `update(); applyCurrentPose()` (wall-clock delta inside `update`).
- `fun tick(deltaTimeSeconds: Float)` — clock step + `applyCurrentPose()` (for fixed-step or injected dt).

**Pros**: Zero breaking changes; demos switch one call; tests could keep clock-only + deferred apply if those APIs stayed public.  
**Cons**: Two “right” ways to drive the player unless documentation clearly recommends `tick` for frame loops.

**Implementation notes**:

- Name `tick` vs `updateAndApply` vs `advance` is a style choice; the implementation plan already suggested `updateAndApplyPose(dt)` as sugar — same idea, possibly overloads for `update()`-style wall clock vs explicit `dt`.
- KDoc should state: **primitive** for separating concerns = internal clock step + apply; **default host integration** = `tick` / `updateAndApply`.

### Option B — Merge apply into `update()` (breaking) — **implemented**

Make `update()` always call `applyCurrentPose()` at the end.

**Pros**: One call in demos; matches intuition.  
**Cons** (accepted for public API):

- **Breaks** any code that relied on multiple clock-only steps before a single apply (uncommon but possible).
- **Blurs** the documented split in the single-authority plan (clock vs commit as distinct phases).
- **Testing**: tests that assert “time advanced, bones unchanged” would need a clock-only entry point; after unification, that is **private** (`stepPlaybackClock`) unless exposed for tests.

If this option is ever chosen, prefer a **major version** or a **new method name** for “clock only” rather than overloading `update` without deprecation.

### Option C — Rename for clarity (optional, can combine with A)

Examples:

- Rename mental model: document `update` as **`advanceClock`** (actual rename is breaking; often done only in a major).
- Keep `update` but add **`advanceClock`** as alias → deprecate `update` later (high churn for JS hosts).

Usually **Option A + better KDoc** avoids rename churn.

### Option D — Engine-owned loop (larger scope)

The host never calls the player; a `ZAnimationSystem` or the frame graph runs all registered players with a fixed order: advance → apply → skin prep. **Out of scope** for this note except as a future architectural direction; it does not remove the two phases internally, it only **hides** them behind a scheduler.

---

## 5. Recommended rollout

### Phase 1 — API

1. Add `tick()` (and optionally `tick(dt: Float)`) on `ZActionPlayer` with KDoc referencing the two-phase design.
2. Optionally add `updateAndApply()` as an alias if the team prefers English-long names for discoverability in IDE autocomplete.

### Phase 2 — Call sites (in-repo)

Update in order of visibility:

1. **Web examples** (`DemoApps/web/examples/*.html`) — replace `update()` + `applyCurrentPose()` with `tick()` (or `tick` + nothing else).
2. **Android samples** (`DemoApps`, `Samples`) — same.
3. **Engine tests** — either keep explicit two-step tests to lock the invariant, or add one integration test that `tick()` equals the sequence.

### Phase 3 — Documentation

1. Top-level KDoc on `ZActionPlayer` class: recommend **`tick` for per-frame host code**; list `update` + `applyCurrentPose` for advanced use.
2. Cross-link this doc and §7 of the implementation plan so “optional convenience” is not duplicated inconsistently.

### Phase 4 — Optional hardening (later)

- Debug-only assert: if `update` was called without a subsequent `apply` before render (heuristic: last apply generation vs clock generation) — **fragile**, only consider if real bugs justify it.

---

## 6. JS export (`@JsExport`) considerations

- Public **`update()`** and **`update(deltaTimeSeconds)`** are exported; **`applyCurrentPose`** and other helpers are **private** and not on the JS surface.
- Web demos use `zernikalos.action.ZActionPlayer`; verify overloads in generated `.d.ts` / `zernikalos.js` after build.

---

## 7. Risks and mitigations

| Risk | Mitigation |
|------|------------|
| Two canonical patterns confuse new users | Single snippet: call **`update()`** once per frame. |
| Future blending applies multiple samples before one commit | Engine-internal graph code can still separate clock vs commit using **private** helpers inside `ZActionPlayer` or a future evaluator type. |
| Redundant second apply in one frame | Unlikely if hosts only call `update` once; second call same frame reapplies same pose (cost only). |

---

## 8. Definition of done (this refactor track)

- [x] `update()` / `update(deltaTimeSeconds)` perform clock advance + internal pose commit.
- [x] Class KDoc describes single-call per-frame integration.
- [x] In-repo skeletal demos use a single `update` call per frame.
- [x] Unit test covers **`update(dt)`** commits pose; old two-phase public API removed.

---

## 9. Relation to single-authority work

The **single-authority** plan still argues for a clear **conceptual** split between sampling and commit. In the shipped design, that split exists **inside** `ZActionPlayer` (`sampleCurrent` → `applyCurrentPose`, both private) while **hosts** see one **`update`** step per frame.

If `SkeletonPoseApplier` or `ZSkeleton.commit` replaces the body of `applyCurrentPose` later, `update()` should keep delegating to that single commit path.
