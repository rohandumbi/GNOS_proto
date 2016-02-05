package com.org.gnos.events;

import java.util.HashMap;
import java.util.Map;

public class GnosEventWithAttributeMap extends GnosEvent {

	public Map<String, String> attributes = new HashMap<String, String>();
	
	public GnosEventWithAttributeMap(Object source, String eventName,HashMap<String, String> attributes) {
		super(source, eventName);
		// TODO Auto-generated constructor stub
		this.attributes = attributes;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
