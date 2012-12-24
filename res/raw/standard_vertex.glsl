precision highp float;

attribute vec4 vPosition;

uniform int u_Time;

varying vec2 v_Position;
varying float v_TopCurve;
varying float v_BottomCurve;
varying float v_Vignette;

void main() {
    gl_Position = vPosition;
    v_Position = (vPosition.xy+1.0)/2.0;
    v_TopCurve = .92 + .06*sin(v_Position.x*2.1 + float(u_Time) / 104.0);
    v_BottomCurve = .1+.04*sin(v_Position.x*4.6 - float(u_Time) / 63.0);
    v_Vignette = 1.0 - pow(abs(vPosition.x), 2.0);
}
