# Orthographic camera / lens proposal (`ortho.md`)

Orthographic projection is **not** implemented today: `ZMatrix4` exposes `perspective` only, and `ZCamera` is bound to `ZPerspectiveLens`. This document is for **design discussion** before implementation.

---

## 1. Use cases

- **2D-style** or isometric gameplay with 3D meshes (consistent screen-space scale).
- **CAD / technical** views, floor plans, UI compositing in 3D space.
- **Editor** gizmos and orthographic viewports alongside perspective views.
- **Shadow maps** (directional light ortho frusta) — may share the same math helper even if authored as a different type later.

---

## 2. Math contract

### 2.1 Canonical orthographic matrix

Define an API that maps **user parameters** to the same **column-major 4×4** convention as `ZMatrix4.perspective` (see `ZMatrix4.kt`). Typical parameters:

- `left`, `right`, `bottom`, `top` — clip volume in **view space** (or world space if documented otherwise; **view space** is recommended for consistency with perspective).
- `near`, `far` — depth clip planes (reuse semantics from `ZLensData.near` / `far`).

Suggested signature:

```kotlin
// English names only in real code; illustrative snippet:
fun ortho(result: ZMatrix4, left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float)
```

**Handedness and depth:** Must match existing perspective and depth buffer setup for each backend (OpenGL-style NDC Z range vs Metal / Vulkan / D3D differences if the engine already normalizes). If today’s perspective assumes a specific NDC Z, ortho must use the **same** mapping.

### 2.2 Relating ortho extents to viewport aspect

Unlike perspective (FOV + aspect), ortho often specifies a **world half-extent** or **height** and derives width:

- **Height-primary:** `halfHeight = H`, `halfWidth = H * aspectRatio` with `aspectRatio = width / height` (same as current lens aspect story).
- **Width-primary:** derive height from width / aspect.

Resize behavior should mirror `ZPerspectiveLens`: on viewport resize, update transient dimensions and recompute `left/right/bottom/top` from the chosen policy **or** keep fixed world units and letterbox (product decision — recommend **height-primary + aspect** as default for parity with “vertical FOV” mental model).

---

## 3. Data model options

### Option A — `ZOrthographicLensData` extends `ZLensData`

Parallel to `ZPerspectiveLensData`:

- Serialized: `near`, `far`, plus either **four floats** (`left`, `right`, `bottom`, `top`) **or** two floats (`halfWidth`, `halfHeight`) plus a flag for which mode is active.
- `projectionMatrix` getter calls `ZMatrix4.ortho(...)`.

**Pros:** Fits existing protobuf + `ZSerializableComponent` pattern.  
**Cons:** Many serialized numbers if using explicit L/R/B/T; need sensible defaults.

### Option B — “Size + center” encoding

Store `orthoHeight` (or `size`) and optional `centerX` / `centerY` offsets in view space; derive L/R/B/T each frame.

**Pros:** Fewer author-facing parameters; good for games.  
**Cons:** Less direct for CAD-style asymmetric frusta unless extended.

**Recommendation:** Start with **Option A** using **`halfHeight` + aspect-derived `halfWidth`** (and optional uniform scale), with a path to add explicit L/R/B/T for tooling later.

---

## 4. Component API sketch

Mirror `ZPerspectiveLens`:

- `ZOrthographicLens` / `ZOrthographicLensData`, `@JsExport` where needed.
- Implement `ZResizable`; in `onViewportResize`, set `data.setDimensions` and mark aspect source (same as perspective).
- `Default` companion with conservative `near` / `far` and a reasonable ortho height (e.g. matching a typical 2D unit scale — to be chosen).

Constructors (illustrative):

- `OrthographicLens(near, far, halfHeight)` — width from aspect.
- `OrthographicLens(near, far, halfHeight, aspectRatio)` — fixed aspect for tests or headless.

---

## 5. `ZCamera` integration

Today `ZCamera` stores `ZPerspectiveLens` at `@ProtoNumber(4)`. Possible approaches:

1. **Replace with polymorphic lens** — One protobuf field typed as a sealed / polymorphic `ZLens` graph (requires loader and proto updates; aligns with [`camera-improvement-plan.md`](./camera-improvement-plan.md) Phase B).
2. **Dual optional fields** — `perspectiveLens` vs `orthographicLens` with runtime invariant “exactly one”; simpler migration but uglier schema.
3. **Wrapper component** — Camera holds a generic `ZLens` only in memory; serialization maps to oneof. Best long-term if polymorphic infra already exists elsewhere.

**Open question:** Should `ZObjectType.CAMERA` stay a single type with swappable lens, or split object types? **Recommendation:** single `ZCamera`, swappable lens.

---

## 6. Shader and uniform impact

Orthographic projection is still **`mat4 projectionMatrix`**. Existing `ZProjectionMatrixGenerator` and shader uniforms likely need **no** change if only the matrix values differ.

Verify:

- **Skybox / infinite** shaders that assume perspective divide quirks.
- **Fog** or linear depth formulas that assume perspective **w** in clip space.

Document any shader that assumes `clip.w != 1` for world reconstruction.

---

## 7. Testing checklist

- Golden `ortho` matrix for symmetric frustum (`-1..1` cube style scaled by near/far).
- Resize: aspect changes → horizontal extent updates when using height-primary policy.
- Serialization round-trip for `ZOrthographicLensData`.
- Integration: one sample scene (or test) rendering a cube with ortho active camera on at least one backend.

---

## 8. Summary

Add **`ZMatrix4.ortho`**, **`ZOrthographicLens` (+ data + serializer)**, generalize **`ZCamera`** to accept non-perspective lenses, and keep **aspect / resize** behavior consistent with the perspective lens. Resolve **proto shape** early (polymorphic lens vs dual fields). Confirm **depth / handedness** matches existing perspective across GLES, Metal, and WebGPU.
