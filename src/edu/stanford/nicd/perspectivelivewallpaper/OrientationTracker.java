package edu.stanford.nicd.perspectivelivewallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

public class OrientationTracker implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private LowPassFilter AccelFilter = new LowPassFilter();
	private LowPassFilter MagFilter = new LowPassFilter();

	private float[] initialOrientation;
	private float[] currentOrientationDifference = new float[3];

	public OrientationTracker(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public boolean onResume() {
		AccelFilter = new LowPassFilter();
		MagFilter = new LowPassFilter();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
		return true;
	}

	public boolean onPause() {
		mSensorManager.unregisterListener(this);
		initialOrientation = null;
		currentOrientationDifference = new float[3];
		return true;
	}

	// TODO: handle landscape.
	public void update(float strength) {
		if (AccelFilter.GetFiltered() == null || MagFilter.GetFiltered() == null)
			return;
		AccelFilter.Update(strength);
		MagFilter.Update(strength);
		float[] mR = new float[9];
		SensorManager.getRotationMatrix(mR, null, AccelFilter.GetFiltered(), MagFilter.GetFiltered());
		float[] OrientationDifference = new float[3];
		SensorManager.getOrientation(mR, OrientationDifference);
		if(initialOrientation == null) {
			initialOrientation = new float[3];
			System.arraycopy(OrientationDifference, 0, initialOrientation, 0, 3);
		}
		for(int i=0; i<3; i++)
			OrientationDifference[i] = OrientationDifference[i] - initialOrientation[i];
		currentOrientationDifference = OrientationDifference;
	}

	public float[] getOrientationDifference() {
		return currentOrientationDifference;
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer)
			AccelFilter.AddSample(event.values);
		else if (event.sensor == mMagnetometer)
			MagFilter.AddSample(event.values);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	class LowPassFilter {
		private final int ARRAY_LENGTH = 3;
		//private final float FILTER_STRENGTH = .95f;

		private float[] filteredData;
		private float[] lastData = new float[ARRAY_LENGTH];

		public void AddSample(float[] sample) {
			if(filteredData == null) {
				filteredData = new float[ARRAY_LENGTH];
				System.arraycopy(sample, 0, filteredData, 0, ARRAY_LENGTH);
				return;
			}
			System.arraycopy(sample, 0, lastData, 0, ARRAY_LENGTH);
		}
		
		public void Update(float strength) {
			if(filteredData == null)
				return;
			for(int i=0; i<ARRAY_LENGTH; i++)
				filteredData[i] = filteredData[i] * (1 - strength) + lastData[i] * strength;
		}

		public float[] GetFiltered() {
			return filteredData;
		}	
	}
}


