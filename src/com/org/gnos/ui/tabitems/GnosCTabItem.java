package com.org.gnos.ui.tabitems;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

public abstract class GnosCTabItem extends CTabItem{

	public GnosCTabItem(CTabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	public abstract void createContent(CTabFolder parent) throws Exception;

}
