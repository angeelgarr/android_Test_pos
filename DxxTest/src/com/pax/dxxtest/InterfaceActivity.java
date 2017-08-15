package com.pax.dxxtest;

import com.pax.dxxtest.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InterfaceActivity extends ListActivity {
	
	private String mModule;
	private String[] mLcd = {
            "1.ScrCls",
            "2.ScrProcessImage",
            "3.ScrShowText",
//            "4.promptMenu",
//            "5.promptMessage"
    };
	private String[] mKeyboard = {
            "1.KbCheck",
            "2.KbGetHzSting",
            "3.KbGetString",
            "4.Kblight",
            "5.Kbhit",
            "6.Kbflush",
            "7.KbGetkey",
            "8.Kbmute"
    };
	private String[] mBase = {
			"1.Open",
            "2.Close",
            "3.abort",
            "4.setSessionKey",
            "5.enableKeypad",
            "6.disableKeyPad",     
            "7.promptPIN",
//            "8.downLoadPara",
//            "9.downLoadApp",
//            "10.downLoadCAPK",
//            "11.deleteAllAPP",
//            "12.deleteAllCAPK",
           
            "8.downloadFile_APP",
            "9.downloadFile_EMVPARA",
            "10.downloadKey",
            "11.getEncryptedData",
            "12.readCardDataSwipe",
            "13.readCardDataManualEntry",
            "14.readCardDataAll",
            "15.promptMenu",
            "16.promptMessage",
            "17.getBatteryLevel",
            "18.setLowBatteryThreshold",
            "19.getLowBatteryThreshold",
            "20.setEMVTags",
            "21.getEMVTags",
            "22.createMAC",
            "23.validateMAC",
            "24.authorizeCard",
            "25.completeOnLineEMV",
            "26.readCardDataIC",
            "27.ICTest",
           // "28.EMVTest",
            "28.promptAdditionalInfo",
            "29.removeCard",
            "30.setParameter",
            "31.getParameter",
            "32.getDateTime",
            "33.setDateTime",
            "34.getStatusUpdate",
            "35.downloadFile_FONT",
          
    };
	private String[] mEmvpara = {
			"1.EMVInitializeParameter",
			"2.EMVAddAIDParameter",
			"3.EMVGetTotalAIDNumber",
			"4.EMVGetAIDParameter",
			"5.EMVDeleteAIDParameter",
			"6.EMVSetParameter",
			"7.EMVGetParameter",
			"8.EMVAddCAPK",
			"9.EMVGetTotalCAPKNumber",
			"10.EMVGetCAPK",
			"11.EMVDelCAPK"
	};
	private String[] mIcc = {
            "1.IccAutoResp",
            "2.IccClose",
            "3.IccDetect",
            "4.IccInit",
            "5.IccIsoCommand",
            "6.IccDetectExt",
            "7.DownloadCAPK",
            "8.DownloadAPP",
            "9.DownloadParam"
    };
	private String[] mMag = {
            "1.MagOpen",
            "2.MagSwiped",
            "3.MagRead",
            "4.MagClose",
            "5.MagReset"
    };
	private String[] mClss = {
            "1.DownloadCAPK",
            "2.DownloadAPP",
            "3.DownloadParam",
            "4.ClssTrans"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        
        Intent intent = getIntent();
        mModule = intent.getStringExtra("Module");
        
        TextView tv = (TextView)findViewById(R.id.list_title);
        tv.setText(mModule);
        
        if(mModule.equals("LCD"))
        {
        	setListAdapter(new MainListAdapter(this, mLcd));
        }
        else if(mModule.equals("KEYBOARD"))
        {
        	setListAdapter(new MainListAdapter(this, mKeyboard));
        }
        else if(mModule.equals("BASE"))
        {
        	setListAdapter(new MainListAdapter(this, mBase));
        }
        else if(mModule.equals("EMVPARA"))
        {
        	setListAdapter(new MainListAdapter(this, mEmvpara));
        }
        else if(mModule.equals("ICC"))
        {
        	setListAdapter(new MainListAdapter(this, mIcc));
        }
        else if(mModule.equals("MAG"))
        {
        	setListAdapter(new MainListAdapter(this, mMag));
        }
        else if(mModule.equals("CLSS"))
        {
        	setListAdapter(new MainListAdapter(this, mClss));
        }

    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
    	Intent intent;

		intent = new Intent(InterfaceActivity.this, FunctionActivity.class);
		intent.putExtra("Module", mModule);
		
    	if(mModule.equals("LCD"))
    	{
	    	switch (position) {
			case 0:
				intent.putExtra("Interface", "ScrCls");
				intent.putExtra("FuncNum", "1");
				break;
			case 1:
				intent.putExtra("Interface", "ScrProcessImage");
				intent.putExtra("FuncNum", "3");
				break;
			case 2:
				intent.putExtra("Interface", "ScrShowText");
				intent.putExtra("FuncNum", "8");
				break;
//			case 3:
//				intent.putExtra("Interface", "promptMenu");
//				intent.putExtra("FuncNum", "8");
//				break;
//			case 4:
//				intent.putExtra("Interface", "promptMessage");
//				intent.putExtra("FuncNum", "7");
//				break;
//				
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("KEYBOARD"))
    	{
	    	switch (position) {
			case 0:
				intent.putExtra("Interface", "KbCheck");
				intent.putExtra("FuncNum", "4");
				break;
			case 1:
				intent.putExtra("Interface", "KbGetHzString");
				intent.putExtra("FuncNum", "4");
				break;
			case 2:
				intent.putExtra("Interface", "KbGetString");
				intent.putExtra("FuncNum", "3");
				break;
			case 3:
				intent.putExtra("Interface", "Kblight");
				intent.putExtra("FuncNum", "1");
				break;
			case 4:
				intent.putExtra("Interface", "Kbhit");
				intent.putExtra("FuncNum", "4");
				break;
			case 5:
				intent.putExtra("Interface", "Kbflush");
				intent.putExtra("FuncNum", "2");
				break;
			case 6:
				intent.putExtra("Interface", "KbGetkey");
				intent.putExtra("FuncNum", "4");
				break;
			case 7:
				intent.putExtra("Interface", "Kbmute");
				intent.putExtra("FuncNum", "1");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("BASE"))
    	{
	    	switch (position) {
			case 0:
				intent.putExtra("Interface", "Open");
				intent.putExtra("FuncNum", "13");
				
				break;
			case 1:
				intent.putExtra("Interface", "Close");
				intent.putExtra("FuncNum", "4");
				break;
			case 2:
				intent.putExtra("Interface", "abort");
				intent.putExtra("FuncNum", "9");
				break;
			case 3:
				intent.putExtra("Interface", "setSessionKey");
				intent.putExtra("FuncNum", "4");
				break;
			case 4:
				intent.putExtra("Interface", "enableKeypad");
				intent.putExtra("FuncNum", "3");
				break;
			case 5:
				intent.putExtra("Interface", "disableKeyPad");
				intent.putExtra("FuncNum", "2");
				break;
			case 6:
				intent.putExtra("Interface", "promptPIN");
				intent.putExtra("FuncNum", "8");
				break;
			case 7:
				intent.putExtra("Interface", "downloadFile_APP");
				intent.putExtra("FuncNum", "6");
				break;
			case 8:
				intent.putExtra("Interface", "downloadFile_EMVPARA");
				intent.putExtra("FuncNum", "13");
				break;
			case 9:
				intent.putExtra("Interface", "downloadKey");
				intent.putExtra("FuncNum", "9");
				break;
			case 10:
				intent.putExtra("Interface", "getEncryptedData");
				intent.putExtra("FuncNum", "6");
				break;
			case 11:
				intent.putExtra("Interface", "readCardDataSwipe");
				intent.putExtra("FuncNum", "9");
				break;
			case 12:
				intent.putExtra("Interface", "readCardDataManualEntry");
				intent.putExtra("FuncNum", "4");
				break;
			case 13:
				intent.putExtra("Interface", "readCardDataAll");
				intent.putExtra("FuncNum", "26");
				break;
			case 14:
				intent.putExtra("Interface", "promptMenu");
				intent.putExtra("FuncNum", "10");
				break;
			case 15:
				intent.putExtra("Interface", "promptMessage");
				intent.putExtra("FuncNum", "9");
				break;
			case 16:
				intent.putExtra("Interface", "getBatteryLevel");
				intent.putExtra("FuncNum", "4");
				break;
			case 17:
				intent.putExtra("Interface", "setLowBatteryThreshold");
				intent.putExtra("FuncNum", "10");
				break;
			case 18:
				intent.putExtra("Interface", "getLowBatteryThreshold");
				intent.putExtra("FuncNum", "2");
				break;
			case 19:
				intent.putExtra("Interface", "setEMVTags");
				intent.putExtra("FuncNum", "7");
				break;
			case 20:
				intent.putExtra("Interface", "getEMVTags");
				intent.putExtra("FuncNum", "5");
				break;
			case 21:
				intent.putExtra("Interface", "createMAC");
				intent.putExtra("FuncNum", "7");
				break;
			case 22:
				intent.putExtra("Interface", "validateMAC");
				intent.putExtra("FuncNum", "6");
				break;
			case 23:
				intent.putExtra("Interface", "authorizeCard");
				intent.putExtra("FuncNum", "18");
				break;
			case 24:
				intent.putExtra("Interface", "completeOnLineEMV");
				intent.putExtra("FuncNum", "6");
				break;
			case 25:
				intent.putExtra("Interface", "readCardDataIC");
				intent.putExtra("FuncNum", "8");
				break;
			case 26:
				intent.putExtra("Interface", "ICTest");
				intent.putExtra("FuncNum", "4");
				break;
			case 27:
				intent.putExtra("Interface", "promptAdditionalInfo");
				intent.putExtra("FuncNum", "5");
				break;
			case 28:
				intent.putExtra("Interface", "removeCard");
				intent.putExtra("FuncNum", "2");
				break;
			case 29:
				intent.putExtra("Interface", "setParameter");
				intent.putExtra("FuncNum", "12");
				break;
			case 30:
				intent.putExtra("Interface", "getParameter");
				intent.putExtra("FuncNum", "9");
				break;
			case 31:
				intent.putExtra("Interface", "getDateTime");
				intent.putExtra("FuncNum", "1");
				break;
			case 32:
				intent.putExtra("Interface", "setDateTime");
				intent.putExtra("FuncNum", "1");
				break;
			case 33:
				intent.putExtra("Interface", "getStatusUpdate");
				intent.putExtra("FuncNum", "1");
				break;
			case 34:
				intent.putExtra("Interface", "downloadFile_FONT");
				intent.putExtra("FuncNum", "1");
				break;
			default:
				return;
	    	} 
    	}
    
    	else if(mModule.equals("EMVPARA"))
    	{
    		switch (position){
    		case 0:
    			intent.putExtra("Interface", "EMVInitializeParameter");
				intent.putExtra("FuncNum", "7");
				break;
    		case 1:
    			intent.putExtra("Interface", "EMVAddAIDParameter");
				intent.putExtra("FuncNum", "7");
				break;
    		case 2:
    			intent.putExtra("Interface", "EMVGetTotalAIDNumber");
				intent.putExtra("FuncNum", "5");
				break;
    		case 3:
    			intent.putExtra("Interface", "EMVGetAIDParameter");
				intent.putExtra("FuncNum", "3");
				break;
    		case 4:
    			intent.putExtra("Interface", "EMVDeleteAIDParameter");
				intent.putExtra("FuncNum", "4");
				break;
    		case 5:
    			intent.putExtra("Interface", "EMVSetParameter");
				intent.putExtra("FuncNum", "3");
				break;
    		case 6:
    			intent.putExtra("Interface", "EMVGetParameter");
				intent.putExtra("FuncNum", "2");
				break;
    		case 7:
    			intent.putExtra("Interface", "EMVAddCAPK");
				intent.putExtra("FuncNum", "5");
				break;
    		case 8:
    			intent.putExtra("Interface", "EMVGetTotalCAPKNumber");
				intent.putExtra("FuncNum", "5");
				break;
    		case 9:
    			intent.putExtra("Interface", "EMVGetCAPK");
				intent.putExtra("FuncNum", "2");
				break;
    		case 10:
    			intent.putExtra("Interface", "EMVDelCAPK");
				intent.putExtra("FuncNum", "3");
				break;
    		default:
				return;
    		}
    	}
    	
    	else if(mModule.equals("CLSS"))
    	{
	    	switch (position) {
			case 0:
				intent.putExtra("Interface", "IccAutoResp");
				intent.putExtra("FuncNum", "1");
				break;
			case 1:
				intent.putExtra("Interface", "IccClose");
				intent.putExtra("FuncNum", "1");
				break;
			case 2:
				intent.putExtra("Interface", "IccDetect");
				intent.putExtra("FuncNum", "1");
				break;
			case 3:
				intent.putExtra("Interface", "IccInit");
				intent.putExtra("FuncNum", "1");
				break;
			case 4:
				intent.putExtra("Interface", "IccIsoCommand");
				intent.putExtra("FuncNum", "1");
				break;
			case 5:
				intent.putExtra("Interface", "IccDetectExt");
				intent.putExtra("FuncNum", "1");
				break;
			case 6:
				intent.putExtra("Interface", "DownloadCAPK");
				intent.putExtra("FuncNum", "1");
				break;
			case 7:
				intent.putExtra("Interface", "DownloadAPP");
				intent.putExtra("FuncNum", "1");
				break;
			case 8:
				intent.putExtra("Interface", "DownloadParam");
				intent.putExtra("FuncNum", "1");
				break;
			default:
				return;
	    	} 
    	}
    	else if(mModule.equals("ICC"))
    	{
	    	switch (position) {
			case 0:
				intent.putExtra("Interface", "IccAutoResp");
				intent.putExtra("FuncNum", "1");
				break;
			case 1:
				intent.putExtra("Interface", "IccClose");
				intent.putExtra("FuncNum", "1");
				break;
			case 2:
				intent.putExtra("Interface", "IccDetect");
				intent.putExtra("FuncNum", "1");
				break;
			case 3:
				intent.putExtra("Interface", "IccInit");
				intent.putExtra("FuncNum", "1");
				break;
			case 4:
				intent.putExtra("Interface", "IccIsoCommand");
				intent.putExtra("FuncNum", "1");
				break;
			case 5:
				intent.putExtra("Interface", "IccDetectExt");
				intent.putExtra("FuncNum", "1");
				break;
			case 6:
				intent.putExtra("Interface", "DownloadCAPK");
				intent.putExtra("FuncNum", "1");
				break;
			case 7:
				intent.putExtra("Interface", "DownloadAPP");
				intent.putExtra("FuncNum", "1");
				break;
			case 8:
				intent.putExtra("Interface", "DownloadParam");
				intent.putExtra("FuncNum", "1");
				break;
			default:
				return;
	    	}
    	}
    	else
    	{
    		return;
    	}

		startActivity(intent);
	}

	private class MainListAdapter extends BaseAdapter {
    	private Context mContext;
    	private String[] mStrings;
    	
        public MainListAdapter(Context context, String[] strAry) {
            mContext = context;
            mStrings = strAry;
        }

        public int getCount() {
            return mStrings.length;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return !mStrings[position].startsWith("-");
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = (TextView) LayoutInflater.from(mContext).inflate(
                		android.R.layout.simple_expandable_list_item_1, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(mStrings[position]);
            return tv;
        }
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  finish();
		  }
		  return false;
	}	
}
