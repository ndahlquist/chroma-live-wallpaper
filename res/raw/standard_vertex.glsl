attribute vec4 vPosition;

uniform int u_Time;       // The current time

varying vec2 v_Position;
varying float topCurve;
varying float bottomCurve;
varying vec4 color;

void main() {
    gl_Position = vPosition;
    v_Position = (vPosition.xy+1.0)/2.0;
    topCurve = .85+.1*sin(v_Position.x*2.1 + float(u_Time) / 104.0);
    bottomCurve = .15+.1*sin(v_Position.x*4.6 - float(u_Time) / 63.0);
    
    float hue = v_Position.x * 280.0-30.0;
    hue = mod(hue, 360.0);
    float saturation = 1.0;
    float value = 1.0;

    float sector = floor(hue / 60.0);
    float f = hue / 60.0 - sector;
    float p = value * ( 1.0 - saturation );
    float q = value * ( 1.0 - saturation * f );
    float t = value * ( 1.0 - saturation * ( 1.0 - f ) );

    if(sector == 0.0)
        color = vec4(value, t, p, 1.0);
    else if(sector == 1.0)
        color = vec4(q, value, p, 1.0);
    else if(sector == 2.0)
        color = vec4(p, value, t, 1.0);
    else if(sector == 3.0)
        color = vec4(p, q, value, 1.0);
    else if(sector == 4.0)
        color = vec4(t, p, value, 1.0);
    else /* sector == 5.0*/
        color = vec4(value, p, q, 1.0);
}
