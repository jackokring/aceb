package com.github.jackokring.aceb;

import android.content.Context;
import android.hardware.*;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;

public class Joy implements SensorEventListener, OnClickListener {
	
	SensorManager mSensorManager;
	Sensor mSensor;
	int rotation;
	boolean fire = false;
	Desktop d;

	public Joy(Desktop desktop) {
		d = desktop;
		mSensorManager = (SensorManager) desktop.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		rotation = desktop.getWindowManager().getDefaultDisplay().getRotation();//on reconstruction
		d.findViewById(R.id.font).setOnClickListener(this);
	}

	public void pause(boolean b) {
		if(mSensor == null) return;
		if(b) {
			mSensorManager.unregisterListener(this);
		} else {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	char[] mask = {
		1, //X+
		2, //Y+
		4, //Z+
		8, //X-
		16, //Y-
		32, //Z-
		64 //F
	};

	public char get() {
		int g = d.sp.getInt("pref_joy", 1);
		char value = 0;
		grav[0] = g * grav[0];
		grav[1] = g * grav[1];
		for(int i = 0; i < grav.length; i++) {
			if(grav[i] > 4) value |= mask[i];
			if(grav[i] < 4) value |= mask[i + grav.length];
		}
		if(fire) value |= mask[grav.length << 1];
		
		fire = false;//reset it
		return value;
	}
	
	float[] gravity = new float[3];
	float[] linear = new float[3];
	float[] grav = new float[3];
	float[] lin = new float[3];
			
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
		grav[2] = gravity[2];
		lin[2] = linear[2];
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
