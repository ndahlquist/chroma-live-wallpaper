package edu.stanford.nicd.chroma;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

class ChromaSprite {

	private final int mProgramHandle;
	private final int mPositionHandle;
	
	public MotionEvent motionEvent;
	private int frameNum;
	public int displayWidth;

	private final int COORDS_PER_VERTEX = 3;
	private final int BYTES_PER_FLOAT = 4;
	private final int VERTEX_STRIDE = COORDS_PER_VERTEX * BYTES_PER_FLOAT;
	private final int vertexCount;
	private final int coordinateHandle[] = new int[1];

	public ChromaSprite(Context context) throws Exception {
	
		// Copy the geometry to GPU memory
		vertexCount = LoadGeometry(coordinateHandle);

		// Compile and link shader programs
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.sprite_v);   		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		if(vertexShaderHandle == 0) // Shader compilation failed.
			throw new Exception("Vertex shader compilation failed.", null);
		
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.sprite_f);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);	
		if(fragmentShaderHandle == 0)
			throw new Exception("Fragment shader compilation failed.", null);
		
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
				new String[] {"a_Position"});
		if(mProgramHandle == 0)
			throw new Exception("Shader compilation failed during linking.", null);
		
		// Store handles to attributes and uniforms
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");

		// Initialize frameNumber to last known or random
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		frameNum = prefs.getInt("frameNum", (int) (65535.0 * Math.random()));

	}
	
	private int LoadGeometry(int[] buffers) {
		
		final float[] coordinates = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f};

		final FloatBuffer mBuffer = ByteBuffer.allocateDirect(coordinates.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mBuffer.put(coordinates, 0, coordinates.length);
		mBuffer.position(0);

		GLES20.glGenBuffers(1, buffers, 0);						

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mBuffer.capacity() * BYTES_PER_FLOAT, mBuffer, GLES20.GL_STATIC_DRAW);			

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		mBuffer.limit(0);
		
		return coordinates.length;
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
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glFrontFace(GLES20.GL_CW);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_BLEND);
		
		// Pass in the position information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, coordinateHandle[0]);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// Draw the triangles
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

}