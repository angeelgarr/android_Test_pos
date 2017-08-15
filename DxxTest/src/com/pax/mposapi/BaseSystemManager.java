package com.pax.mposapi;

import java.io.IOException;

import android.R.integer;
import android.content.Context;
import android.provider.Settings.Global;

import com.pax.dxxtest.GGlobal;
import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Comm;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.MyLog;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * BaseSystemManager ���ڹ����ϵͳ, ��(��ȡϵͳ��Ϣ, ���Ʒ�����ȵ�
 * </div>
 * <div class="en">
 * BaseSystemManager manages the basic system functionalities, including 
 * getting system information, controlling beeper, exchanging data  etc. 
 * </div>
 *
 */
public class BaseSystemManager {
    public static final byte DATA_TYPE_EMV_PARAM_CMAC = 0; 
    public static final byte DATA_TYPE_SERVER_RANDOM = 1; 
    public static final byte DATA_TYPE_DEV_AND_SERVER_TOKEN = 2;  
    
    private static final int DATA_BLOCK_SEND_MAX = 1024;
    private static final int DATA_BLOCK_RECV_MAX = 4096;
	
	private static final String TAG = "BaseSystemManager";
    private final Proto proto;
    private final Proto proto1;  //By Jim 2014.09.11
    private Context context;
    private static BaseSystemManager instance;

    /**
     * <div class="zh">
     * ʹ��ָ����Context�����BaseSystemManager����
     * </div>
     * <div class="en">
     * Create a BaseSystemManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */
    private BaseSystemManager(Context context) {
    	proto = Proto.getInstance(context);
    	proto1= Proto.getInstance(context);  //By Jim 2014.09.11
    	this.context = context;
    }
    
    /**
     * Create a BaseSystemManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static BaseSystemManager getInstance(Context context) {
        if (instance == null) {
        	instance = new BaseSystemManager(context);
        }
        return instance;
    }

    /**
     * <div class="zh">����terminal�Ƿ�ɴ�</div>
     * <div class="en">test if terminal reachable.</div>
     * 
     * @return
     * <div class="zh">
     * 			true - �ɴ�,  false - ���ɴ�
     * </div>
     * <div class="en">
     *          true - reachable,  false - unreachable
     * </div>
     */    
    public boolean ping() {
    	RespCode rc = new RespCode();
    	
    	try {
    		proto.sendRecv(Cmd.CmdType.BASE_PING, new byte[0], rc, new byte[0]);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    /**
     * <div class="zh">
     * ��������100ms
     * </div>
     * <div class="en">
     * Beeper beeps for 100ms
     * </div>
     *  
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public void beep() throws BaseSystemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.BASE_BEEP, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new BaseSystemException(rc.code);    		
    	}
    }

    /**
     * 
     * <div class="zh">
     * ��ȡϵͳʱ��
     * </div>

     * <div class="en">
     * Get system date and time
     * </div>
     * 
     * @return
     * <div class="zh">
     * 			ʱ�䴮, ��ʽΪ YYMMDDhhmmssww, ww��ʾ����, 01~07, ��ӦΪ����һ~������
     * </div>
     * <div class="en">
     *          the date & time string, format is YYMMDDhhmmssww, ww stands for weekday,
     *          ranged from 01~07, corresponding to Monday~Sunday 
     * </div>
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public String getTime() throws BaseSystemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] timeBcd = new byte[7];
    	proto.sendRecv(Cmd.CmdType.BASE_GET_DATETIME, new byte[0], rc, timeBcd);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new BaseSystemException(rc.code);    		
    	}
		return Utils.bcd2Str(timeBcd);	//YYMMDDhhmmssww	ww is weekday 01~07
    }
    
    /**
     * <div class="zh">
     * ��ȡ�ն�ϵ�к�
     * </div>
     * 
     * <div class="en">
     * Read the serial number of the terminal.
     * </div>
     * 
     * @return 
     * <div class="zh">
     * 			�ն�ϵ�к�<br/>
     * 			��������Ϊ""
     * </div>
     * <div class="en">
     * 			device Serial No<br/>
     *         return "" if the serial number does not exists.
     * </div>
     *  
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public String readSN() throws BaseSystemException, IOException, ProtoException, CommonException {
        
    	byte[] sn = new byte[33]; 
        String serialNo = "";
        
        RespCode rc = new RespCode();
        proto.sendRecv(Cmd.CmdType.BASE_READ_SN, new byte[0], rc, sn);
        if (rc.code == 0) {
        	serialNo = new String(sn, 1, sn[0]);
        }
        else {
        	throw new BaseSystemException(rc.code);    		
        }
        
        return serialNo;
    }

    /**
     * <div class="zh">
     * ��ȡ�ն�)չϵ�к�
     * </div>
     * 
     * <div class="en">
     * Read the extended serial number of the terminal.
     * </div>
     * 
     * @return 
     * <div class="zh">
     * 			�ն�)չϵ�к�<br/>
     * 			��������Ϊ""
     * </div>
     * <div class="en">
     * 			device extended Serial No<br/>
     *         return "" if the extended serial number does not exists.
     * </div>
     * 
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public String exReadSN() throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] sn = new byte[33]; 
        String serialNo = "";
        
        RespCode rc = new RespCode();
        proto.sendRecv(Cmd.CmdType.BASE_READ_EXSN, new byte[0], rc, sn);
        if (rc.code == 0) {
        	serialNo = new String(sn, 1, sn[0]);
        }
        else {
        	throw new BaseSystemException(rc.code);    		
        }
        
        return serialNo;
    }

    /**
     * <div class="zh">
     * �����ն�(<b><font color="red">ע��: D180��֧�ִ˹���</font></b>)
     * </div>
     *  
     * <div class="en">
     * reboot the terminal(<b><font color="red">Note: D180 does not support this function</font></b>)
     * </div>
     * 
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public void reboot() throws BaseSystemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.BASE_REBOOT, new byte[0], rc, new byte[0]);

    	// close connection actively to avoid communcation error the first time after target boot up.
    	// no need care about if reboot succeeded or not.
		closeConnection(); 
    	
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new BaseSystemException(rc.code);    		
    	}
    }    

    /**
     * <div class="zh">
     * ��ȡ8�ֽ��������
     * </div>
	 *
     * <div class="en">
     * get 8 bytes true random number
     * </div>
     * 
     * @return 
     * <div class="en">
     * 	8 bytes true random number 
     * </div>
     * 
     * <div class="zh">
     * 	8�ֽ��������
     * </div>
     * 
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public byte[] pciGetRandom() throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] random = new byte[8];
        RespCode rc = new RespCode();
        int len = proto.sendRecv(Cmd.CmdType.BASE_GET_RANDOM, new byte[0], rc, random);
        if (len == 8 && rc.code == 0) {
        	return random;
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    }
    
    /**
     * read the battery level
     * 
     *  @return
     *  	0 - Battery voltage low and battery icon blinks. Suggested that do not process transaction, print and wireless communication etc. at this moment. You should recharge the battery immediactely to avoid shut down and lost data. <br/>
     *  	1 - Battery icon displays 1 grid. <br/>
     *  	2 - Battery icon displays 2 grid. <br/>
     *  	3 - Battery icon displays 3 grid. <br/>
     *  	4 - Battery icon displays 4 grid. <br/>
     *  	5 - Powered by external power supply and the battery in charging.<br/>
     *  	6 - Powered by external power supply and the battery charging finished. Battery icon displays full grids.
     *  
     * @throws BaseSystemException
     * base system error
     * @throws IOException
     * communication error
     * @throws ProtoException
     * protocol error
     * @throws CommonException
     * common error  
     */
    public int batteryCheck() throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[1];
        RespCode rc = new RespCode();
        proto.sendRecv(Cmd.CmdType.BASE_BATTERY_CHECK, new byte[0], rc, resp);
        if (rc.code == 0) {
        	return resp[0];
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    }
    
    /**
     * <div class="zh">
     * ��ȡ�ն˰汾��Ϣ
     * </div>
     * 
     * <div class="en">
     *  Get version information of the terminal.
     * </div>
     * 
     * @return 
     * 
     * <div class="zh">
	 *		�汾��Ϣ (8���ֽ�)<br/>
			VerInfo[0] : BOOT�汾��(1��ʼ����) <br/>
			VerInfo[1] : �����汾��(1��ʼ����) <br/>
			VerInfo[2] : ��شΰ汾��(0��ʼ����) <br/>
			VerInfo[3] : ���Ӳ���汾��(�ο�Ӳ���汾)  <br/>
			VerInfo[4] : �ӿڰ�������Ϣ <br/>
			VerInfo[5] : )չ��������Ϣ <br/>
			VerInfo[6] : ��ͷ��������Ϣ(S90˵����÷�) <br/>
			VerInfo[7] : ����
     * </div>
     * <div class="en">
	 *	Version information (8 bytes) <br/>
     *         VerInfo[0] : BOOT version number (ascending from 1) <br/>
     *         VerInfo[1] : monitor major version number (ascending from 1) <br/>
     *         VerInfo[2] : monitor minor version number (ascending from 1) <br/>
     *         VerInfo[3] : main PCB hardware version number(reter to hardware
     *         version) <br/>
     *         VerInfo[4] : interface PCB configuration information <br/>
     *         VerInfo[5]: extended PCB configuration information <br/>
     *         VerInfo[6]: magcard reader PCB configuration information <br/>
     *         VerInfo[7]: Reserved
     * </div>

     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public byte[] readVerInfo() throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] ver = new byte[8];
        RespCode rc = new RespCode();
        proto.sendRecv(Cmd.CmdType.BASE_READ_VER_INFO, new byte[0], rc, ver);
        if (rc.code == 0) {
        	return ver;
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    }
    
    /**
     * <div class="zh">
     * ��ȡ�ն��ͺż�������Ϣ
     * </div>
     * 
     * <div class="en">
     * Get terminal model and configuration information.
     * </div>
     * 
     * @return
     * <div class="zh"><p>
     *         TermInfo[0]: �ն��ͺ�[0x00-0xFF]<br/>
     *         1-P60-S <br/>
     *         2-P70 <br/>
     *         3-P60-S1 <br/>
     *         4-P80 <br/>
     *         5-P78 <br/>
     *         6-P90 <br/>
     *         7-S80 <br/>
     *         8-SP30 <br/>
     *         9-S60 <br/>
     *         10-S90 <br/>
     *         11-S78 <br/>
     *         12-MT30 <br/>
     *         13��T52 <br/>
     *		   14��S58 <br/>
     *         15��D200<br/>
     *         16��D210<br/>
     *         17��D300<br/>
     *         35-D180<br/>         
     *         0x80-R50 <br/>
     *         0x81-P50 <br/>
     *         0x82-P58 <br/>
     *         0x83-R30 <br/>
     *         0x84-R50-M <br/>
     *         0x91-D800<br/>
     *         </p>
     *         <p>
     *         TermInfo[1]: ��ӡ������ <br/>
     *         'S'-��ʽ��ӡ��. <br/>
     *         'T'-�����ӡ��
     *         <p>
     *         TermInfo[2]: MODEMģ��������Ϣ <br/> 
     *          0��������MODEMͨ��ģ�� <br/>
	 * 			����MODEMģ���ͺű���:<br/>
     *         0x01 -TDK 73K222 1200/1200 <br/>
     *         0x02 -TDK 73K224 2400/2400 <br/>
     *         0x10 -Silicon 2414 14.4k/2400 <br/>
     *         0x20 -conexant 81802 33.6k/9600 <br/>
     *         0x40 -conextant 93001-V92 56k/9600 <br/>
     *         0x41 -conextant 93001-V32B 14.4K/9600 <br/>
     *         0x80 -Zilog �Դ�  Modem 14.4K/2400
     *         <p>
     *         TermInfo[3]: ���ͬ��������Ϣ <br/>
     *         1-1200 <br/>
     *         2-2400 <br/>
     *         3-9600 <br/>
     *         4-14400
     *         <p>
     *         TermInfo[4]: ����첽������Ϣ <br/>
     *         1-1200 2-2400 3-4800 4-7200 5-9600 6-12000 7-14400 8-19200
     *         9-24000 10-26400 11-28800 12-31200 13-33600 14-48000 15-56000
     *         <p>
     *         TermInfo[5]: PCI������Ϣ <br/>
     *         0-������PCI��ȫģ��. <br/>
     *         ������PCI�İ�ȫģ��.
     *         <p>
     *         Out_info[6]: USB���(HOST)������Ϣ <br/>
     *         0   ����USB���ӿ� <br/>
	 * 			����USB���ӿڰ汾����(������USB1.1,USB2.0,USB-OTG��)
     *         <p>
     *         TermInfo[7]: USB�豸������Ϣ <br/>
	 *			0   ���޴�USB�ӿ�<br/>
	 *			����USB�豸�ӿڰ汾����
     *         <p>
     *         TermInfo[8]: LAN(TCP/IP)ģ��������Ϣ <br/>
     *         0 -��TCP/IPģ��. <br/>
     *         ����TCP/IPģ��汾����.
     *         </p>
     *         <p>
     *         TermInfo[9]: GPRSģ��������Ϣ <br/>
     *         0 -��GPRSģ��. <br/>
     *         ����GPRSģ��汾����.
     *         </p>
     *         <p>
     *         TermInfo[10]: CDMAģ��������Ϣ <br/>
     *         0 -�� CDMAģ��. <br/>
     *         ����CDMAģ��汾����.
     *         </p>
     *         <p>
     *         TermInfo[11]: WI-FIģ��������Ϣ <br/>
     *         0 -��WI-FIģ��. <br/>
     *         ����WI-FIģ��汾����.
     *         </p>
     *         <p>
     *         TermInfo[12]: �ǽӴ�ʽ��ģ��������Ϣ
     *         <br/>
     *         0 -�޷ǽӴ�ʽ��ģ�� <br/>
     *         0x01 -RF�ӿ�оƬΪRC531 <br/>
     *         0x02 -RF�ӿ�оƬΪPN512 <br/>
     *         ��S90���ṩ��TermInfo[12]�ж�оƬ���͡�
     *         </p>
     *         <p>
     *         TermInfo[13]: �Ƿ��������ֿ� <br/>
     *         0 -�������ֿ�. <br/>
     *         1 -�����ֿ�.
     *         </p>
     *         <p>
     *         TermInfo[14]: �ֿ�汾��Ϣ <br/>
     *         0 -���ֿ��ļ�. <br/>
     *         �����ֿ�汾��.
     *         </p>
     *         <p>
     *         TermInfo[15]: IC����ģ��������Ϣ <br/>
     *         0x00 -û��IC������. <br/>
     *         Others-�� ICC ����.
     *         </p>
     *         <p>
     *         TermInfo[16]: �ſ�����ģ�� <br/>
     *         0x00 -no MSR reader. <br/>
     *         Others-has MSR reader.
     *         </p>
     *         <p>
     *         TermInfo[17]: �Ƿ���Tilt Sensor <br/>
     *         0 -�� Tilt Sensor. <br/>
     *         1 -�� Tilt Sensor.
     *         </p>
     *         <p>
     *         TermInfo[18]: WCDMAģ��������Ϣ <br/>
     *         0 -��WCDMAģ��. <br/>
     *         ����WCDMAģ��汾����.
     *         </p>
     *         <p>
     *         TermInfo[19]: �Ƿ���touchscreen <br/>
     *         0 -�� touch screen. <br/>
     *         1 -�� touch screen.
     *         </p>
     *         <p>
     *         TermInfo[20]: �Ƿ���6��ģ�� <br/>
     *         0 -��6��ģ��. <br/>
     *         1 -��6��ģ��.
     *         </p>
     *         <p>
     *         TermInfo[18]-[29]:����
     *         </p>
     * </div>
     * <div class="en"><p>
     *         TermInfo[0]: terminal model[0x00-0xFF]<br/>
     *         1-P60-S <br/>
     *         2-P70 <br/>
     *         3-P60-S1 <br/>
     *         4-P80 <br/>
     *         5-P78 <br/>
     *         6-P90 <br/>
     *         7-S80 <br/>
     *         8-SP30 <br/>
     *         9-S60 <br/>
     *         10-S90 <br/>
     *         11-S78 <br/>
     *         12-MT30 <br/>
     *         13-T52 <br/>
     *		   14-S58 <br/>
     *         15-D200<br/>
     *         16-D210<br/>
     *         17-D300<br/>  
     *         35-D180<br/>                
     *         0x80-R50 <br/>
     *         0x81-P50 <br/>
     *         0x82-P58 <br/>
     *         0x83-R30 <br/>
     *         0x84-R50-M <br/>
     *         0x91-D800<br/>
     *         TermInfo[1]: printer type <br/>
     *         'S'-Stylus printer. <br/>
     *         'T'-Thermal printer.
     *         </p>
     *         <p>
     *         TermInfo[2]: MODEM module configuration information 0 -do not
     *         support MODEM communication module Others -MODEM module type code
     *         <br/>
     *         0x01 -TDK 73K222 1200/1200 <br/>
     *         0x02 -TDK 73K224 2400/2400 <br/>
     *         0x10 -Silicon 2414 14.4k/2400 <br/>
     *         0x20 -conexant 81802 33.6k/9600 <br/>
     *         0x40 -conextant 93001-V92 56k/9600 <br/>
     *         0x41 -conextant 93001-V32B 14.4K/9600 <br/>
     *         0x80 -Zilog combine Modem 14.4K/2400
     *         </p>
     *         <p>
     *         TermInfo[3]: MODEM highest sync. baud rate <br/>
     *         1-1200 <br/>
     *         2-2400 <br/>
     *         3-9600 <br/>
     *         4-14400
     *         </p>
     *         <p>
     *         TermInfo[4]: MODEM highest async. baud rate <br/>
     *         1-1200 2-2400 3-4800 4-7200 5-9600 6-12000 7-14400 8-19200
     *         9-24000 10-26400 11-28800 12-31200 13-33600 14-48000 15-56000
     *         </p>
     *         <p>
     *         TermInfo[5]: PCI configuration information <br/>
     *         0-no embedded PCI secure module. <br/>
     *         Others-compliant PCI secure module.
     *         </p>
     *         <p>
     *         Out_info[6]: USB HOST configuration information <br/>
     *         0 -no USB host interface. <br/>
     *         Others-USB host interface version code.
     *         </p>
     *         <p>
     *         TermInfo[7]: USB device interface information <br/>
     *         0-no dependent USB device interface. <br/>
     *         Others-USB device interface version code.
     *         </p>
     *         <p>
     *         TermInfo[8]: LAN(TCP/IP)module configuration information <br/>
     *         0 -no TCP/IP module. <br/>
     *         Others-TCP/IP module version code.
     *         </p>
     *         <p>
     *         TermInfo[9]: GPRS module configuration information <br/>
     *         0 -no GPRS module. <br/>
     *         Others-GPRS module version code.
     *         </p>
     *         <p>
     *         TermInfo[10]: CDMA module configuration information <br/>
     *         0 -no CDMA module. <br/>
     *         Others-CDMA module version code.
     *         </p>
     *         <p>
     *         TermInfo[11]: WI-FI module configuration information <br/>
     *         0 -no WI-FI module. <br/>
     *         Others-WI-FI module version code(Invalid for S80).
     *         </p>
     *         <p>
     *         TermInfo[12]: Contactless reader module configuration information
     *         <br/>
     *         0 -no contactless reader module <br/>
     *         0x01 -RF interface chip is RC531 <br/>
     *         0x02 -RF interface chip is PN512 <br/>
     *         Only S90 can supply 'TermInfo[12] judge the chip type'.
     *         </p>
     *         <p>
     *         TermInfo[13]: has Chinese font library or not <br/>
     *         0 -not Chinese font library. <br/>
     *         1 -Chinese font library.
     *         </p>
     *         <p>
     *         TermInfo[14]: the version information of font library <br/>
     *         0 -no font library file. <br/>
     *         Others-the version number of font library.
     *         </p>
     *         <p>
     *         TermInfo[15]: ICC reader module configuration information <br/>
     *         0x00 -no ICC reader. <br/>
     *         Others-has ICC reader.
     *         </p>
     *         <p>
     *         TermInfo[16]: MSR reader module <br/>
     *         0x00 -no MSR reader. <br/>
     *         Others-has MSR reader.
     *         </p>
     *         <p>
     *         TermInfo[17]: has Tilt Sensor or not <br/>
     *         0 -no Tilt Sensor. <br/>
     *         1 -has Tilt Sensor.
     *         </p>
     *         <p>
     *         TermInfo[18]: has WCDMA module or not <br/>
     *         0 -no WCDMA module. <br/>
     *         Others-WCDMA module version code.
     *         </p>
     *         <p>
     *         TermInfo[19]: has touch screen or not <br/>
     *         0 -no touch screen. <br/>
     *         1 -has touch screen.
     *         </p>
     *         <p>
     *         TermInfo[20]: has bluetooth or not <br/>
     *         0 -no bluetooth. <br/>
     *         1 -has bluetooth.
     *         </p>
     *         <p>
     *         TermInfo[18]-[29]:reserved
     *         </p>
     * </div>
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public byte[] getTermInfo() throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] info = new byte[30];
        RespCode rc = new RespCode();
        proto.sendRecv(Cmd.CmdType.BASE_READ_TERM_INFO, new byte[0], rc, info);
        if (rc.code == 0) {
        	return info;
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    }

    /**
     * <div class="zh">
	 * ���������ָ����Ƶ�ʺͳ���ʱ�䷢��.�а������ֹBeepF(�����������ʱ��������Ӧ����)���˳���ػ����⣩��
     * </div>
     * 
     * <div class="en">
     * Beep at specified frequency and lasts for specified time. Stop beep when
     * any key pressed (stop respond to key pressing when the buffer is full),
     * then quit the function (except On/Off key).
     * </div>
     * 
     * @param mode
     * <div class="zh">
	 *			Ƶ���趨��������0~6��ֵ, 0Ϊ���Ƶ��, 6Ϊ���Ƶ��
     * </div>
     * 
     * <div class="en">
     *            Specify the frequencies. mode% 7 could be 0 ~ 6. <br/>
     *            0 indicates the lowest frequency <br/>
     *            6 indicates the highest frequency
     * </div>
     * @param DlyTime
     * <div class="zh">
	 *			������ʱ��(��λ��ms)
     * </div>
     * <div class="en">
     *            Lasting time of beep(unit: ms)
     * </div>
     * @throws BaseSystemException
     * <div class="zh">��ϵͳ����</div>
     * <div class="en">base system error</div>
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
    public void beepF(byte mode, int DlyTime) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] req = new byte[3];
    	req[0] = mode;
    	req[1] = (byte)(DlyTime / 256);
    	req[2] = (byte)(DlyTime % 256);
    	
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.BASE_BEEF, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new BaseSystemException(rc.code);    		
    	}
    }    

    /**
     * <div class="zh">
	 * �ر���POS terminal��l��<br/>
     * </div>
     * 
     * <div class="en">
     * close the connection with POS terminal
     * </div>
     */        
    public void closeConnection() {
    	Comm.getInstance(context).close();
    }
    
    /**
     * exchange data with the pos terminal
     * @param type
     * 	data type<br/>
     * 	<ul>
     * 		<li>{@link #DATA_TYPE_EMV_PARAM_CMAC} - EMV param + CMAC(8B)
     * 		<li>{@link #DATA_TYPE_SERVER_RANDOM} - auth dukpt engine no.(1B) + data dukpt engine no.(1B) + server random(16B)
     * 		<li>{@link #DATA_TYPE_DEV_AND_SERVER_TOKEN} - encrypted (device token(16B) + server token(16B))
     *  </ul>
     * @param data
     * 	data bytes to send<br/>
     * @return
     * 		the data returned, depends on the data type. null if no data returned.
     * 	<ul>
     * 		<li>for type {@link #DATA_TYPE_EMV_PARAM_CMAC}, returns null
     * 		<li>for type {@link #DATA_TYPE_SERVER_RANDOM}, returns encrypted data(device token(16B) + device random(16B)) + KSN(10B)
     * 		<li>for type {@link #DATA_TYPE_DEV_AND_SERVER_TOKEN}, returns encrypted data(server token(16B) + device sn(16B))
     *  </ul>
     * @throws BaseSystemException
     * 	base system error
     * @throws IOException
     * 	communication error
     * @throws ProtoException
     * 	protocol error
     * @throws CommonException
     * 	common error
     */
    public byte[] exchangeData(byte type, byte[] data) throws BaseSystemException, IOException, ProtoException, CommonException {
    	int totalLen = data.length;
    	int offset = 0;
    	byte[] ret = null; 
    	int remainLen = totalLen;
    	
    	while (remainLen > 0) {
    		int thisLen = (remainLen > DATA_BLOCK_SEND_MAX) ? DATA_BLOCK_SEND_MAX : remainLen;
    		byte[] req = new byte[1 + 4 + 4 + 4 + thisLen];
    		req[0] = type;
    		Utils.int2ByteArray(totalLen, req, 1);				// total len
    		Utils.int2ByteArray(offset, req, 1 + 4);			// offset
    		Utils.int2ByteArray(thisLen, req, 1 + 4 + 4);		// this len
    		System.arraycopy(data, offset, req, 1 + 4 + 4 + 4, thisLen);	//data

        	RespCode rc = new RespCode();
        	byte[] resp = new byte[DATA_BLOCK_RECV_MAX]; 
        	int respLen = proto.sendRecv(Cmd.CmdType.CMD_EXCHANGE_DATA, req, rc, resp);
        	if (rc.code == 0) {
        		//success
        		/*
        		if (type == DATA_TYPE_EMV_PARAM_CMAC) {
        			// do nothing
        		} else if (type == DATA_TYPE_SERVER_RANDOM || type == DATA_TYPE_DEV_AND_SERVER_TOKEN) {
        			ret = new byte[respLen];
        			System.arraycopy(resp, 0, ret, 0, respLen);
        			//break;		// because I know what the data is!
        		}
        		*/
        		
        		// don't care about the type
        		if (respLen > 0) {
        			ret = new byte[respLen];
        			System.arraycopy(resp, 0, ret, 0, respLen);
        		}
        		
        	} else if (CommonException.isCommonExceptionCode(rc.code)){
            	throw new CommonException(rc.code);    		
        	} else {
        		throw new BaseSystemException(rc.code);
        	}
    		
    		offset += thisLen;
    		remainLen -= thisLen;
    	}
    	
    	return ret;
    }
    
    /**
     * calculate CMAC,  See http://en.wikipedia.org/wiki/CMAC for details.
     * @param data
     * 	data to calculate CMAC(3DES).
     * @return
     * 	CMAC, 8 bytes
     * @throws BaseSystemException
     * 	base system error
     * @throws IOException
     * 	communication error
     * @throws ProtoException
     * 	protocol error
     * @throws CommonException
     * 	common error
     */
    public byte[] calcCMAC(byte[] data) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] req = new byte[2 + data.length];
    	Utils.short2ByteArray((short)data.length, req, 0);
    	System.arraycopy(data, 0, req, 2, data.length);
    	
        RespCode rc = new RespCode();
        byte[] resp = new byte[8];
        proto.sendRecv(Cmd.CmdType.CMD_CALC_CMAC, req, rc, resp);
        if (rc.code == 0) {
        	//success
        } else if (CommonException.isCommonExceptionCode(rc.code)){
        	throw new CommonException(rc.code);    		
    	} else {
    		throw new BaseSystemException(rc.code);
    	}
        
        return resp;
    }
    
    /**
     * <div class="zh">��ʼ��֧���豸</div>
     * <div class="en">Initializes the payment device</div>
     * 
     * @return
     * <div class="zh">
     * 			���������
     * </div>
     * <div class="en">
     *          data content
     * </div>
     */    

    //open ����
    public byte[] open(String sequenceID,String EMDKapplication,String securityMode,String secretCode) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();//rc=0
        String s="open|"+sequenceID+"|"+EMDKapplication+"|"+securityMode+"|"+secretCode;
    	byte[] req = s.getBytes();//req
    	MyLog.i(TAG, "open sendRecv1........");
        proto.sendRecvOPen(Cmd.CmdType.MTLA_OPEN,req, rc, resp);
        if (rc.code == 0) 
        {
        	//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    	return resp;
    }
    public byte[] open111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();//rc=0
        String s=index;
    	byte[] req = s.getBytes();//req
    	MyLog.i(TAG, "open sendRecv1........");
        proto.sendRecvOPen(Cmd.CmdType.MTLA_OPEN,req, rc, resp);
        if (rc.code == 0) 
        {
        	//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    	return resp;
    }
    
    /**
     * <div class="zh">�ر����豸��l��</div>
     * <div class="en">Closes the port and disconnects the PAYMENT DEVICE device</div>
     * 
     * @return
     * <div class="zh">
     * 			���������
     * </div>
     * <div class="en">
     *          data content
     * </div>
     */    
    public byte[] setSessionKey (String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
        String s="setSessionKey|"+sequenceID+"|"+sessionCode;
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_SETSESSION_KEY,req, rc, resp);
        if (rc.code == 0) 
        {
        		//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    	return resp;
    }
    public byte[] setSessionKey1 (String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
        String s="setSessionKey1|"+sequenceID+"|"+sessionCode;
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_SETSESSION_KEY,req, rc, resp);
        if (rc.code == 0) 
        {
        		//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    	return resp;
    }
    public byte[] setSessionKey111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
        String s=index;
    	byte[] req = s.getBytes();
    	
        proto.sendRecv(Cmd.CmdType.MTLA_SETSESSION_KEY,req, rc, resp);
        if (rc.code == 0) 
        {
        		//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
    	return resp;
    }
    
    public byte[] close(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	String s="close|"+sequenceID+"|"+sessionCode;
    	byte[] req = s.getBytes();
    	
        proto.sendRecvClose(Cmd.CmdType.MTLA_CLOSE,req, rc, resp);
        if (rc.code == 0) {
        	//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
        return resp;
    }
    public byte[] close111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	String s=index;
    	byte[] req = s.getBytes();
    	
        proto.sendRecvClose(Cmd.CmdType.MTLA_CLOSE,req, rc, resp);
        if (rc.code == 0) {
        	//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
        return resp;
    }
    public byte[] close1(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
    	byte[] resp = new byte[128];
    
        RespCode rc = new RespCode();
    	String s="close|"+sequenceID+"|"+sessionCode;
    	byte[] req = s.getBytes();
    	
        proto.sendRecvClose1(Cmd.CmdType.MTLA_CLOSE,req, rc, resp);
        if (rc.code == 0) {
        	//success
        } else {
        	throw new BaseSystemException(rc.code);    		
        }
        return resp;
    }

/**
 * <div class="zh">�ر����豸��l��</div>
 * <div class="en">Closes the port and disconnects the PAYMENT DEVICE device</div>
 * 
 * @return
 * <div class="zh">
 * 			���������
 * </div>
 * <div class="en">
 *          data content
 * </div>
 * @throws InterruptedException 
 */    


public byte[] abort(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException, InterruptedException {
	byte[] resp = new byte[1280];

    RespCode rc = new RespCode();
	/*String s="abort|123456|1234567890";*/
    String s="abort|"+sequenceID+"|"+sessionCode;
	byte[] req = s.getBytes();
	MyLog.i(TAG, "enter sendRecv1........");
	//proto.comm.close();
	//proto1.comm.connect();
	proto.tries=0;
    proto.sendRecv1(Cmd.CmdType.MTLA_ABORT,req, rc, resp);
    //proto1.comm.close();
    if (rc.code == 0) {
    	//success
    } else {
    	throw new BaseSystemException(rc.code);    		
    }
    return resp;
	}

public byte[] abort111(String index) throws BaseSystemException, IOException, ProtoException, CommonException, InterruptedException {
	byte[] resp = new byte[1280];

    RespCode rc = new RespCode();
	/*String s="abort|123456|1234567890";*/
    String s=index;
	byte[] req = s.getBytes();
	MyLog.i(TAG, "enter sendRecv1........");
	proto.tries=0;
	//proto.comm.close();
	//proto1.comm.connect();
	proto.sendRecv1(Cmd.CmdType.MTLA_ABORT,req, rc, resp);

	//Ocean 2015.2.4
	//Thread.sleep(1000);
    //proto1.comm.close();
    if (rc.code == 0) {
    	//success
    } else {
    	throw new BaseSystemException(rc.code);    		
    }
    return resp;
	}

	public byte[] readCardData(String sequenceID,String sessionCode,String transactionType,
	String amount,String otherAmount,String cardEntryMode,String readTimeout,String message1, String message2) throws BaseSystemException, IOException, ProtoException, CommonException {
	byte[] resp = new byte[1024];

    RespCode rc = new RespCode();
    String s="readCardData|"+sequenceID+"|"+sessionCode+"|"+transactionType+"|"
    +amount+"|"+otherAmount+"|"+cardEntryMode+"|"+readTimeout+"|"+message1+"|"+message2;
	byte[] req = s.getBytes();
	
    proto.sendRecv(Cmd.CmdType.MTLA_READ_CARDDATA,req, rc, resp);
    if (rc.code == 0) {
    	//success
    } else {
    	throw new BaseSystemException(rc.code);    		
    }
    return resp;
	}
	public byte[] readCardData1111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[1024];

	    RespCode rc = new RespCode();
	    String s=index;
	    MyLog.e(TAG, "readCardData1111: " + s);
		byte[] req = s.getBytes();
		
//	    proto.sendRecv(Cmd.CmdType.MTLA_READ_CARD_DATA,req, rc, resp);
		proto.sendRecv(Cmd.CmdType.MTLA_READ_CARD_DATA,req, rc, resp);
	    
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] readCardData111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[1024];

	    RespCode rc = new RespCode();
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_READ_CARDDATA,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] readCardData111Step(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[1024];

	    RespCode rc = new RespCode();
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecvStep(Cmd.CmdType.MTLA_READ_CARDDATA,req, rc, resp, false);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] readCardData1(String sequenceID,String sessionCode,String transactionType,
			String amount,String otherAmount,String cardEntryMode,String readTimeout,String message1, String message2,String m1,String m2) throws BaseSystemException, IOException, ProtoException, CommonException {
			byte[] resp = new byte[1024];

		    RespCode rc = new RespCode();
		    String s="readCardData|"+sequenceID+"|"+sessionCode+"|"+transactionType+"|"
		    +amount+"|"+otherAmount+"|"+cardEntryMode+"|"+readTimeout+"|"+message1+"|"+message2+"|"+m1 +"|"+m2;
			byte[] req = s.getBytes();
			
		    proto.sendRecv(Cmd.CmdType.MTLA_READ_CARDDATA,req, rc, resp);
		    if (rc.code == 0) {
		    	//success
		    } else {
		    	throw new BaseSystemException(rc.code);    		
		    }
		    return resp;
			}
	public byte[] readCardData2(String sequenceID,String sessionCode,String transactionType,
			String amount,String otherAmount,String cardEntryMode,String readTimeout,String message1, String message2,String m1,String m2) throws BaseSystemException, IOException, ProtoException, CommonException {
			byte[] resp = new byte[1024];

		    RespCode rc = new RespCode();
		    String s="read|"+sequenceID+"|"+sessionCode+"|"+transactionType+"|"
		    +amount+"|"+otherAmount+"|"+cardEntryMode+"|"+readTimeout+"|"+message1+"|"+message2+"|"+m1 +"|"+m2;
			byte[] req = s.getBytes();
			
		    proto.sendRecv(Cmd.CmdType.MTLA_READ_CARDDATA,req, rc, resp);
		    if (rc.code == 0) {
		    	//success
		    } else {
		    	throw new BaseSystemException(rc.code);    		
		    }
		    return resp;
			}
	
	public byte[] enableKeypad(String sequenceID,String sessionCode,String timeout) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="enableKeypad|"+sequenceID+"|"+sessionCode+"|"+timeout;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_ENABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] enableKeypad111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_ENABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] enableKeyPad1(String sequenceID,String sessionCode,String timeout) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="open|"+sequenceID+"|"+sessionCode+"|"+timeout;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_ENABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] disableKeyPad(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="disableKeypad|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_DISABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] disableKeyPad111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_DISABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] disableKeyPad1(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="disableKeypad1|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_DISABLE_KEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}

	public byte[] promptPIN(String sequenceId,String sessionCode,String accountNumber,String pinOptional,
			String minLength,String maxLength,String messageTitle,String message1,String message2,String readTimeout) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="promptPIN|"+sequenceId+"|"+sessionCode+"|"+accountNumber+"|"+pinOptional
	    		+"|"+minLength+"|"+maxLength+"|"+messageTitle+"|"+message1+"|"+message2+"|"+readTimeout;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETPIN,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] promptPIN111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETPIN,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getEMVTags(String sequenceID,String sessionCode,String TLVTag) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

	    RespCode rc = new RespCode();
	    //<TagIDs>=<value>  Values=|5A085413330089601075|5E0300|0000000000|
	    //Rx Message : ��getEmvTags|<sequenceID>|<sessionCode>|<TagIDs>=<value>|<value>|<value>��
	    //TagIDs=|5A|9F34|95

	    String s="getEmvTags|"+sequenceID+"|"+sessionCode+"|"+TLVTag;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getEMVTags111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

	    RespCode rc = new RespCode();
	    //<TagIDs>=<value>  Values=|5A085413330089601075|5E0300|0000000000|
	    //Rx Message : ��getEmvTags|<sequenceID>|<sessionCode>|<TagIDs>=<value>|<value>|<value>��
	    //TagIDs=|5A|9F34|95

	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getEMVTags1(String sequenceID,String sessionCode,String TLVTag) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

	    RespCode rc = new RespCode();
	    //<TagIDs>=<value>  Values=|5A085413330089601075|5E0300|0000000000|
	    //Rx Message : ��getEmvTags|<sequenceID>|<sessionCode>|<TagIDs>=<value>|<value>|<value>��
	    //TagIDs=|5A|9F34|95

	    String s="getEmv|"+sequenceID+"|"+sessionCode+"|"+TLVTag;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setEMVTags(String sequenceID,String sessionCode,String TagIDs) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
	    //��setEmvTags|<sequenceID>|<sessionCode>
	    //<TagIDs>=<value>|<value>|<value>|<Values>=<value>|<value>|<value>��

	    String s="setEmvTags|"+sequenceID+"|"+sessionCode+"|"+TagIDs;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setEMVTags111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
	    //��setEmvTags|<sequenceID>|<sessionCode>
	    //<TagIDs>=<value>|<value>|<value>|<Values>=<value>|<value>|<value>��

	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setEMVTags1(String sequenceID,String sessionCode,String TagIDs) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
	    //��setEmvTags|<sequenceID>|<sessionCode>
	    //<TagIDs>=<value>|<value>|<value>|<Values>=<value>|<value>|<value>��

	    String s="set|"+sequenceID+"|"+sessionCode+"|"+TagIDs;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETEMVTAG,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] getBatteryLevel(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();

	    String s="getBatteryLevel|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_LEVEL,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getBatteryLevel111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();

	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_LEVEL,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getBatteryLevel1(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();

	    String s="get|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_LEVEL,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] setBatteryThreshhold(String sequenceID,String sessionCode,String minimumBatteryValue,String lowBatteryMessage) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
	    String s="setLowBatteryTRhreshold|"+sequenceID+"|"+sessionCode+"|"+minimumBatteryValue+"|"+lowBatteryMessage;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETBATTERY_THRESHOLE,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setBatteryThreshhold111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
	    String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETBATTERY_THRESHOLE,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getLowBatteryThreshhold(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
		System.err.print("Test........");
 	    RespCode rc = new RespCode();
        String s="getLowBatteryTRhreshold|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_THRESHOLD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getLowBatteryThreshhold111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
		System.err.print("Test........");
 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_THRESHOLD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getLowBatteryThreshhold1(String sequenceID,String sessionCode) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
		System.err.print("Test........");
 	    RespCode rc = new RespCode();
        String s="get|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETBATTERY_THRESHOLD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	
	public byte[] createMAC(String sequenceID,String sessionCode, String key) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="createMAC|"+sequenceID+"|"+sessionCode+"|"+key;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_CREATMAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] createMAC111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_CREATMAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] createMAC1(String sequenceID,String sessionCode, String key) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="create|"+sequenceID+"|"+sessionCode+"|"+key;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_CREATMAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] validateMAC(String sequenceID, String sessionCode, String u8MACBlock, String langCode, String u8TPKKey, String u8TAKKey, String message1, String message2, String u8MACData) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="validateMAC|"+sequenceID+"|"+sessionCode+"|"+u8MACBlock+"|"
 	    +langCode+"|"+u8TPKKey+"|"+u8TAKKey+"|"+message1+"|"+message2+"|"+u8MACData;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_VALIDATE_MAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] validateMAC111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_VALIDATE_MAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] validateMAC1(String sequenceID, String sessionCode, String u8MACBlock, String langCode, String u8TPKKey, String u8TAKKey, String message1, String message2, String u8MACData) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="creat|"+sequenceID+"|"+sessionCode+"|"+u8MACBlock+"|"
 	    +langCode+"|"+u8TPKKey+"|"+u8TAKKey+"|"+message1+"|"+message2+"|"+u8MACData;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_VALIDATE_MAC,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] completeOnLineEMV(String sequeneID, String sessionCode, String hostDecision, 
			String displayResult, String TagIDs) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="completeOnLineEMV|"+sequeneID+"|"+sessionCode+"|"+hostDecision+"|"+
 	    displayResult+"|"+TagIDs;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_EMVOLINE_FINISH,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] completeOnLineEMV111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_EMVOLINE_FINISH,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] completeOnLineEMV1(String sequenceID, String sessionCode, String hostDecision, 
			String displayResult, String TagIDs) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="completeOnLine|"+sequenceID+"|"+sessionCode+"|"+hostDecision+"|"+
 	    displayResult+"|"+TagIDs;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_EMVOLINE_FINISH,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] completeOnLineEMV2(String sequeneID, String sessionCode, String hostDecision, 
			String displayResult, String TagIDs,String Values) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="completeOnLineEMV|"+sequeneID+"|"+sessionCode+"|"+hostDecision+"|"+
 	    displayResult+"|"+TagIDs+"|"+Values;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_EMVOLINE_FINISH,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] authorizeCard(String sequenceID, String sessionCode, String amount, String otherAmount,String merchantDecision,
			String DisplayResult, String PINTryExceedStatus, String displayAmount, String displayAppExpired,String timeout,String Tag) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

 	    RespCode rc = new RespCode();
        String s="authorizeCard|"+sequenceID+"|"+sessionCode+"|"+amount+"|"+otherAmount+"|"+merchantDecision+"|"
        		+DisplayResult+"|"+PINTryExceedStatus+"|"+displayAmount+"|"+displayAppExpired+"|"+timeout+"|"+Tag;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_AUTHORIZE_CARD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] authorizeCard111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_AUTHORIZE_CARD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] authorizeCard111Step(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecvStep(Cmd.CmdType.MTLA_AUTHORIZE_CARD,req, rc, resp,false);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] authorizeCard1(String sequenceID, String sessionCode, String amount, String otherAmount,String merchantDecision,
			String DisplayResult, String PINTryExceedStatus, String displayAmount, String displayAppExpired,String timeout, String Tag) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[512];

 	    RespCode rc = new RespCode();
        String s="authorize|"+sequenceID+"|"+sessionCode+"|"+amount+"|"+otherAmount+"|"
        		+DisplayResult+"|"+PINTryExceedStatus+"|"+displayAmount+"|"+displayAppExpired+"|"+timeout + "|" +Tag;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_AUTHORIZE_CARD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] promptAdditionalInfo(String sequenceId, String sessionCode, String amount, String langCode, String tip,
			String cashback, String surcharge, String readTimeout) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="promptAdditionalInfo|"+sequenceId+"|"+sessionCode+"|"+amount+"|"+
        			langCode+"|"+tip+"|"+cashback+"|"+surcharge+"|"+readTimeout;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_OTHERINFO,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] promptAdditionalInfo111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_OTHERINFO,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] removeCard(String sequenceId, String sessionCode, String message1, String message2) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="removeCard|"+sequenceId+"|"+sessionCode+"|"+
        		message1+"|"+message2;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_REMOVECARD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] removeCard111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_REMOVECARD,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}

	public byte[] setParameter(String sequenceId, String sessionCode, String parameter,String sleepModeTimeout,String dataEncryptionKeySlot,
			String dataEncryptionType,String maskFirstDigits, String LanguageType,String AIDFilterAllowedFlag,String BTDiscoveryAllowedFlag,String DukptKeySlot) throws BaseSystemException, IOException, ProtoException, CommonException {
		
	
//		e.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome","sleepModeTimeout=0",
//				"dataEncryptionKeySlot=2","dataEncryptionType=4","maskFirstDigits=4","LanguageType=0","AIDFilterAllowedFlag=0",
//				"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
		
		
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="setParameter|"+sequenceId+"|"+sessionCode+"|"+parameter+"|"+sleepModeTimeout+"|"+dataEncryptionKeySlot+"|"+
 	    dataEncryptionType+"|"+maskFirstDigits+"|"+LanguageType+"|"+AIDFilterAllowedFlag+"|"+BTDiscoveryAllowedFlag+"|"+DukptKeySlot;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setParameter111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] setParameter1(String sequenceId, String sessionCode, String parameter,String sleepModeTimeout,
			String dataEncryptionType,String dataEncryptionKeySlot, String maskFirstDigits) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="set|"+sequenceId+"|"+sessionCode+"|"+parameter+"|"+sleepModeTimeout 
        		+"|"+dataEncryptionType+"|"+dataEncryptionKeySlot+"|"+maskFirstDigits;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] getParameter(String sequenceId, String sessionCode, String parameter1, String parameter2, String parameter3, String parameter4, String parameter5) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="getParameter|"+sequenceId+"|"+sessionCode+"|"+
        		parameter1 + "|" + parameter2 + "|" + parameter3 +"|" + parameter4 + "|" +parameter5;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	public byte[] getParameter111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}

	public byte[] getParameter1(String sequenceId, String sessionCode, String parameter1, String parameter2, String parameter3, String parameter4, String parameter5) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s="a"+"|"+sequenceId+"|"+sessionCode+"|"+
        		parameter1 + "|" + parameter2 + "|" + parameter3 +"|" + parameter4 + "|" +parameter5;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GETPARAMETER,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
