package com.org.gnos.tabitems;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class GnosTabItem extends TabItem{

	public GnosTabItem(TabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	protected abstract void createContent(TabFolder parent);

}
