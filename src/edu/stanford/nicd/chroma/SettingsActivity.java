package edu.stanford.nicd.chroma;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		SeekBar seekBarSpeed = (SeekBar) findViewById(R.id.seekBarThrottle);
		seekBarSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {       
				MyRenderer.FPS_THROTTLE = 100 - progress;
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}  
		});
		seekBarSpeed.setProgress(40);
	}

}
