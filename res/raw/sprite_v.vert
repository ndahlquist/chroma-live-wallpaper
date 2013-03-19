precision highp float;

attribute vec4 a_Position;

varying vec2 v_Position;

void main() {
    gl_Position = a_Position;
    v_Position = a_Position.xy;
}
