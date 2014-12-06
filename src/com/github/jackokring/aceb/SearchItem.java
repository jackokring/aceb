package com.github.jackokring.aceb;

import com.android.vending.Purchase;

public class SearchItem {
	
	protected Machine ma;
	protected String find;
	protected boolean notOwned = false;
	protected Purchase p;
	protected int SKU;
	
	//TODO: extend
	public int getSKU() {
		return SKU;
	}
	
	public boolean payMany() {
		return notOwned;
	}
	
	public Purchase getPurchase() {
		return p;
	}
	
	public void setPurchase(Purchase pp) {
		p = pp;
	}
	
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
