package com.org.gnos.licensing;

import com.sun.jna.Library;

public interface CInterface extends Library
{  
	public int KEYBD(int iVar);
	public int KFUNC(int iVarA, int iVarB, int iVarC, int iVarD);
	public void KGETGUSN (byte[] pArray);
}
