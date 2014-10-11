package com.github.jackokring.aceb;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Joy implements Runnable, SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int value;

	public Joy(Desktop desktop) {
		mSensorManager = (SensorManager) desktop.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		pause(false);
	}

	@Override
	public void run() {
		if(mSensor == null) return;
		
		// TODO Auto-generated method stub

	}

	public void pause(boolean b) {
		if(mSensor == null) return;
		if(b) mSensorManager.unregisterListener(this);
		else mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public int get() {
		return value;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
