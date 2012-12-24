package edu.stanford.nicd.perspectivelivewallpaper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import net.rbgrn.android.glwallpaperservice.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

public class MyRenderer implements GLWallpaperService.Renderer {

	private Context context;
	protected ChromaBackground mBackground;
	private Sprite mSprites;

	public MyRenderer(Context context) {
		this.context = context;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		if(mBackground == null)
			mBackground = new ChromaBackground(context);
		if(mSprites == null)
			mSprites = new Sprite(context);
	}

	public void onDrawFrame(GL10 unused) {
		mBackground.draw();
		mSprites.draw();
		try {
			Thread.sleep(20); // TODO
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

	public void onTouchEvent(MotionEvent event) {
		// TODO: possible null pointer bug
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
	private int mColorSwathHandle;
	private int mNoiseHandle;
	public MotionEvent motionEvent;
	private int frameNum = 0;
	private int displayWidth;

	static final int COORDS_PER_VERTEX = 3;
	static final int TESSELATION_FACTOR = 10;
	static final int BYTES_PER_FLOAT = 4;
	private final int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * BYTES_PER_FLOAT;

	@SuppressWarnings("deprecation")
	public ChromaBackground(Context context) {

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
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.chroma_vertex);   		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);

		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.chroma_fragment);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);	

		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
				new String[] {"u_Time",  "vPosition", "u_ColorSwath", "u_Noise"});

		mColorSwathHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ColorSwath");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		TextureHelper.loadTexture(context, R.drawable.chromaswath, mColorSwathHandle);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mColorSwathHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);			
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		mNoiseHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Noise");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		TextureHelper.loadTexture(context, R.drawable.noise, mNoiseHandle);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNoiseHandle);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);			
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		// Measure the screen size
		WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			displayWidth = size.x;
		}else{
			Display d = w.getDefaultDisplay(); 
			displayWidth = d.getWidth();
		}
	}

	public void draw() {

		GLES20.glEnable(GLES20.GL_DITHER);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgramHandle);

		// Pass the current frame number
		mTimeHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Time"); // TODO
		GLES20.glUniform1i(mTimeHandle, frameNum++);

		// Pass in touches
		int mTouch0Handle = GLES20.glGetUniformLocation(mProgramHandle, "u_Touch0");
		if(motionEvent == null) {
			GLES20.glUniform2f(mTouch0Handle, -1.0f, -1.0f);
		} else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
				motionEvent = null;
				GLES20.glUniform2f(mTouch0Handle, -1.0f, -1.0f);
		} else {//for(int p = 0; p < motionEvent.getPointerCount(); p++)
			//switch(motionEvent.getPointerCount()) {
			//case 1:
				GLES20.glUniform2f(mTouch0Handle, motionEvent.getX(0) / displayWidth, 0.0f);
			//}
		}

		// Pass in the texture information
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glUniform1i(mColorSwathHandle, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mColorSwathHandle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glUniform1i(mNoiseHandle, 1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNoiseHandle);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "vPosition"); // TODO

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
