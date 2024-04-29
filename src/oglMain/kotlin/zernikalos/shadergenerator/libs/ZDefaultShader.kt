package zernikalos.shadergenerator.libs

const val defaultVertexShaderSource = """
#ifdef USE_SKINNING
    uniform mat4 u_bones[150];
    uniform mat4 u_bindMatrix;
    uniform mat4 u_invBindMatrix;
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

void main() {
    gl_Position = u_mvpMatrix * vec4(a_position,1);
   
#ifdef USE_TEXTURES 
    v_uv = a_uv;
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
    #ifdef USE_TEXTURES
        f_color = texture(u_texture, v_uv);
    #endif
    #ifdef USE_COLORS
        f_color = vec4(v_color.xyz, 1);
    #endif
}
"""