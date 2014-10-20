package com.github.jackokring.aceb;

public class SearchItem {
	
	Machine ma;
	
	//TODO: extend
	public String toString() {
		return ma.getClass().getSimpleName();
	}
	
	public void setMachine(Machine m) {
		ma = m;
	}
	
	public Machine getMachine() {
		return ma;
	}
}
