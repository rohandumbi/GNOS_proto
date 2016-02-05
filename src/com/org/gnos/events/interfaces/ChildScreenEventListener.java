package com.org.gnos.events.interfaces;

import java.util.EventListener;
import com.org.gnos.events.BasicScreenEvent;

public interface ChildScreenEventListener extends EventListener {
	public void onChildScreenEventFired(BasicScreenEvent e);
}
