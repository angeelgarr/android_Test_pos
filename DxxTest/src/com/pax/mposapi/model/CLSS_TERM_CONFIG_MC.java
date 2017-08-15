package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * terminal configuration for PayPass, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class CLSS_TERM_CONFIG_MC {
	
	/**
	 * if aucMaxLifeTimeTorn valid or not. 1 for yes, 0 otherwise
	 */
	public byte ucMaxLifeTimeTornFlg;
	/**
	 * Max Lifetime of Torn Log, 2 bytes
	 */
	public final byte[] aucMaxLifeTimeTorn;		//[2];   /*Max Lifetime of Torn Log*/
	/**
	 * if ucMaxNumberTorn valid or not. 1 for yes, 0 otherwise
	 */
	public byte ucMaxNumberTornFlg;
	/**
	 * Max number of Torn Log
	 */
	public byte ucMaxNumberTorn;          /*Max number of Torn Log*/
	/**
	 * if aucBalanceBeforeGAC valid or not. 1 for yes, 0 otherwise
	 */
	public byte ucBalanceBeforeGACFlg;
	/**
	 * Balance read before GAC, 6 bytes
	 */
	public final byte[] aucBalanceBeforeGAC;	//[6];  /*Balance read before GAC*/
	/**
	 * if aucBalanceAfterGAC valid or not. 1 for yes, 0 otherwise
	 */	
	public byte ucBalanceAfterGACFlg;
	/**
	 * Balance read after GAC, 6 bytes
	 */
	public final byte[] aucBalanceAfterGAC;		//[6];    /*Balance read after GAC*/
	/**
	 * if ucMobileSup valid or not. 1 for yes, 0 otherwise
	 */	
	public byte ucMobileSupFlg;
	/**
	 * Mobile Support Indicator
	 */
	public byte ucMobileSup;                   /*Mobile Support Indicator*/
	/**
	 * if ucHoldTimeValue valid or not. 1 for yes, 0 otherwise
	 */		
	public byte ucHoldTimeValueFlg;
	/**
	 * Hold Time Value
	 */
	public byte ucHoldTimeValue;            /*Hold Time Value*/
	/**
	 * if aucInterDevSerNum valid or not. 1 for yes, 0 otherwise
	 */			
	public byte ucInterDevSerNumFlg;
	/**
	 * Interface Device Serial Number, 4 bytes
	 */
	public final byte[] aucInterDevSerNum;		//[4];    /*Interface Device Serial Number*/
	/**
	 * if ucKernelID valid or not. 1 for yes, 0 otherwise
	 */				
	public byte ucKernelIDFlg;
	/**
	 * Kernel ID
	 */
	public byte ucKernelID;                     /*Kernel ID*/
	/**
	 * if aucMsgHoldTime valid or not. 1 for yes, 0 otherwise
	 */		
	public byte ucMsgHoldTimeFlg;
	/**
	 * Message Hold Time, 3 bytes
	 */
	public final byte[] aucMsgHoldTime;			//[3];       /*Message Hold Time*/

    /**
     * create an CLSS_TERM_CONFIG_MC instance
     */
    public CLSS_TERM_CONFIG_MC() {
    	aucMaxLifeTimeTorn = new byte[2];   /*Max Lifetime of Torn Log*/
    	aucBalanceBeforeGAC = new byte[6];  /*Balance read before GAC*/
    	aucBalanceAfterGAC = new byte[6];    /*Balance read after GAC*/
    	aucInterDevSerNum = new byte[4];    /*Interface Device Serial Number*/
    	aucMsgHoldTime = new byte[3];       /*Message Hold Time*/
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

    	ss.put(ucMaxLifeTimeTornFlg);
    	ss.put(aucMaxLifeTimeTorn);		//[2];   /*Max Lifetime of Torn Log*/
    	ss.put(ucMaxNumberTornFlg);
    	ss.put(ucMaxNumberTorn);          /*Max number of Torn Log*/
    	ss.put(ucBalanceBeforeGACFlg);
    	ss.put(aucBalanceBeforeGAC);	//[6];  /*Balance read before GAC*/
    	ss.put(ucBalanceAfterGACFlg);
    	ss.put(aucBalanceAfterGAC);		//[6];    /*Balance read after GAC*/
    	ss.put(ucMobileSupFlg);
    	ss.put(ucMobileSup);                   /*Mobile Support Indicator*/
    	ss.put(ucHoldTimeValueFlg);
    	ss.put(ucHoldTimeValue);            /*Hold Time Value*/
    	ss.put(ucInterDevSerNumFlg);
    	ss.put(aucInterDevSerNum);		//[4];    /*Interface Device Serial Number*/
    	ss.put(ucKernelIDFlg);
    	ss.put(ucKernelID);                     /*Kernel ID*/
    	ss.put(ucMsgHoldTimeFlg);
    	ss.put(aucMsgHoldTime);			//[3];       /*Message Hold Time*/
    	
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
        
        ucMaxLifeTimeTornFlg = ss.get();
    	ss.get(aucMaxLifeTimeTorn);		//[2];   /*Max Lifetime of Torn Log*/
    	ucMaxNumberTornFlg = ss.get();
    	ucMaxNumberTorn = ss.get();          /*Max number of Torn Log*/
    	ucBalanceBeforeGACFlg = ss.get();
    	ss.get(aucBalanceBeforeGAC);	//[6];  /*Balance read before GAC*/
    	ucBalanceAfterGACFlg = ss.get();
    	ss.get(aucBalanceAfterGAC);		//[6];    /*Balance read after GAC*/
    	ucMobileSupFlg = ss.get();
    	ucMobileSup = ss.get();                   /*Mobile Support Indicator*/
    	ucHoldTimeValueFlg = ss.get();
    	ucHoldTimeValue = ss.get();            /*Hold Time Value*/
    	ucInterDevSerNumFlg = ss.get();
    	ss.get(aucInterDevSerNum);		//[4];    /*Interface Device Serial Number*/
    	ucKernelIDFlg = ss.get();
    	ucKernelID = ss.get();                     /*Kernel ID*/
    	ucMsgHoldTimeFlg = ss.get();
    	ss.get(aucMsgHoldTime);			//[3];       /*Message Hold Time*/
    	
    }
}