//byte[] FILE
	public byte[] downloadFile(String sequenceID, String sessionCode, String type, String mark, 
			String transferlength, byte[] FILE) throws BaseSystemException, IOException, ProtoException, CommonException {
		
//		GGlobal g = new GGlobal();
//		g.getgglobal(1);
		
		byte[] resp = new byte[128];
		int i;
 	    RespCode rc = new RespCode();
        String s="downloadFile|"+sequenceID+"|"+sessionCode+"|"+
        		type+"|"+mark+"|"+transferlength+"|";
		byte[] req = s.getBytes();
		byte[] temp;
		if(FILE != null)
		{
			temp=new byte[req.length+FILE.length];
		}
		else
		{
			temp=new byte[req.length];
		}
		for(i=0;i<req.length;i++)
		{
			temp[i]=req[i];
		}
		if(FILE != null)
		{
			for(i=0;i<FILE.length;i++)
			{
				temp[req.length+i]=FILE[i];
			}
		}
	    proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_FILE,temp, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
//	    g.getgglobal(1);
	    return resp;
	   
	}
//	public byte[] downloadRSAKey(String sequenceId, String sessionCode,String aucModulus, String aucExponent) throws BaseSystemException, IOException, ProtoException, CommonException {
//		byte[] resp = new byte[128];
//	
// 	    RespCode rc = new RespCode();
//        String s="downloadKey|"+sequenceId+"|"+sessionCode+"|"+aucModulus+"|"+aucExponent;
//        //String t="|";
//		byte[] req = s.getBytes();
//	    proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_RSAKEY,req, rc, resp);
//	    if (rc.code == 0) {
//	    	//success
//	    } else {
//	    	throw new BaseSystemException(rc.code);    		
//	    }
//	    return resp;
//	}
	public byte[] downloadKey(String sequenceID, String sessionCode,String keyType, String keyData1,String keyData2,String destKeySlot,String srcKeyType,String srcKeySlot,String checkMode,String KCVLength,String CheckBuff) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
	
 	    RespCode rc = new RespCode();
        String s="downloadKey|"+sequenceID+"|"+sessionCode+"|"+keyType+"|"+keyData1+"|"+keyData2+"|"+destKeySlot+"|"+srcKeyType+"|"+srcKeySlot+"|"+checkMode+"|"+KCVLength+"|"+CheckBuff;
        //String t="|";
		byte[] req = s.getBytes();
	    proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_RSAKEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
	}
	
	public byte[] downloadKey111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
	
 	    RespCode rc = new RespCode();
        String s = index;
        //String t="|";
		byte[] req = s.getBytes();
	    proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_RSAKEY,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
	}

	public byte[] getEncryptedData(String sequenceID, String sessionCode,String dataFlag) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[1024];
 	    RespCode rc = new RespCode();
        String s="getEncryptedData|"+sequenceID+"|"+sessionCode+"|"+dataFlag;
        //String t="|";
		byte[] req = s.getBytes();
		   proto.sendRecv(Cmd.CmdType.MTLA_GET_ENCRYPTED_DATA,req, rc, resp);
		    if (rc.code == 0) {
		    	//success
		    } else {
		    	throw new BaseSystemException(rc.code);    		
		    }
		    return resp;
		}
	public byte[] getEncryptedData111(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[1024];
 	    RespCode rc = new RespCode();
        String s=index;
        //String t="|";
		byte[] req = s.getBytes();
		   proto.sendRecv(Cmd.CmdType.MTLA_GET_ENCRYPTED_DATA,req, rc, resp);
		    if (rc.code == 0) {
		    	//success
		    } else {
		    	throw new BaseSystemException(rc.code);    		
		    }
		    return resp;
		}


	
	public byte[] RSAKeyTest(String sequenceId, String Moduel, String pubkey) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];
		int i;
		int j,k,l,m;
 	    RespCode rc = new RespCode();
        String s="RSAKeyTest|"+sequenceId+"|"+Moduel+"|"+pubkey+"|";
		
        byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_RSAKEY_TEST,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;

	}
	
	public byte[] getDateTime(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GET_DATE_TIME,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] setDateTime(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_SET_DATE_TIME,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	
	public byte[] getStatusUpdate(String index) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
        String s=index;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_GET_STATUS_UPDATE,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
	
	public byte[] getpinblock(int KeyIdx, int ExpPinLenIn,int DataIn) throws BaseSystemException, IOException, ProtoException, CommonException {
		byte[] resp = new byte[128];

 	    RespCode rc = new RespCode();
 	    
 	   String s=KeyIdx+"|"+ExpPinLenIn+"|"+DataIn;
		
       byte[] req = s.getBytes();
		
 	    proto.sendRecv(Cmd.CmdType.MTLA_GET_STATUS_UPDATE,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
		}
}


/**
 * <div class="zh"></div>
 * <div class="en">Motorola EMDK will have user and administrator access level, this API is only accessible by the system administrator. The administrator or the store owner will set the D180 terminal access session code.</div>
 * 
 * @return
 * <div class="zh">
 * 			���������
 * </div>
 * <div class="en">
 *          data content
 * </div>
 */    

