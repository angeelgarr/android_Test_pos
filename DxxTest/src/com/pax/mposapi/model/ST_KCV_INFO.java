package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��������KCV��Ϣ������,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">describes the KCV information, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class ST_KCV_INFO {
	/**
	 * <div class="zh">
	 * KCV ��֤ģʽ <br/>
	 * ע�⵱ʹ��{@link com.pax.mposapi.PedManager#pedGetKcv} �ӿ�ʱ,ʹ�õ�mode 0 ʵ���� �� {@link com.pax.mposapi.PedManager#pedWriteKey} �ӿ���ʹ�õ� mode 1 ������ͬ
	 * </div>
	 * <div class="en">
	 * KCV check mode <br/>
	 * Note that in  {@link com.pax.mposapi.PedManager#pedGetKcv}, mode 0's functionality is the same as mode 1's
	 * functionality in {@link com.pax.mposapi.PedManager#pedWriteKey}.
	 * </div>
	 *
	 */
    public byte iCheckMode;
    
	/**
	 * <div class="zh">
	 *  KCV �������<br/>
	 *  �� {@link com.pax.mposapi.PedManager#pedGetKcv}, {@link com.pax.mposapi.PedManager#pedWriteKey}, 
	 *  {@link com.pax.mposapi.PedManager#pedWriteTIK} ��غ���˵��<br/>
	 *  �ܳ������Ϊ128�ֽ�.
	 * </div>
	 * <div class="en">
	 *  KCV related data <br/>
	 * see details in {@link com.pax.mposapi.PedManager#pedGetKcv}, {@link com.pax.mposapi.PedManager#pedWriteKey}, 
	 *  {@link com.pax.mposapi.PedManager#pedWriteTIK}.<br/>
	 *  maximum total length is 128 bytes.	 
	 * </div>
	 *
	 */
    public final byte[] aucCheckBuf; // [128];

    /**
     * <div class="zh">
     *  �ڲ�ʹ��, Ӧ�ó���������Ч, 
     *  �ڵ��� {@link com.pax.mposapi.PedManager#pedGetKcv}ʱ, �������ó�true, ����������ó�false
     * </div>
     * <div class="en">
     *  only for internal use, APP should ignore this flag.
     *  when calling {@link com.pax.mposapi.PedManager#pedGetKcv}, MUST set to true, set to false in other cases.
     * </div>
     */
    public boolean isForGetKcv = false;

    /**
     * <div class="zh"> ����һ��ST_KCV_INFO����</div>
     * <div class="en"> create an ST_KCV_INFO instance </div>
     */
    public ST_KCV_INFO() {
        aucCheckBuf = new byte[128];
    }

    /**
     * <div class="zh">
     * ����object�е�����д��byte����
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	�õ��İ�����object���ݵ�byte����.
     * </div>
     * <div class="en">
     * 	a byte array including data of this object.
     * </div>
     */  
    public byte[] serialToBuffer() {
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.clear();
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ss.put(this.iCheckMode);
        if (iCheckMode == 0 || iCheckMode == 1 || iCheckMode == 2) {
        	if (iCheckMode > 0 || isForGetKcv) {
            	ss.put((byte)(aucCheckBuf[0] + 1));
        		ss.put(aucCheckBuf, 0, aucCheckBuf[0] + 1);
        	}
        } else {	//mode 3
        	int kcvDataLen = aucCheckBuf[0];
        	int kcvLen = aucCheckBuf[1 + kcvDataLen + 1];
        	int checkBufLen = 3 + kcvDataLen + kcvLen;
        	ss.put((byte)checkBufLen);
        	ss.put(aucCheckBuf, 0, checkBufLen);
        }

        ss.flip();
        byte[] ret = new byte[ss.limit()];
        ss.get(ret);
        return ret;
    }
    /**
     * <div class="zh">
     * ��һ��byte�����ж�ȡ���ݲ���¼�ڱ�object��
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   �Ӵ�byte������ȡ���ݵ���object��
     * </div>
     * <div class="en">
     *   a byte array from which data should be read
     * </div>
     * 
     */
    public void serialFromBuffer(byte[] bb) {
        ByteBuffer ss = ByteBuffer.wrap(bb);
        ss.order(ByteOrder.LITTLE_ENDIAN);
        if (isForGetKcv) {
        	ss.get(aucCheckBuf, 0, bb.length);
        } else {
        	iCheckMode = ss.get();
        	ss.get(aucCheckBuf);
        }
    }
}
