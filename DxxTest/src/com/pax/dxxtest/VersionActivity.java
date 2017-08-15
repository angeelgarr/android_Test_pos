package com.pax.dxxtest;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;



public class VersionActivity extends Activity {

	private TextView mTextView;
	private LinearLayout mLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_version);
		mLayout =new LinearLayout(this);//�������Բ���
		mTextView = new TextView(this);//����һ��TextView
		mTextView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));//���ÿ��
		
		mTextView.setText("APP Version:VD180_V1.2.7" +"\n"+
						  "APK Version:V1.1.3"+"\n");
		mTextView.setTextSize(20);//�����С
	//	mTextView.setBackgroundColor(Color.BLUE);//���ñ�����ɫ
		mTextView.setGravity(Gravity.CENTER);//����
		mTextView.setPadding(0, 20, 0, 0);//left,top,right,buttom
		mLayout.addView(mTextView);//����ͼ��ӵ�������
		
		setContentView(mLayout);
	}

}
