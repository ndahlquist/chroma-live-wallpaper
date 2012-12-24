precision mediump float;

uniform int u_Time;
uniform sampler2D u_ColorSwath;
uniform sampler2D u_Noise;

varying vec2 v_Position;  // XY position, from 0.0 to 1.0
varying float v_TopCurve;
varying float v_BottomCurve;
varying float v_Vignette;

void main() {
   
	float value = 10.0 * (v_TopCurve-v_Position.y) * (v_Position.y-v_BottomCurve) * v_Vignette;
	value = max(0.0, value);
	value = min(value, 1.0);

	vec4 color = texture2D(u_ColorSwath, vec2(v_Position.x, v_Position.y));
	
	float noise0 = texture2D(u_Noise,vec2(v_Position.x, fract(float(u_Time) / 353.0))).r;
	float noise1 = texture2D(u_Noise,vec2(v_Position.x, fract(float(u_Time) / 258.0))).b;
	float combinedNoise = .24+1.2*pow(noise0 * noise1, 1.5);
	
	gl_FragColor = min(combinedNoise * value, 1.0) * color;
 
}
