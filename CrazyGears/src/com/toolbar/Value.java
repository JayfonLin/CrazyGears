package com.toolbar;

public class Value {
	private boolean isNewGear;
	
	Value() {
		isNewGear = false;
	}
	public void invertValue(){
		if(isNewGear){
			isNewGear = false;
		}
		else{
			isNewGear = true;
		}
	}
	public boolean  getValue(){
		return isNewGear;
	}
}
