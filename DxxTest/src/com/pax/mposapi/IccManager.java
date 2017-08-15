package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.APDU_RESP;
import com.pax.mposapi.model.APDU_SEND;

/**
 * <div class="zh">
 * IccManager 用于访问IC卡读卡器并与IC卡交互
 * </div>
 * <div class="en">
 * IccManager is used to control the ICC reader to interact with the ICC.  
 * </div>
 *
 */
public class IccManager {
    //bit 0 ~ 2
    public static final byte ICC_SLOT_BIT_SLOT0 = 0x00;
    public static final byte ICC_SLOT_BIT_SLOT1 = 0x01;
    public static final byte ICC_SLOT_BIT_SLOT2 = 0x02;
    public static final byte ICC_SLOT_BIT_SLOT3 = 0x03;
    public static final byte ICC_SLOT_BIT_SLOT4 = 0x04;
    public static final byte ICC_SLOT_BIT_SLOT5 = 0x05;
    public static final byte ICC_SLOT_BIT_SLOT6 = 0x06;
    public static final byte ICC_SLOT_BIT_SLOT7 = 0x07;
    //bit 3 ~ 4
    public static final byte ICC_SLOT_BIT_1_8V	= 0x08;
    public static final byte ICC_SLOT_BIT_3V	= 0x10;
    public static final byte ICC_SLOT_BIT_5V	= 0x18;
    //bit 5
    public static final byte ICC_SLOT_BIT_NO_PPS= 0x00;
    public static final byte ICC_SLOT_BIT_PPS	= 0x20;
    //bit 6
    public static final byte ICC_SLOT_BIT_9600	= 0x00;
    public static final byte ICC_SLOT_BIT_38400	= 0x40;
    //bit 7
    public static final byte ICC_SLOT_BIT_EMV	= 0x00;
    public static final byte ICC_SLOT_BIT_ISO7816_3 = (byte)0x80;
		
    private static final String TAG = "IccManager";
    private final Proto proto;
    private static IccManager instance;
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出IccManager对象
     * </div>
     * <div class="en">
     * Create a IccManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */
    private IccManager(Context context) {
    	proto = Proto.getInstance(context);
    }

