package edu.stanford.nicd.perspectivelivewallpaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;

import net.rbgrn.android.glwallpaperservice.*;
import android.content.Context;
import android.opengl.GLES20;

// Original code provided by Robert Green
// http://www.rbgrn.net/content/354-glsurfaceview-adapted-3d-live-wallpapers
public class MyRenderer implements GLWallpaperService.Renderer {

	private Context context;
	protected Triangle mTriangle;

	public MyRenderer(Context context) {
		this.context = context;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		if(mTriangle == null)
			mTriangle = new Triangle(context);
	}

	public void onDrawFrame(GL10 unused) {
		mTriangle.draw();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

}

class Triangle {

	private final FloatBuffer vertexBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mTimeHandle;

	private int frameNum = 0;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static final int TESSELATION_FACTOR = 10;
	private final int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	public Triangle(Context context) {

		float[] triangleCoords = new float[2*TESSELATION_FACTOR*COORDS_PER_VERTEX];
		for(int i=0; i<TESSELATION_FACTOR; i++) {
			// Bottom
			triangleCoords[6*i+0] =  2.0f*i/(TESSELATION_FACTOR-1.0f) - 1.0f;
			triangleCoords[6*i+1] = -1.0f;
			triangleCoords[6*i+2] =  0.0f;

			// Top
			triangleCoords[6*i+3] =  2.0f*i/(TESSELATION_FACTOR-1.0f) - 1.0f;
			triangleCoords[6*i+4] =  1.0f;
			triangleCoords[6*i+5] =  0.0f;
		}
		vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				triangleCoords.length * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(triangleCoords);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		// prepare shaders and OpenGL program

		// Compile the shader programs.
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.standard_vertex);   		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);

		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.chroma_fragment);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);	

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShaderHandle);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShaderHandle); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

	}

	public void draw() {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mTimeHandle = GLES20.glGetUniformLocation(mProgram, "u_Time");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniform1i(mTimeHandle, frameNum++);

		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
