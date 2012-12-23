precision mediump float; // TODO

uniform vec4 vColor;
uniform int u_Time;       // The current time

varying vec2 v_Position;  // XY position, from 0.0 to 1.0
varying float topCurve;
varying float bottomCurve;
varying vec4 color;

void main() {

    float value = 10.0 * (topCurve-v_Position.y) * (v_Position.y-bottomCurve);
    if(value <= 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }   
    value = min(value, 1.0);
    
    gl_FragColor = color * value;
 
}
