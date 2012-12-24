package edu.stanford.nicd.chroma;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Context;

public class WallpaperHarness extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}

class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new MyTestRenderer(context));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}

class MyTestRenderer extends MyRenderer implements GLSurfaceView.Renderer {
	private long lastMeterTime = 0;
	private int lastMeterFrame = 0;
	
	public MyTestRenderer(Context context) {
		super(context);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		lastMeterFrame++;
		if(System.currentTimeMillis() - lastMeterTime >= 1000) {
			float FramesPerSecond = lastMeterFrame / ((System.currentTimeMillis() - lastMeterTime) / 1000.0f);
			Log.i("WallpaperHarness", "FPS: " + FramesPerSecond);
			lastMeterTime = System.currentTimeMillis();
			lastMeterFrame = 0;
		}
		mBackground.draw();
	}
	
}