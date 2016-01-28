package com.org.gnos.events;

import java.util.EventObject;

public class ChildScreenEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	public String eventName;

	public ChildScreenEvent(Object source, String eventName) {
		super(source);
		this.eventName = eventName;
	}

}
