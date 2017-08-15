package com.pax.dxxtest;

import com.pax.mposapi.ConfigManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputActivity extends Activity {
	private static String TAG = "InputActivity";
	private TextView tvTips;
	private EditText index;
	private int func;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_interface);
        Intent intent = getIntent();
        final String mInterface = intent.getStringExtra("Interface");
        String idxText = intent.getStringExtra("index");
        func = intent.getIntExtra("FUNC", 0);
        TextView tv = (TextView)findViewById(R.id.title);
        tvTips = (TextView)findViewById(R.id.tips);
        tv.setText(mInterface);
        index = (EditText) findViewById(R.id.index); 
        index.setText(idxText);
        context = getApplicationContext();
        Button confirm = (Button) findViewById(R.id.confirm); 
        confirm.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				if(!index.getText().toString().equals("")){	
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), BaseActivity.class);
					String value = index.getText().toString();
					
					ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
					cfg.saveTagValue(mInterface, value);
					
					intent.putExtra("index", value);
					intent.putExtra("FUNC", func);
					setResult(RESULT_OK, intent);
					Log.e(TAG, "InputActivity 1");
					finish();
				}
				else {
					tvTips.setText("����Ϊ��");
				}
			}
		});
	}
	
//    private void showTips() {
//
//        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("退出程序").setMessage("是否退出程序").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//
//                }).setNegativeButton("取消",
//
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        return;
//                    }
//                }).create(); // 创建对话框
//        alertDialog.show(); // 显示对话框
//    }
//    
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            showTips();
//            return false;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
}
