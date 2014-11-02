package com.github.jackokring.aceb;

public class LFO extends S {
	
	S lfo;
	int f, l, v;
	
	LFO(Tester m, char i, S using) {
		super(m, i);
		lfo = using;
	}
	
	public LFO(String s, String using) {
		super(s);
		lfo = new S(using);
	}
	
	public void setLFO(S in) {
		lfo = in;
	}
	
	@Override
	public char charAt(int i) {
		char c = super.charAt(i);
		char mod = lfo.charAt(i % lfo.len);
		f += mod;
		l += (mod >> 7);
		v += (mod >> 11);
		f &= 127;
		l &= 15;
		v &= 15;
		boolean apply = (mod & 0x8000) == 0;
		if(apply) {
			int x = c + f;
			x &= 127;//fmod
			c |= ~127 | x;
			
			x = c + (l << 7);
			x &= (15 << 7);//len mod
			c |= ~(15 << 7) | x;
			
			x = c + (v << 11);
			x &= (15 << 11);//vol mod
			c |= ~(15 << 11) | x;
		}
		return c;
	}
}