precision mediump float;

uniform vec4 vColor;
varying vec2 v_Position;       // XY position, from 0.0 to 1.0

void main() {

    float topCurve = .8+.2*sin(v_Position.x*2.1+6.8);
    float bottomCurve = .1+.1*sin(v_Position.x*4.6);
    float barCurve = 1.0;
    //if(barCurve <= 0.0)
    //    barCurve = 0.0;
    //else
    //    barCurve = min(barCurve, 1.0);

    float hue = v_Position.x * 280.0-30.0;
    hue = mod(hue, 360.0);
    float saturation = 1.0;
    float value = 10.0 * (topCurve-v_Position.y) * (v_Position.y-bottomCurve);
    if(value <= 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }   
    value = min(value*barCurve, 1.0);
    float sector = floor(hue / 60.0);
    float f = hue / 60.0 - sector;
    float p = value * ( 1.0 - saturation );
    float q = value * ( 1.0 - saturation * f );
    float t = value * ( 1.0 - saturation * ( 1.0 - f ) );

    if(sector == 0.0)
        gl_FragColor = vec4(value, t, p, 1.0);
    else if(sector == 1.0)
        gl_FragColor = vec4(q, value, p, 1.0);
    else if(sector == 2.0)
        gl_FragColor = vec4(p, value, t, 1.0);
    else if(sector == 3.0)
        gl_FragColor = vec4(p, q, value, 1.0);
    else if(sector == 4.0)
        gl_FragColor = vec4(t, p, value, 1.0);
    else /* sector == 5.0*/
        gl_FragColor = vec4(value, p, q, 1.0);
 
}