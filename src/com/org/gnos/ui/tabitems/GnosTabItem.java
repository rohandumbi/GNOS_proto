package com.org.gnos.ui.tabitems;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class GnosTabItem extends TabItem{

	public GnosTabItem(TabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	public abstract void createContent(TabFolder parent);

}
