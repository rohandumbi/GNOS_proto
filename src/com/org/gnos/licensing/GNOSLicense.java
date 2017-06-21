package com.org.gnos.licensing;

public class GNOSLicense {

	static Keylok lock = new Keylok();
	
	static {
		System.out.println("Inside keylock static code");
		lock.Main();
	}
	public static boolean isValid() throws Exception{
			
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
