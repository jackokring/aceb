package com.github.jackokring.aceb;

public class SearchItem {
	
	protected Machine ma;
	protected String find;
	
	//TODO: extend
	public SearchItem(String name) {
		find =  name;
	}
	
	public String toString() {
		return find;
	}
	
	public void setMachine(Machine m) {
		ma = m;
	}
	
	public Machine getMachine() {
		return ma;
	}
}
