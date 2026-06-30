# Phase C2: `ZGpuFrame` + `ZGpuCommandEncoder` Orchestration

**Status**: Landed (Phase C2)
**Created**: 2026-06  
**Parent**: [GPU Rendering Pipeline Refactor](../gpu-rendering-pipeline-refactor.md)  
**Previous**: [Phase C1 — Common render pass descriptor](./gpu-phase-c1-render-pass-descriptor.md)

This document records the landed C2 slice. C2 completes the frame/command-encoder orchestration originally planned after C1, while leaving `ZGpuDevice`, first-class pipeline resources, multipass scheduling, and OpenGL parity to later phases.

---

## 1. Summary

| Area | Status |
|------|--------|
| `ZGpuFrame` expect/actual contract | **Done** |
| `ZGpuCommandEncoder` expect/actual contract | **Done** |
| Common `ZRendererBase.renderFrame()` uses frame + encoder orchestration | **Done** |
| Metal command-buffer creation/submission moved into `ZGpuFrame` | **Done** |
| WebGPU command-encoder creation/submission moved into `ZGpuFrame` | **Done** |
| `beginRenderPass(ZGpuRenderPassDescriptor)` consumes C1 descriptors | **Done** |
| Pass lifecycle scoped with `activePass` and guaranteed `pass.end()` | **Done** |
| OpenGL | **Deferred** — immediate-mode path remains until Phase F |
| `ZGpuDevice` resource factories | **Deferred** — Phase D |
| Multi-pass frame graph / pass registry | **Deferred** — Phase E |

---

## 2. Landed Architecture

The frame flow now lives in common renderer code:

```kotlin
renderFrame()
  -> createGpuFrame()
  -> gpuFrame.begin()
  -> gpuFrame.beginRecording()
  -> renderViewports(encoder)
       -> viewport.buildRenderPassDescriptor()
       -> encoder.beginRenderPass(desc)
       -> renderingContext.withActivePass(pass) { renderScene() }
       -> pass.end()
  -> encoder.finish()
  -> gpuFrame.submit(encoder)
  -> gpuFrame.end()
```

Metal and WebGPU supply the concrete frame/encoder implementations. OpenGL returns `null` from `createGpuFrame()` and keeps the old immediate-mode viewport/scene path until the Phase F adapter.

---

## 3. Package Layout

```text
engine/src/commonMain/kotlin/zernikalos/context/gpu/
  ZGpuFrame.kt
  ZGpuCommandEncoder.kt

engine/src/metalMain/kotlin/zernikalos/context/gpu/
  ZGpuFrame.metal.kt
  ZGpuCommandEncoder.metal.kt

engine/src/webgpuMain/kotlin/zernikalos/context/gpu/
  ZGpuFrame.wgpu.kt
  ZGpuCommandEncoder.wgpu.kt

engine/src/oglMain/kotlin/zernikalos/context/gpu/
  ZGpuFrame.ogl.kt
  ZGpuCommandEncoder.ogl.kt
```

Renderer entry points:

```text
engine/src/commonMain/kotlin/zernikalos/renderer/ZRenderer.kt
engine/src/metalMain/kotlin/zernikalos/renderer/ZRenderer.metal.kt
engine/src/webgpuMain/kotlin/zernikalos/renderer/ZRenderer.wgpu.kt
```

---

## 4. API Contracts

### `ZGpuFrame`

```kotlin
expect class ZGpuFrame {
    fun begin(): Boolean
    fun beginRecording(): ZGpuCommandEncoder?
    fun submit(encoder: ZGpuCommandEncoder)
    fun end()
}
```

Responsibilities:

- Own per-frame command-buffer / command-encoder setup.
- Return `false` or `null` when the backend cannot render the frame.
- Submit the recorded work.
- Clear per-frame backend references in `end()`.

### `ZGpuCommandEncoder`

```kotlin
expect class ZGpuCommandEncoder {
    fun beginRenderPass(descriptor: ZGpuRenderPassDescriptor): ZGpuRenderPass?
    fun finish()
}
```

Responsibilities:

- Open a backend render pass from the C1 common descriptor.
- Keep native attachment/view encoding at the backend boundary.
- Finalize encoder-side recording before `ZGpuFrame.submit()`.

---

## 5. Backend Notes

### Metal

- `ZGpuFrame.begin()` creates the Metal command buffer through `ZMtlRenderingContext`.
- `ZGpuCommandEncoder.beginRenderPass()` takes `MTKView.currentRenderPassDescriptor`, applies the common pass descriptor, opens the native render command encoder, and returns `ZGpuRenderPass`.
- `ZGpuFrame.submit()` presents the current drawable and commits the command buffer.

### WebGPU

- `ZGpuFrame.begin()` creates the WebGPU command encoder.
- `ZGpuCommandEncoder.beginRenderPass()` asks the viewport renderer to merge the common descriptor with current swapchain/depth views, then opens the native WebGPU pass.
- `ZGpuFrame.submit()` finishes and submits the native command buffer.

### OpenGL

OpenGL actuals are stubs so common code can compile, but `ZRendererBase.createGpuFrame()` remains `null` for the OpenGL renderer path. A real OpenGL pass adapter is still Phase F.

---

## 6. Non-goals

- `ZGpuDevice` and resource factories (Phase D)
- Replacing native pipeline handles in object renderers (Phase D)
- Multipass scheduling, pass registry, or frame graph (Phase E)
- OpenGL render-pass parity (Phase F)
- Raster-state enum cleanup (tracked separately in [GPU Raster-State Enum Unification](../gpu-raster-state-enums-unification.md))

---

## 7. Exit Criteria

| Criterion | Status |
|-----------|--------|
| `ZRendererBase` uses `ZGpuFrame` + `ZGpuCommandEncoder` for Metal/WebGPU | **Met** |
| `beginFrame` / `submitFrame` backend logic lives in `ZGpuFrame` actuals | **Met** |
| `beginRenderPass` accepts `ZGpuRenderPassDescriptor` from C1 | **Met** |
| Metal/WebGPU renderers shrink to frame wiring and backend-specific render state | **Met** |
| OpenGL path remains unchanged and explicitly deferred | **Met** |

---

## 8. Implementation Log

### 2026-06 — Phase C2: Frame + command encoder orchestration

**Scope**: common `ZGpuFrame` / `ZGpuCommandEncoder` contracts, Metal/WebGPU actuals, common renderer orchestration, OpenGL stubs, pass lifecycle cleanup.

**Files touched**:

| Area | Files |
|------|-------|
| Common | `renderer/ZRenderer.kt`, `context/gpu/ZGpuFrame.kt`, `context/gpu/ZGpuCommandEncoder.kt` |
| Metal | `context/gpu/ZGpuFrame.metal.kt`, `context/gpu/ZGpuCommandEncoder.metal.kt`, `renderer/ZRenderer.metal.kt` |
| WebGPU | `context/gpu/ZGpuFrame.wgpu.kt`, `context/gpu/ZGpuCommandEncoder.wgpu.kt`, `renderer/ZRenderer.wgpu.kt` |
| OpenGL | `context/gpu/ZGpuFrame.ogl.kt`, `context/gpu/ZGpuCommandEncoder.ogl.kt` |

**Validation**: `compileKotlinJs`, `compileKotlinIosArm64`, and `compileAndroidMain` succeeded after the C2 doc/lifecycle cleanup.

---

## 9. Handoff

Recommended next implementation slice: finish [GPU Raster-State Enum Unification](../gpu-raster-state-enums-unification.md) before Phase D, so future `ZGpuDevice` pipeline factories inherit one common raster/depth vocabulary instead of backend hardcodes.
