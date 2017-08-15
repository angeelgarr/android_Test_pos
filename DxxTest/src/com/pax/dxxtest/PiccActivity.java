package com.pax.dxxtest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.pax.mposapi.BaseSystemManager;
import com.pax.mposapi.PiccManager;
import com.pax.mposapi.PiccManager.PiccCardInfo;
import com.pax.mposapi.UIManager;
import com.pax.mposapi.model.APDU_RESP;
import com.pax.mposapi.model.APDU_SEND;
import com.pax.mposapi.model.PICC_PARA;
import com.pax.mposapi.util.Utils;
import com.pax.dxxtest.R;

public class PiccActivity extends Activity {

	private PiccManager picc;
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
	    picc = PiccManager.getInstance(this);
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
	
	private void test() {
		text = (TextView)findViewById(R.id.textViewBase);		
		progressDialog = new ProgressDialog(PiccActivity.this);
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
					ui.scrCls();
					picc.piccOpen();
					for (int i = 0; i < 5; i++) {
						//turn off all lights
						picc.piccLight((byte)(PiccManager.PICC_LIGHT_BLUE |PiccManager.PICC_LIGHT_YELLOW | PiccManager.PICC_LIGHT_GREEN | PiccManager.PICC_LIGHT_RED), (byte)0);
						SystemClock.sleep(100);
						//turn on all lights
						picc.piccLight((byte)(PiccManager.PICC_LIGHT_BLUE | PiccManager.PICC_LIGHT_YELLOW | PiccManager.PICC_LIGHT_GREEN | PiccManager.PICC_LIGHT_RED), (byte)1);
						SystemClock.sleep(100);
					}
					
					PICC_PARA piccPara = new PICC_PARA();
					picc.piccSetup((byte)'r', piccPara);
					byte[] paraBytes = piccPara.serialToBuffer();
					record += "picc para: " + Utils.byte2HexStr(paraBytes, 0, paraBytes.length);

					ui.scrShowText("%P1010Present Card...");
					
					PiccCardInfo pci = null;
					long countDown = 20000;
					long end = System.currentTimeMillis() + countDown;
					while ((countDown = (end - System.currentTimeMillis())) > 0)  {
						if ((pci = picc.piccDetect((byte)0)) != null) {
							base.beep();
							break;
						}						
						ui.scrShowText("%P1020%F0" + String.format("%02d", (int)(countDown / 1000)));
						SystemClock.sleep(300);
					}		

					if (pci != null) {
						record += String.format("CardType: %s, serialNo: %s, cid: %d, other: %s\n", 
								new String(new byte[]{pci.CardType}), Utils.byte2HexStr(pci.SerialInfo, 0, pci.SerialInfo.length),
								pci.CID, Utils.byte2HexStr(pci.Other, 0, 3 + pci.Other[2]));

						APDU_SEND apduSend = new APDU_SEND();

						byte[] selPpseCmd = {
								(byte)0x00, (byte)0xa4, (byte)0x04, (byte)0x00
						};
						byte[] selPpseData = {
								'2', 'P', 'A', 'Y', '.', 'S', 'Y', 'S', '.', 'D', 'D', 'F', '0', '1' 
						};
						System.arraycopy(selPpseCmd, 0, apduSend.Command, 0, 4);
						apduSend.Lc = (short)selPpseData.length;
						System.arraycopy(selPpseData, 0, apduSend.DataIn, 0, selPpseData.length);
						apduSend.Le = (short)256;
						APDU_RESP apduResp = picc.piccIsoCommand(pci.CID, apduSend);
						record += "ppse sel: " + Utils.byte2HexStr(apduResp.DataOut, 0, apduResp.LenOut) + "\n";
					
						while (!picc.piccRemove((byte)'e', pci.CID)) {
							SystemClock.sleep(1000);
							BaseSystemManager.getInstance(PiccActivity.this).beep();
						}
						record += "picc removed";
					} else {
						record += "No Card Detected\n";
					}
					
					ui.scrCls();
					ui.scrShowText("%P1010%F1Picc test end");
					
					picc.piccClose();
					
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
