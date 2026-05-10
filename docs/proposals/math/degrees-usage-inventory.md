# Angle units inventory (engine)

This document tracks **which public engine surfaces treat numeric angles as radians**, and keeps a **short historical record** of the migration away from implicit degrees.

Scope: `engine/src/**` unless noted.

---

## Current API contract (authoritative): radians

All symbols below take or expose **angles in radians**. This is the contract enforced by implementation and KDoc.

| Area | API surface | Unit |
|------|-------------|------|
| Perspective | [`ZMatrix4.perspective`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZMatrix4.kt) — `fov` | **Radians** (vertical full angle) |
| Camera / lens | [`ZPerspectiveLens`](../../../engine/src/commonMain/kotlin/zernikalos/components/camera/ZPerspectiveLens.kt) / [`ZPerspectiveLensData.fov`](../../../engine/src/commonMain/kotlin/zernikalos/components/camera/ZPerspectiveLens.kt) | **Radians** |
| Camera KDoc | [`ZCamera`](../../../engine/src/commonMain/kotlin/zernikalos/objects/ZCamera.kt) — lens `fov` described via lens | **Radians** |
| Default lens FOV | [`ZPerspectiveLens.Default`](../../../engine/src/commonMain/kotlin/zernikalos/components/camera/ZPerspectiveLens.kt) | **π/4** (~45° previously) |
| Euler storage | [`ZEuler`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZEuler.kt) — `roll`, `pitch`, `yaw` | **Radians** |
| Quaternion from Euler | [`ZQuaternion.fromEuler`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZQuaternion.kt) | **Radians** (via `ZEuler`) |
| Quaternion axis–angle | [`ZQuaternion.fromAngleAxis`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZQuaternion.kt), [`rotate`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZQuaternion.kt) | **Radians** |
| Euler from quaternion | [`ZEuler.fromQuaternion`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZEuler.kt) | **Radians** (output) |
| Instance quaternion | [`ZQuaternion.toEuler`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZQuaternion.kt) | **Radians** |
| Transform | [`ZTransform.rotationEuler`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZTransform.kt), `yaw` / `pitch` / `roll` | **Radians** |
| Transform axis–angle | [`ZTransform.setRotation`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZTransform.kt), [`rotate`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZTransform.kt), [`rotateAround`](../../../engine/src/commonMain/kotlin/zernikalos/math/ZTransform.kt) | **Radians** |

Indirect callers (anything passing an angle into the rows above) must use **radians** as well.

---

## Historical note (pre–radians migration)

Before unifying on radians, the engine effectively treated many of these inputs as **degrees**:

- `ZMatrix4.perspective` used `tan(fov * (PI / 360f))` (vertical FOV in degrees).
- `ZQuaternion.fromAngleAxis` multiplied by `PI/180`; `fromEuler` treated `ZEuler` components as degrees.
- `ZEuler.fromQuaternion` scaled trig results by `180/PI` and used `±90f` for gimbal pitch.
- `ZTransform.rotationEuler` KDoc mentioned radians while `fromEuler` still interpreted degrees (mismatch).

Tests under `engine/src/commonTest/.../math/` were updated to use radian literals or degree→radian helpers where appropriate.

---

## Serialization and backward compatibility

[`ZPerspectiveLensData`](../../../engine/src/commonMain/kotlin/zernikalos/components/camera/ZPerspectiveLens.kt) is protobuf-serializable: the **`fov` float now means radians**. Payloads authored when `fov` meant degrees must convert on load (e.g. `fov_rad = fov_deg * π/180`), or bump a format version.

Same caution for any custom asset pipeline that stored Euler or axis angles assuming degrees.

---

## Follow-up outside `engine/`

Search consumers of `ZPerspectiveLens`, `ZCamera`, `ZMatrix4.perspective`, `ZQuaternion.fromAngleAxis` / `fromEuler`, and `ZTransform` rotation APIs in **samples, DemoApps, Nest, ZKBuilder**, and web/JS glue, especially after SDK regeneration.

---

## Related proposal

Planned **mixed strategy**: shared **`Angles.degreesToRadians` / `radiansToDegrees`**; primary **`ZTransform.*Degrees`** authoring surface; **`ZEuler.fromDegrees`**; **lens FOV in degrees**; optional later **`ZQuaternion`** / **`ZMatrix4.perspectiveDegrees`** companions (documented as future work). Details: [degrees-companion-api-rationale.md](degrees-companion-api-rationale.md).
