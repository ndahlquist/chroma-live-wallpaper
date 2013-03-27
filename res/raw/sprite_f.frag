precision mediump float;

attribute vec4 a_Position;
varying vec2 v_Position;  // [-1.0, 1.0]

void main() {

	float fx = (v_Position.x+1.0)/2.0;
	float fy = (v_Position.y+1.0)/2.0;

	float distSqr = pow(fx - .5, 2.0) + pow(fy - .5, 2.0);

	distSqr = clamp(3.0 * distSqr, 0.0, 1.0);

    gl_FragColor = vec4(distSqr, 0.0, 1.0 - distSqr, 1.0);
}
