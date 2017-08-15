package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * KeyboardManager ���ڿ��Ƽ���
 * </div>
 * <div class="en">
 * KeyboardManager is used to control the keyboard
 * </div>
 *
 */
public class KeyboardManager {
    public static final int KEY_F1		= 0x01;
    public static final int KEY_F2		= 0x02;
    public static final int KEY_CLEAR	= 0x08;
    public static final int KEY0		= '0';
    public static final int KEY1		= '1';
    public static final int KEY2		= '2';
    public static final int KEY3		= '3';
    public static final int KEY4		= '4';
    public static final int KEY5		= '5';
    public static final int KEY6		= '6';
    public static final int KEY7		= '7';
    public static final int KEY8		= '8';
    public static final int KEY9		= '9';
    public static final int KEY_CANCEL	= 0x1B;
    public static final int KEY_ENTER	= 0x0D;
    
    // below 2 keys are for D800
    public static final int KEY_ALPHA = 0x07;
    public static final int KEY_NUM = 0x23;
	
    private static final String TAG = "KeyboardManager";
    private final Proto proto;
    private Context context;
    private static KeyboardManager instance;
    
    private final int ADDITIONAL_TIMEOUT_FOR_INPUT = 60000;
    
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
     * ʹ��ָ����Context�����KeyboardManager����
     * </div>
     * <div class="en">
     * Create a KeyboardManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */    
    private KeyboardManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }

    /**
     * Create a KeyboardManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static KeyboardManager getInstance(Context context) {
        if (instance == null) {
        	instance = new KeyboardManager(context);
        }
        return instance;
    }

    /**
     * <div class="zh">
     * �����̻��������Ƿ�����δ��ȡ�ߵİ���ֵ<br/>
     * ������32�ֽڵİ���������������������а���ֵ,��ͨ��{@link #kbGetkey(int timeout)}��������
     * </div>
     * <div class="en">
     * Check whether there are unread key values in keyboard buffer.<br/>
     * Keyboard has a 32-bytes key buffer. Key values can be read out by {@link #kbGetkey(int timeout)} if there exist key values in buffer.
     * </div>
     * 
     * @return
     * <div class="zh">
	 *		true: ��<br/>
	 *		false: ��
     * </div> 
     * <div class="en">     
     *      ture: yes<br/>
     *      false: no
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public boolean kbhit() throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.KBD_HIT, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		if (resp[0] == 0) {
    			return true;
    		} else {
    			return false;
    		}
    	} else {
    		throw new KeyboardException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * �����ǰ���̻������е�����δ��ȡ�İ���<br/>
     * ʹ�øú�����ջ�����,�ٵ���{@link #kbhit()}���ж��Ƿ��а����¼�
     * </div>
     * <div class="en">
     * Clear all the key values which have not been read in current keyboard buffer.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public void kbflush() throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.KBD_FLUSH, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new KeyboardException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * ��ȡ���̻����������������һ����ֵ�����������Ϊ��,��ȴ���������<br/>
     * ������32�ֽڵİ���������,��������ʱ��������,��˰�����������
     * </div>
     * <div class="en">
     * get the first key code from the buffer. Will wait for key pressing if not pressed previously<br/>
     * At most 32 key codes can be cached, key code will be discarded if cache is full.  
     * </div>
     * 
     * @param timeout
     * <div class="zh">
     * 		��ⰴ���ĳ�ʱʱ��, ��λms.
     * </div>
     * <div class="en">
     * 		timeout to get the key code, in ms.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		-1: �ڳ�ʱʱ����û�м�⵽��ֵ<br/>
     * 		>=0: ��ֵ, �μ�{@link #KEY_F1}��.
     * </div>
     * <div class="en">
     * 		-1: no key pressed within timeout<br/>
     * 		>=0: key code, see {@link #KEY_F1} etc.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public int kbGetkey(int timeout) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[4];
    	Utils.int2ByteArray(timeout, req, 0);
    	byte[] resp = new byte[1];
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout;
    	
    	try {
	    	proto.sendRecv(Cmd.CmdType.KBD_GET_KEY, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		if (resp[0] == (byte)0xFF) {
	    			return -1;
	    		} else {
	    			return resp[0];
	    		}
	    	} else {
	    		throw new KeyboardException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * �����ַ���,�ַ�������ָ����ģʽ��ʾ����Ļ��<br/>
     * 1.���뷽ʽ�Ķ���:(���ȼ�˳��Ϊbit3>bit4>bit5, ��mode&0x38���ܵ�0��;<br/> 
     * 2.���������,����������ֻ����������Ļ������ĵķ�ʽ(��bit3ȷ��)��ʾ����Ļ�ϡ� <br/>
     * 3.���mode��bit7=1, ��ú����ɴ����ʼ���ִ�,�ô�����ʾ����ʼ���λ��,�������Ѽ�������ִ���<br/>
     * 4.������в���¼�Ͱ������ܼ���<br/>
     * 5.����CLEAR��,�����������ʾ,����˸�Ĺ���,�����������ʾ,����������롣<br/>
     * 6.���ٰ�ͬһ�����ּ��������Լ���Сд�ַ�֮������л�, �л��ĳ�ʱʱ����1�롣   
     * </div>
     * <div class="en">
     * get input string<br/>
     * 1.mode bits priority: bit3>bit4>bit5, mode & 0x38 can't be 0;<br/>
     * 2.the inputted characters will be displayed either with plain text or with '*'(controlled by bit 3);<br/>
     * 3.set bit7 to 1 if initial string is needed;<br/>
     * 4.Func key is NOT recorded in the output string;<br/>
     * 5.Pressing clear key will delete 1 character for plain text for clear all inputted charaters for password;<br/>
     * 6.Pressing 1 key multiple times quickly(in 1 second) to switch among digit and alpha characters;<br/>
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		ģʽ,��λ��ʾ
     * 		<ul>
     * 			<li>D7	1(0) ��(��)�س��˳��ʹ���ʼ���봮����
     *			<li>D6	1(0) ��(С)���� 
     *			<li>D5	1(0) �ܣ��������� 
     *			<li>D4	1(0) ��(��)����ĸ 
     * 			<li>D3	1(0) ��(��)������ʾ��ʽ,��ʾΪ��*��
     *			<li>D2	1(0) ��(��)��������
     *			<li>D1	1(0) ��(��)С����
     *			<li>D0	1(0) ��(��)��ʾ
     *		</ul>
     * </div>
     * <div class="en">
     * 		mode, bit setting are:
     * 		<ul>
     * 			<li>D7	1(0) enter to quit and with inital string. 1-yes, 0-no
     *			<li>D6	1(0) font size. 1-big, 0-small
     *			<li>D5	1(0) can input digits. 1-yes, 0-no
     *			<li>D4	1(0) can input alhpa characters. 1-yes, 0-no
     * 			<li>D3	1(0) password mode(display '*'). 1-yes, 0-no
     *			<li>D2	1(0) alignment. 1-left, 0-right
     *			<li>D1	1(0) with dot. 1-yes, 0-no
     *			<li>D0	1(0) display inversed.  1-no, 0-yes
     *		</ul>
     * </div>
     *
     * @param minLen
     * <div class="zh">
     * 		��Ҫ���봮����С����
     * </div>
     * <div class="en">
     * 		minimum length required to input
     * </div>
     * 
     * @param maxLen
     * <div class="zh">
     * 		��Ҫ���봮����󳤶�(�������ֵ��128�ֽ�)
     * </div>
     * <div class="en">
     * 		maximum length allowed to input(at most 128 bytes)
     * </div>
     * 
     * @param x
     * <div class="zh">
     * 		���봮����ʼx����
     * </div>
     * <div class="en">
     * 		x coordinate of the start position to input
     * </div>
     * 
     * @param y
     * <div class="zh">
     * 		���봮����ʼy����
     * </div>
     * <div class="en">
     * 		y coordinate of the start position to input
     * </div>
     * 
     * @param initialString
     * <div class="zh">
     * 		��ʼ���봮. ���Ϊnull, ��ʾû�г�ʼ���봮. <b>ֻ�е�D7Ϊ1ʱ�Ż���Ч</b>
     * </div>
     * <div class="en">
     * 		initial string. null for missing.  <b>only valid when D7 is 1</b>
     * </div>
     * 
     * @param status
     * <div class="zh">
     * 		[���]����״̬. ��status[0]��Ч.  0x00: �ɹ�, 0x0D: �û��س��˳�(ENTER������), 0xFF: �û�ȡ������
     * </div>
     * <div class="en">
     * 		[output]input status. only status[0] is valid.  0x00: OK, 0x0D: enter to quit, 0xFF: cancelled
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		�û�������ַ���, ����status[0]Ϊ0ʱ��Ч, ����Ϊnull.
     * </div>
     * <div class="en">
     * 		the input string, only valid when status[0] is 0, otherwise it's null.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public String kbGetString(int mode, int minLen, int maxLen, int x, int y, String initialString, byte[] status) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = null;
    	int initialStrLen = 0;
    	if (initialString != null) {
    		 encoded = initialString.getBytes(encoding);
    		 initialStrLen = encoded.length;
    	}
    	
    	byte[] req = new byte[8 + initialStrLen];
    	req[0] = (byte)mode;
    	req[1] = (byte)minLen;
    	req[2] = (byte)maxLen;
    	Utils.short2ByteArray((short)x, req, 3);
    	Utils.short2ByteArray((short)y, req, 5);
    	req[7] = (byte)initialStrLen;
    	if (initialStrLen > 0) {
    		System.arraycopy(encoded, 0, req, 8, initialStrLen);
    	}
    	
    	byte[] resp = new byte[1024];
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += ADDITIONAL_TIMEOUT_FOR_INPUT;
    	
    	try {
	    	proto.sendRecv(Cmd.CmdType.KBD_GET_STRING, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		status[0] = resp[0];
	    		if (resp[0] == (byte)0) {
	    			return new String(resp, 2, resp[1], encoding);
	    		} else if (resp[0] == (byte)0x0D || resp[0] == (byte)0xFF) {
	    			return null;
	    		} else {
	    			throw new KeyboardException(-1);	//param error.
	    		}
	    	} else {
	    		throw new KeyboardException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * �������빦�ܵ������ַ����뺯��,Ҳ������Ӣ�ĺ������ַ�<br/>
     * 1.���뷽���л�,��F1���ܼ����ڡ�ƴ�����֡�,����д��ĸ��,��Сд��ĸ��,����λ�롱֮���л�.ƴ�����뷨״̬��, ��F2���ܼ���ҳ;<br/> 
     * 2.��������,�ڡ�ƴ�����֡������뷽ʽ��,���ΰ���ƴ�����ڵ����ּ����ɡ��������롰�С���,�����롰14664��,Ȼ���á�ȷ�ϡ���ѡ���С��ּ��ɡ� <br/>
     * 3.�����д��ĸ������,�ڡ���д��ĸ�������뷽ʽ��,��������Ҫ����ĸ���������ڵİ���,��0.8���ʱ���ڲ�ͣ����ͬһ����,��������ַ�Ϊ�ð�������ʾ����һ�ַ�,�����������¡�1��������,������ַ�Ϊ��Q����<br/>
     * 4.����Сд��ĸ������,�ڡ�Сд��ĸ�������뷽ʽ��,��������Ҫ����ĸ���������ڵİ���,��0.8���ʱ���ڲ�ͣ����ͬһ����,��������ַ�Ϊ�ð�������ʾ����һ�ַ�,�����������¡�1����3��,������ַ�Ϊ��z����<br/>
     * 5.���¡�CLEAR���������������ַ���<br/>
     * 6.D300��֧�֡�<br/>
     * 
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * get input string with Chinese character support<br/>
     * 1.IME switched by pressing F1 key(pinyin->capital->lower-case->quwei). F2 key is used for page scrolling;<br>
     * 2.When using pinyin, pressing related button in order. e.g. to input '��', enter'14664', press enter and select it.<br/>
     * 3.When inputing alpha characters or digits, in capital mode or lower-case mode, pressing 1 key multiple times quickly(in 0.8 second)
     * to switch to the needed one and select it.<br/>
     * 4.press clear key to clear inputted character.<br/> 
     * 5.D300 doesn't support this function
     * </div>
     * 
     * @param max
     * <div class="zh">
     * 		��Ҫ���봮����󳤶�(�������ֵ��128�ֽ�)
     * </div>
     * <div class="en">
     * 		maximum length allowed to input(at most 128 bytes)
     * </div>
     *
     * @param timeout
     * <div class="zh">
     * 		��ʱ�趨ֵ(��λ��), 0��ʾ���޵ȴ�
     * </div>
     * <div class="en">
     * 		timeout(in second), 0 means wait forever
     * </div>
     * 
     * @param initialString
     * <div class="zh">
     * 		��ʼ���봮. ���Ϊnull, ��ʾû�г�ʼ���봮.
     * </div>
     * <div class="en">
     * 		initial string. null for missing. 
     * </div>
     * 
     * @param status
     * <div class="zh">
     * 		[���]����״̬. ��status[0]��Ч.  0x00: �ɹ�, -3: ��ʱ, -1: �û�ȡ������
     * </div>
     * <div class="en">
     * 		[output]input status.  only status[0] is valid.  0x00: OK, -3: timeout, -1: cancelled
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		������ַ���, ����status[0]Ϊ0ʱ��������, ����Ϊnull.
     * </div>
     * <div class="en">
     * 		inputted characters, only valid when status[0] is 0, otherwise it's null.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public String kbGetHzString(int max, int timeout, String initialString, byte[] status) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = null;
    	int initialStrLen = 0;
    	if (initialString != null) {
    		 encoded = initialString.getBytes(encoding);
    		 initialStrLen = encoded.length;
    	}
    	
    	byte[] req = new byte[3 + 1 + initialStrLen];
    	req[0] = (byte)max;
    	if (timeout == 0 || timeout > 0xFFFF) {
    		timeout = 0xFFFF;
    	}
    	Utils.short2ByteArray((short)timeout, req, 1);
    	req[3] = (byte)initialStrLen;
    	if (initialStrLen > 0) {
    		System.arraycopy(encoded, 0, req, 4, initialStrLen);
    	}
    	
    	byte[] resp = new byte[1024]; 
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout * 1000;
    	
    	try {
	    	proto.sendRecv(Cmd.CmdType.KBD_GET_HZ_STRING, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		status[0] = resp[0];
	    		if (resp[0] == 0) {
	    			return new String(resp, 2, resp[1], encoding);
	    		} else {
	    			return null;
	    		}
	    	} else {
	    		throw new KeyboardException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }    
    
    /**
     * <div class="zh">
     * ���ð�������<br/>
     * Ĭ�������,ϵͳ���ñ���ģʽΪ1
     * </div>
     * <div class="en">
     * set keyboard backlight mode<br/>
     * the default backligh mode is 1
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		0-�رձ���; 1-���Ᵽ����15��,15����Զ��ر�; 2-���ⳣ��; 3-���ⳤ��,���ᱻˢ��,�忨���¼�����⡣
     * </div>
     * <div class="en">
     * 		0-off; 1-auto-off after 15 seconds; 2-on; 3-always off, cannot be activated by card insertion/swiping. 
     * </div>
     * 
     * @throws KeyboardException<div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public void kblight(int mode) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)mode;
    	proto.sendRecv(Cmd.CmdType.KBD_BACKLIGHT, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new KeyboardException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * ���ð�����������<br/>
     * δ���������,ϵͳ���ð�����ģʽΪ1, mode�������ʱ����ԭ���ò���.�����������ͨ���̰���Դ������ˢ�ſ�(�ſ��Ѵ�)�����IC������, ��ע����Ļ����ʱ, ���̱��ر�, LCD��������Ϊ10%, ���᲻��{@link #kblight(int)}��������Ϊ������Ӱ��
     * </div>
     * <div class="en">
     * set keyboard locking mode<br/>
     * Default mode is 1. Keyborad can be unlocked by pressing power switch, swiping card or inserting ICC card.
     * Note that if screen is locked, then keyboard is locked and LCD backlight's brightness is 10%, in that case, setting keyboard backlight to on has no effect.
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		0-��������, �̰���Դ������ˢ�ſ���IC���ɽ���; 1-���ֽ���״̬30��, 30����Զ�����, ���߶̰���Դ�����������ͽ������л�; 2-���ְ�������״̬, ���ٱ��̰���Դ������.
     * </div>
     * <div class="en">
     * 		0-keyboard locked, can be unlocked by pressing power switch, swiping card or inserting IC card;<br/>
     * 		1-auto-lock if idle for 30 seconds, can be lock/unlocked by pressing power switch;<br/>
     * 		2-unlocked, pressing power switch doesn't lock/unlock.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public void kbLock(int mode) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)mode;
    	proto.sendRecv(Cmd.CmdType.KBD_LOCK, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new KeyboardException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * ��ȡ��ǰ���̵�״̬��Ϣ<br/>
     * ��ȡ������������״̬�Ĺ��ܿ���SysSleep���ʹ��, ����⵽��������ʱ����SysSleep��������״̬, �˾ٿ���������ʵ�ʱ�����Ƶ�Դ����
     * </div>
     * <div class="en">
     * get current status of keyboard<br/>
     * Entering sleep status when keyboard locking detected can save power.
     * </div>
     * 
     * @param cmd
     * <div class="zh">
     * 		0- ������������״̬; 1- �����������ڵļ�ֵ��; 2- �Ƿ��а�����; 3- ���������Ƿ��
     * </div>
     * <div class="en">
     * 		0- if locked or not; 1 - if there's key code in key buffer or not; 2- if beep or not when pressing; 3- if keyboard backlight on or not; 
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		cmd=0: 0- ��������δ��, 1- ������������<br/>
     *		cmd=1: >=0 ��ֵ��<br/>
     *		cmd=2: 0- ������, 1- ���� <br/>
     *		cmd=3: 0- ����ر�, 1- ���⿪��
     * </div>
     * <div class="en">
     * 		cmd=0: 0- unlocked, 1- locked<br/>
     *		cmd=1: >=0 number of key code<br/>
     *		cmd=2: 0- not beep, 1- beep<br/>
     *		cmd=3: 0- backlight off, 1- backlight on
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
     * <div class="en">Keyboard error</div>
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
    public int kbCheck(int cmd) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)cmd;
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.KBD_CHECK, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		return resp[0];
    	} else {
    		throw new KeyboardException(rc.code);
    	}
    }

    /**
     * <div class="zh">���ð������ڰ���ʱ�Ƿ���.</div>
     * <div class="en">Set whether a beep should be made when a key is pressed.</div>
     * @param flag
     * <div class="zh">
     * 			0 - ������, 1 - ����
     * </div>
     * <div class="en">
     *          0 - not beep,  1 - beep
     * </div>
     * @throws KeyboardException
     * <div class="zh">Keyboard����</div>
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
    public void kbmute(byte flag) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = flag;
    	proto.sendRecv(Cmd.CmdType.KBD_MUTE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new KeyboardException(rc.code);    		
    	}
    }
 
    
    public String kbtest(int max, int timeout, String initialString, byte[] status) throws KeyboardException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = null;
    	int initialStrLen = 0;
    	if (initialString != null) {
    		 encoded = initialString.getBytes(encoding);
    		 initialStrLen = encoded.length;
    	}
    	
    	byte[] req = new byte[3 + 1 + initialStrLen];
    	req[0] = (byte)max;
    	if (timeout == 0 || timeout > 0xFFFF) {
    		timeout = 0xFFFF;
    	}
    	Utils.short2ByteArray((short)timeout, req, 1);
    	req[3] = (byte)initialStrLen;
    	if (initialStrLen > 0) {
    		System.arraycopy(encoded, 0, req, 4, initialStrLen);
    	}
    	
    	byte[] resp = new byte[1024]; 
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout * 1000;
    	
    	try {
	    	proto.sendRecv(Cmd.CmdType.KBD_GET_HZ_STRING, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		status[0] = resp[0];
	    		if (resp[0] == 0) {
	    			return new String(resp, 2, resp[1], encoding);
	    		} else {
	    			return null;
	    		}
	    	} else {
	    		throw new KeyboardException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }    
}
