package com.org.gnos.events.interfaces;

import java.util.EventListener;
import com.org.gnos.events.ChildScreenEvent;

public interface ChildScreenEventListener extends EventListener {
	public void onChildScreenEventFired(ChildScreenEvent e);
}
