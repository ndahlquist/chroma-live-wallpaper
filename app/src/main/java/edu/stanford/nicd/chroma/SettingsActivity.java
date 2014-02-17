package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import javax.microedition.khronos.opengles.GL10;

public class SettingsActivity extends Activity {

    private static final String FPS_THROTTLE = "fpsThrottle";

    private GLSurfaceView mGLView;
    private MyHarnessedRenderer mRenderer;

    SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

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
        addContentView(overlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        SeekBar speedSeekbar = (SeekBar) findViewById(R.id.seekBarThrottle);
        speedSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putInt(FPS_THROTTLE, 100 - progress);
                editor.commit();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        int fpsThrottle = mSharedPrefs.getInt(FPS_THROTTLE, 40);
        speedSeekbar.setProgress(100 - fpsThrottle);

        Spinner colorSpinner = (Spinner) findViewById(R.id.color_spinner);
        ArrayAdapter<CharSequence> colorSpinnerAdapter = ArrayAdapter
                .createFromResource(this, R.array.color_choices, R.layout.spinner_layout);
        colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorSpinnerAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRenderer != null) { mRenderer.close(); }
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
                while (true) {
                    if (kill) { return; }
                    if (mGLView != null) { mGLView.requestRender(); }
                    int fpsThrottle = mSharedPrefs.getInt(FPS_THROTTLE, 40);
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
