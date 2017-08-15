package com.pax.dxxtest;

import com.pax.dxxtest.R;
import com.pax.dxxtest.MsrActivity;

import utils.TDES;
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

import com.pax.mposapi.DataModel.DataWithEncryptionMode;
import android.util.Log;
import com.pax.mposapi.util.Utils;

import com.pax.mposapi.MagManager;
import com.pax.mposapi.UIManager;

public class MsrActivity extends Activity {

	private String mInterface;
	private String mMethod;
	private String mNo;
	private MagManager msr;
	private UIManager ui;
	private TextView text;
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_base);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);	
	    // TODO Auto-generated method stub
		
	    Intent intent = getIntent();
        mInterface = intent.getStringExtra("Interface");
        mMethod = intent.getStringExtra("Method");
        mNo = intent.getStringExtra("No");
        
        TextView tv = (TextView)findViewById(R.id.base_title);
        tv.setText(mInterface + "_" + mMethod + mNo);
		
	   
	    msr = MagManager.getInstance(this);
	    ui = UIManager.getInstance(this);
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
		progressDialog = new ProgressDialog(MsrActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
				
		new Thread(new Runnable(){
		
			public void run(){
				Looper.prepare();	
				if(mInterface.equals("MagOpen"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestMagOpenFUN1();
						}
					
					}
				}
				else if(mInterface.equals("MagSwiped"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestMagSwipedFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestMagSwipedFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestMagSwipedFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestMagSwipedFUN4();
						}
						else if(mNo.equals("5"))
						{
							TestMagSwipedFUN5();
						}
					}
				}
				else if(mInterface.equals("MagRead"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestMagReadFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestMagReadFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestMagReadFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestMagReadFUN4();
						}
						else if(mNo.equals("5"))
						{
							TestMagReadFUN5();
						}
					}
				}
				else if(mInterface.equals("MagClose"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestMagCloseFUN1();
						}
					}
				}
				else if(mInterface.equals("MagReset"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestMagResetFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestMagResetFUN2();
						}
					
					}
				}
				
			}
		}).start();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  finish();
		  }
		  return false;
	}	
	private void TestMagOpenFUN1()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			long countDown = 20000;
			long end = System.currentTimeMillis() + countDown;
			while (!msr.magSwiped() && (countDown = (end - System.currentTimeMillis())) > 0)  {
				ui.scrShowText("%P1020%F0" + String.format("%02d", (int)(countDown / 1000)));
				SystemClock.sleep(300);
			}
			
			if (countDown <= 0) {
				record += "Timed out";
			}
			else {
				String[] tracks = msr.magRead();
				
				record += "Track1: " + tracks[0] + "\n\n";
				record += "Track2: " + tracks[1] + "\n\n";
				record += "Track3: " + tracks[2] + "\n\n";
				
			}
			msr.magClose();
			record += "MagOpen test end"; 

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
	private void TestMagSwipedFUN1()
	{
		String record = "";
		int step = 0;
		try{
		
				ui.scrCls();
				ui.scrShowText("%P1010%F1Swipe Card >>>");
				
				msr.magOpen();
				
				long countDown = 20000;
				long end = System.currentTimeMillis() + countDown;
				while (!msr.magSwiped() && (countDown = (end - System.currentTimeMillis())) > 0)  {
					ui.scrShowText("%P1020%F0" + String.format("%02d", (int)(countDown / 1000)));
					SystemClock.sleep(300);
				}
				
				if (countDown <= 0) {
					record += "Timed out";
				}
				else {
					boolean bret = msr.magSwiped();
					record +="magSwiped:" + bret +"\n";
					
					boolean bret1 = msr.magSwiped();
					record +="magSwiped:" + bret1 +"\n";
					
					boolean bret2 = msr.magSwiped();
					record +="magSwiped:" + bret2 +"\n";
					
					boolean bret3 = msr.magSwiped();
					record +="magSwiped:" + bret3 +"\n";
				}
				msr.magClose();
				
				ui.scrCls();
				ui.scrShowText("%P1010%F1msr test end.");
			
			record += "magSwiped test end"; 

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
	private void TestMagSwipedFUN2()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			
		
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
				
			boolean bret1 = msr.magSwiped();
			record +="magSwiped:" + bret1 +"\n";
				
			boolean bret2 = msr.magSwiped();
			record +="magSwiped:" + bret2 +"\n";
				
			boolean bret3 = msr.magSwiped();
			record +="magSwiped:" + bret3 +"\n";
			
			msr.magClose();
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1msr test end.");
		
			record += "magSwiped test end"; 

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
	private void TestMagSwipedFUN3()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			
			SystemClock.sleep(2000);
			
			ui.scrCls();
			ui.scrShowText("msgread<<");
			String[] tracks = msr.magRead();
				
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
				
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
					
			boolean bret1 = msr.magSwiped();
			record +="magSwiped:" + bret1 +"\n";
				
			
			msr.magClose();
			record += "MagOpen test end"; 

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
	private void TestMagSwipedFUN4()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			SystemClock.sleep(2000);
			
			ui.scrCls();
			ui.scrShowText("%open again >>>");
			msr.magOpen();
			
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
					
			boolean bret1 = msr.magSwiped();
			record +="magSwiped:" + bret1 +"\n";
			
			ui.scrCls();
			ui.scrShowText("%Swipe Card again >>>");	
			SystemClock.sleep(2000);
			boolean bret2 = msr.magSwiped();
			record +="magSwiped:" + bret2 +"\n";
					
			boolean bret3 = msr.magSwiped();
			record +="magSwiped:" + bret3 +"\n";
			
			msr.magClose();
			record += "magSwiped test end"; 

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
	private void TestMagSwipedFUN5()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			SystemClock.sleep(2000);
			
			ui.scrCls();
			ui.scrShowText("%reset >>>");
			msr.magReset();
			
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
					
			boolean bret1 = msr.magSwiped();
			record +="magSwiped:" + bret1 +"\n";
			
			msr.magClose();
			record += "Magswiped test end"; 

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
	private void TestMagReadFUN1()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			msr.magReset();
			long countDown = 20000;
			long end = System.currentTimeMillis() + countDown;
			while (!msr.magSwiped() && (countDown = (end - System.currentTimeMillis())) > 0)  {
				ui.scrShowText("%P1020%F0" + String.format("%02d", (int)(countDown / 1000)));
				SystemClock.sleep(300);
			}
			
			if (countDown <= 0) {
				record += "Timed out";
			}
			else {
				String[] tracks = msr.magRead();
				
				record += "Track1: " + tracks[0] + "\n\n";
				record += "Track2: " + tracks[1] + "\n\n";
				record += "Track3: " + tracks[2] + "\n\n";
				
			}
			msr.magClose();
			record += "Magread test end"; 

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
	private void TestMagReadFUN2()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			msr.magReset();
			
			SystemClock.sleep(10000);
			ui.scrCls();
			ui.scrShowText("Swipe Card 4times>>>");
			
			boolean bret = msr.magSwiped();
			record +="msgswiped:" + bret + "\n";
		
				String[] tracks = msr.magRead();
				
				record += "Track1: " + tracks[0] + "\n\n";
				record += "Track2: " + tracks[1] + "\n\n";
				record += "Track3: " + tracks[2] + "\n\n";
				
		
			msr.magClose();
			record += "Magread test end"; 


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
	private void TestMagReadFUN3()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			
			
			msr.magOpen();
			msr.magReset();
			
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			SystemClock.sleep(2000);
			
		
				String[] tracks = msr.magRead();
				
				record += "Track1: " + tracks[0] + "\n\n";
				record += "Track2: " + tracks[1] + "\n\n";
				record += "Track3: " + tracks[2] + "\n\n";
				
		
			msr.magClose();
			record += "Magread test end"; 
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
	private void TestMagReadFUN4()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			msr.magReset();
			
			SystemClock.sleep(2000);
			ui.scrCls();
			
			
			boolean bret = msr.magSwiped();
			record +="msgswiped:" + bret + "\n";
		
			String[] tracks = msr.magRead();
				
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
				
			String[] tracks1 = msr.magRead();
			
			record += "Track1: " + tracks1[0] + "\n\n";
			record += "Track2: " + tracks1[1] + "\n\n";
			record += "Track3: " + tracks1[2] + "\n\n";
		
			msr.magClose();
			record += "Magread test end"; 

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
	private void TestMagReadFUN5()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			msr.magOpen();
			msr.magReset();
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			SystemClock.sleep(2000);
			
			boolean bret = msr.magSwiped();
			record +="msgswiped:" + bret + "\n";
		
			String[] tracks = msr.magRead();	
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
				
		
			msr.magClose();
			record += "Magread test end"; 

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
	private void TestMagCloseFUN1()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			msr.magOpen();
			msr.magReset();
			ui.scrShowText("%P1010%F1close >>>");
			msr.magClose();
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			SystemClock.sleep(2000);
			
			boolean bret = msr.magSwiped();
			record +="msgswiped:" + bret + "\n";
		
			String[] tracks = msr.magRead();	
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
				
		
			
			record += "Magread test end"; 

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
	private void TestMagResetFUN1()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			SystemClock.sleep(2000);
			
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
					
			ui.scrCls();
			ui.scrShowText("%reset >>>");
			msr.magReset();
			
			String[] tracks = msr.magRead();	
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
			msr.magClose();
			record += "Magswiped test end"; 
 

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
	private void TestMagResetFUN2()
	{
		String record = "";
		int step = 0;
		try{
			
			ui.scrCls();
			ui.scrShowText("%P1010%F1Swipe Card >>>");
			
			msr.magOpen();
			SystemClock.sleep(2000);
			
			boolean bret = msr.magSwiped();
			record +="magSwiped:" + bret +"\n";
					
			ui.scrCls();
			ui.scrShowText("%reset >>>");
			msr.magReset();
			
			ui.scrShowText("wait swipe card >>>");
			SystemClock.sleep(2000);
			String[] tracks = msr.magRead();	
			record += "Track1: " + tracks[0] + "\n\n";
			record += "Track2: " + tracks[1] + "\n\n";
			record += "Track3: " + tracks[2] + "\n\n";
			msr.magClose();
			record += "Magswiped test end";  

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
		
}
