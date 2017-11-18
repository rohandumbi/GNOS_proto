package com.org.gnos.licensing;

public class GNOSLicense {

	static final boolean DEV_MODE = true;
	static Keylok lock = new Keylok();
	
	public static void initialize() throws Exception {
		lock.Main();
		isValid();
	}
	public static boolean isValid() throws Exception{
		if(DEV_MODE) return true;
    	boolean hasDevice = lock.KLCheck();
    	if(hasDevice) {
    		lock.ReadAuth();
    		try{
    			return lock.CkLeaseExpiration_Custom();

    		} catch (Exception e){
    			throw new Exception("License has expired");
    		} 		
    	} else {
    		throw new Exception("Dongle not attached");
    	}
	}
}
