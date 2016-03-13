package com.org.gnos.utilities;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class ClickBehavior extends MouseAdapter {
	private final Runnable action;
	private boolean armed = false;
	public static final int LEFT_BUTTON = 1;

	public ClickBehavior( Runnable action ) {
		this.action = action;
	}
	@Override
	public void mouseDown( MouseEvent event ) {
		// TODO: decent implementation
		if( event.button == LEFT_BUTTON ) {
			armed = true;
		}
	}
	@Override
	public void mouseUp( MouseEvent event ) {
		// TODO: decent implementation
		if( armed && inRange( event ) ) {
			action.run();
		}
		armed = false;

	}

	static boolean inRange( MouseEvent event ) {
		Point size = ((Control)event.widget).getSize();
		return event.x >= 0
			   && event.x <= size.x
			   && event.y >= 0
			   && event.y <= size.y;
	}

}
