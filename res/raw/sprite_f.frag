precision mediump float;

varying vec2 v_Position;  // [-1.0, 1.0]

void main() {
	
	//float value = v_Position.x + v_Position.y;
	//value = clamp(value, 0.0, 1.0);
	
	gl_FragColor = vec4(1.0, 1.0, .9, 1.0);
 
}
