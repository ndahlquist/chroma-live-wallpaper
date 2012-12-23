precision highp float;

attribute vec4 vPosition;

uniform int u_Time;

varying vec2 v_Position;
varying float v_TopCurve;
varying float v_BottomCurve;
varying vec4 v_Color;

void main() {
    gl_Position = vPosition;
    v_Position = (vPosition.xy+1.0)/2.0;
    v_TopCurve = .85+.1*sin(v_Position.x*2.1 + float(u_Time) / 104.0);
    v_BottomCurve = .15+.1*sin(v_Position.x*4.6 - float(u_Time) / 63.0);
    
    float hue = v_Position.x * 280.0-30.0;
    hue = mod(hue, 360.0);

    // Piecewise hue function.
    float sector = floor(hue / 60.0);
    float f = hue / 60.0 - sector;
    if(sector == 0.0)
        v_Color = vec4(1.0, f, 0.0, 1.0);
    else if(sector == 1.0)
        v_Color = vec4(1.0 - f, 1.0, 0.0, 1.0);
    else if(sector == 2.0)
        v_Color = vec4(0.0, 1.0, f, 1.0);
    else if(sector == 3.0)
        v_Color = vec4(0.0, 1.0 - f, 1.0, 1.0);
    else if(sector == 4.0)
        v_Color = vec4(f, 0.0, 1.0, 1.0);
    else // sector == 5.0
        v_Color = vec4(1.0, 0.0, 1.0 - f, 1.0);
}
