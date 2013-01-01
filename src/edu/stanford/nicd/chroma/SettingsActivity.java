package edu.stanford.nicd.chroma;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity  {

	private GLSurfaceView mGLView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the LW as background.
		mGLView = new GLSurfaceView(this);
		mGLView.setEGLContextClientVersion(2);
		mGLView.setRenderer(new MyHarnessedRenderer(getApplicationContext()));
		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setContentView(mGLView);

		// Inflate the xml and overlay it.
		View overlay = getLayoutInflater().inflate(R.layout.settings_layout, null);
		addContentView(overlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		SeekBar seekBarSpeed = (SeekBar) findViewById(R.id.seekBarThrottle);
		seekBarSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {       
				//MyRenderer.FPS_THROTTLE = 100 - progress;
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}  
		});
		//seekBarSpeed.setProgress(40);
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

	class MyHarnessedRenderer extends MyRenderer implements GLSurfaceView.Renderer {
		private long lastMeterTime = 0;
		private int lastMeterFrame = 0;

		WatcherThread watcher;

		public MyHarnessedRenderer(Context context) {
			super(context);
			watcher = new WatcherThread();
			watcher.start(); // TODO: kill on
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

		class WatcherThread extends Thread {

			public boolean kill = false;
			private final int FPS_THROTTLE = 60;

			@Override
			public void run() {
				while(true) {
					if(kill)
						return;
					mGLView.requestRender();
					try {
						Thread.sleep(FPS_THROTTLE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
