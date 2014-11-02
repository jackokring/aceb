package com.github.jackokring.aceb;

public class S {

	char[] back;
	char len;
	char start;
	
	S(Tester m, char i) {
		synchronized(m) {
			back = m.m;
			len = back[i];
			start = i++;
		}
	}
	
	public S(String s) {
		fromString(s);
	}
	
	public synchronized void fromString(String s) {
		char len2 = (char)s.length();
		len = 0;
		back = new char[len2];
		start = 0;
		for(int i = 0; i < len2; i++)
			back[i] = s.charAt(i);
		len = len2;
	}
	
	@Override
	public synchronized String toString() {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < len; i++) {
			s.append(back[start + i]);
		}
		return s.toString();
	}

	public synchronized int length() {
		return len;
	}

	public synchronized char charAt(int i) {
		return back[start + i];
	}
	
	public synchronized void putAt(char c, int i) {
		if(i >= len || i < 0) throw new RuntimeException("Audio error");
		back[start + i] = c;
	}
}
