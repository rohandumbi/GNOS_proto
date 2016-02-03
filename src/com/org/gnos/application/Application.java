package com.org.gnos.application;

import org.eclipse.swt.widgets.Display;

import com.org.gnos.ui.screens.prototypes.HelloWorld;
import com.org.gnos.ui.screens.prototypes.MultiScreenTest;
import com.org.gnos.ui.screens.prototypes.ScreenController;
import com.org.gnos.ui.screens.v1.GNOSApplication;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelloWorld oHelloWorld = new HelloWorld();
		oHelloWorld.setBlockOnOpen(true);
		/*oHelloWorld.open();*/
		
		MultiScreenTest oMultiScreenTest = new MultiScreenTest();
		oMultiScreenTest.setBlockOnOpen(true);
		/*oMultiScreenTest.open();*/
		
		ScreenController oScreenController = new ScreenController();
		oScreenController.setBlockOnOpen(true);
		//oScreenController.open();
		
		GNOSApplication oGnosApplication = new GNOSApplication();
		oGnosApplication.setBlockOnOpen(true);
		oGnosApplication.open();

		Display.getCurrent().dispose();
	}

}
