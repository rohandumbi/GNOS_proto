package com.org.gnos.licensing;

public class GNOSLicense {

	static Keylok lock = new Keylok();
	
	static {
		System.out.println("Inside keylock static code");
		lock.Main();
	}
	public static boolean isValid() {
			
    	boolean hasDevice = lock.KLCHECK();
    	if(hasDevice) {
    		return lock.CkLeaseExpiration();
    		
    	}
    	return false;
	}
}
