package com.org.gnos.application;

import org.eclipse.swt.widgets.Display;

import com.org.gnos.ui.screens.HelloWorld;
import com.org.gnos.ui.screens.MultiScreenTest;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*HelloWorld oHelloWorld = new HelloWorld();
		oHelloWorld.setBlockOnOpen(true);
		oHelloWorld.open();*/
		
		MultiScreenTest oMultiScreenTest = new MultiScreenTest();
		oMultiScreenTest.setBlockOnOpen(true);
		oMultiScreenTest.open();

		Display.getCurrent().dispose();
	}

}
