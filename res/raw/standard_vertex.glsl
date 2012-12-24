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
    v_TopCurve = .92 + .04*sin(v_Position.x+float(u_Time)/157.3) - .03*sin((v_Position.x+float(u_Time) / 104.0)*2.1);
    v_BottomCurve = .12 + .04*sin((v_Position.x+float(u_Time)/230.3)*1.4) - .03*sin((v_Position.x+float(u_Time) / 204.0)*.8);
    v_Vignette = 1.0 - pow(abs(vPosition.x), 2.0);
}
