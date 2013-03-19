package edu.stanford.nicd.chroma;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import net.rbgrn.android.glwallpaperservice.*;

import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;

public class MyRenderer implements GLWallpaperService.Renderer {

	private Context context;
	protected ChromaBackground mBackground;
	private ChromaSprite mSprites;

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
			try {
				mSprites = new ChromaSprite(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void onDrawFrame(GL10 unused) {
		//mBackground.draw();
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


