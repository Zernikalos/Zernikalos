/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator.libs

const val defaultVertexShaderSource = """
    
uniform MatrixBlock
{
  mat4 projection;
  mat4 modelview;
} matrices;

#ifdef USE_SKINNING
    uniform mat4 u_bones[100];
    uniform mat4 u_invBindMatrix[100];
#endif
uniform mat4 u_projMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_mvpMatrix;

#ifdef USE_SKINNING
    in vec4 a_boneIndices;
    in vec4 a_boneWeight;
#endif
#ifdef USE_NORMALS
    in vec3 a_normal;
#endif
#ifdef USE_TEXTURES
    in vec2 a_uv;
    out vec2 v_uv;
#endif
#ifdef USE_COLORS
    in vec3 a_color;
    out vec3 v_color;
#endif
in vec3 a_position;

#ifdef USE_SKINNING
vec4 calcSkinnedPosition() {
    vec4 skinnedPosition = vec4(0.0);
     
     // Sum the transformations from each influencing bone
    for (int i = 0; i < 4; ++i) {
        if (a_boneWeight[i] > 0.0) {
            int boneID = int(a_boneIndices[i]);
            mat4 boneMatrix = u_bones[boneID];
            mat4 boneInverseMatrix = u_invBindMatrix[boneID];

            // Apply the bone transformation
            vec4 localPosition = boneInverseMatrix * vec4(a_position, 1.0);

            skinnedPosition += a_boneWeight[i] * (boneMatrix * localPosition);
        }
    }
    
    return skinnedPosition;
}
#endif

void main() {
    #ifdef USE_SKINNING
        gl_Position = u_mvpMatrix * calcSkinnedPosition();
    #else
        gl_Position = u_mvpMatrix * vec4(a_position,1);
    #endif

#ifdef USE_TEXTURES
    #ifdef FLIP_TEXTURE_Y
        v_uv = vec2(a_uv.x, 1.0 - a_uv.y);;
    #else
        v_uv = a_uv;
    #endif
#endif
#ifdef USE_COLORS
    v_color = a_color;
#endif
}
"""

const val defaultFragmentShaderSource = """
precision mediump float;

#ifdef USE_TEXTURES
    uniform sampler2D u_texture;
    smooth in vec2 v_uv;
#endif
#ifdef USE_COLORS
    smooth in vec3 v_color;
#endif
out vec4 f_color;

void main() {
    #if defined(USE_TEXTURES)
        f_color = texture(u_texture, v_uv);
    #elif defined(USE_COLORS)
        f_color = vec4(v_color.xyz, 1);
    #else
        f_color = vec4(1, 1, 1, 1);
    #endif
}
"""