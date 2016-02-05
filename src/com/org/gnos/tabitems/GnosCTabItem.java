package com.org.gnos.tabitems;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TabFolder;

public abstract class GnosCTabItem extends CTabItem{

	public GnosCTabItem(CTabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	public abstract void createContent(CTabFolder parent);

}
