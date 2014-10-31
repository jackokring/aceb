package com.github.jackokring.aceb;

public class S {

	char[] back;
	char len;
	char start;
	
	public S(Tester m, char i) {
		back = m.m;
		len = back[i];
		start = i++;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < len; i++) {
			s.append(back[start + i]);
		}
		return s.toString();
	}

	public int length() {
		return len;
	}

	public char charAt(int i) {
		return back[start + i];
	}
}
