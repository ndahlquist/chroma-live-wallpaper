package edu.stanford.nicd.chroma;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.learnopengles.android.common.*;
import net.rbgrn.android.glwallpaperservice.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

public class MyRenderer implements GLWallpaperService.Renderer {

	private Context context;
	protected ChromaBackground mBackground;
	private Sprite mSprites;

	public MyRenderer(Context context) {
		this.context = context;
	}
	
	public void close() {
		if(mBackground != null)
			mBackground.close(context);
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		if(mBackground == null)
			try {
				mBackground = new ChromaBackground(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		if(mSprites == null)
			mSprites = new Sprite(context);
	}

	public void onDrawFrame(GL10 unused) {
		mBackground.draw();
		mSprites.draw();
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		mBackground.displayWidth = width;
	}

	public void onTouchEvent(MotionEvent event) {
		if(mBackground != null)
			mBackground.motionEvent = event;
	}

}

class Sprite {

	public Sprite(Context context) {

	}

	public void draw() {

	}

}

class ChromaBackground {

	private final FloatBuffer vertexBuffer;
	private final int mProgramHandle;
	private int mPositionHandle;
	private int mTimeHandle;
	private int mTouchHandle;
	private int mColorSwathHandle;
	private int mNoiseHandle;
	public MotionEvent motionEvent;
	private int frameNum;
	public int displayWidth;

	static final int COORDS_PER_VERTEX = 3;
	static final int TESSELATION_FACTOR = 10;
	static final int BYTES_PER_FLOAT = 4;
	private final int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * BYTES_PER_FLOAT;
	
	public ChromaBackground(Context context) throws Exception {

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
		ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * BYTES_PER_FLOAT);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(triangleCoords);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		// Compile the shader programs.
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.chroma_v);   		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		if(vertexShaderHandle == 0) // Shader compilation failed.
			throw new Exception("Vertex shader compilation failed.", null);
		
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.chroma_f);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);	
		if(fragmentShaderHandle == 0) // Shader compilation failed.
			throw new Exception("Fragment shader compilation failed.", null);
		
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
				new String[] {"u_Time",  "a_Position", "u_ColorSwath", "u_Noise"});
		if(mProgramHandle == 0) // Shader compilation failed.
			throw new Exception("Shader compilation failed during linking.", null);
		
		// Store handles to attributes and uniforms
		mColorSwathHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ColorSwath");
		mNoiseHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Noise");
		mTimeHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Time");
		mTouchHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Touch");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
		
		// Load textures
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		TextureHelper.loadTexture(context, R.drawable.chromaswath, mColorSwathHandle);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mColorSwathHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);			
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		TextureHelper.loadTexture(context, R.drawable.noise, mNoiseHandle);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNoiseHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);			
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		// Initialize frameNumber to last known or random
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		frameNum = prefs.getInt("frameNum", (int) (65535.0 * Math.random()));
	}
	
	public void close(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("frameNum", frameNum);
		editor.commit();
	}

	public void draw() {

		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgramHandle);

		// Specify rendering settings
		GLES20.glEnable(GLES20.GL_DITHER);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		// Pass the current frame number
		if(++frameNum >= 65535) // Wrap to GLSL highp int
			frameNum = -65535;
		GLES20.glUniform1i(mTimeHandle, frameNum);

		// Pass in touches
		if(motionEvent == null) {
			GLES20.glUniform4f(mTouchHandle, -1.0f, -1.0f, -1.0f, -1.0f);
		} else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
				motionEvent = null;
				GLES20.glUniform4f(mTouchHandle, -1.0f, -1.0f, -1.0f, -1.0f);
		} else {
			switch(motionEvent.getPointerCount()) {
			case 1:	GLES20.glUniform4f(mTouchHandle, motionEvent.getX(0) / displayWidth, -1.0f, -1.0f, -1.0f);
					break;
			case 2:	GLES20.glUniform4f(mTouchHandle, motionEvent.getX(0) / displayWidth,
						motionEvent.getX(1) / displayWidth, -1.0f, -1.0f);
					break;
			case 3: GLES20.glUniform4f(mTouchHandle, motionEvent.getX(0) / displayWidth,
					motionEvent.getX(1) / displayWidth, motionEvent.getX(2) / displayWidth, -1.0f);
				break;
			default: GLES20.glUniform4f(mTouchHandle, motionEvent.getX(0) / displayWidth,
					motionEvent.getX(1) / displayWidth, motionEvent.getX(2) / displayWidth,
					motionEvent.getX(3) / displayWidth);
			}
		}

		// Pass in the texture information
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glUniform1i(mColorSwathHandle, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mColorSwathHandle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glUniform1i(mNoiseHandle, 1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNoiseHandle);

		// Enable a handle to the ChromaBackground vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the ChromaBackground coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);

		// Draw the triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
