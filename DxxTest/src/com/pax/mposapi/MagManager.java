package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;

/**
 * <div class="zh">
 * MagManager 用于读取磁条卡信息
 * </div>
 * <div class="en">
 * MagManager is used to read magnetic stripe card 
 * </div>
 *
 */
public class MagManager {
    private static final String TAG = "MagManager";
    private final Proto proto;
    private static MagManager instance;
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出MagManager对象
     * </div>
     * <div class="en">
     * Create a MagManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */
    private MagManager(Context context) {
    	proto = Proto.getInstance(context);
    }

    /**
     * Create a MagManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static MagManager getInstance(Context context) {
        if (instance == null) {
        	instance = new MagManager(context);
        }
        return instance;
    }
    
    /**
     * <div class="zh">
     * 复位磁头,并清除磁卡缓冲区数据。
     * </div>
     * <div class="en">
     * Reset magnetic stripe card reader, and clears buffer of magnetic stripe
     * card.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">磁条卡阅读器错误</div>
     * <div class="en">magnetic card reader error</div>
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
    public void magReset() throws MagException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.MSR_RESET, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new MagException(rc.code);		
    	}
    }

    /**
     * <div class="zh">
     * 打开磁卡阅读器。
     * </div>
     * <div class="en">
     * Switch on magnetic stripe card reader.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">磁条卡阅读器错误</div>
     * <div class="en">magnetic card reader error</div>
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
    public void magOpen() throws MagException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.MSR_OPEN, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new MagException(rc.code);		
    	}
    }

    /**
     * <div class="zh">
     * 关闭磁卡阅读器。
     * </div>
     * <div class="en">
     * Switch off magnetic stripe card reader.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">磁条卡阅读器错误</div>
     * <div class="en">magnetic card reader error</div>
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
    public void magClose() throws MagException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.MSR_CLOSE, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
    		throw new MagException(rc.code);		
    	}
    }

    /**
     * <div class="zh">
     * 检查是否刷过卡
     * </div>
     * <div class="en">
     * Check whether a card is swiped.
     * </div>
     * 
     * @return
     * <div class="zh">true : 有刷卡<br/> false : 无刷卡</div>
     * <div class="en">true : Card swiped<br/> false : No swiping</div>
     * 
     * @throws MagException
     * <div class="zh">磁条卡阅读器错误</div>
     * <div class="en">magnetic card reader error</div>
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
    public boolean magSwiped() throws MagException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.MSR_IS_SWIPED, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		if (resp[0] == 00) {
    			return true;
    		} else {
    			return false;
    		}
    	} else {
    		throw new MagException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 读取磁卡缓冲区的1、2、3磁道的数据。<br/>
     * 磁道数据编码为 "iso-8859-1"
     * </div>
     * <div class="en">
     * Read data of track 1, 2, 3 from magnetic stripe card buffer.<br/>
     * the charset name of the track data is "iso-8859-1"
     * </div>
     * 
     * @return
     * <div class="zh">
     * String[0]:　1磁道数据, 如果没有数据则为空串<br/>
     * String[1]:　2磁道数据 , 如果没有数据则为空串<br/>
     * String[2]:　3磁道数据 , 如果没有数据则为空串<br/>
     * </div>
     * 
     * <div class="en">
     * String[0]: track 1 data, empty string if no data<br/>
     * String[1]: track 2 data, empty string if no data<br/>
     * String[2]: track 3 data, empty string if no data<br/>
     * </div>
     * @throws MagException
     * <div class="zh">磁条卡阅读器错误</div>
     * <div class="en">magnetic card reader error</div>
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
    public String[] magRead() throws MagException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();    	
    	String[] tracks = new String[3];
    	byte[] resp = new byte[3 + (79 + 8) + (40 + 8) + (107 + 8)];	// +8 is to prepare for the p2pe 
    	proto.sendRecv(Cmd.CmdType.MSR_READ, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		int len1 = resp[0];
    		int len2 = resp[1 + len1];
    		int len3 = resp[1 + len1 + 1 + len2];
    		tracks[0] = new String(resp, 1, len1, "iso-8859-1");
    		tracks[1] = new String(resp, 1 + len1 + 1, len2, "iso-8859-1");
    		tracks[2] = new String(resp, 1 + len1 + 1 + len2 + 1, len3, "iso-8859-1");
    	} else {
    		throw new MagException(rc.code);		
    	}
    	
    	return tracks;
    }
}
