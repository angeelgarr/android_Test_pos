package com.pax.dxxtest;

import junit.runner.Version;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pax.dxxtest.R;
import com.pax.dxxtest.ConfigActivity;
import com.pax.dxxtest.InterfaceActivity;
import com.pax.dxxtest.ModuleActivity;
//import com.pax.dxxtest.ModuleActivity.MainListAdapter;
import com.pax.mposapi.comm.Comm;

public class ModuleActivity extends ListActivity {
	
	private String[] mStrings = {
//            "1.LCD",
//            "2.KEYBOARD",
            "1.BASE",
            "2.EMVPARA",
//            "5.ICC",
//            "6.MAG",
//            "7.CLSS",
            "3.CONFIG",
            "4.Version"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        
        TextView tv = (TextView)findViewById(R.id.list_title);
        tv.setText(getString(R.string.dxx_test));
        
        setListAdapter(new MainListAdapter(this));
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
    	Intent intent;
    	switch (position) {
//		case 0:	//LCD
//			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);
//			intent.putExtra("Module", "LCD");
//			break;
//		case 1:	//KEYBOARD
//			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);
//			intent.putExtra("Module", "KEYBOARD");
//			break;
		case 0:	//BASE
			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);
			intent.putExtra("Module", "BASE");
			break;
		case 1:	//EMVPARA
			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);
			intent.putExtra("Module", "EMVPARA");
			break;
//		case 4:	//ICC
//			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);
//			intent.putExtra("Module", "ICC");
//			break;
//		case 5:	//MAG
//			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);	
//			intent.putExtra("Module", "MAG");
//			break;
//		case 6:	//PICC
//			intent = new Intent(ModuleActivity.this, InterfaceActivity.class);	
//			intent.putExtra("Module", "CLSS");
//			break;
		case 2:	//CONFIG
			intent = new Intent(ModuleActivity.this, ConfigActivity.class);	
			intent.putExtra("Module", "Config");
			break;
		case 3:	//CONFIG
			intent = new Intent(ModuleActivity.this, VersionActivity.class);	
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

    protected void dialog() {
		AlertDialog.Builder builder = new Builder(this)
			.setMessage(getString(R.string.exit_program))
			.setTitle(getString(R.string.prompt))
			.setPositiveButton(getString(R.string.confirm), new OnClickListener() {
		
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   
			    dialog.dismiss();
				Comm.getInstance(ModuleActivity.this).close();
			    ModuleActivity.this.finish();
			    //ActivityManager.getInstance().exit(MainActivity.class);
		   }
		  });
		
		  builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
		
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   dialog.dismiss();
		   }
		  });

		  builder.create().show();
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return false;
	}
}
