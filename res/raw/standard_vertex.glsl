attribute vec4 vPosition;

varying vec2 v_Position;        // This will be passed into the fragment shader.

void main() {
    gl_Position = vPosition;
    v_Position = (vPosition.xy+1.0)/2.0;
}