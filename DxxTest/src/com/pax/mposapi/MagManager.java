package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;

/**
 * <div class="zh">
 * MagManager ���ڶ�ȡ��������Ϣ
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
     * ʹ��ָ����Context�����MagManager����
     * </div>
     * <div class="en">
     * Create a MagManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
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
     * ��λ��ͷ,������ſ����������ݡ�
     * </div>
     * <div class="en">
     * Reset magnetic stripe card reader, and clears buffer of magnetic stripe
     * card.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">�������Ķ�������</div>
     * <div class="en">magnetic card reader error</div>
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
     * �򿪴ſ��Ķ�����
     * </div>
     * <div class="en">
     * Switch on magnetic stripe card reader.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">�������Ķ�������</div>
     * <div class="en">magnetic card reader error</div>
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
     * �رմſ��Ķ�����
     * </div>
     * <div class="en">
     * Switch off magnetic stripe card reader.
     * </div>
     * 
     * @throws MagException
     * <div class="zh">�������Ķ�������</div>
     * <div class="en">magnetic card reader error</div>
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
     * ����Ƿ�ˢ����
     * </div>
     * <div class="en">
     * Check whether a card is swiped.
     * </div>
     * 
     * @return
     * <div class="zh">true : ��ˢ��<br/> false : ��ˢ��</div>
     * <div class="en">true : Card swiped<br/> false : No swiping</div>
     * 
     * @throws MagException
     * <div class="zh">�������Ķ�������</div>
     * <div class="en">magnetic card reader error</div>
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
     * ��ȡ�ſ���������1��2��3�ŵ������ݡ�<br/>
     * �ŵ����ݱ���Ϊ "iso-8859-1"
     * </div>
     * <div class="en">
     * Read data of track 1, 2, 3 from magnetic stripe card buffer.<br/>
     * the charset name of the track data is "iso-8859-1"
     * </div>
     * 
     * @return
     * <div class="zh">
     * String[0]:��1�ŵ�����, ���û��������Ϊ�մ�<br/>
     * String[1]:��2�ŵ����� , ���û��������Ϊ�մ�<br/>
     * String[2]:��3�ŵ����� , ���û��������Ϊ�մ�<br/>
     * </div>
     * 
     * <div class="en">
     * String[0]: track 1 data, empty string if no data<br/>
     * String[1]: track 2 data, empty string if no data<br/>
     * String[2]: track 3 data, empty string if no data<br/>
     * </div>
     * @throws MagException
     * <div class="zh">�������Ķ�������</div>
     * <div class="en">magnetic card reader error</div>
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
