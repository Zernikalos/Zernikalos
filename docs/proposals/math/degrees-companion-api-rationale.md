# Rationale: companion degree APIs alongside radian-first math

## Context

The engine’s **canonical** angle unit for the audited public APIs is **radians** (see [degrees-usage-inventory.md](degrees-usage-inventory.md)). That matches low-level graphics and physics conventions and keeps quaternion/trigonometry code straightforward.

Many application authors still **think and author in degrees**: UI sliders, gameplay tuning (“rotate 2° per frame”), camera FOV (“45°”), and tooling output. Forcing every call site to multiply by `π/180` is repetitive and error-prone. Large engines therefore often expose **explicit degree-shaped APIs** next to radian-oriented ones, rather than inferring units from raw `Float`.

This note argues **where** degree-shaped surfaces help, and records the **planned mixed strategy** for Zernikalos: **explicit `*Degrees` APIs** on high-touch types plus a small **`Angles` conversion utility**, inspired by Godot, Unity, and Unreal.

---

## What other engines do (patterns, not prescriptions)

### Godot

- Core properties such as `rotation` use **radians** in 3D.
- The engine adds **`rotation_degrees`** as a parallel, clearly named surface so editors and gameplay code can work in degrees without ambiguity.

**Takeaway:** duplication is acceptable when **names** encode the unit (`*_degrees` vs base property).

### Unity

- **Mixed by layer:** `Transform.eulerAngles` and `Quaternion.Euler` are oriented toward **degrees** for authoring.
- **`Mathf.Sin` / `Cos` / etc.** use **radians**; **`Mathf.Deg2Rad` / `Rad2Deg`** bridge the two.

**Takeaway:** separate **authoring / inspector** APIs from **math library** APIs; document which is which.

### Unreal

- **`FRotator`** (Pitch, Yaw, Roll) is conventionally expressed in **degrees** at the gameplay/editor boundary.
- Lower-level math still uses radians where trig expects it.

**Takeaway:** strong **types or named structs** at the boundary reduce accidental mixing.

---

## Where companion degree APIs tend to pay off

### 1. Field of view and lens authoring

Designers specify vertical/horizontal FOV in **degrees** everywhere (tools, specs, comparisons with other engines). A radian-only `fov` forces mental conversion at every construction site.

**Benefit:** constructors or setters such as `fovDegrees` / `setVerticalFovDegrees` keep demos and editor bindings readable.

### 2. Transform nudges from input or UI

Small rotations per frame or per drag (“2 units”) are often described in **degrees** during tuning. Axis–angle helpers that accept **degrees** avoid scattering `* PI/180f` in gameplay code.

**Benefit:** clearer intent in samples and shipped apps; radians remain the implementation path internally.

### 3. Euler inspection and debugging

HUDs and logs often print **degrees**. Allowing **setting** Euler from degrees with an explicit API (or a `ZEuler.fromDegrees` factory) matches how humans iterate on pitch/yaw limits.

**Benefit:** fewer bugs when copying values from DCC or reference docs that use degrees.

### 4. Education and onboarding

Tutorials and quickstarts read better with degree-shaped APIs for camera and orbit controls; radian-first APIs add friction for newcomers.

---

## Where to stay radian-only (or push conversion outward)

- **Internal math:** quaternion normalization, slerp angles between quaternions, matrix decomposition—keep **radians** end-to-end inside hot paths.
- **Interchange with radian-native libraries:** if a subsystem assumes radians, avoid double conversion; convert once at the boundary.
- **Serialized “source of truth”:** pick one stored unit per field (today: radians for `fov` and `ZEuler`-backed data). Companion degree APIs should be **thin adapters**, not a second stored convention unless versioning demands it.

---

## Planned approach: mixed API + shared converters

Zernikalos will adopt **both** of the following (not mutually exclusive):

### A. Explicit `*Degrees` entry points (clarity at call sites)

**Adopted policy (not “degrees everywhere”):** concentrate authoring ergonomics on the **object transform boundary** and a **small minimum outside it**; keep `ZQuaternion` / most `Companion` paths **radian-only** at first, using **`Angles`** + **`ZEuler.fromDegrees`** where quaternion math is composed manually.

- **`ZTransform` (primary):** all axis-angle companions—`rotateDegrees`, `setRotationDegrees`, `rotateAroundDegrees` (with the same overload shapes as today’s radian APIs). This is where gameplay, cameras-as-nodes, and demos spend most of their time.
- **`ZTransform` Euler surface:** **`rotationEulerDegrees`** get/set (read/write `ZEuler` components interpreted as degrees, backed by radian `rotationEuler` / `fromEuler`), plus **`yawDegrees`**, **`pitchDegrees`**, **`rollDegrees`** getters (and setters if a clear, non-ambiguous mutating API is defined) so HUD and inspector code avoid scattering `Angles.radiansToDegrees` on `yaw` / `pitch` / `roll`.
- **FOV / projection (minimum outside `ZTransform`):** degree-oriented constructors or setters on **`ZPerspectiveLens`** (and KDoc on **`ZCamera`** lens) delegating to radian `fov` / `ZMatrix4.perspective`. Closes the gap where transforms never see FOV.
- **`ZEuler.fromDegrees(roll, pitch, yaw)`** (or equivalent `Companion` factory): builds a radians-backed `ZEuler` for `rotationEuler = …` and for the implementation of **`rotationEulerDegrees`** setter—no `fromEulerDegrees` on `ZQuaternion` in the first wave.
- **`ZQuaternion` / `ZMatrix4.perspectiveDegrees`:** **not** required for the first delivery if the above suffices; see **Possible future enhancements** below for the full audit list if usage proves otherwise.

