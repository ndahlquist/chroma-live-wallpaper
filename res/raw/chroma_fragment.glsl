precision mediump float;

uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_Position;  // XY position, from 0.0 to 1.0
varying float v_TopCurve;
varying float v_BottomCurve;
varying float v_Vignette;

void main() {
   
	float value = 10.0 * (v_TopCurve-v_Position.y) * (v_Position.y-v_BottomCurve) * v_Vignette;
	value = max(0.0, value);
	value = min(value, 1.0);
    
    	vec2 samplePoint = vec2(v_Position.x, v_Position.y);
	vec4 swath = texture2D(u_Texture, samplePoint);
	
	gl_FragColor = value * swath;
 
}
