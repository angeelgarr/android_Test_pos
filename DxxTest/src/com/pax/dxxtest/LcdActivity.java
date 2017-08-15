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
import android.widget.TextView;
import android.widget.Toast;

import com.pax.dxxtest.R;
import com.pax.mposapi.KeyboardManager;
import com.pax.mposapi.UIManager;

public class LcdActivity extends Activity {
	
	private String mInterface;
	private String mMethod;
	private String mNo;
	
	private UIManager ui;
	private KeyboardManager kbd;
	private TextView text;
	private ProgressDialog progressDialog;
	
	private final int NORMALRESULT = 1;
	private final int EXCEPTRESULT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        Intent intent = getIntent();
        mInterface = intent.getStringExtra("Interface");
        mMethod = intent.getStringExtra("Method");
        mNo = intent.getStringExtra("No");
        
        TextView tv = (TextView)findViewById(R.id.base_title);
        tv.setText(mInterface + "_" + mMethod + mNo);
        
	    ui = UIManager.getInstance(this);
	    kbd = KeyboardManager.getInstance(this);
	    
	    test();
    }

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NORMALRESULT:
			case EXCEPTRESULT:
				Bundle bundle = msg.getData();
				String result = bundle.getString("result");
				text.setText(result);
				break;
			}
	    }

	};

	private void test() {
		text = (TextView)findViewById(R.id.textViewBase);		
		progressDialog = new ProgressDialog(LcdActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
	//	progressDialog.show();
				
		new Thread(new Runnable(){
			
			public void run(){
				Looper.prepare();				
				if(mInterface.equals("ScrCls"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestScrClsFUN1();
						}
					}
				}
				else if(mInterface.equals("ScrProcessImage"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestScrProcessImageFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestScrProcessImageFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestScrProcessImageFUN3();
						}
					}
				}
				else if(mInterface.equals("ScrShowText"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestScrShowTextFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestScrShowTextFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestScrShowTextFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestScrShowTextFUN4();
						}
						else if(mNo.equals("5"))
						{
							TestScrShowTextFUN5();
						}
						else if(mNo.equals("6"))
						{
							TestScrShowTextFUN6();
						}
						else if(mNo.equals("7"))
						{
							TestScrShowTextFUN7();
						}
						else if(mNo.equals("8"))
						{
							TestScrShowTextFUN8();
						}
					}
				}
				else if(mInterface.equals("promptMenu"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestpromptMenuFUN1();
						}
						if(mNo.equals("2"))
						{
							TestpromptMenuFUN2();
						}
						if(mNo.equals("3"))
						{
							TestpromptMenuFUN3();
						}
						if(mNo.equals("4"))
						{
							TestpromptMenuFUN4();
						}
						if(mNo.equals("5"))
						{
							TestpromptMenuFUN5();
						}
						if(mNo.equals("6"))
						{
							TestpromptMenuFUN6();
						}
						if(mNo.equals("7"))
						{
							TestpromptMenuFUN7();
						}
						if(mNo.equals("8"))
						{
							TestpromptMenuFUN8();
						}
					}
				}
				else if(mInterface.equals("promptMessage"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestpromptMessageFUN1();
						}
						if(mNo.equals("2"))
						{
							TestpromptMessageFUN2();
						}
						if(mNo.equals("3"))
						{
							TestpromptMessageFUN3();
						}
						if(mNo.equals("4"))
						{
							TestpromptMessageFUN4();
						}
						if(mNo.equals("5"))
						{
							TestpromptMessageFUN5();
						}
						if(mNo.equals("6"))
						{
							TestpromptMessageFUN6();
						}
						if(mNo.equals("7"))
						{
							TestpromptMessageFUN7();
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
	
	private void TestScrClsFUN1()
	{
		String record = "";
		int step = 0;
		try{
			for(int i = 0; i < 3; i++)
			{
				ui.scrShowText("%P0000nkmadfjfgjsoiruifgslaqrpohgmyduihbgdnjktgshioadfgnkfhgisrhgnjkzdfgsiurhtnsrkhfisghrsengkfhgisrhjtgjkfgnfjxfkgnsmzowotltfgtnfmngshi");
				SystemClock.sleep(500);
				step++;
				
				ui.scrCls();
				step++;
				
				kbd.kbGetkey(3000);
				step++;
			}
			
			record += "LCD test end"; 

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

	private void TestpromptMenuFUN1()
	{
		String record = "";

		int step = 0;
		Toast toast=Toast.makeText(LcdActivity.this,"Test -------000000", Toast.LENGTH_LONG);
		toast.show();
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"sale","void","offline","REFUND","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN2()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123a@","D180EMDK","Prompt% 1Menu","[       ]",
		    		"saleA#","(void2)","@o#1ffline","REF&^UND","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
		//	progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN3()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"sale","void","offline","REFUND","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
		//	progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN4()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"sale","void","offline","REFUND","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN5()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"sale","","offline","","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN6()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"","void","","REFUND","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN7()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"","void","","","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMenuFUN8()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu("123","D180EMDK","Prompt Menu","Please Select",
		    		"","","","","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMessageFUN1()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "Prompt Menu", "Select",
					"A", "B", "False","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN2()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "Prompt Menu", "Select",
					"A", "B", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN3()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "Prompt Menu", "Select",
					"A", "B", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN4()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "", "Select",
					"", "", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN5()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "", "",
					"", "", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMessageFUN6()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "Prompt Messsage", "Select aaaaaaaa",
					"Aaaaaaaaaaaaaaaaaaaa", "Bbbbbbbbbbbbbbb", "faLse","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMessageFUN7()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMessage("1", "D180EMDK", "Prompt Message", "Select aaaaaaaa",
					"Aaaaaaaaaaaaaaaaaaaa", "Bbbbbbbbbbbbbbb", "tARuE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestScrProcessImageFUN1()
	{
		String record = "";
		int step = 0;
		try{
			String imageFileName = "1.bmp";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 50, 50);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			imageFileName = "2.jpeg";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 50, 50);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;

			imageFileName = "3.png";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 50, 50);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;

			imageFileName = "7.bmp";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			imageFileName = "8.jpeg";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;

			imageFileName = "9.png";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrProcessImageFUN2()
	{
		String record = "";
		int step = 0;
		try{
			String imageFileName = "2014.bmp";
			String imageFileName1 = "2014.png";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			ui.scrProcessImage(imageFileName1, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			ui.scrProcessImage(imageFileName1, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			ui.scrProcessImage(imageFileName1, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrProcessImageFUN3()
	{
		String record = "";
		int step = 0;
		try{
			String imageFileName = "4.bmp";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_BMP, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			imageFileName = "5.jpeg";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_JPG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;

			imageFileName = "6.png";
			//load
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_LOAD, 0, 0);
			step++;
			//disp
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_DISPLAY, 0, 0);
			step++;
			//clear
			ui.scrProcessImage(imageFileName, UIManager.UI_IMAGE_TYPE_PNG, UIManager.UI_PROCESS_IMAGE_CMD_CLEAN, 0, 0);
			step++;
			
			record += "LCD test end"; 

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
	
	private void TestScrShowTextFUN1()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%P0000mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P0000vkfksrueankfakdfgfsfgsfgdfgnhksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P0000kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P5050mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P5050vkfksrueankfakdfgfsfgsfgdfgnhksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P5050kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN2()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%F0mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F0vkfksrueankfakdfgfsfgsfgdfgnhmvkfksrueankfakdfmvkfksrueankfakdfmvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F0kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1vkfksrueankfakdfgfsfgsfgdfgnheankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2vkfksrueankfakdfgfsfgsfgdfgnhkdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F0鑾潪璇夋硶瑙勫啘澶�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F0鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F0鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1鑾潪璇夋硶瑙勫啘澶�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F1鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2鑾潪璇夋硶瑙勫啘澶�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏�?");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%F2鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝�?��鎳掑垎鍗℃�鏌旂牬鐑�?��鍝﹂樋娴风彁鍝﹀晩鑾潪璇夋硶瑙勫啘澶珮鎼滅殑鍣㈡斁鍋囦粯娆句簡鐧昏鍝﹀垎鎳掑垎鍗℃�鏌旂牬鐑﹀摝鍝﹂樋娴风彁鍝�?��");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN3()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%R1mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%R0mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%R1vkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%R0vkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%R1kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("%R0kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN4()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\rmvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksru\reankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf\r");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\rvkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdf\rgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh\r");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\rkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsf\rgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh\r");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN5()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\nmvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksru\neankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf\n");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\nvkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdf\ngfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh\n");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\nkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsf\ngdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh\n");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN6()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\fmvkfksrueankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksru\feankfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("mvkfksrueankfakdf\f");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\fvkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdf\fgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksrueankfakdfgfsfgsfgdfgnh\f");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("\fkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsf\fgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh\f");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN7()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%P3030mvkfk%F1sruean%R0kfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksr%P4040ueankfak%F0dfgfsfgs%R1fgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankf%F2akdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgd%R0fgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfg%P5050fsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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

	private void TestScrShowTextFUN8()
	{
		String record = "";
		int step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("\fmvkfk\rsruean\nkfakdf");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("vkfksr\rueankfak\ndfgfsfgs\ffgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			ui.scrCls();
			ui.scrShowText("kfksrueankf\nakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgd\ffgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfg\rfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnhkfksrueankfakdfgfsfgsfgdfgnh");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "LCD test end"; 

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