    /**
     * Create an IccManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static IccManager getInstance(Context context) {
        if (instance == null) {
        	instance = new IccManager(context);
        }
        return instance;
    }
            
    /**
     * <div class="zh">复位IC卡片,并返回卡片的复位应答内容。</div>
     * <div class="en">Reset IC card and return ATR of the card.</div>
     * 
     * @param slot
     * <div class="zh">
     * 			通道号及相关参数.
     * 		<ul>
     * 			<li> bit[2:0]: 0~7编号的通道号, 0 为半没式大卡座(用户卡), 1 为全没式大卡座, 2、3、4、5 为 SAM卡小卡座
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_SLOT0}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT1}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT2}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT3}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT4}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT5}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT6}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT7}
     * 				</ul>
     * 			<li> bit[4:3]: 00:5V, 01:1.8V, 10:3V, 11:5V
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_5V}
     * 					<li>{@link #ICC_SLOT_BIT_1_8V}
     * 					<li>{@link #ICC_SLOT_BIT_3V}
     * 				</ul>
     *			<li> bit[5]: 表示对PPS协议支持, 0表示不支持, 1表示支持
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_PPS}
     * 					<li>{@link #ICC_SLOT_BIT_NO_PPS}
     * 				</ul>
     *			<li> bit[6]: 表示上电复位使用速率,  0:默认(9600), 1:38400
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_9600}
     * 					<li>{@link #ICC_SLOT_BIT_38400}
     * 				</ul>
	 *			<li> bit[7]: 表示支持的规范类型, 0:EMV 1: ISO7816-3
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_EMV}
     * 					<li>{@link #ICC_SLOT_BIT_ISO7816_3}
     * 				</ul>
	 *		</ul> 
	 *		<p> 可将以上每组中的一个用bit-or生成本参数  </p>
     * </div>
     * <div class="en">
     *          slot number and related parameters
     *      <ul>
     * 			<li> bit[2:0]: channel, 0~7 channel No. 0 is half-inserted big slot(user card), 1 is full-inserted big slot, 2, 3, 4,5 are SAM card small slot
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_SLOT0}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT1}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT2}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT3}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT4}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT5}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT6}
     * 					<li>{@link #ICC_SLOT_BIT_SLOT7}
     * 				</ul>
     * 			<li> bit[4:3]: operation condition, 00:5V, 01:1.8V, 10:3V, 11:5V
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_5V}
     * 					<li>{@link #ICC_SLOT_BIT_1_8V}
     * 					<li>{@link #ICC_SLOT_BIT_3V}
     * 				</ul>
     *			<li> bit[5]: support for PPS protocol, 0 not support PPS, 1
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_PPS}
     * 					<li>{@link #ICC_SLOT_BIT_NO_PPS}
     * 				</ul>
     *			<li> bit[6]: speed used in ATR, 0: default(9600), 1:38400
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_9600}
     * 					<li>{@link #ICC_SLOT_BIT_38400}
     * 				</ul>
	 *			<li> bit[7]: specification, 0: EMV 1: ISO7816-3
     * 				<ul>
     * 					<li>{@link #ICC_SLOT_BIT_EMV}
     * 					<li>{@link #ICC_SLOT_BIT_ISO7816_3}
     * 				</ul>
	 *		</ul> 
     *          <p>you may do bit-or some of them to generate this argument </p>         
     * </div>
     * @return
     * <div class="zh">
     * 		卡片复位应答。
     * </div>
     * <div class="en">
     *          Answer To Reset information of the card.
     * </div>
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public byte[] iccInit(byte slot) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = slot;
    	
        byte[] resp = new byte[33];
    	proto.sendRecv(Cmd.CmdType.ICC_INIT, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		byte[] atr = new byte[resp[0]];
    		System.arraycopy(resp, 1, atr, 0, resp[0]);
    		return atr;
    	} else {
        	throw new IccException(rc.code); 		
    	}
    }

    /**
     * <div class="zh">对指定卡座中的卡片下电。</div>
     * <div class="en">Close specified contact IC card slot, switches off IC card power.</div>
     * 
     * @param slot
     * <div class="zh">
     * 			参考IccInit中的slot说明
     * </div>
     * <div class="en">
     *          see slot description of {@link #iccInit}
     * </div>
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public void iccClose(byte slot) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = slot;
    	
    	proto.sendRecv(Cmd.CmdType.ICC_CLOSE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new IccException(rc.code);	
    	}
    }

    /**
     * <div class="zh">设置IccIsoCommand函数是否自动发送GET RESPONSE指令。</div>
     * <div class="en">Set up whether IccIsoCommand () sends Get Response instruction
     * automatically or not.</div>
     * 
     * @param slot
     * <div class="zh">
     * 			参考IccInit中的slot说明
     * </div>
     * <div class="en">
     *          see slot description of {@link #iccInit}
     * </div>
     * 
     * @param autoresp
     *  
     * <div class="zh">
     * 		<ul>
     *            <li>1 = 自动发送 ;
     *            <li>0 = 不自动发送;
     *            <li>其他 - 无效.
     *      </ul>
     * </div>
     *  
     * <div class="en">
     * 		<ul>
     *            <li>1 = auto-send ;
     *            <li>0 = do not send;
     *            <li>Others: Invalid.
     *      </ul>
     * </div>
     *     
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public void iccAutoResp(byte slot, byte autoresp) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = slot;
    	req[1] = autoresp;
    	
    	proto.sendRecv(Cmd.CmdType.ICC_AUTO_RESP, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new IccException(rc.code);	
    	}
    }
    
    /**
     * <div class="zh">IC卡操作函数。该函数支持IC卡通用接口协议(T=0及T=1)。</div>
     * <div class="en">IC card operation function. This function supports IC card universal
     * interface protocol (T=0 and T=1)</div>
     * 
     * @param slot
     * <div class="zh">
     * 			参考IccInit中的slot说明
     * </div>
     * <div class="en">
     *          see slot description of {@link #iccInit}
     * </div>
     * 
     * @param ApduSend  
     * <div class="zh">
	 *			[输入] 发送到IC卡的数据, 参考 {@link APDU_SEND}
     * </div>
     *  
     * <div class="en">
     *          [input] Data structure send to IC card. see {@link APDU_SEND}
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			从IC卡接收到的数据, 参考 {@link APDU_RESP}
     * </div>
     *  
     * <div class="en">
     *           Data structure received from IC card. see {@link APDU_RESP}
     * </div>
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public APDU_RESP iccIsoCommand(byte slot, APDU_SEND ApduSend) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
        byte[] apduSend = ApduSend.serialToBuffer();
    	byte[] req = new byte[1 + apduSend.length];
    	req[0] = slot;
    	System.arraycopy(apduSend, 0, req, 1, apduSend.length);
    	APDU_RESP apduResp = new APDU_RESP();
    	byte[] resp = apduResp.serialToBuffer();
    	proto.sendRecv(Cmd.CmdType.ICC_ISOCOMMAND, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		apduResp.serialFromBuffer(resp);
    	} else {
        	throw new IccException(rc.code);	
    	}
    	return apduResp;
    }

    
    /**
     * <div class="zh">检查指定的卡座内是否有卡,对于0-1卡座使用到位检测。对于2-5卡座做上电复位检测. 
     * <b><font color="red">对于 带电动读卡器的D800, 请使用  {@link #iccDetectExt(byte)} </font></b>
     * </div>
     * <div class="en">Check whether card is inserted in specified slot. In-place check will take place for No.0-1 slots. 
     * Power-on and resetting will take place for No. 2-5 slots.
     * <b><font color="red">For D800 with motor-driven card reader, please use {@link #iccDetectExt(byte)} instead.</font></b>
     * </div>
     * 
     * @param slot
     * <div class="zh">
     * 			参考IccInit中的slot说明
     * </div>
     * <div class="en">
     *          see slot description of {@link #iccInit}
     * </div>
     * 
     * @return 
     * <div class="zh">
     * 			true : 检测到卡片 <br/>
     * 			false : 未检测到卡片<br/>
     * </div>
     *  
     * <div class="en">
     * 			true : icc detected<br/>
     * 			false : no icc detected<br/>
     * </div>
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public boolean iccDetect(byte slot) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = slot;
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.ICC_DETECT, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		if (resp[0] == (byte)0) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	} else {
        	throw new IccException(rc.code);	
    	}
    }

    /**
     * <div class="zh">检查指定的卡座内是否有卡,对于0-1卡座使用到位检测。对于2-5卡座做上电复位检测。
     * <b><font color="red">返回值0x01, 0x02, 0x03, 0x04, 0x05只适用于 带电动读卡器的D800</font></b>
     * </div>
     * <div class="en">Check whether card is inserted in specified slot. In-place check will take place for No.0-1 slots. Power-on and resetting will take place for No. 2-5 slots.
     * <b><font color="red">return codes 0x01, 0x02, 0x03, 0x04, 0x05 are only for D800 with motor-driven</font></b>
     * </div>
     * 
     * @param slot
     * <div class="zh">
     * 			参考IccInit中的slot说明
     * </div>
     * <div class="en">
     *          see slot description of {@link #iccInit}
     * </div>
     * 
     * @return 
     * <div class="zh">
     * 			0x0 : 检测到IC卡 <br/>
     * 			0x1 : 卡槽口有卡片未取走<br/>
     * 			0x2 : 无法识别该卡(并弹卡)<br/>
     * 			0x3 : 有磁卡<br/>
     * 			0x4 : 有双界面卡(磁卡和IC卡)<br/>
     * 			0x5 : 无此设备号<br/>
     * 		   0xff : 无卡<br/>
     * </div>
     *  
     * <div class="en">
     * 			0x0 : IC Card detected<br/>
     * 			0x1 : There's card in the entry<br/>
     * 			0x2 : Card is not recognizable(card will be pushed out)<br/>
     * 			0x3 : Magnetic Stripe Card detected<br/>
     * 			0x4 : Dual-interface Card(IC Card and Magetic Stripe Card) detected<br/>
     * 			0x5 : No such device<br/>
     * 		   0xff : No Card detected<br/>
     * </div>
     * @throws IccException
     * <div class="zh">ICC错误</div>
     * <div class="en">icc error</div>
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
    public byte iccDetectExt(byte slot) throws IccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = slot;
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.ICC_DETECT, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		return resp[0];
    	} else {
        	throw new IccException(rc.code);	
    	}
    }

}
