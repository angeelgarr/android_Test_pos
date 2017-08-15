package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;

/**
 * <div class="zh">
 * <b><font color=red>ע��: D800 ��֧����ʾ����</font></b><br/>
 * UIManager ���ڿ���UI, ��ؽӿ�ֻ�����ھ߱���Ӧģ��Ļ���
 * </div>
 * <div class="en">
 * <b><font color=red>NOTE: this module is not applicable for D800
 * </font></b><br/>
 * UIManager is used to control the UI, only valid for model with display screen
 * </div>
 *
 */
public class UIManager {
    public static final byte UI_IMAGE_TYPE_BMP = 0;
    public static final byte UI_IMAGE_TYPE_JPG = 1;
    public static final byte UI_IMAGE_TYPE_PNG = 2;
    
    public static final byte UI_PROCESS_IMAGE_CMD_LOAD = 0;
    public static final byte UI_PROCESS_IMAGE_CMD_DISPLAY = 1;
    public static final byte UI_PROCESS_IMAGE_CMD_CLEAN = 3;
    
    public static final byte UI_LANG_ZH		= 0x00;
    public static final byte UI_LANG_EN		= 0x01;	
	
    private static final String TAG = "UIManager";
    private final Proto proto;
    private Context context;
    private static UIManager instance;
    
    /**
     * <div class="zh">
     * ��ʾ�ַ����ı���, Ĭ��Ϊ "gb2312"
     * </div>
     * <div class="en">
     * encoding of the string to display, default to "gb2312"
     * </div>
     */    
    public String encoding = "gb2312";
    
