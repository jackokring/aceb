package com.github.jackokring.aceb;

public class Rel {

	float x[] = new float[2];
	float vx[] = new float[2];
	float m;
	static final float g = 1;
	float theta;
	float gCalc = 1;
	
	static Rel rest;//object which is at rest
	
	public float[] vDial() {
		float tmp[] = new float[2];
		for(int i = 0; i < 2; i++) {
			tmp[i] = vx[i] - rest.vx[i];
			tmp[i] *= tmp[i];
			tmp[i] = 1 - tmp[i];
			tmp[i] = (float)Math.sqrt(tmp[i]);
		}
		return tmp;
	}
	
	public float gDial() {
		if(rest == this) return 1;
		float tmp[] = new float[2];
		float r[] = vDial();
		float rr = 0;
		float tmp2;
		for(int i = 0; i < 2; i++) {
			tmp[i] = x[i] - rest.x[i];
			tmp[i] *= r[i];
			rr += tmp[i] * tmp[i];
		}
		rr = (float)Math.sqrt(rr);
		//collide? 
		tmp2 = 1 - 2 * g * m / rr;
		//calculate g dilate
		return (float)Math.sqrt(tmp2);//total dilation;
	}	
	
	public void gCalc() {
		float gSum = 1;
		Rel tmp = rest;
		rest = this;
		while(/*test*/) {
			gSum *= gDial();//for each
		}
		rest = tmp;
		gCalc = gSum;
	}
}
