package com.github.jackokring.aceb;

import com.android.vending.Purchase;

public class BoughtItem extends SearchItem {

	public BoughtItem(String name) {
		super(name);
	}

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
}
