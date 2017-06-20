/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.org.gnos.licensing;

import java.io.DataInputStream;
//import java.lang.annotation.*;
import java.util.HashMap;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class Keylok {

    // UNIQUE TO DEVICE DATA: The below codes are for the demonstration device only.
    // Unique codes will be assigned to your company when you purchase devices.
    static final int ValidateCode1 = 0X619A; // Codes used as part of active algorithm to   
    static final int ValidateCode2 = 0XE852; // check for installation of proper device 
    static final int ValidateCode3 = 0X98C2;                                             
    static final int ClientIDCode1 = 0X6894;                                            
    static final int ClientIDCode2 = 0X4F28;                                            
    static final int ReadCode1     = 0X65B;  // Codes used to authorize read operations  
    static final int ReadCode2     = 0X74F0;                                            
    static final int ReadCode3     = 0XEE8F;                                             
    static final int WriteCode1    = 0X80B6;  // Codes used to authorize write operations
    static final int WriteCode2    = 0X2DA0;                                            
    static final int WriteCode3    = 0X585C;     

    
    // COMMAND CODES for KFUNC operations:
    static final int TERMINATE     = -1;
    static final int KLCHECK       = 1;
    static final int READAUTH      = 2;
    static final int GETSN         = 3;
    static final int GETVARWORD    = 4;
    static final int WRITEAUTH     = 5;
    static final int WRITEVARWORD  = 6;
    static final int DECREMENTMEM  = 7;
    static final int GETEXPDATE    = 8;
    static final int CKLEASEDATE   = 9;
    static final int SETEXPDATE    =10;
    static final int SETMAXUSERS   =11;
    static final int GETMAXUSERS   =12;
    static final int REMOTEUPDUPT1 =13;
    static final int REMOTEUPDUPT2 =14;
    static final int REMOTEUPDUPT3 =15;
    static final int REMOTEUPDCPT1 =16;
    static final int REMOTEUPDCPT2 =17;
    static final int REMOTEUPDCPT3 =18;
    static final int GETDONGLETYPE =33;
    static final int CKREALCLOCK   =82;


    // KEYBD Function Argument
    private static int LAUNCHANTIDEBUGGER = 0; /* Launch Anti-debugger PPMON.EXE */

   // LEASE EXPIRATION & REMOTE UPDATE CONSTANTS
    static final int BASEYEAR           = 1990; // Reference for expiration dates
    static final int LEASEEXPIRED       = 0xFFFE;
    static final int SYSDATESETBACK     = 0xFFFD;
    static final int NOLEASEDATE        = 0xFFFC;
    static final int LEASEDATEBAD       = 0xFFFB;
    static final int LASTSYSDATECORRUPT = 0xFFFA;

    static final int KEY_ERROR_NOERROR = 0;

    // REMOTE UPDATE TASK CODES
    static final int REMOTEADD          = 0;
    static final int REMOTEDATEEXTEND   = 1;
    static final int REMOTEOR           = 2;
    static final int REMOTEREPLACE      = 3;
    static final int REMOTEGETMEMORY    = 4;
    static final int REMOTESETUSERCT    = 5;
    static final int REMOTEGETUSERCT    = 6;
    static final int REMOTEGETDATE      = 7;
    static final int REMOTEINVALID      = 8;

    // COUNTER DECREMENT RETURN CODES
    static final int VALIDCOUNT = 0;
    static final int NOCOUNTSLEFT = 1;
    static final int INVALIDADDRESS = 2;
    static final int NOWRITEAUTH = 3;

    //RTC Error Codes
    static final int KEY_ERROR_NO_REALCLOCK =0x20000018;  // No RTC on board
    static final int KEY_ERROR_RTC_NO_POWER =0x20000019;  // RTC has been powered down (battery has lost power)



    static public int MenuSize = 1;
    public String libName;
    public int KFUNCErrorReturn;


    public interface CInterface extends Library
    {  
    	public int KEYBD(int iVar);
    	public int KFUNC(int iVarA, int iVarB, int iVarC, int iVarD);
    	public void KGETGUSN (byte[] pArray);
    }

    
    ////////////////////////////////////////////////////////////////////////
    //
    //    JMain()
    //
    //    Loads our DLL called using JNI - Java Native Interface
    //
    ////
    public void Main()
    {
         libName = GetArch();
    }

   ////////////////////////////////////////////////////////////////////////
    //
    //    GetInt()
    //
    ////
    private int GetInt()
    {
        DataInputStream din = new DataInputStream(System.in);
        String instr;
        int Value=0;

        try
        {

          instr = din.readLine();
          Integer n = new Integer(instr);
          Value = n.intValue();

        }

        catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
        return Value;
    }

        ////////////////////////////////////////////////////////////////////////
    //
    //    GetKey()
    //
    //
    //
    ////
    public static void GetKey()
    {
        try
        {
          byte bArray[] = new byte[128];
          System.out.println();
          System.out.println("Press ENTER key when ready to continue.");
          System.in.read(bArray);
        }
        catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    GetResponse()
    //
    //
    //
    ////
    public static int GetResponse()
    {
        byte bArray[] = new byte[128];
        int Value;

        try
        {
          System.in.read(bArray);
        }
        catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
        Value = (int) bArray[0];
        if (Value > 'Z')
           Value-=32;
        return Value;
    }


    public String GetArch()
    {
          String osName = System.getProperty("os.name");
          String osArch = System.getProperty("os.arch");
          System.out.println(osName + " " + osArch) ;

          if (osName.startsWith("Windows"))
          {
            if (osArch.indexOf("64")>-1)
                libName = "kl2dll64";
            else
                libName = "kl2dll32";
          }
          else
          {
             if (osArch.indexOf("64")>-1)
             {
                libName = "kfunc64";
                if (Platform.isLinux())
                    {
                        System.setProperty("jna.library.path","/usr/lib64");
                    }
             }
             else
             {
                libName = "kfunc32";
                if (Platform.isLinux())
                    {
                        System.setProperty("jna.library.path","/usr/lib");
                    }
             }
          }
          System.out.println(libName);
          return libName;
    }

    
    ///////////////////////////////////////////////////////////////////////
    //
    //   KTASK
    //
    //       Sends a Keylok Cmnd and three args to KFUNC in KL2DLL32.DLL
    //       Returns the 32 bit value from the DLL call.
    //
    ////
    public int KTASK(int P1, int P2, int P3, int P4)
    {

        int RetVal32 = 0;
          int KbStatus;
          int Arg1, Arg2, Arg3, Arg4;
          CInterface demo;
          try
          {
        	  demo = (CInterface) Native.loadLibrary(libName, CInterface.class);
        	  
          if (P1 < 0)
             Arg1 = P1 + 65536;
          else
             Arg1 = P1;

          if (P2 < 0)
             Arg2 = P2 + 65536;
          else
             Arg2 = P2;

          if (P3 < 0)
             Arg3 = P3 + 65536;
          else
             Arg3 = P3;

          if (P4 < 0)
             Arg4 = P4 + 65536;
          else
             Arg4 = P4;

          //KEYBD( LAUNCHANTIDEBUGGER );               // Launch anti-debugger

          RetVal32 = demo.KFUNC(Arg1, Arg2, Arg3, Arg4);
          }
            catch (Throwable t) {
            System.out.println("Library " + libName + " not found.  Application ending.") ;
            System.exit(1);
          }
          return RetVal32;
     }

    ///////////////////////////////////////////////////////////////////////
    //
    //   ReadAuth()
    //
    //       Read Authorization
    ////
    public void ReadAuth()
    {
    	KTASK(READAUTH, ReadCode1, ReadCode2, ReadCode3);
    	System.out.println("Read Authorization sequence has been sent.");
    }
    
    ///////////////////////////////////////////////////////////////////////
    //
    //   WriteAuth()
    //
    //       Write Authorization
    ////
    public void WriteAuth()
    {
    	KTASK(WRITEAUTH, WriteCode1, WriteCode2, WriteCode3);
        System.out.println("Write Authorization sequence has been sent.");
    }
    
    public void GetGlobalID()
    {
          byte buffer[] = new byte[8];
          CInterface demo;
          try
          {
            if (libName.equals("kl2dll32")){
                demo = (CInterface) Native.loadLibrary(libName, CInterface.class,
                    new HashMap() {{ put("KGETGUSN","_KGETGUSN@4"); }});
            }
            else{
                demo = (CInterface) Native.loadLibrary(libName, CInterface.class);
            }
            demo.KGETGUSN(buffer);
            String myGUSN = "";
            String myHiGUSN = "";
            String myLoGUSN = "";
            for (int i = buffer.length; i>4; i--)
            {
                myLoGUSN += Integer.toString((buffer[i-1] & 0xff ) + 0x100, 16).substring(1);
            }
            for (int i = (buffer.length-4); i>0; i--)
            {
                myHiGUSN += Integer.toString((buffer[i-1] & 0xff ) + 0x100, 16).substring(1);
            }
            myGUSN = myHiGUSN + myLoGUSN;
            System.out.println(String.format("Globally Unique Serial #: %s", myGUSN));
          }
            catch (Throwable t) {
            System.out.println("Library " + libName + " not found.  Application ending.") ;
            System.exit(1);
          }
          return;
     }


    ////////////////////////////////////////////////////////////////////////
    //
    //    RotateLeft)
    //
    //        Rotates the bits in the target left the number of positions
    //       identified by the argument Counts.
    //
    ////
    public short RotateLeft( int Target, int Counts)
    {
        int i;
        int LocalTarget;
        int HighBit;

        LocalTarget = Target;
          for (i=0; i<Counts; i++)
        {
            HighBit = LocalTarget & 0X8000;
            LocalTarget = (LocalTarget << 1) + (HighBit >> 15);
        }
        return (short)(LocalTarget & 0xffff);
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    SetLeaseExpirationDate()
    //
    //
    ////
    public void SetLeaseExpirationDate()
    {
      int Month = 0, Day = 0, Year = 0, chResponse;
      int WriteDate;
//      WriteAuth();
      //Get the desired lease expiration date
      System.out.println("Please input the lease expiration date to be programmed into the KEYLOK");
      while ((Month < 1) | (Month > 12)) {
        System.out.println("Month of lease expiration (e.g. 1=January, 12=December)- MONTH:");
        Month = GetInt();
      }
      while ((Day < 1) | (Day > 31)) {
        System.out.println("Day-of-month of lease expiration (i.e. 1-31)           -   DAY:");
        Day = GetInt();
      }
      while ((Year < 1997) | (Year > 2050)) {
        System.out.println("Year of lease expiration (e.g. '1994')                 -  YEAR:");
        Year = GetInt();
      }

      //Confirm proper lease expiration date entered
      ClearScreen();
      System.out.println("You have entered a lease expiration date of: " + Month + "/" + Day + "/" + Year);
      System.out.println ("Is this correct? (Y or N)");

      chResponse = GetResponse();
      if ( chResponse == 'Y') {

        ClearScreen();
        //Convert the input expiration date into the proper storage format
        //Date will be stored in YYYYYYYMMMMDDDDD format with YYYYYY being the
        //number of years after BASEYEAR (e.g. YYYYYYY = 3 for 1993 with base 1990)
        WriteDate = 512 * (Year - BASEYEAR) + (32 * Month) + Day;

        //Write the expiration date into the KEYLOK
        KTASK(SETEXPDATE, 0, WriteDate, 0);
      }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    SetLeaseExpirationDate()
    //
    //
    ////
    public void ClearLeaseExpirationDate()
    {
      int chResponse;

      //Confirm proper lease expiration date entered
      ClearScreen();

      System.out.println ("Are you sure you want to clear the expiration date? (Y or N)");

      chResponse = GetResponse();
      if ( chResponse == 'Y') {
        KTASK(SETEXPDATE, 0, 0, 0);
        }

      ClearScreen();
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    CkLeaseExpiration()
    //
    //
    ////
    public void CkLeaseExpiration()
    {
      int DateRead;
      int SystemYear, SystemMonth, SystemDay;
      int ExpYear, ExpMonth, ExpDay;
      int RetVal1, RetVal2;

      ClearScreen();
      // Check for expiration of lease for product whose expiration date is stored
      // within the KEYLOK device.
      RetVal1 = KTASK(CKLEASEDATE, 0, 0, 0);
      RetVal2 = (RetVal1 >> 16) & 0xffff;
      RetVal1 = RetVal1 & 0xffff;
      switch(RetVal2) {
        case LEASEEXPIRED: // Lease has expired
          // Display Message Indicating Lease has Expired
          System.out.println("The lease associated with the use of this software has expired.");
          break;
        case SYSDATESETBACK: // The system date is earlier than one saved in KEYLOK
          System.out.println("WARNING: The system clock has been set back to an earlier date.");
          break;
        case NOLEASEDATE: // No lease date has been programmed
          System.out.println("The KEYLOK has not been programmed with a lease expiration date.");
          break;
        case LEASEDATEBAD: // An invalid lease date exists
          System.out.println("The programmed lease expiration date is in the past.");
          System.out.println("Please reprogram with a future date.");
          break;
        case LASTSYSDATECORRUPT: // Last system date corrupt as stored in device
          System.out.println("The 'last system date' as stored in KEYLOK is corrupt.");
          break;
        default: // Lease has not expired
          // Convert Current Date to Readable Format
          DateRead = RetVal1; // YYYYYYYM MMMDDDDD
          // Display approximate number of days until lease expires
          // The number of days is computed by comparing the Aggregate Days
          // associated with both the system date and lease expiration dates.
          // Aggregate days is computed by multiplying 365 x # years, plus
          // 30 x # months, plus the day-of-the-month, plus 1 day for each
          // quarter within a partial year.  As the two dates come closer
          // together the accuracy of the computation generally improves.
          // If more accuracy is required contact Microcomputer Applications
          // technical support to receive a program enhancement.
          System.out.println("The software lease will expire in approximately " + RetVal2 + " days.");
          SystemYear = BASEYEAR + (int) (DateRead / 512);
          SystemMonth = (int)((DateRead & 0X1E0) / 32);
          SystemDay = DateRead & 0x1F;
          System.out.println("The system reports the date as: " + SystemMonth + "/" + SystemDay + "/" + SystemYear);

          // Read and Convert Lease Expiration Date to Readable Format from Storage Format
          RetVal1 = KTASK(GETEXPDATE, 0, 0, 0);
          RetVal2 = (RetVal1 >> 16) & 0xffff;
          RetVal1 = RetVal1 & 0xffff;
          DateRead = RetVal1;
          ExpYear = (DateRead & 0XFE00) / 512 + BASEYEAR;
          ExpMonth = (DateRead & 0X1E0) / 32;
          ExpDay = (DateRead & 0X1F);
          System.out.println("The lease is set to expire on : " + ExpMonth + "/" + ExpDay + "/" + ExpYear);
          break;
      }
    }
    
    ////////////////////////////////////////////////////////////////////////
    //
    //    CkLeaseExpiration() - custom
    //
    //
    ////
    public boolean CkLeaseExpiration_Custom() throws Exception
    {
      boolean isValid = false;
      int DateRead;
      int SystemYear, SystemMonth, SystemDay;
      int ExpYear, ExpMonth, ExpDay;
      int RetVal1, RetVal2;

      ClearScreen();
      // Check for expiration of lease for product whose expiration date is stored
      // within the KEYLOK device.
      RetVal1 = KTASK(CKLEASEDATE, 0, 0, 0);
      RetVal2 = (RetVal1 >> 16) & 0xffff;
      RetVal1 = RetVal1 & 0xffff;
      switch(RetVal2) {
        case LEASEEXPIRED: // Lease has expired
          // Display Message Indicating Lease has Expired
        	throw new Exception("The lease associated with the use of this software has expired.");
        case SYSDATESETBACK: // The system date is earlier than one saved in KEYLOK
        	throw new Exception("WARNING: The system clock has been set back to an earlier date.");
        case NOLEASEDATE: // No lease date has been programmed
        	throw new Exception("The KEYLOK has not been programmed with a lease expiration date.");
        case LEASEDATEBAD: // An invalid lease date exists
        	throw new Exception("The programmed lease expiration date is in the past.Please reprogram with a future date.");
        case LASTSYSDATECORRUPT: // Last system date corrupt as stored in device
        	throw new Exception("The 'last system date' as stored in KEYLOK is corrupt.");
        default: 
        	
          // Lease has not expired
          isValid = true;
      }
      
      return isValid;
    }

        ////////////////////////////////////////////////////////////////////////
    //
    //    ClRTC()
    //
    //
    ////
    public void CkRTC()
    {
	int RetVal1 = KTASK(CKREALCLOCK, 0, 0, 0);
        System.out.println("RetVal1 " + RetVal1);
        int RetVal2 = (RetVal1 >> 16) & 0xffff;
        System.out.println("RetVal2 " + RetVal2);
        RetVal1 = RetVal1 & 0xffff;
        System.out.println("RetVal1 " + RetVal1);
        switch(RetVal1) {
        case KEY_ERROR_NOERROR: // No Errors
          // Display Message Indicating Lease has Expired
            long ms = 1000L * (RetVal2 * 65536 + RetVal1);
            long sysTime = System.currentTimeMillis();
            System.out.println("The lease associated with the use of this software has expired.");
          break;
        case KEY_ERROR_NO_REALCLOCK: 
            System.out.println("ERROR: No real time clock on the dongle.");
          break;
        case KEY_ERROR_RTC_NO_POWER: 
          System.out.println("WARNING: The real time clock has lost power.");
          break;
        default: //Unexpected error
          System.out.println("Unexpected Real Time Clock error.");
          break;
        }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    SetMaxNetworkCount()
    //
    //
    ////
    public void SetMaxNetworkCount()
    {
      int MaxCount=128;
//      WriteAuth();
      while (MaxCount < 1 || MaxCount > 127) {
        System.out.println("How many simultaneous network users do you wish to authorize (1-127): ");
        MaxCount = GetInt();
      }
      KTASK(SETMAXUSERS, MaxCount, 0, 0);
    }



    ////////////////////////////////////////////////////////////////////////
    //
    //    GetMaxNetworkCount()
    //
    //
    ////
    public void GetMaxNetworkCount()
    {
      int RetVal1;
//      ReadAuth();
      RetVal1 = KTASK(GETMAXUSERS, 0, 0, 0);
      RetVal1 = RetVal1 & 0xffff;
      System.out.println(RetVal1 + " simultaneous network users are authorized.");
      GetKey();

    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    EndUserRemoteUpdate()
    //
    //
    ////
    public void EndUserRemoteUpdate()
    {
      int RetVal1, RetVal2, intChecksum;
      int DisplayValue0, DisplayValue1, DisplayValue2, DisplayValue3;
      int Checksum, ClientChecksum;
      int Code1, Code2, Code3, Response;
      int XorValue, SendValue1, SendValue2;

      DisplayValue0 = 0;
      DisplayValue1 = 0;
      DisplayValue2 = 0;
      DisplayValue3 = 0;

      RetVal1 = KTASK(REMOTEUPDUPT1,0,0,0); // Remote Update User Part 1
      RetVal2 = (RetVal1 >> 16) & 0xffff;
      RetVal1 = RetVal1 & 0xffff;
      DisplayValue0 = RetVal1;
      DisplayValue1 = RetVal2;

      RetVal1 = KTASK(REMOTEUPDUPT2,0,0,0); // Remote Update User Part 2
      RetVal2 = (RetVal1 >> 16) & 0xffff;
      RetVal1 = RetVal1 & 0xffff;
      DisplayValue2 = RetVal1;
      DisplayValue3 = RetVal2;

      // Compute checksum to add to display for remote verification
      Checksum = (DisplayValue0 + DisplayValue1 +
                                  DisplayValue2 + DisplayValue3) % 65536;
      System.out.println("END USER - REMOTE UPDATE SIMULATION");
      System.out.println("Please call your software developer at (xxx) xxx-xxxx.");
      System.out.println("The following 5 codes must be provided to the developer in");
      System.out.println("order to receive your special activation code sequence.");
      System.out.println( Checksum + " " + DisplayValue0 + " " +
                       DisplayValue1 + " " + DisplayValue2 + " " + DisplayValue3);
      intChecksum = (int)Checksum;

      System.out.println("Once you confirm that your software supplier has input the");
      System.out.println("codes correctly you can proceed with the remote task.");
      GetKey();
      System.out.println("Please input the 4 codes provided by your software supplier, (one per line).");
      Checksum = GetInt();
      Code1 = GetInt();
      Code2 = GetInt();
      Code3 = GetInt();
      System.out.println("Please have your software supplier confirm that the following is correct.");
      System.out.println(Checksum + " " + Code1 + " " + Code2 + " " + Code3);
      System.out.println("Is it correct? ('Y'es or 'N'o or 'E'xit)");
      Response = GetResponse();
      ClientChecksum = (Code1 + Code2 + Code3) % 65536;
      if ((Checksum == ClientChecksum) && (Response == 'Y')) {
        RetVal1 = KTASK(REMOTEUPDUPT3, Code1, Code2, Code3);
        RetVal2 = (RetVal1 >> 16) & 0xffff;
        RetVal1 = RetVal1 & 0xffff;
        if ( RetVal1 != 0 ||
             RetVal2 == REMOTEGETMEMORY ||  // Some tasks can have '0'
             RetVal2 == REMOTEREPLACE       // return values.
           ) {
          XorValue = ClientIDCode1 ^ ClientIDCode2;
          SendValue1 = RetVal1 ^ XorValue;
          SendValue2 = RetVal2 ^ XorValue;
          Checksum = (SendValue1 + SendValue2 + XorValue) % 65536;
          System.out.println("END USER - REMOTE TASK SIMULATION");
          System.out.println("Please read the following 3 codes to the developer");
          System.out.println( Checksum);
          System.out.println( SendValue1);
          System.out.println( SendValue2);
          System.out.println("Once you confirm that your software developer has input the codes");
          System.out.println("correctly you can continue.");
        }
        else
          System.out.println("ERROR: Update was not performed!");
      }
    }




    ////////////////////////////////////////////////////////////////////////
    //
    //    SoftwareDeveloperRemoteUpdate()
    //
    //
    ////
    public void SoftwareDeveloperRemoteUpdate()
    {
      int Response = 0, Task, RetVal1, RetVal2, Value=0, XorValue, DateRead;
      int Checksum, ClientChecksum;
      int Code1, Code2, Code3, Code4, Address, ExpYear, ExpMonth, ExpDay;

      do
      {
        System.out.println("Please input 5 codes displayed on end user's system (One per line).");
        ClientChecksum = GetInt();
        Code1 = GetInt();
        Code2 = GetInt();
        Code3 = GetInt();
        Code4 = GetInt();
        System.out.println("Please have the end user confirm that the following is correct.");
        System.out.println(ClientChecksum + " " + Code1 + " " + Code2 + " " + Code3 + " " + Code4);
        System.out.println("Is it correct? ('Y'es or 'N'o or 'E'xit)");
        Response = GetResponse();
      } while ((Response != 'Y') && (Response != 'E'));

      if (Response == 'Y') {
        // Verify checksum is valid
        Checksum = (Code1 + Code2 + Code3 + Code4) % 65536;
        if (Checksum == ClientChecksum)
        {
          System.out.println("                 REMOTE UPDATE OF END USER'S DEVICE MEMORY");
          System.out.println("      What type of memory modification do you wish to perform?");
          System.out.println("      A = Add new value to existing value in device memory.");
          System.out.println("          Used to extend counters.");
          System.out.println("      C = Get the maximum network user count.");
          System.out.println("      D = Get the current lease expiration date.");
          System.out.println("      E = Extend the lease expiration date by 'n' months.");
          System.out.println("      M = Get current memory contents.");
          System.out.println("      O = Bitwise OR new value to existing value in device memory.");
          System.out.println("          Used to add additional licenses when individual bits are");
          System.out.println("          used to control access to applications or features within");
          System.out.println("          an application.");
          System.out.println("      R = Replace existing value in device memory with new value.");
          System.out.println("      S = Set the maximum network user count.");
          System.out.println("      X = Exit - Return to Main Menu.");
          Response = GetResponse();
          Address = 0;
          switch(Response)
          {
            case 'A': // Add to memory
              Task = REMOTEADD;
              System.out.println("What address contains the counter to be modified? ");
              Address = GetInt();
              System.out.println("How many counts would you like to add to the existing value? ");
              Value = GetInt();
              break;
            case 'C': // Get max user count
              Task = REMOTEGETUSERCT;
              Value = 0;
              break;
            case 'D': // Get lease end date
              Task = REMOTEGETDATE;
              Value = 0;
              break;
            case 'E': // Extend lease expiration date
              Task = REMOTEDATEEXTEND;
              System.out.println("How many months would you like to extend the existing date? ");
              Value = GetInt();
              break;
            case 'M': // Get memory contents
              Task = REMOTEGETMEMORY;
              System.out.println("For what address in KEYLOK memory do you want to retrieve contents? ");
              Address = GetInt();
              Value = 0;
              break;
            case 'O': // OR new value with old
              Task = REMOTEOR;
              System.out.println("What address contains the license to be bitwise 'OR' modified? ");
              Address = GetInt();
              System.out.println("What value (decimal) would you like to bitwise 'OR' with existing value? ");
              Value = GetInt();
              break;
            case 'R': // Replace old value with new
              Task = REMOTEREPLACE;
              System.out.println("For which address do you wish to replace the memory contents? ");
              Address = GetInt();
              System.out.println("What value (decimal) would you like to replace the existing value with? ");
              Value = GetInt();
              break;
            case 'S': // Set max user count
              Task = REMOTESETUSERCT;
              System.out.println("How many network users would you like to have authorized? ");
              Value = GetInt();
              break;
            default:
              Task = REMOTEINVALID;
              break;
          }
          if (Task < REMOTEINVALID)
          {
            RetVal1 = KTASK(REMOTEUPDCPT1, (Task << 13) + Address, Value, Code1); // Remote Update Client Part 1
            RetVal2 = (RetVal1 >> 16) & 0xffff;
            RetVal1 = RetVal1 & 0xffff;
            if ( RetVal1 != 0 )
              System.out.println("ERROR: Invalid address.");
            else
            {
              RetVal1 = KTASK(REMOTEUPDCPT2, Code2, Code3, Code4); // Remote Update Client Part 2
              RetVal2 = (RetVal1 >> 16) & 0xffff;
              RetVal1 = RetVal1 & 0xffff;
              if ((RetVal2 & 0x8000) == 0x8000) {
                System.out.println("ERROR: A data exchange/entry error has been made, or the");
                System.out.println("       end user's computer is not set to the same date as");
                System.out.println("       this computer.");
              }
              else //no errors detected
              {
                System.out.println("Your user is working with KEYLOK serial number " + (RetVal2 & 0x7FFF));
                System.out.println("Please instruct the user to enter the following codes at his computer.");
                Code1 = RetVal1;
                RetVal1 = KTASK(REMOTEUPDCPT3, 0, 0, 0); // Remote Update Client Part 3
                RetVal2 = (RetVal1 >> 16) & 0xffff;
                RetVal1 = RetVal1 & 0xffff;
                Checksum = (Code1 + RetVal1 + RetVal2) % 65536;
                System.out.println(Checksum + " " + Code1 + " " + RetVal1 + " " + RetVal2);
                GetKey();
                do
                {
                  System.out.println("Please input 3 codes displayed on end user's system (One per line).");
                  ClientChecksum = GetInt();
                  Code1 = GetInt();
                  Code2 = GetInt();
                  System.out.println("Please have the end user confirm that the following is correct.");
                  System.out.println(ClientChecksum + " " + Code1 + " " + Code2);
                  System.out.println("Is it correct? ('Y'es or 'N'o or 'E'xit)");
                  Response = GetResponse();
                } while (Response != 'Y' && Response != 'E');
                if (Response == 'Y') {
                  // Verify checksum is valid
                  XorValue = ClientIDCode1 ^ ClientIDCode2;
                  Checksum = (Code1 + Code2 + XorValue) % 65536;
                  if (Checksum == ClientChecksum) {
                    RetVal1 = Code1 ^ XorValue;
                    RetVal2 = Code2 ^ XorValue;
                    if ( RetVal1 != 0 ||                // Some tasks can
                         RetVal2 == REMOTEGETMEMORY ||  // have '0'
                         RetVal2 == REMOTEREPLACE       // return values.
                       ) {
                      switch ( RetVal2 ) {

                        case REMOTEADD:
                          System.out.println("The new counter value is " + RetVal1);
                          break;
                        case REMOTEDATEEXTEND:
                          DateRead = RetVal1; // YYYYYYYM MMMDDDDD
                          ExpYear = BASEYEAR + (int) (DateRead / 512);
                          ExpMonth = (int)((DateRead & 0X1E0) / 32);
                          ExpDay = DateRead & 0x1F;
                          System.out.println("The new lease expiration date is: " + ExpMonth + "/" + ExpDay + "/" + ExpYear);
                          break;
                        case REMOTEOR:
                          System.out.println("The new licensing value is " + RetVal1);
                          break;
                        case REMOTEREPLACE:
                          System.out.println("The modified memory address now contains " + RetVal1);
                          break;
                        case REMOTEGETMEMORY:
                          System.out.println("The current contents of this address are " + RetVal1);
                          break;
                        case REMOTESETUSERCT:
                          System.out.println("The modified user count now contains " + RetVal1);
                          break;
                        case REMOTEGETUSERCT:
                          System.out.println("The current network user count is set to " + RetVal1);
                          break;
                        case REMOTEGETDATE:
                          DateRead = RetVal1; // YYYYYYYM MMMDDDDD
                          ExpYear = BASEYEAR + (int) (DateRead / 512);
                          ExpMonth = (int)((DateRead & 0X1E0) / 32);
                          ExpDay = DateRead & 0x1F;
                          System.out.println("The current lease expiration date is: " + ExpMonth + "/" + ExpDay + "/" + ExpYear);
                          break;
                        default:
                          System.out.println("ERROR: Invalid codes entered.");
                          break;
                      }
                    }
                    else
                      System.out.println("ERROR: Codes input are invalid.");
                  }
                }
              }
            }
          }
        }
        else {
          System.out.println("ERROR: Codes are invalid !!!");
          GetKey();
        }
      }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    RemoteUpdateMenuOptions()
    //
    //
    ////
    public void RemoteUpdateMenuOptions(int RemoteUpdateMenuTask)

    {
            switch (RemoteUpdateMenuTask)
            {
              case 'a':                // A
              case 'A':                // Software Developer Remote Update
                 SoftwareDeveloperRemoteUpdate();
                 break;

              case 'b':                // B
              case 'B':                // End User Remote Update
                 EndUserRemoteUpdate();
                 break;

              default:
                 return;

            }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    NetworkMenuOptions()
    //
    //
    ////
    public void NetworkMenuOptions(int NetworkTask)

    {
            switch (NetworkTask)
            {
              case 'a':                // A
              case 'A':                // Set Maximum Network Count
                 SetMaxNetworkCount();
                 break;

              case 'b':                // B
              case 'B':                // Get Maximum Network Count Currently Allowed
                 GetMaxNetworkCount();
                 break;

              default:
                 return;

            }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    ClockMenuOptions()
    //
    //
    ////
    public void ClockMenuOptions(int ClockTask)

    {
            switch (ClockTask)
            {
              case 'a':               // Set Expiration Date
              case 'A':
                 SetLeaseExpirationDate();
                 break;

              case 'b':               // Check Expiration Date
              case 'B':
                 CkLeaseExpiration();
                 break;

              case 'c':               // Check Expiration Date
              case 'C':
                 CkRTC();
                 break;

              case 'd':               // Clear Expiration Date
              case 'D':
                 ClearLeaseExpirationDate();
                 break;
                 
              default:
                 return;

            }
            GetKey();
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    KLCheck()
    //
    //        Perform check for proper device.
    //
    ////
    public boolean KLCheck()
    {
        int RetVal1;
        int RetVal2;
        boolean lock = false;
        // Authenticate Part A:

        RetVal1 = KTASK(KLCHECK, ValidateCode1, ValidateCode2, ValidateCode3);
        RetVal2 = (RetVal1 >> 16) & 0xffff;
        RetVal1 = RetVal1 & 0xffff;

        // Authenticate Part B:

        RetVal1 = KTASK(RotateLeft(RetVal1, RetVal2 & 7) ^ ReadCode3 ^ RetVal2,
                        RotateLeft(RetVal2, RetVal1 & 15), RetVal1 ^ RetVal2, 0);
        RetVal2 = (RetVal1 >> 16) & 0xffff;
        RetVal1 = RetVal1 & 0xffff;

        if ((RetVal1 == ClientIDCode1) && (RetVal2 == ClientIDCode2))
        {
            // ActiveMenuSize = 4;
            System.out.println("The proper KEYLOK security device is attached.");
            RetVal1 = KTASK(GETDONGLETYPE,0,0,0);
            switch( RetVal1 )
            {
		   case 1:
                        System.out.println("Fortress attached.");
                        GetGlobalID();
                        break;
		   case 2:
			System.out.println("KEYLOK3 attached.");
                        break;
		   case 3:
                        // Default - NOTE: Could be Parallel
			System.out.println("KEYLOK2 attached.");
			break;
            }
            lock = true;
        }
        else
        {
            System.out.println("No KEYLOK or wrong KEYLOK device attached.");
        }
        return lock;
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    ReadVarMem()
    //
    //        Read Variable Memory.
    //
    ////
    public void ReadVarMem()
    {
        int Address;
        int RetVal1;

        System.out.println();
        System.out.println("Which address (0-55) would you like to retrieve?");
        Address = GetInt();

        RetVal1 = KTASK(GETVARWORD, Address, 0, 0);
        RetVal1 = RetVal1 & 0xffff;
        System.out.println("Address: " + Address + " contains: " + RetVal1);
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    WriteVarMem()
    //
    //        Write Variable Memory.
    //
    ////
    public void WriteVarMem()
    {
        int Address;
        int Value;

        System.out.println();
        System.out.println("Which address (0-55) would you like to write to?");
        Address = GetInt();

        System.out.println();
        System.out.println("What value (0-65535) would you like to write to this address?");
        Value = GetInt();

        KTASK(WRITEVARWORD, Address, Value, 0);
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    DecCounter()
    //
    //        Decrement Counter
    //
    ////
    public void DecCounter()
    {
        int Address;
        int RetVal1;
        int RetVal2;

        System.out.println();
        System.out.println("Which counter address (0-55) would you like to decrement?");
        Address = GetInt();

        RetVal1 = KTASK(DECREMENTMEM, Address, 0, 0);
        RetVal2 = (RetVal1 >> 16) & 0xffff;
        RetVal1 = RetVal1 & 0xffff;
        switch(RetVal2) {
          case VALIDCOUNT:                  /* No error encountered */
            switch(RetVal1) {
              case 0:
                System.out.println("CAUTION: This counter is fully counted down to zero.");
                break;
              default:
                System.out.println("NOTE: There are " + RetVal1 + " counts remaining for this usage.");
                break;
            }
            break;
          case NOWRITEAUTH:
            System.out.println("ERROR: Memory write has not been authorized.");
            break;
          case INVALIDADDRESS:
            System.out.println("ERROR: Address is out of range.");
            break;
          case NOCOUNTSLEFT:
            System.out.println("ERROR: This counter is already fully counted down to zero.");
            break;
          default:
            System.out.println("ERROR: Unrecognized counter decrement error.");
            break;
        }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    ExpDate()
    //
    //        Expiration Date Functions
    //
    ////
    public void ExpDate()
    {
      int task;
//      ReadAuth();
      task = 0;
      while( task != 'x' && task != 'X' ) 
      {
        try
        {
          byte bArray[] = new byte[128];

          Keylok jm = new Keylok() ;

          System.out.println("          KEYLOK SECURITY SYSTEM DEMONSTRATION") ;
          System.out.println("          (C) Copyright 1982 - 2014 - All Rights Reserved");
          System.out.println("                By:   KEYLOK");
          System.out.println("                         (800) 453-9565");
          System.out.println();
          System.out.println("   EXPIRATION DATE OPERATIONS - Using Computer's System Clock:");
          System.out.println("   NOTE: Attempts to set back system clock are detected.");
          System.out.println();
          System.out.println("           A = Set Expiration Date.");
          System.out.println("               NOTE: Automatically initializes Last System Date ");
          System.out.println("                     and Time to the current system date and time ");
          System.out.println("                     any time this function is called.");
          System.out.println("           B = Check Expiration Date.");
          System.out.println("               NOTE: Last System Date & Time are refreshed.");
          System.out.println("           C = Check real time clock.");
          System.out.println("           D = Clear Expiration Date.");
          System.out.println("           X = Return to Main Menu.");
          System.out.println();
          System.out.println("           What would you like to do?");

          System.in.read(bArray);

          task = (int)bArray[0];

          jm.ClockMenuOptions(task) ;

        }
        catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
      }
      System.out.println("");
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    NetworkOptions()
    //
    //        I = Network Control Operations
    //
    ////
    public void NetworkOptions()
    {
      int task;

      task = 0;
      while( task != 'x' && task != 'X' ) {
        try
        {
          byte bArray[] = new byte[128];

          Keylok jm = new Keylok() ;

          System.out.println("                 KEYLOK SECURITY SYSTEM DEMONSTRATION") ;
          System.out.println("          (C) Copyright 1982 - 2014 - All Rights Reserved");
          System.out.println("                          By:   KEYLOK");
          System.out.println("                         (800) 453-9565");
          System.out.println("                         (303) 801-0338");
          System.out.println();
          System.out.println("                    NETWORK SETUP OPERATIONS");
          System.out.println();
          System.out.println("             A = Set Maximum Simulataneous User Count.");
          System.out.println("             B = Get Maximum Simultaneous User Count.");
          System.out.println("             X = Return to Main Menu.");
          System.out.println();
          System.out.println("           What would you like to do?");

          System.in.read(bArray);

          task = (int)bArray[0];

          jm.NetworkMenuOptions(task) ;

        }
        catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
      }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    RemoteUpdateOptions()
    //
    //        J = Remote Memory Update
    //
    ////
    public void RemoteUpdateOptions()
    {
      int task;

      task = 0;
      try
        {
          byte bArray[] = new byte[128];

          Keylok jm = new Keylok() ;

          System.out.println("                   KEYLOK SECURITY SYSTEM DEMONSTRATION") ;
          System.out.println("             (C) Copyright 1982 - 2014 - All Rights Reserved");
          System.out.println("                              By:   KEYLOK");
          System.out.println("                             (800) 453-9565");
          System.out.println("                             (303) 801-0338");
          System.out.println();
          System.out.println("              REMOTE KEYLOK MEMORY MODIFICATION OPERATIONS");
          System.out.println();
          System.out.println("This task is used to remotely extend counters, lease expiration dates,");
          System.out.println("simultaneous network users, to add additional software licences at a client");
          System.out.println("facility without having to exchange devices or physically obtain access to");
          System.out.println("their device.  It also allows you to obtain informaton about current settings");
          System.out.println("at the remote installation.");
          System.out.println("NOTICE: This function requires TWO computers to demonstrate the functionality.");
          System.out.println("        One computer must be used to simulate the software developer's facility");
          System.out.println("        The other computer is used to simulate the end user of your software.");
          System.out.println("        Both computer must be set to the same date.");
          System.out.println("                   Which platform is this computer simulating.");
          System.out.println("                   A = Software Developer.");
          System.out.println("                   B = End User.");
          System.out.println("                   X = Return to Main Menu.");
          System.out.println();
          System.out.println("           What would you like to do?");

          System.in.read(bArray);

          task = (int)bArray[0];

          jm.RemoteUpdateMenuOptions(task) ;

        }
      catch (Exception e)
        {
          // message and stack trace in case we throw an exception
          System.out.println("Exception thrown while exercising methods") ;
          System.out.println(e.getMessage()) ;
          e.printStackTrace() ;
        }
    }


    ////////////////////////////////////////////////////////////////////////
    //
    //    ClearScreen()
    //
    ////
    private void ClearScreen()
    {
      int i;

      for (i=1; i<51; i++)
          System.out.println();
    }
//
//
//
//    ////////////////////////////////////////////////////////////////////////
//    //
//    //    PerformSelectedTask()
//    //
//    //
//    ////
//    public int PerformSelectedTask(int task)
//
//    {
//          int RetVal1;
//          int RetVal2;
//
//          ClearScreen();
//          if (task > 'Z')
//            task-=32;                // Convert to upper case
//
//          switch (task)
//          {
//            case 'A':                // Perform Authentication (CheckForKL)
//               Menu_A();
//               MenuSize = 2;
//               break;
//
//            case 'B':                // Perform Read Authorization
//               KTASK(READAUTH, ReadCode1, ReadCode2, ReadCode3);
//               System.out.println("Read Authorization sequence has been sent.");
//               MenuSize = 3;
//               break;
//
//            case 'C':                // Read Serial Number
//               RetVal1 = KTASK(GETSN, 0, 0, 0);
//               RetVal1 = RetVal1 & 0xffff;
//               System.out.println("Serial Number = " + RetVal1);
//               break;
//
//            case 'D':               // Read Variable Memory
//               Menu_D();
//               break;
//
//            case 'E':               // Perform Write Authorization
//               KTASK(WRITEAUTH, WriteCode1, WriteCode2, WriteCode3);
//               System.out.println("Write Authorization sequence has been sent.");
//               MenuSize = 6;
//               break;
//
//            case 'F':               // Write Variable Memory
//               Menu_F();
//               break;
//
//            case 'G':               // Decrement Counter
//               Menu_G();
//               break;
//
//            case 'H':               // Expiration Date Functions
//               Menu_H();
//               break;
//
//            case 'I':               // Network Control Operations
//               Menu_I();
//               break;
//
//            case 'J':               // Remote Memory Update
//               Menu_J();
//               break;
//
//            default:
//               return(MenuSize);
//
//          }
//          GetKey();
//          return(MenuSize);
//    }
//
//
//
//    ////////////////////////////////////////////////////////////////////////
//    //
//    //    ExerciseJCW()
//    //
//    ////
//    public void ExerciseJCW(int choice)
//
//    {
//          switch (choice)
//          {
//            case 65:
//            case 97:
//               Menu_A();            // Perform Menu A commands.
//               break;
//            default:
//               break;
//
//          }
//    }
//
//
//     ////////////////////////////////////////////////////////////////////////
//    //
//    //    main()
//    //
//    //    entry point
//    //
//    ////
//    public static void main(String argv[])
//    {
//        try
//        {
//          byte bArray[] = new byte[128];
//          int task;
//          int MenuSize = 1;
//          int NewMenuSize;
//
//          Keylok jm = new Keylok() ;
//
//          task = 0;
//          while( task != 'x' && task != 'X' ) {
//            System.out.println("                 KEYLOK SECURITY SYSTEM DEMONSTRATION") ;
//            System.out.println("          (C) Copyright 1982 - 2014 - All Rights Reserved");
//            System.out.println("                          By:   KEYLOK");
//            System.out.println("                         (800) 453-9565");
//            System.out.println("                         (303) 801-0338");
//            System.out.println();
//            System.out.println("              A = Authentication (Check For KEYLOK)");
//            if (MenuSize > 1)
//              System.out.println("              B = Read Authorization");
//            if (MenuSize > 2) {
//              System.out.println("              C = Read Serial Number");
//              System.out.println("              D = Read Variable Memory");
//              System.out.println("              E = Write Authorization");
//            }
//            if (MenuSize > 5) {
//              System.out.println("              F = Write Variable Memory");
//              System.out.println("              G = Decrement Counter");
//              System.out.println("              H = Expiration Date Operations");
//              System.out.println("              I = Network Control Operations");
//              System.out.println("              J = Remote Memory Update");
//            }
//            System.out.println("              X = Exit");
//            System.out.println();
//            System.out.println("              What would you like to do?");
//            System.out.println();
//
//            System.in.read(bArray);
//
//            task = (int)bArray[0];
//
//            NewMenuSize = jm.PerformSelectedTask(task) ;
//            MenuSize = NewMenuSize;
//
//          }
//        }
//        catch (Exception e)
//        {
//          // message and stack trace in case we throw an exception
//          System.out.println("Exception thrown while exercising methods") ;
//          System.out.println(e.getMessage()) ;
//          e.printStackTrace() ;
//        }
//      }
}
