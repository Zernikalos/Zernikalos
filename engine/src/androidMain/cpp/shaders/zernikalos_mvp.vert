#version 450

layout(location = 0) in vec3 a_position;

layout(set = 0, binding = 0) uniform SceneMatrix {
    mat4 u_projMatrix;
    mat4 u_viewMatrix;
    mat4 u_mvpMatrix;
} sceneMatrix;

void main() {
    gl_Position = sceneMatrix.u_mvpMatrix * vec4(a_position, 1.0);
}
