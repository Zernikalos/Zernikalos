# Camera subsystem improvement proposal (Zernikalos engine)

This document proposes a staged evolution of the camera and lens stack beyond the current **perspective-only** implementation. It complements [`ortho.md`](./ortho.md), which focuses on orthographic projection.

---

## 1. Current state (baseline)

Relevant code today:

| Area | Location | Notes |
|------|----------|--------|
| Lens base | `engine/.../components/camera/ZLensData.kt` | `near` / `far`, optional `_aspectRatio`, transient `width` / `height`, `aspectRatio` derived from dimensions when `useComputedAspectRatio` is used. Single shared `matrix` buffer; subclasses write into it in `projectionMatrix` getters. |
| Perspective | `engine/.../components/camera/ZPerspectiveLens.kt` | `ZPerspectiveLens` + `ZPerspectiveLensData`; `ZResizable` → `onViewportResize` updates dimensions on `data`. |
| Camera object | `engine/.../objects/ZCamera.kt` | `lens` is **hard-typed** to `ZPerspectiveLens` (protobuf field). `projectionMatrix` delegates to lens. `viewMatrix` is `transform.matrix`. `viewProjectionMatrix` = `projectionMatrix * viewMatrix`. |
| Math | `engine/.../math/ZMatrix4.kt` | `ZMatrix4.perspective(...)` only; **no** `ortho` helper yet. |
| Consumers | e.g. `ZProjectionMatrixGenerator`, `ZModelViewProjectionMatrixGenerator`, `ZViewMatrixGenerator`, shaders (`projectionMatrix` uniforms) | Active camera drives scene matrices. |

Strengths: small surface area, clear default (`ZPerspectiveLens.Default`), resize path exists via `ZResizable` / `internalOnViewportResize`.

Limitations:

1. **Single projection model** — No orthographic (or oblique / custom) lens; `ZCamera` cannot represent a scene-authorable ortho camera without API churn (see `ortho.md`).
2. **Tight coupling** — `ZCamera.lens` is `ZPerspectiveLens`, blocking polymorphic `ZLens`-style APIs and polymorphic serialization without schema changes.
3. **Projection recomputation** — Every read of `projectionMatrix` recomputes into the same backing `matrix` in `ZLensData`; no explicit dirty/caching policy (fine for low frequency, worth defining if uniforms read matrices multiple times per frame).
4. **Aspect policy** — When width/height are unset, `computeAspectRatio()` returns `1f`; behavior is implicit. Document when explicit `aspectRatio` vs viewport-driven ratio is authoritative (especially for headless or offscreen targets).
5. **View matrix semantics** — `viewMatrix` is documented as “view” but sourced from `transform.matrix` (object TRS/world matrix from `ZTransform`). A formal **audit** should confirm whether the shader pipeline expects **world-from-camera** vs **camera-from-world** (inverse), and whether multiply order `projectionMatrix * viewMatrix` matches all backends’ clip conventions. If a bug or convention mismatch exists, fix it as part of “camera hardening,” not only as a lens feature.
6. **Authoring ergonomics** — No first-class helpers for common cases (e.g. vertical vs horizontal FOV, physical sensor metaphor, clip control / reversed-Z strategy if ever adopted).

---

## 2. Goals

1. **Projection abstraction** — Treat “lens” as a **projection provider** (`near`, `far`, `aspectRatio` / viewport hooks, `projectionMatrix`) that `ZCamera` holds without fixing the concrete type to perspective.
2. **Orthographic support** — Add math + data + component parity with perspective; wire through serialization and active camera (details in `ortho.md`).
3. **Predictable matrices** — Document and test **clip space**, **row/column convention**, and **V/P/VP** composition against one golden reference (e.g. a few fixed camera poses and frusta).
4. **Resize and aspect** — Single documented rule: viewport resize updates lens dimensions; optional override for fixed aspect (editor preview, render targets).
5. **Optional performance** — If profiling shows matrix getter pressure, introduce **dirty flags** or frame-tagged caches on lens data without changing public behavior.

Non-goals for the first iteration: post-processing stack, physical cameras (exposure/focus), VR stereo rig, cascaded shadow-specific split matrices (can consume the same lens types later).

---

## 3. Proposed phases

### Phase A — Hardening and documentation (low risk)

- Add **unit tests** for `ZMatrix4.perspective` and (once added) ortho against known vectors / corner frustum points.
- Document **FOV axis** (today: vertical degrees, matching `Matrix.perspective`-style comment in `ZMatrix4`).
- Clarify **`viewMatrix` / `viewProjectionMatrix`** naming and math in KDoc and in this folder; align `ZViewMatrixGenerator` behavior with the chosen convention.
- Optional: **separate** internal projection scratch matrix from a publicly returned copy if aliasing ever surfaces (multiple threads or accidental mutation).

### Phase B — Lens type generalization

- Introduce a **`ZLens`** (or sealed hierarchy) interface / abstract component mirroring the responsibilities of `ZPerspectiveLens`: implements `ZResizable` where applicable, exposes `projectionMatrix`, serializes through existing `ZSerializableComponent` patterns.
- Refactor **`ZCamera`** to hold **`ZLens`** (or a sealed `ZCameraLens` wrapper) instead of `ZPerspectiveLens` only.
- Update **protobuf / polymorphic registration** (`ZkoLoader`, `ZkoObjectProto`, etc.) so a camera can deserialize with either lens kind.
- Keep **JS export** story in mind: stable constructors and `@JsName` for new types.

### Phase C — Orthographic implementation

- Implement `ZMatrix4.ortho` (or `orthographic`) and `ZOrthographicLens` / `ZOrthographicLensData` per `ortho.md`.
- Ensure **uniform generators** and **all shader backends** that consume `projectionMatrix` remain valid (ortho is still a `mat4`; no shader change strictly required unless depth range differs by design).

### Phase D — Ergonomics and extras (prioritize by product need)

- **FOV modes**: vertical (default), horizontal, or diagonal — single source of truth converted to the matrix function.
- **Clip tuning**: optional `infinitePerspective` or reversed depth **only** if the render backend agrees (Metal / GLES / WebGPU depth range); gate behind explicit API.
- **Editor/debug**: frustum visualization helpers (CPU-side corner computation) for both perspective and ortho.

---

## 4. Risks and mitigations

| Risk | Mitigation |
|------|------------|
| Breaking serialized `.zko` / proto camera payloads | Version fields or new oneof; migration path for old files that only had perspective. |
| Subtle VP bugs after refactor | Golden tests + one visual regression scene per backend if available. |
| JS / Kotlin API binary expectations | Keep deprecated overloads or static factories where `ZPerspectiveLens` was assumed. |

---

## 5. Success criteria

- A scene can use **either** perspective **or** orthographic active cameras without forking engine code.
- Documentation states **matrix layout**, **view definition**, and **aspect ratio** rules; tests cover projection helpers.
- No regression in existing samples that rely on `ZPerspectiveLens.Default` and `ZCamera.DefaultPerspectiveCamera`.

---

## 6. Related reading

- [`ortho.md`](./ortho.md) — Orthographic lens design and API options.
