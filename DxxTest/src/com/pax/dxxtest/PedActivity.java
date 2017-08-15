package com.pax.dxxtest;


import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Map;

import utils.MAC;
import utils.RSAUtil;
import utils.TDES;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.pax.mposapi.BaseSystemManager;
import com.pax.mposapi.PedException;
import com.pax.mposapi.PedManager;
import com.pax.mposapi.PedManager.PinDukptOutput;
import com.pax.mposapi.PedManager.RsaRecoverOutput;
import com.pax.mposapi.UIManager;
import com.pax.mposapi.model.ST_KCV_INFO;
import com.pax.mposapi.model.ST_KEY_INFO;
import com.pax.mposapi.model.ST_RSA_KEY;
import com.pax.mposapi.util.Utils;
import com.pax.dxxtest.R;

public class PedActivity extends Activity {

	private static final String TAG = "PedActivity";
	private PedManager ped;
	private UIManager ui;
	private BaseSystemManager base;
	private TextView text;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
	    // TODO Auto-generated method stub
	    setContentView(R.layout.activity_base);
	    ped = PedManager.getInstance(this);
	    ui = UIManager.getInstance(this);
	    base = BaseSystemManager.getInstance(this);
	    test();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
			case 2:
				Bundle bundle = msg.getData();
				String result = bundle.getString("result");
				text.setText(result);
				break;
			}
	    }

	};
	
    private boolean extractPan(byte[] in, byte[] out) {
    	if (in.length > 19 || in.length < 13) {
    		return false;
    	}
    	
    	out[0] = out[1] = out[2] = out[3] = '0';
    	System.arraycopy(in, in.length - 13, out, 4, 12);
    	
    	return true;
    }
	
	private void test() {
		text = (TextView)findViewById(R.id.textViewBase);		
		progressDialog = new ProgressDialog(PedActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
				
		new Thread(new Runnable(){
			String record = "";
			
			public void run(){
				Looper.prepare();
				int step = 0;
				try{

					final byte TLK_IDX = (byte)1;
					final byte TPK_IDX = (byte)1;
					final byte TAK_IDX = (byte)2;
					final byte TDK_IDX = (byte)3;
					final byte TIK_IDX = (byte)1;
																		
					ui.scrCls();
					ui.scrShowText("%P1010%F1Testing ped...");
					record += String.format("ver: %s\n", new String(ped.pedGetVer()));
					String clearDataToTest = "hello, I'm clear text";

					//TLK 24, clear
					byte[] tlk24Clr = {
						0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
						0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
						0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77
					}; 
					
					//TMK 24, clear
					byte[] tmk24Clr = {
						0x10, 0x13, 0x13, 0x15, 0x15, 0x15, 0x16, 0x1a,
						0x1a, 0x16, 0x13, 0x15, 0x1a, 0x16, 0x16, 0x15,
						0x13, 0x15, 0x1a, 0x16, 0x16, 0x15, 0x16, 0x13,
					};
					//TPK 24, clear
					byte[] tpk24Clr = {
						0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
						0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f,
						0x20, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77
					};
					//TAK 24, clear
					byte[] tak24Clr = {
						0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
						0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f,
						0x30, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77
					};
					//TDK 24, clear
					byte[] tdk24Clr = {
						0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,
						0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
						0x40, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77
					};

					byte[] tmk24Cipher = TDES.encryptMode(tlk24Clr, tmk24Clr);
					byte[] tpk24Cipher = TDES.encryptMode(tmk24Clr, tpk24Clr);
					byte[] tak24Cipher = TDES.encryptMode(tmk24Clr, tak24Clr);
					byte[] tdk24Cipher = TDES.encryptMode(tmk24Clr, tdk24Clr);

					ST_KEY_INFO keyInfo = new ST_KEY_INFO();
					ST_KCV_INFO kcvInfo = new ST_KCV_INFO();
					byte[] kcv;

					ui.scrCls();
					ui.scrShowText("%P1010%F1Erasing...");
					ped.pedErase();
					step++;

					ui.scrCls();
					ui.scrShowText("%P1010%F1Writing keys...");
					
//////////////////////// TLK / TMK,TDK,TAK,TPK Cipher... ///////////////////
					
/*					
					//write TLK clear
					keyInfo.ucSrcKeyType  = 0;
					keyInfo.ucSrcKeyIdx = 0;
					keyInfo.ucDstKeyType = PedManager.PED_TLK; 
					keyInfo.ucDstKeyIdx = TLK_IDX;
					keyInfo.iDstKeyLen  = (byte)tlk24Clr.length;
					System.arraycopy(tlk24Clr, 0, keyInfo.aucDstKeyValue, 0, tlk24Clr.length);
					kcvInfo.iCheckMode = 1;
					kcvInfo.aucCheckBuf[0] = 4;
					final byte[] zeros = new byte[8];
					kcv = TDES.encryptMode(tlk24Clr, zeros);
					Log.i(TAG, "kcv is: " + Utils.byte2HexStr(kcv, 0, kcv.length));
					System.arraycopy(kcv, 0, kcvInfo.aucCheckBuf, 1, 4);					
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;

					//try getting kcv here
					ST_KCV_INFO kcvCheckInfo = new ST_KCV_INFO();
					kcvCheckInfo.iCheckMode = (byte)0x00;
					kcvCheckInfo.aucCheckBuf[0] = 8;		//8 zeros
					//kcvCheckInfo.isForGetKcv = true;			//actually it's mode 1 as in write key!!  no need set it. auto set it in sdk.
					ped.pedGetKcv((byte)PedManager.PED_TLK, TLK_IDX, kcvCheckInfo);
					byte[] kcvZeros = TDES.encryptMode(tlk24Clr, zeros);
					//warning: for getKcv, kcvCheckInfo.aucCheckBuf[0] is NOT length, it's the first kcv data.
					//record += "TLK KCV verify ok ? " + Utils.cmpByteArray(kcvCheckInfo.aucCheckBuf, 1, kcvZeros, 0, kcvCheckInfo.aucCheckBuf[0]);
					record += "TLK KCV verify ok ? " + Utils.cmpByteArray(kcvCheckInfo.aucCheckBuf, 0, kcvZeros, 0, 4)  + "\n";
					step++;
					
					//write TMK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TLK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TMK; 
					keyInfo.ucDstKeyIdx = 1;
					keyInfo.iDstKeyLen  = (byte)tmk24Cipher.length;
					System.arraycopy(tmk24Cipher, 0, keyInfo.aucDstKeyValue, 0, tmk24Cipher.length);

					kcvInfo.iCheckMode = 2;	//NOTE: int this mode, every byte of the key MUST be odd parity!!!
					kcvInfo.aucCheckBuf[0] = 4;
					byte[] kcvGenFrom = {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, 
										 (byte)0x90, (byte)0x12, (byte)0x34, (byte)0x56, 
					};
					kcv = TDES.encryptMode(tmk24Clr, kcvGenFrom);
					Log.i(TAG, "kcv is: " + Utils.byte2HexStr(kcv, 0, kcv.length));
					System.arraycopy(kcv, 0, kcvInfo.aucCheckBuf, 1, 4);
					
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;

					//write TPK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TPK; 
					keyInfo.ucDstKeyIdx = TPK_IDX;
					keyInfo.iDstKeyLen  = (byte)tpk24Cipher.length;
					System.arraycopy(tpk24Cipher, 0, keyInfo.aucDstKeyValue, 0, tpk24Cipher.length);
					kcvInfo.iCheckMode = 3;
					kcvGenFrom = new String("12345678").getBytes();
					kcvInfo.aucCheckBuf[0] = (byte)kcvGenFrom.length;
					System.arraycopy(kcvGenFrom, 0, kcvInfo.aucCheckBuf, 1, kcvGenFrom.length);
					byte kcvMacMode = (byte)0;
					kcvInfo.aucCheckBuf[1 + kcvGenFrom.length] = kcvMacMode;
					kcvInfo.aucCheckBuf[2 + kcvGenFrom.length] = (byte)8;
					
					byte[] toMac = new byte[tpk24Cipher.length + kcvGenFrom.length];
					System.arraycopy(tpk24Cipher, 0, toMac, 0, tpk24Cipher.length);
					System.arraycopy(kcvGenFrom, 0, toMac, tpk24Cipher.length, kcvGenFrom.length);
					byte[] kcvMac = MAC.calc(tmk24Clr, toMac, kcvMacMode);	//NOTE: MUST use it's src key to calc mac, this is diff from mode 1 and 2 !
					System.arraycopy(kcvMac, 0, kcvInfo.aucCheckBuf, 3 + kcvGenFrom.length, 8);
					
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//write TAK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TAK; 
					keyInfo.ucDstKeyIdx = TAK_IDX;
					keyInfo.iDstKeyLen  = (byte)tak24Cipher.length;
					System.arraycopy(tak24Cipher, 0, keyInfo.aucDstKeyValue, 0, tak24Cipher.length);
					kcvInfo.iCheckMode = 0;
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//write TDK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TDK; 
					keyInfo.ucDstKeyIdx = TDK_IDX;
					keyInfo.iDstKeyLen  = (byte)tdk24Cipher.length;
					System.arraycopy(tdk24Cipher, 0, keyInfo.aucDstKeyValue, 0, tdk24Cipher.length);
					kcvInfo.iCheckMode = 0;
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//TIK 16, clear
					byte[] tik16Clr = {
						(byte)0x6A, (byte)0xC2, (byte)0x92, (byte)0xFA, (byte)0xA1, (byte)0x31, (byte)0x5B, (byte)0x4D,
						(byte)0x85, (byte)0x8A, (byte)0xB3, (byte)0xA3, (byte)0xD7, (byte)0xD5, (byte)0x93, (byte)0x3A						
					};					
					byte[] tik16Cipher = TDES.encryptMode(tlk24Clr, tik16Clr);
					
					byte[] ksn = {
						(byte)0xff, (byte)0xff, (byte)0x98, (byte)0x76,
						(byte)0x54, (byte)0x32, (byte)0x10, (byte)0xE0,
						(byte)0x00, (byte)0x00
					};

					//write TIK cipher
					kcvInfo.iCheckMode = 0;
					ped.pedWriteTIK((byte)TIK_IDX, (byte)1, (byte)tik16Cipher.length, tik16Cipher, ksn, kcvInfo);
					step++;
*/					
					
////////////////////////no TLK, TMK,TDK,TAK,TPK Clear or cipher... ///////////////////
					//write TMK clr
					keyInfo.ucSrcKeyType  = PedManager.PED_TLK;
					keyInfo.ucSrcKeyIdx = 0;
					keyInfo.ucDstKeyType = PedManager.PED_TMK; 
					keyInfo.ucDstKeyIdx = 1;
					keyInfo.iDstKeyLen  = (byte)tmk24Clr.length;
					System.arraycopy(tmk24Clr, 0, keyInfo.aucDstKeyValue, 0, tmk24Clr.length);

					kcvInfo.iCheckMode = 0;//2;	//NOTE: int this mode, every byte of the key MUST be odd parity!!!
					kcvInfo.aucCheckBuf[0] = 4;
					byte[] kcvGenFrom = {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, 
										 (byte)0x90, (byte)0x12, (byte)0x34, (byte)0x56, 
					};
					kcv = TDES.encryptMode(tmk24Clr, kcvGenFrom);
					Log.i(TAG, "kcv is: " + Utils.byte2HexStr(kcv, 0, kcv.length));
					System.arraycopy(kcv, 0, kcvInfo.aucCheckBuf, 1, 4);
					
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;

					//write TPK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TPK; 
					keyInfo.ucDstKeyIdx = TPK_IDX;
					keyInfo.iDstKeyLen  = (byte)tpk24Cipher.length;
					System.arraycopy(tpk24Cipher, 0, keyInfo.aucDstKeyValue, 0, tpk24Cipher.length);
					kcvInfo.iCheckMode = 0;//3;
					kcvGenFrom = new String("12345678").getBytes();
					kcvInfo.aucCheckBuf[0] = (byte)kcvGenFrom.length;
					System.arraycopy(kcvGenFrom, 0, kcvInfo.aucCheckBuf, 1, kcvGenFrom.length);
					byte kcvMacMode = (byte)0;
					kcvInfo.aucCheckBuf[1 + kcvGenFrom.length] = kcvMacMode;
					kcvInfo.aucCheckBuf[2 + kcvGenFrom.length] = (byte)8;
					
					byte[] toMac = new byte[tpk24Cipher.length + kcvGenFrom.length];
					System.arraycopy(tpk24Cipher, 0, toMac, 0, tpk24Cipher.length);
					System.arraycopy(kcvGenFrom, 0, toMac, tpk24Cipher.length, kcvGenFrom.length);
					byte[] kcvMac = MAC.calc(tmk24Clr, toMac, kcvMacMode);	//NOTE: MUST use it's src key to calc mac, this is diff from mode 1 and 2 !
					System.arraycopy(kcvMac, 0, kcvInfo.aucCheckBuf, 3 + kcvGenFrom.length, 8);
					
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//write TAK clear
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 0;
					keyInfo.ucDstKeyType = PedManager.PED_TAK; 
					keyInfo.ucDstKeyIdx = TAK_IDX;
					keyInfo.iDstKeyLen  = (byte)tak24Clr.length;
					System.arraycopy(tak24Clr, 0, keyInfo.aucDstKeyValue, 0, tak24Clr.length);
					kcvInfo.iCheckMode = 0;
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//write TDK cipher
					keyInfo.ucSrcKeyType  = PedManager.PED_TMK;
					keyInfo.ucSrcKeyIdx = 1;
					keyInfo.ucDstKeyType = PedManager.PED_TDK; 
					keyInfo.ucDstKeyIdx = TDK_IDX;
					keyInfo.iDstKeyLen  = (byte)tdk24Cipher.length;
					System.arraycopy(tdk24Cipher, 0, keyInfo.aucDstKeyValue, 0, tdk24Cipher.length);
					kcvInfo.iCheckMode = 0;
					ped.pedWriteKey(keyInfo, kcvInfo);
					step++;
					
					//TIK 16, clear
					byte[] tik16Clr = {
						(byte)0x6A, (byte)0xC2, (byte)0x92, (byte)0xFA, (byte)0xA1, (byte)0x31, (byte)0x5B, (byte)0x4D,
						(byte)0x85, (byte)0x8A, (byte)0xB3, (byte)0xA3, (byte)0xD7, (byte)0xD5, (byte)0x93, (byte)0x3A						
					};					
					byte[] tik16Cipher = TDES.encryptMode(tlk24Clr, tik16Clr);
					
					byte[] ksn = {
						(byte)0xff, (byte)0xff, (byte)0x98, (byte)0x76,
						(byte)0x54, (byte)0x32, (byte)0x10, (byte)0xE0,
						(byte)0x00, (byte)0x00
					};

					//write TIK clear
					kcvInfo.iCheckMode = 0;
					ped.pedWriteTIK((byte)TIK_IDX, (byte)0, (byte)tik16Clr.length, tik16Clr, ksn, kcvInfo);
					step++;

///////////////////////////////////////////////////////////////////////////////////////
					
					
					ui.scrCls();
					ui.scrShowText("%P1010%F1Verifying...");					
					//verify tdk function
					//enc
					byte[] dataEncrypted = ped.pedCalcDES(TDK_IDX, clearDataToTest.getBytes(), clearDataToTest.getBytes().length, (byte)1);
					
					byte[] manualData = new byte[dataEncrypted.length];
					System.arraycopy(clearDataToTest.getBytes(), 0, manualData, 0, clearDataToTest.getBytes().length);
					byte[] manualEnc = TDES.encryptMode(tdk24Clr, manualData);
					Log.i(TAG, "manual : " + Utils.byte2HexStr(manualEnc, 0, manualEnc.length));
					Log.i(TAG, "ped:     " + Utils.byte2HexStr(dataEncrypted, 0, manualEnc.length));
					if (Arrays.equals(manualEnc, dataEncrypted)) {
						record += "TDK result verified\n";
					} else {
						record += "TDK result error!\n";
					}
					
					step++;
					//dec
					byte[] dataDecrypted = ped.pedCalcDES(TDK_IDX, dataEncrypted, dataEncrypted.length, (byte)0);
					step++;
					if (new String(dataDecrypted).trim().equals(clearDataToTest)) {
						record += "TDK works!\n";
					}
					else {
						record += "TDK error!\n";
					}
					
					//verify tak function
					byte macMode = (byte)0;
					byte[] mac0 = ped.pedGetMac(TAK_IDX, clearDataToTest.getBytes(), clearDataToTest.getBytes().length, macMode);
					step++;
					byte[] mac0MyCalc = MAC.calc(tak24Clr, clearDataToTest.getBytes(), macMode);
					if (!Arrays.equals(mac0, mac0MyCalc)) {
						record += "TAK Mode" + macMode + " error!\n";
					} else {
						record += "TAK Mode" + macMode + " works!\n";
					}
					
					macMode = (byte)1;
					byte[] mac1 = ped.pedGetMac(TAK_IDX, clearDataToTest.getBytes(), clearDataToTest.getBytes().length, macMode);
					step++;
					byte[] mac1MyCalc =  MAC.calc(tak24Clr, clearDataToTest.getBytes(), macMode);
					if (!Arrays.equals(mac1, mac1MyCalc)) {
						record += "TAK Mode" + macMode + " error!\n";
					} else {
						record += "TAK Mode" + macMode + " works!\n";
					}
					
					macMode = (byte)2;
					byte[] mac2 = ped.pedGetMac(TAK_IDX, clearDataToTest.getBytes(), clearDataToTest.getBytes().length, macMode);
					step++;
					byte[] mac2MyCalc =  MAC.calc(tak24Clr, clearDataToTest.getBytes(), macMode);
					if (!Arrays.equals(mac2, mac2MyCalc)) {
						record += "TAK Mode" + macMode + " error!\n";
					} else {
						record += "TAK Mode" + macMode + " works!\n";
					}

					// test M/S pin
					ui.scrCls();
					ui.scrShowText("%F1Input pin:\nM/S, 4 digits, 20s");
					base.beep();
					String expPinLen = "4";
					String pan = "4012345678909";
					//calc pin block
					byte pinMode = 0;
		    		byte[] pinData = new byte[16];
		    		if (!extractPan(pan.getBytes(), pinData)) {
		    			throw new RuntimeException("invalid pan");
		    		}					

		    		ui.scrShowText("%P1020");
		    		byte[] pinBlockMs = new byte[0];	//have to initialize for later use.
		    		try {
		    			pinBlockMs = ped.pedGetPinBlock(TPK_IDX, expPinLen, pinData, pinMode, 20000);
		    		} catch (PedException e) {
		    			if (e.exceptionCode == PedException.PED_ERR_INPUT_TIMEOUT) {
		    				record += "M/S get pin timeout\n";
		    			} else if (e.exceptionCode == PedException.PED_ERR_INPUT_CANCEL) {
		    				record += "M/S get pin cancelled\n";
		    			} else {
		    				throw e;
		    			}
		    		}
					step++;
					//decrypt it and get the clear pin
					if (pinBlockMs.length > 0) {
						byte[] pbClr0 = TDES.decryptMode(tpk24Clr, pinBlockMs);
						byte[] pinDataBcd = Utils.str2Bcd(new String(pinData));
						byte[] pinClr = Utils.xor(pbClr0, pinDataBcd, 8);
						String pinStr = Utils.bcd2Str(pinClr);
						int pinLen = pinClr[0];
						pinStr = pinStr.substring(2, 2 + pinLen);
						ui.scrCls();
						ui.scrShowText("%F1Mode 0: Did you input" + pinStr + "?");
						SystemClock.sleep(2000);
					}
					
					// test DUKPT pin
					ui.scrCls();
					ui.scrShowText("%F1Input pin:\nDUKPT, 4 digits, 20s\ninput 1234 for verify");
					base.beep();
					
					PinDukptOutput pdo = null;
					byte[] ksnCmp = {
							(byte)0xff, (byte)0xff, (byte)0x98, (byte)0x76,
							(byte)0x54, (byte)0x32, (byte)0x10, (byte)0xE0,
							(byte)0x00, (byte)0x01
						};					
					pinMode = 0;
					ui.scrShowText("%P1020");
					try {
						pdo = ped.pedGetPinDukpt(TIK_IDX, expPinLen, pinData, pinMode, 20000);
					} catch (PedException e) {
		    			if (e.exceptionCode == PedException.PED_ERR_INPUT_TIMEOUT) {
		    				record += "DUKPT get pin timeout\n";
		    			} else if (e.exceptionCode == PedException.PED_ERR_INPUT_CANCEL) {
		    				record += "DUKPT get pin cancelled\n";
		    			} else {
		    				throw e;
		    			}
		    		}
					step++;
					//input PIN 1234, you'll get the following pin block if correct.
					byte[] pinBlockCmp = {(byte)0x1B, (byte)0x9C, (byte)0x18, (byte)0x45, (byte)0xEB, (byte)0x99, (byte)0x3A, (byte)0x7A}; 
					if (Arrays.equals(pdo.pinBlockOut, pinBlockCmp)) {
						record += "DUKPT PIN works!\n";
					} else {
						//record += "DUKPT PIN can only be verified for the 1st time for PIN 1234.\n";
					}
					if (Arrays.equals(pdo.ksnOut, ksnCmp)) {
						record += "DUKPT ksn correct!\n";
					} else {
						//record += "DUKPT ksn can only be verified for the 1st time!\n";
					}

					ui.scrCls();
					ui.scrShowText("%P1010%F1Testing RSA ");
					//test rsa.
					final byte RSA_PUK_IDX = 1;
					final byte RSA_PVK_IDX = 2;
					
					//gen key pair
					Map<String, Object> map = RSAUtil.genKeyPair(1024);
					RSAPublicKey publicKey = (RSAPublicKey)map.get("RSAPublicKey");
					RSAPrivateKey privateKey = (RSAPrivateKey)map.get("RSAPrivateKey");
					
					//fill pub key info into ST_RSA_KEY
					ST_RSA_KEY rsaKey = new ST_RSA_KEY();
					byte[] pukExp = publicKey.getPublicExponent().toByteArray();
					rsaKey.iExponentLen = pukExp.length * 8;
					Log.i(TAG, "pukExp LEN is : " + pukExp.length);
					System.arraycopy(pukExp, 0, rsaKey.aucExponent, 0, pukExp.length);
					byte[] mod = publicKey.getModulus().toByteArray();
					int modLen = mod.length;
					int modOff = 0;
					if (mod[0] == (byte)0) {
						modLen--;
						modOff++;
					}
					rsaKey.iModulusLen = modLen * 8;
					Log.i(TAG, "pukModulus LEN is : " + mod.length);
					Log.i(TAG, "modulus is :" + Utils.byte2HexStr(mod, 0, mod.length));
					System.arraycopy(mod, modOff, rsaKey.aucModulus, 0, modLen);
					
					//write pub key
					byte[] rsaPukKeyInfo = new String("This is PUBLIC KEY for test").getBytes();
					System.arraycopy(rsaPukKeyInfo, 0, rsaKey.aucKeyInfo, 0, rsaPukKeyInfo.length); 
					ped.pedWriteRsaKey(RSA_PUK_IDX, rsaKey);
					step++;

					//fill private key info into ST_RSA_KEY
					byte[] pvkExp = privateKey.getPrivateExponent().toByteArray();
					Log.i(TAG, "pvkExp LEN is : " + pvkExp.length);
					Log.i(TAG, "private exp is : " + Utils.byte2HexStr(pvkExp, 0, pvkExp.length));
					int expLen = pvkExp.length;
					int expOff = 0;
					if (pvkExp[0] == (byte)0) {
						expLen--;
						expOff++;
					}
					rsaKey.iExponentLen = expLen * 8;
					System.arraycopy(pvkExp, expOff, rsaKey.aucExponent, 0, expLen);
					//rsaKey.iModulusLen = modLen * 8;	//use previous one
					//System.arraycopy(mod, modOff, rsaKey.aucModulus, 0, modLen);
					
					//write priv key
					byte[] rsaPvkKeyInfo = new String("This is PRIVATE KEY for test").getBytes();
					System.arraycopy(rsaPvkKeyInfo, 0, rsaKey.aucKeyInfo, 0, rsaPvkKeyInfo.length); 
					ped.pedWriteRsaKey(RSA_PVK_IDX, rsaKey);
					step++;

					byte[] dataToRecover = new byte[modLen];
					Utils.randomBytes(dataToRecover);
					dataToRecover[0] = 0x00;	//to ensure the BIG INTEGER smaller than modulus
					
					RsaRecoverOutput rroEnc = ped.pedRsaRecover(RSA_PUK_IDX, dataToRecover);
					step++;
					record += "key info: " +  new String(rroEnc.pucKeyInfo).trim() + "\n";
					
					RsaRecoverOutput rroDec = ped.pedRsaRecover(RSA_PVK_IDX, rroEnc.pucData);
					step++;
					record += "key info: " +  new String(rroDec.pucKeyInfo).trim() + "\n";
					
					if (Arrays.equals(rroDec.pucData, dataToRecover)) {
						record += "RSA works!\n";
					} else {
						record += "RSA failed!\n";
					}

					ui.scrCls();
					ui.scrShowText("%P1010%F1Ped test end");
					
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
				}catch(Exception exception){
					exception.printStackTrace();
					Bundle bundle = new Bundle();
					bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} finally {
					progressDialog.dismiss();
				}
			}
		}).start();
	}
	

}