**Rule:** implementations convert once (`degrees → radians`) then call the existing radian API; **no** second stored unit unless serialization versioning requires it.

### B. `Angles` utility (`degreesToRadians` / `radiansToDegrees`)

A small shared helper (object `Angles` or equivalent in `zernikalos.math`) provides:

- `degreesToRadians(degrees: Float): Float`
- `radiansToDegrees(radians: Float): Float`

**Use cases:** custom pipelines, one-off formulas, bindings that receive floats without wrapping methods, tests, and any code that prefers a single import over scattering `PI / 180f`. This parallels Unity’s `Deg2Rad` / `Rad2Deg`-style constants folded into named functions.

**Relationship to (A):** `*Degrees` methods should delegate through these helpers (or the same inline factor) so conversion stays consistent and grep-friendly.

---

## Math module (`zernikalos.math`): audit and planned surfaces

This section is an audit of **`engine/src/commonMain/kotlin/zernikalos/math/*.kt`**: every type was scanned for **public (or commonly used) surfaces that carry a scalar angle in radians**.

**Types with no angle semantics** (no degree companions proposed): `ZVector2`, `ZVector4`, `ZBox2D`, `ZColor`, `ZScalar`, `ZAlgebraObject`, `ZAlgebraObjectCollection`, `ZVoidAlgebraObject`—lengths, colors, or opaque handles only.

FOV on **`ZPerspectiveLens`** / **`ZCamera`** lives **outside** this package (`zernikalos.components.camera` / `zernikalos.objects`); it is part of the **adopted minimum** outside `ZTransform` (see **Planned approach** earlier in this document). **`ZMatrix4.perspective`** is in `math`; optional `perspectiveDegrees` is listed under **Possible future enhancements** if the lens layer alone is enough for authoring.

### Adopted near-term surface (`math` + lens)

| Location | Radian API today | What we ship first |
|----------|------------------|-------------------|
| **`ZTransform`** | `setRotation`, `rotate`, `rotateAround` (all axis–angle overloads) | Matching **`*Degrees`** overloads delegating through `Angles` then existing radian implementations. |
| **`ZTransform`** | `rotationEuler` (get/set via `ZEuler` in radians) | **`rotationEulerDegrees`** get/set: degrees on the public surface, conversion to/from radian-backed `ZEuler` / quaternion internally (setter may delegate to **`ZEuler.fromDegrees`** + `fromEuler`). |
| **`ZTransform`** | `yaw`, `pitch`, `roll` (derived scalars, radians) | **`yawDegrees`**, **`pitchDegrees`**, **`rollDegrees`** (getters at minimum; setters only if semantics stay unambiguous vs `rotationEulerDegrees`). |
| **`ZEuler`** | Constructor / properties in radians | **`Companion.fromDegrees(roll, pitch, yaw)`** (name TBD) returning a radians-backed `ZEuler` for direct assignment, tooling, and for implementing **`rotationEulerDegrees`**. |
| **`Angles`** | — | **`degreesToRadians` / `radiansToDegrees`** shared by `ZTransform.*Degrees`, Euler/axis helpers, and app code. |
| **Lens / camera** (outside `math`) | `fov` in radians | **Degree-oriented FOV** on lens (constructors/setters) calling radian storage / `perspective`. |

**Euler without quaternion `fromEulerDegrees`:** authoring from degree triples uses **`ZEuler.fromDegrees`** (and/or **`rotationEulerDegrees`**) + `ZQuaternion.fromEuler` on set—no duplicate **`ZQuaternion.fromEulerDegrees`** required for the first wave.

---

### Possible future enhancements: parity and internal math ergonomics

The following items were identified in the same audit but are **not** committed for the first delivery. They remain here so the analysis is not lost if direct **`ZQuaternion`** / raw **`ZMatrix4`** usage in tooling, animation, or importers grows.

