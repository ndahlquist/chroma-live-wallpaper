precision mediump float;

uniform int u_Time;
uniform sampler2D u_ColorSwath;
uniform sampler2D u_Noise;
uniform vec2 u_Touch0; // xposition[0, 1] and down time.
//uniform vec2 u_Touch1;

varying vec2 v_Position;  // XY position, from 0.0 to 1.0
varying float v_TopCurve;
varying float v_BottomCurve;
varying float v_Vignette;

void main() {
   
     float TouchMultiplier;
     if(v_Position.x == -1.0)
     	TouchMultiplier = 1.0;
     else
     	TouchMultiplier = 1.0 + .01 / abs(u_Touch0.x - v_Position.x);
	
	float noise0 = texture2D(u_Noise,vec2(v_Position.x, fract(float(u_Time) / 153.0))).r;
	float noise1 = texture2D(u_Noise,vec2(v_Position.x, fract(float(u_Time) / 378.0))).b;
	float combinedNoise = .24 + noise0 * noise1;
	
	float value = 10.0 * (v_TopCurve-v_Position.y) * (v_Position.y-v_BottomCurve) * v_Vignette;
	value = max(0.0, value);
	value = min(value, 1.0);

	vec4 color = texture2D(u_ColorSwath, vec2(v_Position.x, v_Position.y));
	
	gl_FragColor = min(TouchMultiplier * combinedNoise * value, 1.0) * color;
 
}
