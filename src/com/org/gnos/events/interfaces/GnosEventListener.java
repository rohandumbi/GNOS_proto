package com.org.gnos.events.interfaces;

import java.util.EventListener;
import com.org.gnos.events.GnosEvent;

public interface GnosEventListener extends EventListener {
	public void onGnosEventFired(GnosEvent e);
}
