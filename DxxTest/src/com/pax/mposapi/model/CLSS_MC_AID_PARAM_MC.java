package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * PayPass application parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class CLSS_MC_AID_PARAM_MC {
	/**
	 * Terminal floor limits - the same as floor limits of contact EMV
	 */
	public int FloorLimit;				/*Terminal floor limits - the same as floor limits of contact EMV*/
	/**
	 * Threshold
	 */
	public int Threshold;             /*Threshold*/
	/**
	 * length of UDOL
	 */
	public short usUDOLLen;          /*UDOL*/
	/**
	 * Terminal default UDOL, valid length is usUDOLLen, maximum 256 bytes
	 */
	public final byte[] uDOL;		//[256];            /*Terminal default UDOL*/
	/**
	 * Target Percentage
	 */
	public byte TargetPer;             /*Target Percentage*/
	/**
	 * Maximum Target Percentage
	 */
	public byte MaxTargetPer;       /*Maximum Target Percentage*/
	/**
	 * Whether to do floor limits checking ?1:yes 0:no
	 */
	public byte FloorLimitCheck;     /*Whether to do floor limits checking ?1:yes 0:no*/
	/**
	 * Whether to do random transaction ?1:yes,0:no
	 */
	public byte RandTransSel;       /*Whether to do random transaction ?1:yes,0:no*/
	/**
	 * Whether to do velocity checking?1:yes,0:no
	 */
	public byte VelocityCheck;       /*Whether to do velocity checking?1:yes,0:no*/
	/**
	 * Terminal action code(denial), 5 bytes
	 */
	public final byte[] TACDenial;		//[6];       /*Terminal action code(denial) */
	/**
	 * Terminal action code(online), 5 bytes
	 */
	public final byte[] TACOnline;		//[6];       /* Terminal action code(online) */
	/**
	 * Terminal action code(default), 5 bytes
	 */
	public final byte[] TACDefault;		//[6];      /* Terminal action code(default) */
	/**
	 * Acquirer identification
	 */
    public final byte[] AcquirerId;		//[6];       /*Acquirer identification*/
    /**
     * Terminal default DDOL, maximum 256 bytes
     */
	public final byte[] dDOL;			//[256];           /*Terminal default DDOL*/
	/**
	 * Terminal default TDOL, maximum 256 bytes 
	 */
	public final byte[] tDOL;			//[256];            /*Terminal default TDOL*/
	/**
	 * Application Version, 2 bytes
	 */
	public final byte[] Version;		//[3];           /*Application Version*/
	/**
	 * Merchant forced online Flag, 1: yes,0:no
	 */
	public byte ForceOnline;         /*Merchant forced online Flag, 1: yes,0:no*/
	/**
	 * Magnetic stripe application version, 2 bytes
	 */
	public final byte[] MagAvn;			//[3];          /* Magnetic stripe application version*/
	/**
	 * 1-card reader supports MagStripe  0-does not support
	 */
	public byte ucMagSupportFlg;                    /*1-card reader supports MagStripe  0-does not support*/
	/**
	 * Mag-stripe CVM Capability - CVM Required
	 */
	public byte ucMagStrCVMCapWithCVM;      /*Mag-stripe CVM Capability - CVM Required*/
	/**
	 * Mag-stripe CVM Capability - No CVM Required
	 */
	public byte ucMagStrCVMCapNoCVM;        /*Mag-stripe CVM Capability - No CVM Required*/
	/**
	 * kernel configuration
	 */
	public byte ucKernelConfig;                      /*kernel configuration*/
	/**
	 * reserved
	 */
	public byte ucRFU;			                       /*RFU*/
	
    /**
     * create an CLSS_MC_AID_PARAM_MC instance
     */
    public CLSS_MC_AID_PARAM_MC() {
    	uDOL = new byte[256];            /*Terminal default UDOL*/
    	TACDenial = new byte[6];       /*Terminal action code(denial) */
    	TACOnline = new byte[6];       /* Terminal action code(online) */
    	TACDefault = new byte[6];      /* Terminal action code(default) */
    	AcquirerId = new byte[6];       /*Acquirer identification*/
    	dDOL = new byte[256];           /*Terminal default DDOL*/
    	tDOL = new byte[256];            /*Terminal default TDOL*/
    	Version = new byte[3];           /*Application Version*/
    	MagAvn = new byte[3];          /* Magnetic stripe application version*/
    }

    /**
     * get data from this object and write to a byte array.
     *
     * @return
     * 	a byte array including data of this object.
     */  
    public byte[] serialToBuffer() {
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.clear();

        ss.putInt(FloorLimit);			/*Terminal floor limits - the same as floor limits of contact EMV*/
    	ss.putInt(Threshold);             /*Threshold*/
    	ss.putShort(usUDOLLen);          /*UDOL*/
    	ss.put(uDOL);  //[256];            /*Terminal default UDOL*/
    	ss.put(TargetPer);             /*Target Percentage*/
    	ss.put(MaxTargetPer);       /*Maximum Target Percentage*/
    	ss.put(FloorLimitCheck);     /*Whether to do floor limits checking ?1:yes 0:no*/
    	ss.put(RandTransSel);       /*Whether to do random transaction ?1:yes,0:no*/
    	ss.put(VelocityCheck);       /*Whether to do velocity checking?1:yes,0:no*/
    	ss.put(TACDenial);  //[6];       /*Terminal action code(denial) */
    	ss.put(TACOnline);  //[6];       /* Terminal action code(online) */
    	ss.put(TACDefault);  //[6];      /* Terminal action code(default) */
        ss.put(AcquirerId);  //[6];       /*Acquirer identification*/
    	ss.put(dDOL);  //[256];           /*Terminal default DDOL*/
    	ss.put(tDOL);  //[256];            /*Terminal default TDOL*/
    	ss.put(Version);  //[3];           /*Application Version*/
    	ss.put(ForceOnline);         /*Merchant forced online Flag, 1: yes,0:no*/
    	ss.put(MagAvn);  //[3];          /* Magnetic stripe application version*/
    	ss.put(ucMagSupportFlg);                    /*1-card reader supports MagStripe  0-does not support*/
    	ss.put(ucMagStrCVMCapWithCVM);      /*Mag-stripe CVM Capability - CVM Required*/
    	ss.put(ucMagStrCVMCapNoCVM);        /*Mag-stripe CVM Capability - No CVM Required*/
    	ss.put(ucKernelConfig);                      /*kernel configuration*/
    	ss.put(ucRFU);			                       /*RFU*/
   	 	
        ss.flip();
        byte[] ret = new byte[ss.limit()];
        ss.get(ret);
        return ret;
    }

    /**
     * get data from a byte array to this object
     *
     * @param bb
     *   a byte array from which data should be read
     * 
     */
    public void serialFromBuffer(byte[] bb) {
        ByteBuffer ss = ByteBuffer.wrap(bb);
        ss.order(ByteOrder.BIG_ENDIAN);
        
        FloorLimit = ss.getInt();			/*Terminal floor limits - the same as floor limits of contact EMV*/
        Threshold = ss.getInt();             /*Threshold*/
        usUDOLLen = ss.getShort();          /*UDOL*/
    	ss.get(uDOL);  //[256];            /*Terminal default UDOL*/
    	TargetPer = ss.get();             /*Target Percentage*/
    	MaxTargetPer = ss.get();       /*Maximum Target Percentage*/
    	FloorLimitCheck = ss.get();     /*Whether to do floor limits checking ?1:yes 0:no*/
    	RandTransSel = ss.get();       /*Whether to do random transaction ?1:yes,0:no*/
    	VelocityCheck = ss.get();       /*Whether to do velocity checking?1:yes,0:no*/
    	ss.get(TACDenial);  //[6];       /*Terminal action code(denial) */
    	ss.get(TACOnline);  //[6];       /* Terminal action code(online) */
    	ss.get(TACDefault);  //[6];      /* Terminal action code(default) */
        ss.get(AcquirerId);  //[6];       /*Acquirer identification*/
    	ss.get(dDOL);  //[256];           /*Terminal default DDOL*/
    	ss.get(tDOL);  //[256];            /*Terminal default TDOL*/
    	ss.get(Version);  //[3];           /*Application Version*/
    	ForceOnline = ss.get();         /*Merchant forced online Flag, 1: yes,0:no*/
    	ss.get(MagAvn);  //[3];          /* Magnetic stripe application version*/
    	ucMagSupportFlg = ss.get();                    /*1-card reader supports MagStripe  0-does not support*/
    	ucMagStrCVMCapWithCVM = ss.get();      /*Mag-stripe CVM Capability - CVM Required*/
    	ucMagStrCVMCapNoCVM = ss.get();        /*Mag-stripe CVM Capability - No CVM Required*/
    	ucKernelConfig = ss.get();                      /*kernel configuration*/
    	ucRFU = ss.get();			                       /*RFU*/
   	 	
    }
}
