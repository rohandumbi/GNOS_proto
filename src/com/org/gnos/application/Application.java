package com.org.gnos.application;

import org.eclipse.swt.widgets.Display;

import com.org.gnos.ui.screens.HelloWorld;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelloWorld oHelloWorld = new HelloWorld();
		oHelloWorld.setBlockOnOpen(true);
		
		oHelloWorld.open();

		Display.getCurrent().dispose();
	}

}
