# GLSL Specification for Ambient Light and Multiple Lights Support

## Objective

Extend the current shading system to support:

- Ambient light
- Multiple simultaneous lights

maintaining a clean, scalable architecture compatible with Phong and PBR materials.

---

## 1. Design Principles

### 1.1 Separate Direct Light from Ambient Light

The shader must distinguish between:

- **Direct light**: directional, point, spot
- **Ambient light**: global non-directional contribution
- **Emissive**: energy emitted by the material itself, independent of lights

The final conceptual formula should look like this:

```
finalColor = ambientTerm + directLightingSum + emissive
```

And in PBR, better thought of as:

```
finalColor = indirectLight + directLightingSum + emissive
```

where at the beginning indirectLight can be just a simple ambient fallback.

### 1.2 Ambient Light Should Not Be Modeled the Same in Phong and PBR

**In Phong**

Classic ambient light fits as:

- global ambient color
- global ambient intensity
- multiplied by the material's ambient component

**In PBR**

"Ambient" must be understood as an indirect lighting approximation, not as a classic light.

Until you have IBL, a simple global fallback is acceptable.

### 1.3 Direct Lights Must Be Accumulative

Each light provides an independent contribution, and the final result is the sum of all:

```
accumulatedDirect += contribution(light[i])
```

This applies to both Phong and PBR.

### 1.4 All Lighting Operations Must Be Done in the Same Space

You must choose a space and be consistent. The most practical here is:

**view space** for positions, normals and lights

Therefore:

- `v_viewPosition` is in view space
- `v_normal` is in view space
- light positions and directions must also be in view space, or transformed before use

If you mix world/view/local, lighting will give misleading results.

---

## 2. Ambient Light Support

### 2.1 Functional Requirement

The renderer must support a configurable global ambient contribution that affects all fragments, even when there are no direct lights.

### 2.2 Conceptual Model

**Variant A — Phong**

Classic ambient:

```
ambientContribution = ambientLightColor * ambientLightIntensity * materialAmbient * baseColor
```

**Variant B — PBR**

Ambient as indirect lighting fallback:

```
ambientContribution = ambientLightColor * ambientLightIntensity * albedo * diffuseFactor
```

where diffuseFactor should be reduced in metallic materials.

An acceptable initial approximation:

```
diffuseFactor = (1.0 - metalness)
```

A slightly better one:

```
F0 = mix(vec3(0.04), albedo, metalness)
kD_ambient = (1.0 - metalness) * (vec3(1.0) - F0)
ambientContribution = ambientLightColor * ambientLightIntensity * albedo * kD_ambient
```

### 2.3 Recommended GLSL Interface

Create a global uniform block for ambient:

```glsl
uniform u_ambientLightBlock
{
    vec4 color;
    float intensity;
} u_ambientLight;
```

**Semantics**

- `color.rgb`: ambient light color
- `intensity`: scalar factor
- `color.a`: reserved or ignored

### 2.4 Shading Rules

**In Phong**

The ambient component must be independent of angle and normals.

```glsl
vec3 calculatePhongAmbient(vec4 baseColor) {
    return u_ambientLight.color.rgb
         * u_ambientLight.intensity
         * u_phongMaterial.ambient.rgb
         * baseColor.rgb;
}
```

**In PBR**

The ambient component should mainly affect the diffuse/non-metallic part.

```glsl
vec3 calculatePBRAmbient(vec3 albedo, float metalness) {
    vec3 F0 = mix(vec3(0.04), albedo, metalness);
    vec3 kD = (vec3(1.0) - F0) * (1.0 - metalness);

    return u_ambientLight.color.rgb
         * u_ambientLight.intensity
         * albedo
         * kD;
}
```

It's not real IBL, but it better prepares the step for the future.

### 2.5 Expected Behavior

- If ambient intensity = 0, there is no ambient contribution
- It must work even if no direct light exists
- It must not generate shadows
- It must be global, uniform and computationally cheap

---

## 3. Multiple Lights Support

### 3.1 Functional Requirement

The shader must be able to evaluate several direct lights in the same fragment and accumulate their contributions.

**Supported types:**

- directional
- point
- spot

### 3.2 Conceptual Model

For each fragment:

1. Calculate the base material color
2. Calculate the global ambient contribution
3. Iterate over all active lights
4. For each light:
   - calculate direction
   - calculate attenuation
   - calculate contribution according to material model
5. Sum all
6. Add emissive
7. Apply tonemapping/gamma if applicable

### 3.3 GLSL Data Structure

You have two conceptual options.

**Option A — arrays of structs**

More conceptually readable, although in some targets it may be more delicate depending on backend/driver.

