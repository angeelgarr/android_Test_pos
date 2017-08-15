package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * KeyboardManager 用于控制键盘
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
     * 显示字符串的编码, 默认为 "gb2312"
     * </div>
     * <div class="en">
     * encoding of the string to display, default to "gb2312"
     * </div>
     */    
    public String encoding = "gb2312";
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出KeyboardManager对象
     * </div>
     * <div class="en">
     * Create a KeyboardManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
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
     * 检测键盘缓冲区中是否有尚未被取走的按键值<br/>
     * 键盘有32字节的按键缓冲区。如果缓冲区有按键值,可通过{@link #kbGetkey(int timeout)}函数读出
     * </div>
     * <div class="en">
     * Check whether there are unread key values in keyboard buffer.<br/>
     * Keyboard has a 32-bytes key buffer. Key values can be read out by {@link #kbGetkey(int timeout)} if there exist key values in buffer.
     * </div>
     * 
     * @return
     * <div class="zh">
	 *		true: 有<br/>
	 *		false: 无
     * </div> 
     * <div class="en">     
     *      ture: yes<br/>
     *      false: no
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 清除当前键盘缓冲区中的所有未读取的按键<br/>
     * 使用该函数清空缓冲区,再调用{@link #kbhit()}来判断是否有按键事件
     * </div>
     * <div class="en">
     * Clear all the key values which have not been read in current keyboard buffer.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 读取键盘缓冲区中最早输入的一个键值。如果缓冲区为空,则等待按键输入<br/>
     * 键盘有32字节的按键缓冲区,缓冲区满时继续按键,则此按键被丢弃。
     * </div>
     * <div class="en">
     * get the first key code from the buffer. Will wait for key pressing if not pressed previously<br/>
     * At most 32 key codes can be cached, key code will be discarded if cache is full.  
     * </div>
     * 
     * @param timeout
     * <div class="zh">
     * 		检测按键的超时时间, 单位ms.
     * </div>
     * <div class="en">
     * 		timeout to get the key code, in ms.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		-1: 在超时时间内没有检测到键值<br/>
     * 		>=0: 键值, 参见{@link #KEY_F1}等.
     * </div>
     * <div class="en">
     * 		-1: no key pressed within timeout<br/>
     * 		>=0: key code, see {@link #KEY_F1} etc.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 输入字符串,字符串可以指定的模式显示在屏幕上<br/>
     * 1.输入方式的定义:(优先级顺序为bit3>bit4>bit5, 且mode&0x38不能等0）;<br/> 
     * 2.输入过程中,被输入的数字会依次以明文或者密文的方式(由bit3确定)显示在屏幕上。 <br/>
     * 3.如果mode的bit7=1, 则该函数可带入初始数字串,该串被显示到初始光标位置,被当作已键入的数字串。<br/>
     * 4.输出串中不记录和包含功能键。<br/>
     * 5.按下CLEAR键,如果是明文显示,变成退格的功能,如果是密文显示,清除整个输入。<br/>
     * 6.快速按同一个数字键在数字以及大小写字符之间进行切换, 切换的超时时间是1秒。   
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
     * 		模式,用位表示
     * 		<ul>
     * 			<li>D7	1(0) 能(否)回车退出和带初始输入串参数
     *			<li>D6	1(0) 大(小)字体 
     *			<li>D5	1(0) 能（否）输数字 
     *			<li>D4	1(0) 能(否)输字母 
     * 			<li>D3	1(0) 是(否)密文显示方式,显示为‘*’
     *			<li>D2	1(0) 左(右)对齐输入
     *			<li>D1	1(0) 有(否)小数点
     *			<li>D0	1(0) 正(反)显示
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
     * 		需要输入串的最小长度
     * </div>
     * <div class="en">
     * 		minimum length required to input
     * </div>
     * 
     * @param maxLen
     * <div class="zh">
     * 		需要输入串的最大长度(最大允许值是128字节)
     * </div>
     * <div class="en">
     * 		maximum length allowed to input(at most 128 bytes)
     * </div>
     * 
     * @param x
     * <div class="zh">
     * 		输入串的起始x坐标
     * </div>
     * <div class="en">
     * 		x coordinate of the start position to input
     * </div>
     * 
     * @param y
     * <div class="zh">
     * 		输入串的起始y坐标
     * </div>
     * <div class="en">
     * 		y coordinate of the start position to input
     * </div>
     * 
     * @param initialString
     * <div class="zh">
     * 		初始输入串. 如果为null, 表示没有初始输入串. <b>只有当D7为1时才会生效</b>
     * </div>
     * <div class="en">
     * 		initial string. null for missing.  <b>only valid when D7 is 1</b>
     * </div>
     * 
     * @param status
     * <div class="zh">
     * 		[输出]输入状态. 仅status[0]有效.  0x00: 成功, 0x0D: 用户回车退出(ENTER键按下), 0xFF: 用户取消操作
     * </div>
     * <div class="en">
     * 		[output]input status. only status[0] is valid.  0x00: OK, 0x0D: enter to quit, 0xFF: cancelled
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		用户输入的字符串, 仅当status[0]为0时有效, 否则为null.
     * </div>
     * <div class="en">
     * 		the input string, only valid when status[0] is 0, otherwise it's null.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 具有联想功能的中文字符输入函数,也可输入英文和数字字符<br/>
     * 1.输入方法切换,按F1功能键可在“拼音汉字”,“大写字母”,“小写字母”,“区位码”之间切换.拼音输入法状态下, 按F2功能键翻页;<br/> 
     * 2.输入中文,在“拼音汉字”的输入方式下,依次按下拼音所在的数字键即可。比如输入“中”字,可输入“14664”,然后用“确认”键选择“中”字即可。 <br/>
     * 3.输入大写字母和数字,在“大写字母”的输入方式下,按下你需要的字母或数字所在的按键,在0.8秒的时间内不停按下同一按键,则输入的字符为该按键上显示的下一字符,比如连续按下“1”键两次,输入的字符为“Q”。<br/>
     * 4.输入小写字母和数字,在“小写字母”的输入方式下,按下你需要的字母或数字所在的按键,在0.8秒的时间内不停按下同一按键,则输入的字符为该按键上显示的下一字符,比如连续按下“1”键3次,输入的字符为“z”。<br/>
     * 5.按下“CLEAR”键清除已输入的字符。<br/>
     * 6.D300不支持。<br/>
     * 
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * get input string with Chinese character support<br/>
     * 1.IME switched by pressing F1 key(pinyin->capital->lower-case->quwei). F2 key is used for page scrolling;<br>
     * 2.When using pinyin, pressing related button in order. e.g. to input '中', enter'14664', press enter and select it.<br/>
     * 3.When inputing alpha characters or digits, in capital mode or lower-case mode, pressing 1 key multiple times quickly(in 0.8 second)
     * to switch to the needed one and select it.<br/>
     * 4.press clear key to clear inputted character.<br/> 
     * 5.D300 doesn't support this function
     * </div>
     * 
     * @param max
     * <div class="zh">
     * 		需要输入串的最大长度(最大允许值是128字节)
     * </div>
     * <div class="en">
     * 		maximum length allowed to input(at most 128 bytes)
     * </div>
     *
     * @param timeout
     * <div class="zh">
     * 		超时设定值(单位秒), 0表示无限等待
     * </div>
     * <div class="en">
     * 		timeout(in second), 0 means wait forever
     * </div>
     * 
     * @param initialString
     * <div class="zh">
     * 		初始输入串. 如果为null, 表示没有初始输入串.
     * </div>
     * <div class="en">
     * 		initial string. null for missing. 
     * </div>
     * 
     * @param status
     * <div class="zh">
     * 		[输出]输入状态. 仅status[0]有效.  0x00: 成功, -3: 超时, -1: 用户取消操作
     * </div>
     * <div class="en">
     * 		[output]input status.  only status[0] is valid.  0x00: OK, -3: timeout, -1: cancelled
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		输入的字符串, 仅当status[0]为0时才有意义, 否则为null.
     * </div>
     * <div class="en">
     * 		inputted characters, only valid when status[0] is 0, otherwise it's null.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 设置按键背光<br/>
     * 默认情况下,系统设置背光模式为1
     * </div>
     * <div class="en">
     * set keyboard backlight mode<br/>
     * the default backligh mode is 1
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		0-关闭背光; 1-背光保持亮15秒,15秒后自动关闭; 2-背光常亮; 3-背光长暗,不会被刷卡,插卡等事件激活背光。
     * </div>
     * <div class="en">
     * 		0-off; 1-auto-off after 15 seconds; 2-on; 3-always off, cannot be activated by card insertion/swiping. 
     * </div>
     * 
     * @throws KeyboardException<div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 设置按键锁定功能<br/>
     * 未设置情况下,系统设置按键锁模式为1, mode输入错误时保持原设置不变.按键锁定后可通过短按电源按键、刷磁卡(磁卡已打开)及插拔IC卡解锁, 请注意屏幕锁定时, 键盘被关闭, LCD背光亮度为10%, 均会不受{@link #kblight(int)}函数设置为常亮的影响
     * </div>
     * <div class="en">
     * set keyboard locking mode<br/>
     * Default mode is 1. Keyborad can be unlocked by pressing power switch, swiping card or inserting ICC card.
     * Note that if screen is locked, then keyboard is locked and LCD backlight's brightness is 10%, in that case, setting keyboard backlight to on has no effect.
     * </div>
     * 
     * @param mode
     * <div class="zh">
     * 		0-按键锁定, 短按电源按键、刷磁卡及IC卡可解锁; 1-保持解锁状态30秒, 30秒后自动锁定, 或者短按电源键进行锁定和解锁的切换; 2-保持按键解锁状态, 不再被短按电源键锁定.
     * </div>
     * <div class="en">
     * 		0-keyboard locked, can be unlocked by pressing power switch, swiping card or inserting IC card;<br/>
     * 		1-auto-lock if idle for 30 seconds, can be lock/unlocked by pressing power switch;<br/>
     * 		2-unlocked, pressing power switch doesn't lock/unlock.
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * 获取当前键盘的状态信息<br/>
     * 获取触摸按键锁定状态的功能可与SysSleep结合使用, 当检测到按键已锁时调用SysSleep进入休眠状态, 此举可以在最合适的时机控制电源消耗
     * </div>
     * <div class="en">
     * get current status of keyboard<br/>
     * Entering sleep status when keyboard locking detected can save power.
     * </div>
     * 
     * @param cmd
     * <div class="zh">
     * 		0- 触摸按键锁定状态; 1- 按键缓冲区内的键值数; 2- 是否有按键音; 3- 按键背光是否打开
     * </div>
     * <div class="en">
     * 		0- if locked or not; 1 - if there's key code in key buffer or not; 2- if beep or not when pressing; 3- if keyboard backlight on or not; 
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		cmd=0: 0- 触摸按键未锁, 1- 触摸按键已锁<br/>
     *		cmd=1: >=0 键值数<br/>
     *		cmd=2: 0- 不发声, 1- 发声 <br/>
     *		cmd=3: 0- 背光关闭, 1- 背光开启
     * </div>
     * <div class="en">
     * 		cmd=0: 0- unlocked, 1- locked<br/>
     *		cmd=1: >=0 number of key code<br/>
     *		cmd=2: 0- not beep, 1- beep<br/>
     *		cmd=3: 0- backlight off, 1- backlight on
     * </div>
     * 
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * <div class="en">Keyboard error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
     * <div class="zh">设置按键板在按键时是否发声.</div>
     * <div class="en">Set whether a beep should be made when a key is pressed.</div>
     * @param flag
     * <div class="zh">
     * 			0 - 不发声, 1 - 发声
     * </div>
     * <div class="en">
     *          0 - not beep,  1 - beep
     * </div>
     * @throws KeyboardException
     * <div class="zh">Keyboard错误</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
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
