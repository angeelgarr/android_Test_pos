package com.pax.mposapi.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyLog {
	
	//TODO: adjust these settings for release version!
    public static boolean DEBUG_V = true;
    public static boolean DEBUG_D = true;
    public static boolean DEBUG_I = true;
    public static boolean DEBUG_W = true;
    public static boolean DEBUG_E = true;
     
    public static void toast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    
    public static void v(String tag,String msg){
        if(DEBUG_V){
            Log.v(tag, msg);
        }
    }
     
    public static void d(String tag,String msg){
        if(DEBUG_D){
            Log.d(tag, msg);
        }
    }
    
    public static void i(String tag,String msg){
        if(DEBUG_I){
            Log.i(tag, msg);
        }
    }
    
    public static void w(String tag,String msg){
        if(DEBUG_W){
            Log.w(tag, msg);
        }
    }
    
    public static void e(String tag,String msg){
        if(DEBUG_E){
            Log.e(tag, msg);
        }
    }    
}