```glsl
struct Light {
    vec4 direction;
    vec4 position;
    vec4 color;
    float intensity;
    float type;
    float range;
    float decay;
    float innerAngle;
    float outerAngle;
};

uniform Light u_lights[MAX_LIGHTS];
uniform int u_lightCount;
```

**Option B — uniform block with array**

More aligned with your current style.

```glsl
struct Light {
    vec4 direction;
    vec4 position;
    vec4 color;
    float intensity;
    float type;
    float range;
    float decay;
    float innerAngle;
    float outerAngle;
};

uniform u_lightBlock
{
    Light lights[MAX_LIGHTS];
    int lightCount;
} u_lighting;
```

At the conceptual level, this is the option that best fits your current design.

### 3.4 Maximum Lights Constant

A compilation constant must exist:

```glsl
const int MAX_LIGHTS = 8;
```

or whatever you decide.

**Rule**

The engine will never send more than MAX_LIGHTS.

**Practical recommendation**

Start with:

- 4 for conservative mobile
- 8 for general
- later you can make variants by pipeline/quality

### 3.5 Light Type Semantics

Maintain a clear convention:

- `0.0` = directional
- `1.0` = point
- `2.0` = spot

Although GLSL doesn't have real enums, you should treat this as a logical engine enum.

### 3.6 Common Geometric Calculation Function per Light

The current function can evolve to an indexed version:

```glsl
void computeLightContribution(
    Light light,
    vec3 fragPosition,
    out vec3 outLightDir,
    out float outAttenuation
)
```

**Behavior**

**Directional**

```
outLightDir = normalize(-light.direction.xyz)
outAttenuation = 1.0
```

**Point**

```
toLight = light.position.xyz - fragPosition
dist = length(toLight)
outLightDir = normalize(toLight)
outAttenuation according to range/decay
```

**Spot**

Same as point, plus angular factor.

### 3.7 Light Accumulation in Fragment Shader

The general logic must be:

```glsl
vec3 directLighting = vec3(0.0);

for (int i = 0; i < MAX_LIGHTS; ++i) {
    if (i >= u_lighting.lightCount) {
        break;
    }

    vec3 lightDir;
    float attenuation;
    computeLightContribution(u_lighting.lights[i], v_viewPosition, lightDir, attenuation);

    vec3 lightColor = u_lighting.lights[i].color.rgb
                    * u_lighting.lights[i].intensity
                    * attenuation;

    directLighting += evaluateLight(..., lightDir, lightColor);
}
```

Where evaluateLight will be:

- `calculateBlinnPhongLight(...)`
- `calculatePBRLight(...)`

---

## 4. Recommended Function Refactor

Right now your functions mix quite a bit of logic. It's better to separate them more.

### 4.1 For PBR

Instead of a function that calculates everything at once, separate into two concepts:

**A. Evaluation of a single direct light**

```glsl
vec3 calculatePBRDirectLight(
    vec3 albedo,
    float metalness,
    float roughness,
    vec3 N,
    vec3 V,
    vec3 L,
    vec3 lightColor
)
```

This function returns only the contribution of one light.

**B. Simple ambient/indirect evaluation**

```glsl
vec3 calculatePBRAmbient(
    vec3 albedo,
    float metalness
)
```

**Result**

```
vec3 color = ambient + directSum;
```

### 4.2 For Phong

Same:

**A. Direct light per light**

```glsl
vec3 calculatePhongDirectLight(
    vec3 baseColor,
    vec3 N,
    vec3 V,
    vec3 L,
    vec3 lightColor
)
```

**B. Global ambient**

```glsl
vec3 calculatePhongAmbient(
    vec4 baseColor
)
```

---

## 5. Final Shading Flow Specification

### 5.1 General Flow

**Step 1**

Resolve baseColor:

- texture
- vertex color
- material color
- combination you define

**Step 2**

Prepare base vectors:

- N
- V
- fragPos

**Step 3**

Calculate global ambient

**Step 4**

Iterate lights and accumulate direct contribution

**Step 5**

Add emissive

**Step 6**

Tonemapping and gamma

### 5.2 Formula by Material

**Phong**

```
final = phongAmbient + sum(phongDirectLights) + emissiveOptional
```

**PBR**

```
final = pbrAmbientFallback + sum(pbrDirectLights) + emissive
```

---

## 6. GLSL Implementation Recommendations

### 6.1 Don't Duplicate Loop Logic by Material if You Can Avoid It

Make a function per light and accumulate in a common loop in main, or almost common.

Although you'll have two material routes, the pattern should be the same:

- initial ambient
- light loop
- summation

### 6.2 Keep Functions Small and Single-Purpose

Good decomposition:

