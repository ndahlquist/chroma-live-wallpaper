precision mediump float;

varying vec2 v_Position;  // XY position, from 0.0 to 1.0
varying float v_TopCurve;
varying float v_BottomCurve;
varying vec4 v_Color;

void main() {

    float value = 10.0 * (v_TopCurve-v_Position.y) * (v_Position.y-v_BottomCurve);
    value = max(0.0, value);
    value = min(value, 1.0);
    
    gl_FragColor = value * v_Color;
 
}