    /**
     * <div class="zh">
     * ʹ��ָ����Context�����UIManager����
     * </div>
     * <div class="en">
     * Create a UIManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */    
    private UIManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }
    
    /**
     * Create a UIManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static UIManager getInstance(Context context) {
        if (instance == null) {
        	instance = new UIManager(context);
        }
        return instance;
    }
    
    /**
     * <div class="zh">
     * �����Ļ
     * </div>
     * <div class="en">
     * clear the screen
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */     
    public void scrCls() throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.LCD_CLS, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * Displays two lines of messages on the PAYMENT DEVICE device and provides a menu with a maximum of 4 choices.
     * </div>
     * <div class="en">
     * clear the screen
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */     
    public byte[] promptMenu(String sequenceId,String sessionCode,String messageline1,String messageline2,
    		String choice1,String choice2,String choice3,String choice4,String readTimeout) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	String s="promptMenu|"+sequenceId+"|"+sessionCode+"|"+messageline1+"|"+messageline2+"|"
        +choice1+"|"+choice2+"|"+choice3+"|"+choice4+"|"+readTimeout;
    	
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_MENU,req, rc, resp);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    
    public byte[] promptMenu111(String index) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	String s=index;
    	
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_MENU,req, rc, resp);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    public byte[] promptMenu111Step(String index) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	/*String s="promptMessage|123456|1234567890|messageline1|messageline2|messageline3|messageline4|TRUE|15";*/
    	String s=index;
    	byte[] req = s.getBytes();
    	
        proto.sendRecvStep(Cmd.CmdType.MTLA_MENU,req, rc, resp, false);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    /**
     * <div class="zh">
     * Displays a maximum of 4 lines of messages on the PAYMENT DEVICE device. 
     * </div>
     * <div class="en">
     * clear the screen
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */     
    public byte[] promptMessage(String sequenceId,String sessionCode,String messageline1,String messageline2,
    		String messageline3,String messageline4,String userConfirmationRequired,String timeout) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	/*String s="promptMessage|123456|1234567890|messageline1|messageline2|messageline3|messageline4|TRUE|15";*/
    	String s="promptMessage|"+sequenceId+"|"+sessionCode+"|"+messageline1+"|"+messageline2+"|"+messageline3+"|"+messageline4+"|"+userConfirmationRequired+"|"+timeout;
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_MESSAGE,req, rc, resp);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    
    public byte[] promptMessage111(String index) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	/*String s="promptMessage|123456|1234567890|messageline1|messageline2|messageline3|messageline4|TRUE|15";*/
    	String s=index;
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_MESSAGE,req, rc, resp);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    
    public byte[] promptMessage111Step(String index) throws UIException, IOException, ProtoException, CommonException {

    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	/*String s="promptMessage|123456|1234567890|messageline1|messageline2|messageline3|messageline4|TRUE|15";*/
    	String s=index;
    	byte[] req = s.getBytes();
    	
        proto.sendRecvStep(Cmd.CmdType.MTLA_MESSAGE,req, rc, resp, false);
        if (rc.code == 0) {
        		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    	return resp;
    }
    

    /**
     * <div class="zh">
     * ��ʾ�ַ�. ע���ַ����п��ܰ���Լ�����ض��Ŀ����ַ�, ����λ��,����,���Ե�.
     * </div>
     * <div class="en">
     * display a string. Note that it may include special format, such as position, font, inversion etc.
     * </div>
     * 
     * @param txt
     * <div class="zh">
     *           Ҫ��ʾ���ַ�.<br/>
     *           ֧��\n<br/>
     *           ����֧��Լ���Ŀ����ַ�����
     *           <ul>
     *           	<li>%Pccrr, �������ֵ�λ��, ��λΪpixel, cc��ʾcolumn(��x����), rr ��ʾrow(�� y ����). cc��rr����16��������ʾ, Ĭ��ֵΪ%P0000
     *           	<li>%Ff, �������, f��ʾ�����С, ȡֵ��Χ0~2, Ӣ�ĺ����ĸ�3������.<br/>
     *           		Ӣ��   0: 6*8, 1: 8*16, 2: 12*24<br/>
     *           		����   0: 12*12, 1: 16*16, 2: 24*24
     *           	<li>%Rr, ���Կ���, r��ʾ�Ƿ�ɫ��ӡ. 0 ��ʾ����, 1��ʾ��ɫ
     *           </ul>
     * </div> 
     * <div class="en">     
     *           the string to display<br/>
     *           support\n<br/>
     *           support extra control as follows:
     *           <ul>
     *           	<li>%Pccrr, Position control, in unit of pixel, cc stands for column(x coord.), 
     *           		rr stands for row(y coord.). cc and rr are in hex string format, default value is %P0000
     *           	<li>%Ff, Font control, f is font size, ranged from 0 to 2, 3 fonts for English and Chinese each.<br/>
     *           		For English,  0: 6*8, 1: 8*16, 2: 12*24<br/>
     *           		For Chinese,  0: 12*12, 1: 16*16, 2: 24*24
     *           	<li>%Rr, Inversion control, r toggles inversion. 0 is normal, 1 is invert.
     *           </ul>
     *           
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */           
    public void scrShowText(String txt) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = txt.getBytes(encoding); 
    	
    	byte[] req = new byte[1 + encoded.length];
    	req[0] = (byte)encoded.length;
    	System.arraycopy(encoded, 0, req, 1, encoded.length);
    	proto.sendRecv(Cmd.CmdType.LCD_SHOW_TEXT, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * ��ȡԤ����ַ�
     * </div>
     * <div class="en">
     * get preset text with specified id and lang
     * </div>
     * 
     * @param id
     * <div class="zh">
     *           Ԥ���ַ�����id ( 0 ~ 9)
     * </div> 
     * <div class="en">
     * 			 id of the preset string (0 ~ 9) 
     * </div>
     * 
     * @param lang
     * <div class="zh">
     *           Ԥ���ַ�����lang, 0: ����{@link #UI_LANG_ZH}, 1:Ӣ��{@link #UI_LANG_EN}
     * </div> 
     * <div class="en">
     * 			 lang of the preset string, 0: chinese{@link #UI_LANG_ZH}, 1: english {@link #UI_LANG_EN}
     * </div>
     * 
     * @return
     * <div class="zh">
     *           Ԥ���ַ���
     * </div> 
     * <div class="en">
     * 			 the preset string 
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */        
    public String scrGetTxtById(byte id, byte lang) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = id;
    	req[1] = lang;
    	
    	byte[] resp = new byte[1024];  
    	proto.sendRecv(Cmd.CmdType.LCD_GET_TEXT_BY_ID, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		return new String(resp, 1, resp[0], encoding);
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * ����Ԥ����ַ�
     * </div>
     * <div class="en">
     * set preset text with specified id and lang
     * </div>
     * 
     * @param id
     * <div class="zh">
     *           Ԥ���ַ�����id ( 0 ~ 9)
     * </div> 
     * <div class="en">
     * 			 id of the preset string (0 ~ 9) 
     * </div>
     * 
     * @param lang
     * <div class="zh">
     *           Ԥ���ַ�����lang, 0: ����{@link #UI_LANG_ZH}, 1:Ӣ��{@link #UI_LANG_EN}
     * </div> 
     * <div class="en">
     * 			 lang of the preset string, 0: chinese{@link #UI_LANG_ZH}, 1: english {@link #UI_LANG_EN}
     * </div>
     * 
     * @param txt
     * <div class="zh">
     *           Ҫ���õ��ַ���
     * </div> 
     * <div class="en">
     * 			 a string to set
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */           
    public void scrSetTxtById(byte id, byte lang, String txt) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = txt.getBytes(encoding); 
    	
    	byte[] req = new byte[3 + encoded.length];
    	req[0] = id;
    	req[1] = lang;
    	req[2] = (byte)encoded.length;
    	System.arraycopy(encoded, 0, req, 3, encoded.length);
    	
    	proto.sendRecv(Cmd.CmdType.LCD_SET_TEXT_BY_ID, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * ��ʾԤ����ַ�
     * </div>
     * <div class="en">
     * display preset text with specified id and lang
     * </div>
     * 
     * @param id
     * <div class="zh">
     *           Ԥ���ַ�����id ( 0 ~ 9)
     * </div> 
     * <div class="en">
     * 			 id of the preset string (0 ~ 9) 
     * </div>
     * 
     * @param lang
     * <div class="zh">
     *           Ԥ���ַ�����lang, 0: ����{@link #UI_LANG_ZH}, 1:Ӣ��{@link #UI_LANG_EN}
     * </div> 
     * <div class="en">
     * 			 lang of the preset string, 0: chinese{@link #UI_LANG_ZH}, 1: english{@link #UI_LANG_EN} 
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */      
    public void scrShowTxtById(byte id, byte lang) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = id;
    	req[1] = lang;
    	
    	proto.sendRecv(Cmd.CmdType.LCD_SHOW_TEXT_BY_ID, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }    
 
    /**
     * <div class="zh">
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ��ʾͼ��
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * display image
     * </div>
     * 
     * @param name
     * <div class="zh">
	 *		ͼ���ļ�����
     * </div> 
     * <div class="en">     
     *      image file name
     * </div>
     * @param type
     * <div class="zh">
     * 		ͼ������  
     * 		<ul>
     * 			<li>0: bmp{@link #UI_IMAGE_TYPE_BMP}
     * 			<li>1: jpg{@link #UI_IMAGE_TYPE_JPG}
     * 			<li>2: png{@link #UI_IMAGE_TYPE_PNG}
     * 		</ul>
     * </div> 
     * <div class="en">     
     *      image type
     *      <ul>
     * 			<li>0: bmp{@link #UI_IMAGE_TYPE_BMP}
     * 			<li>1: jpg{@link #UI_IMAGE_TYPE_JPG}
     * 			<li>2: png{@link #UI_IMAGE_TYPE_PNG}
     * 		</ul>
     * </div>
     * 
     * @param cmd
     * <div class="zh">
     * 		�������� 
     * 		<ul>
     * 			<li>0: ����{@link #UI_PROCESS_IMAGE_CMD_LOAD}, ���غ�����ļ�ϵͳ������".t+���"����ʱ�ļ�. ����{@link #UI_PROCESS_IMAGE_CMD_CLEAN}���.
     * 			<li>1: ��ʾ{@link #UI_PROCESS_IMAGE_CMD_DISPLAY}
     * 			<li>3: �����ʱ�ļ�{@link #UI_PROCESS_IMAGE_CMD_CLEAN}
     * 		</ul>
     * </div> 
     * <div class="en">     
     *      command type
     *      <ul>
     * 			<li>0: load		{@link #UI_PROCESS_IMAGE_CMD_LOAD}, if loaded, will generate ".t+number" file. which can be cleaned with command {@link #UI_PROCESS_IMAGE_CMD_CLEAN}. 
     * 			<li>1: display	{@link #UI_PROCESS_IMAGE_CMD_DISPLAY}
     * 			<li>3: clean temp file	{@link #UI_PROCESS_IMAGE_CMD_CLEAN}
     * 		</ul>
     * </div>
     * @param x
     * <div class="zh">
     * 		��ʾͼ���λ��x����
     * </div> 
     * <div class="en">     
     * 		the x position to show the image
     * </div>
     * @param y
     * <div class="zh">
     * 		��ʾͼ���λ��y����
     * </div> 
     * <div class="en">     
     * 		the y position to show the image
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */       
    public void scrProcessImage(String name, byte type, byte cmd, int x, int y) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encodedName = name.getBytes(encoding);
    	
    	byte[] req = new byte[6 + 1 + encodedName.length];
    	req[0] = type;
    	req[1] = cmd;
    	req[2] = (byte)(x / 256);
    	req[3] = (byte)(x % 256);
    	req[4] = (byte)(y / 256);
    	req[5] = (byte)(y % 256);
    	req[6] = (byte)encodedName.length;
    	System.arraycopy(encodedName, 0, req, 7, encodedName.length);
    	
    	proto.sendRecv(Cmd.CmdType.LCD_PROCESS_IMAGE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new UIException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * <b><font color=red>ע��: D200 ��֧�ִ˹��� </font></b><br/>
     * ������Ļ����<br/>
     * Ĭ�������,ϵͳ���ñ���ģʽΪ1, �ڰ����Ͳ�������, OLED������, D200��֧�֡�
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D200 doesn't support this function</font></b><br/>
     * set screen backlight mode<br/>
     * default mode is 1. Not applicable for OLED.
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		0 - �ر���; 1 - D210: ���Ᵽ����30��(��30���,�Զ��ر�)�������ͣ����Ᵽ����һ����(��һ���Ӻ�,�Զ��ر�); 2 - ���ⳣ��,����ֵ�޲���
     * </div>
     * <div class="en">
     * 		0 - off;  1 - D210: auto-off after 30 seconds, other model: auto-off after 1 minute; 2 - on; 
     * </div>
     * 
     * @throws UIException
     * <div class="zh">UI����</div>
     * <div class="en">UI error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */           
    public void scrBacklight(int mode) throws UIException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)mode;
    	proto.sendRecv(Cmd.CmdType.LCD_BACKLIGHT, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new UIException(rc.code);
    	}
    }

}
