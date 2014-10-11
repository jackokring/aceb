package com.github.jackokring.aceb;

import android.content.Context;
import android.hardware.*;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;

public class Joy implements SensorEventListener, OnClickListener {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int value, rotation;
	private boolean fire = false;

	public Joy(Desktop desktop) {
		mSensorManager = (SensorManager) desktop.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		pause(false);
		rotation = desktop.getWindowManager().getDefaultDisplay().getRotation();//on reconstruction
		desktop.findViewById(R.id.font).setOnClickListener(this);
	}

	public void pause(boolean b) {
		if(mSensor == null) return;
		if(b) mSensorManager.unregisterListener(this);
		else mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public int get() {
		//TODO: process fire and accel to one int
		fire = false;//reset it
		return value;
	}
	
	float[] gravity = new float[3];
	float[] linear = new float[3];
	float[] grav = new float[2];
	float[] lin = new float[2];
			
	@Override
	public void onSensorChanged(SensorEvent event) {

		final float alpha = (float)0.8;
	
		// Isolate the force of gravity with the low-pass filter.
		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * -event.values[1];//coordinate system
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
	
		// Remove the gravity contribution with the high-pass filter.
		linear[0] = event.values[0] - gravity[0];
		linear[1] = -event.values[1] - gravity[1];
		linear[2] = event.values[2] - gravity[2];
		  
		//at natural rotation
		switch(rotation) {
		case Surface.ROTATION_90:
			grav[0] = gravity[1];
			grav[1] = -gravity[0];
			lin[0] = linear[1];
			lin[1] = -linear[0];
			break;
		case Surface.ROTATION_180:
			grav[0] = -gravity[0];
			grav[1] = -gravity[1];
			lin[0] = -linear[0];
			lin[1] = -linear[1];
			break;
		case Surface.ROTATION_270:
			grav[0] = -gravity[1];
			grav[1] = gravity[0];
			lin[0] = -linear[1];
			lin[1] = linear[0];
			break;
		default:
			grav[0] = gravity[0];
			grav[1] = gravity[1];
			lin[0] = linear[0];
			lin[1] = linear[1];
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing
		
	}

	@Override
	public void onClick(View v) {
		fire = true;
	}
}
