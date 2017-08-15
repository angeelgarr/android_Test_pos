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

public class FunctionActivity extends ListActivity {
	
	private String mModule;
	private String mInterface;
	private String mMethod;
	private String mFuncNum;
	//private String fFuncNum;
	private String[] mFun1 = {
            "1.FUN1"
    };
	private String[] mFun2 = {
            "1.FUN1",
            "2.FUN2"
    };
	private String[] mFun3 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3"
    };
	private String[] mFun4 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4"
    };
	private String[] mFun5 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5"
    };
	private String[] mFun6 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6"
    };
	private String[] mFun7 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7"
    };
	private String[] mFun8 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8"
    };
	private String[] mFun9 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9"
    };
	private String[] mFun10 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
    };
	private String[] mFun11 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
    };
	private String[] mFun12 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
    };
	private String[] mFun13 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
    };
	private String[] mFun14 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
    };
	private String[] mFun15 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
    };
	private String[] mFun16 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16"
    };
	private String[] mFun17 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17"
    };
	private String[] mFun18 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18"
    };
	private String[] mFun19 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19"
    };
	private String[] mFun20 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20"
    };
	private String[] mFun21 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21"
    };
	private String[] mFun22 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22"
    };
	private String[] mFun23 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23"
    };
	private String[] mFun24 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24"
    };
	private String[] mFun25 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25"
    };
	private String[] mFun26 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25",
            "26.FUn26"
    };
	private String[] mFun27 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25",
            "26.FUn26",
            "27.FUn27"
            
    };
	private String[] mFun28 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25",
            "26.FUn26",
            "27.FUn27",
            "28.FUn28"
            
    };
	private String[] mFun29 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25",
            "26.FUn26",
            "27.FUn27",
            "28.FUn28",
            "29.FUn29"
    };
	private String[] mFun30 = {
            "1.FUN1",
            "2.FUN2",
            "3.FUN3",
            "4.FUN4",
            "5.FUN5",
            "6.FUN6",
            "7.FUN7",
            "8.FUN8",
            "9.FUN9",
            "10.FUN10",
            "11.FUN11",
            "12.FUN12",
            "13.FUN13",
            "14.FUN14",
            "15.FUN15",
            "16.FUN16",
            "17.FUN17",
            "18.FUN18",
            "19.FUN19",
            "20.FUn20",
            "21.FUn21",
            "22.FUn22",
            "23.FUn23",
            "24.FUn24",
            "25.FUn25",
            "26.FUn26",
            "27.FUn27",
            "28.FUn28",
            "29.FUn29",
            "30.FUn30"
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        
        Intent intent = getIntent();
        mModule = intent.getStringExtra("Module");
        mInterface = intent.getStringExtra("Interface");
        mMethod = intent.getStringExtra("Method");
        mFuncNum = intent.getStringExtra("FuncNum");
    //    fFuncNum = intent.getStringExtra("FuncNum");
        intent.getStringExtra("TolNum");
        intent.getStringExtra("StrNum");
        
        TextView tv = (TextView)findViewById(R.id.list_title);
        tv.setText(mInterface);
        
//        if(mMethod.equals("FUN"))
//        {
        	if(mFuncNum.equals("1"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun1));
        	}
        	else if(mFuncNum.equals("2"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun2));
        	}
        	else if(mFuncNum.equals("3"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun3));
        	}
        	else if(mFuncNum.equals("4"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun4));
        	}
        	else if(mFuncNum.equals("5"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun5));
        	}
        	else if(mFuncNum.equals("6"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun6));
        	}
        	else if(mFuncNum.equals("7"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun7));
        	}
        	else if(mFuncNum.equals("8"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun8));
        	}
        	else if(mFuncNum.equals("9"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun9));
        	}
        	else if(mFuncNum.equals("10"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun10));
        	}
        	else if(mFuncNum.equals("11"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun11));
        	}
        	else if(mFuncNum.equals("12"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun12));
        	}
        	else if(mFuncNum.equals("13"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun13));
        	}
        	else if(mFuncNum.equals("14"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun14));
        	}
        	else if(mFuncNum.equals("15"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun15));
        	}
        	else if(mFuncNum.equals("16"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun16));
        	}
        	else if(mFuncNum.equals("17"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun17));
        	}
        	else if(mFuncNum.equals("18"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun18));
        	}
        	else if(mFuncNum.equals("19"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun19));
        	}
        	else if(mFuncNum.equals("20"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun20));
        	}
        	else if(mFuncNum.equals("21"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun21));
        	}
        	else if(mFuncNum.equals("22"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun22));
        	}
        	else if(mFuncNum.equals("23"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun23));
        	}
        	else if(mFuncNum.equals("24"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun24));
        	}
        	else if(mFuncNum.equals("25"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun25));
        	}
        	else if(mFuncNum.equals("26"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun26));
        	}
        	else if(mFuncNum.equals("27"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun27));
        	}
        	else if(mFuncNum.equals("28"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun28));
        	}
        	else if(mFuncNum.equals("29"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun29));
        	}
        	else if(mFuncNum.equals("30"))
        	{
        		setListAdapter(new MainListAdapter(this, mFun30));
        	}
        }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
    	Intent intent;
    	
    	if(mModule.equals("LCD"))
    	{
			intent = new Intent(FunctionActivity.this, LcdActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("KEYBOARD"))
    	{
			intent = new Intent(FunctionActivity.this, KeyboardActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("BASE"))
    	{
			intent = new Intent(FunctionActivity.this, BaseActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
				break;
			case 8:	//5
				intent.putExtra("No", "9");
				break;
			case 9:	//6
				intent.putExtra("No", "10");
				break;
			case 10:	
				intent.putExtra("No", "11");
				break;
			case 11:	
				intent.putExtra("No", "12");
				break;
			case 12:	
				intent.putExtra("No", "13");
				break;
			case 13:	
				intent.putExtra("No", "14");
				break;
			case 14:	
				intent.putExtra("No", "15");
				break;
			case 15:	
				intent.putExtra("No", "16");
				break;
			case 16:	
				intent.putExtra("No", "17");
				break;
			case 17:	
				intent.putExtra("No", "18");
				break;
			case 18:	
				intent.putExtra("No", "19");
				break;
			case 19:	
				intent.putExtra("No", "20");
				break;
			case 20:	
				intent.putExtra("No", "21");
				break;
			case 21:	
				intent.putExtra("No", "22");
				break;
			case 22:	
				intent.putExtra("No", "23");
				break;
			case 23:	
				intent.putExtra("No", "24");
				break;
			case 24:	
				intent.putExtra("No", "25");
				break;
			case 25:	
				intent.putExtra("No", "26");
				break;
			case 26:	
				intent.putExtra("No", "27");
				break;
			case 27:	
				intent.putExtra("No", "28");
				break;
			case 28:	
				intent.putExtra("No", "29");
				break;
			case 29:	
				intent.putExtra("No", "30");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("EMVPARA"))
    	{
			intent = new Intent(FunctionActivity.this, EmvParaActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
				break;
			case 8:	//5
				intent.putExtra("No", "9");
				break;
			case 9:	//6
				intent.putExtra("No", "10");
				break;
			case 10:	
				intent.putExtra("No", "11");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("ICC"))
    	{
			intent = new Intent(FunctionActivity.this, IccActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
				break;
			default:
				return;
	    	}
    	}
    	else if(mModule.equals("CLSS"))
    	{
			intent = new Intent(FunctionActivity.this, ClssActivity.class);
			intent.putExtra("Interface", mInterface);
			intent.putExtra("Method", mMethod);
			
			switch (position) {
			case 0:	//1
				intent.putExtra("No", "1");
				break;
			case 1:	//2
				intent.putExtra("No", "2");
				break;
			case 2:	//3
				intent.putExtra("No", "3");
				break;
			case 3:	//4
				intent.putExtra("No", "4");
				break;
			case 4:	//5
				intent.putExtra("No", "5");
				break;
			case 5:	//6
				intent.putExtra("No", "6");
				break;
			case 6:	//7
				intent.putExtra("No", "7");
				break;
			case 7:	//8
				intent.putExtra("No", "8");
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