- `computeLightContribution(light, fragPos, out dir, out attenuation)`
- `calculatePBRAmbient(...)`
- `calculatePBRDirectLight(...)`
- `calculatePhongAmbient(...)`
- `calculatePhongDirectLight(...)`

That simplifies later adding:

- shadows
- IBL
- light culling
- probes

### 6.3 Minimum Clamps

You must continue using protections like:

- `max(dot(...), 0.0)`
- epsilon in divisions
- clamp in spot cones
- clamp in attenuation

Especially in PBR.

### 6.4 Tonemapping at the End, Not Per Light

Don't do tonemapping per contribution.
First accumulate linear energy, then tonemapping/gamma at the end of the fragment pipeline.

You already have that well on track.

---

## 7. Accepted Limitations at This Phase

This specification does not yet include:

- shadows
- real IBL
- reflection probes
- clustered/deferred lighting
- tile-based light culling
- normal mapping / tangent space
- area lights

And it's fine that it doesn't. First it's good to have:

- a clean base
- multiple lights
- coherent ambient
- good separation between PBR and Phong

---

## 8. Engine Conceptual Contract with GLSL

Although you implement it in the engine, the conceptual contract should be this:

**The engine guarantees that:**

- `lightCount <= MAX_LIGHTS`
- all sent lights are in the same space as `v_viewPosition`
- normals and positions are in the correct space
- materials have valid parameters
- roughness doesn't reach absolute zero if that gives you numerical problems
- spot angles are consistent

**The shader assumes that:**

- data is already ready to evaluate
- it doesn't have to "interpret" weird engine states
- it just lights

---

## 9. Recommended GLSL Pseudocode

### PBR

```glsl
vec3 albedo = materialColor * baseColor.rgb;
float metalness = u_pbrMaterial.metalness;
float roughness = u_pbrMaterial.roughness;

vec3 N = normalize(v_normal);
vec3 V = normalize(-v_viewPosition);

vec3 ambient = calculatePBRAmbient(albedo, metalness);
vec3 direct = vec3(0.0);

for (int i = 0; i < MAX_LIGHTS; ++i) {
    if (i >= u_lighting.lightCount) break;

    vec3 L;
    float attenuation;
    computeLightContribution(u_lighting.lights[i], v_viewPosition, L, attenuation);

    vec3 lightColor = u_lighting.lights[i].color.rgb
                    * u_lighting.lights[i].intensity
                    * attenuation;

    direct += calculatePBRDirectLight(albedo, metalness, roughness, N, V, L, lightColor);
}

vec3 color = ambient + direct + emissive;
color = applyTonemapping(color);
```

### Phong

```glsl
vec3 N = normalize(v_normal);
vec3 V = normalize(-v_viewPosition);

vec3 ambient = calculatePhongAmbient(baseColor);
vec3 direct = vec3(0.0);

for (int i = 0; i < MAX_LIGHTS; ++i) {
    if (i >= u_lighting.lightCount) break;

    vec3 L;
    float attenuation;
    computeLightContribution(u_lighting.lights[i], v_viewPosition, L, attenuation);

    vec3 lightColor = u_lighting.lights[i].color.rgb
                    * u_lighting.lights[i].intensity
                    * attenuation;

    direct += calculatePhongDirectLight(baseColor.rgb, N, V, L, lightColor);
}

vec3 color = ambient + direct;
```

---

## 10. Concrete Decisions I Recommend You Make

If you want this to be clean from the start, I would set these decisions:

**Ambient Light**

- Yes, support it
- Global
- Uniform
- Separate from direct lights block
- In PBR treat it as ambient fallback, not as "the final solution"

**Multiple Lights**

- Forward support
- Fixed array MAX_LIGHTS
- Dynamic lightCount
- Per-fragment loop
- Linear accumulation

**Shader Conceptual API**

- `calculate...Ambient()`
- `calculate...DirectLight()`
- `computeLightContribution()`

That leaves you a very solid base.

---

## 11. Important Observation About Cost

With multiple lights in forward, the cost grows per fragment:

- more lights = more BRDFs evaluated
- in PBR this is noticed much more than in Phong

So at the conceptual level it's convenient that the engine can limit:

- number of active lights per object
- number of lights per material/quality tier
- perhaps one main + N additional later

It doesn't need to be resolved now in the shader, but yes design knowing it exists.

---

## 12. Expected Final Result

After this specification, your shader should conceptually be able to represent this:

- An object without direct lights but with ambient is still visible
- An object with several lights receives the sum of all
- The Phong route uses classic ambient
- The PBR route uses reasonable ambient fallback
- The architecture is prepared to evolve to IBL without breaking the mental model
