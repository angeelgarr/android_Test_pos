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

public class MethodActivity extends ListActivity {
	
	private String mModule;
	private String mInterface;
	private String mFuncNum;
	private String mTolNum;
	private String mStrNum;
	private String[] mFuncs = {
            "1.Func",
            "2.Tol",
            "3.Str"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        
        Intent intent = getIntent();
        mModule = intent.getStringExtra("Module");
        mInterface = intent.getStringExtra("Interface");
        mFuncNum = intent.getStringExtra("FuncNum");
        mTolNum = intent.getStringExtra("TolNum");
        mStrNum = intent.getStringExtra("StrNum");
        
        TextView tv = (TextView)findViewById(R.id.list_title);
        tv.setText(mInterface);
        
        setListAdapter(new MainListAdapter(this));
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
    	Intent intent;

		intent = new Intent(MethodActivity.this, FunctionActivity.class);
		intent.putExtra("Module", mModule);
		intent.putExtra("Interface", mInterface);
		
    	switch (position) {
		case 0:	//Func
			intent.putExtra("Method", "FUN");
			intent.putExtra("FuncNum", mFuncNum);

			break;
		case 1:	//Tol
			intent.putExtra("Method", "TOL");
			intent.putExtra("TolNum", mTolNum);
			break;
		case 2:	//Str
			intent.putExtra("Method", "STR");
			intent.putExtra("StrNum", mStrNum);
			break;
		default:
			return;
    	}

		startActivity(intent);
	}

	private class MainListAdapter extends BaseAdapter {
    	private Context mContext;
    	
        public MainListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return mFuncs.length;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return !mFuncs[position].startsWith("-");
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
            tv.setText(mFuncs[position]);
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
