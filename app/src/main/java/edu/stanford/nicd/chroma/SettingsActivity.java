package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import javax.microedition.khronos.opengles.GL10;

public class SettingsActivity extends Activity  {

	private GLSurfaceView mGLView;
	private MyHarnessedRenderer mRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the LW as background.
		mGLView = new GLSurfaceView(this);
		mRenderer = new MyHarnessedRenderer(getApplicationContext());
		mGLView.setEGLContextClientVersion(2);
		mGLView.setRenderer(mRenderer);
		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setContentView(mGLView);
		mRenderer.start();

		// Inflate the xml and overlay it.
		View overlay = getLayoutInflater().inflate(R.layout.settings_layout, null);
		addContentView(overlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		SeekBar seekBarSpeed = (SeekBar) findViewById(R.id.seekBarThrottle);
		seekBarSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {       
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("fpsThrottle", 100 - progress);
				editor.commit();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}  
		});
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int fpsThrottle = prefs.getInt("fpsThrottle", 40);
		seekBarSpeed.setProgress(100 - fpsThrottle);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mRenderer != null)
			mRenderer.close();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	class MyHarnessedRenderer extends MyRenderer implements GLSurfaceView.Renderer {

		public MyHarnessedRenderer(Context context) {
			super(context);
		}

		WatcherThread watcher;
		
		public void start() {
			watcher = new WatcherThread();
			watcher.start();
		}
		
		public void close() {
			super.close();
			watcher.kill = true;
		}

		@Override
		public void onDrawFrame(GL10 unused) {
			mBackground.draw();
		}

		class WatcherThread extends Thread {

			public boolean kill = false;

			@Override
			public void run() {
				while(true) {
					if(kill)
						return;
					if(mGLView != null)
						mGLView.requestRender();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					int fpsThrottle = prefs.getInt("fpsThrottle", 40);
					try {
						Thread.sleep(fpsThrottle);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