| Type | Radian-bearing API today | Possible degree companion(s) | Notes / trigger to implement |
|------|--------------------------|-------------------------------|------------------------------|
| **ZQuaternion** (instance) | `rotate(angle, axis)` | **`rotateDegrees`** | When code frequently mutates quaternions in isolation from `ZTransform`. |
| **ZQuaternion** (instance) | `fromAngleAxis(angle, x, y, z)` | **`fromAngleAxisDegrees`** | Same. |
| **ZQuaternion** (instance) | `toEuler()` | Optional **`toEulerDegrees()`** returning degree-valued triple or wrapper | Prefer `Angles` on components unless a dedicated type is introduced. |
| **ZQuaternion.Companion** | `rotate(result, q, angle, axis)` | **`rotateDegrees`** | Parity with instance API for functional style. |
| **ZQuaternion.Companion** | `fromAngleAxis(result, …)` (both overloads) | **`fromAngleAxisDegrees`** | Importers / IK that build quaternions without a `ZTransform`. |
| **ZQuaternion.Companion** | `fromEuler` / `fromEuler(roll, pitch, yaw)` | **`fromEulerDegrees`** (same overload shapes) | When degree tuples are fed straight into quaternion construction more often than into `ZEuler`. |
| **ZEuler.Companion** | `fromQuaternion` | Optional **`fromQuaternionToDegrees`** or degree wrapper | Risk: two meanings for `ZEuler` unless naming is unmistakable; **`Angles.radiansToDegrees` per component** is usually enough. |
| **ZMatrix4.Companion** | `perspective(result, fov, …)` | **`perspectiveDegrees`** | When matrices are built without going through lens; optional if lens-only FOV in degrees is always used. |
| **ZMatrix4.Companion** | `rotate(result, q)`, `lookAt`, `fromQuaternion` | *(none for scalar degrees)* | Unless a future axis-angle matrix helper appears. |
| **ZVector3** | `rotate(q)`, `rotateVector(…)` | *(none)* | Angles always carried by quaternion, not a raw scalar on the vector API. |

#### `ZQuaternion.slerp` (unchanged in any phase)

`slerp` / `slerpIp` take **`t` in `[0, 1]`** as blend factor; internal `halfTheta` comes from quaternion geometry in radians. **`t` is not an authoring angle**—no degree-shaped parameter belongs on `slerp`.

#### If phase 2 is opened

Re-evaluate telemetry or PR feedback: add **`ZQuaternion`** `*Degrees` and **`ZMatrix4.perspectiveDegrees`** in the smallest set that removes repeated `Angles` boilerplate, without mirroring the entire `Companion` tree unless necessary.

---

## Risks and mitigations

| Risk | Mitigation |
|------|------------|
| Callers pass degrees into radian APIs by mistake | Explicit `*Degrees` naming; IDE samples; lint or assertions in debug builds for absurd values |
| Two units in serialized data | Single canonical stored unit; degrees only at construction/UI |
| JS export surface doubles | Mirror naming in Kotlin/JS (`rotateDegrees`) |

---

## Conclusion

**Radians remain the canonical contract** for stored fields and core math (see inventory). The **mixed plan** layers:

1. **`Angles.degreesToRadians` / `radiansToDegrees`** for universal, explicit conversion.
2. **Primary authoring boundary:** **`ZTransform`** `*Degrees` APIs for axis–angle work typical of gameplay and demos.
3. **Minimum outside transforms:** **lens / camera FOV in degrees** (and optionally later **`ZMatrix4.perspectiveDegrees`**—see **Possible future enhancements** earlier in this document).
4. **`rotationEulerDegrees`** and **`yawDegrees` / `pitchDegrees` / `rollDegrees`** on **`ZTransform`** for inspector-style read/write without manual conversion.
5. **`ZEuler.fromDegrees`** so Euler-from-tools and the above stay consistent without widening **`ZQuaternion`** in the first wave.

Deeper **`ZQuaternion`** `Companion` **`*Degrees`** mirrors stay **documented as future work**, not forgotten.

This matches how larger engines separate **authoring ergonomics** from **internal radians**, without dropping the single-unit spine inside the engine.

---

## Next steps (implementation checklist)

**Phase 1 (adopted scope)**

- Add **`Angles`** in the `math` package; document in KDoc (English).
- Implement all **`ZTransform`** `*Degrees` axis–angle methods (mirror overloads of `setRotation` / `rotate` / `rotateAround`); add `@JsName` for JS export where needed.
- Add **`ZTransform.rotationEulerDegrees`** and **`yawDegrees` / `pitchDegrees` / `rollDegrees`** (getters; setters per agreed semantics); add `@JsName` where exported.
- Add **`ZEuler.fromDegrees`** (or equivalent `Companion` factory) for degree triples → radians-backed `ZEuler`.
- Add **degree-oriented FOV** on **`ZPerspectiveLens`** (and/or camera docs) delegating to radian `fov` / `ZMatrix4.perspective`.
- Regenerate/update JS SDK and refresh samples (e.g. DemoApps) to prefer `*Degrees`, `ZEuler.fromDegrees`, or `Angles` as appropriate.

**Phase 2 (optional — see section *Possible future enhancements* earlier in this document)**

- Add **`ZQuaternion`** `fromAngleAxisDegrees` / `fromEulerDegrees` / `rotateDegrees` (instance + `Companion`) only if direct quaternion authoring still carries conversion noise.
- Add **`ZMatrix4.perspectiveDegrees`** if raw matrix pipelines need it without duplicating lens logic.
