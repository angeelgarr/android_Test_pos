package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">�������� modem ���Ų��� ,�ṩ�ӿڽ����ݴ��л�������,��������ж�������<br/>
 * ���첽ͨѶ��,SSETUP�ֶκ���������Ϣ,AsMode�ֶ�Ҳ���ܺ���������Ϣ;�����ߵĸ�4λ��0ʱ,��ֻ�к��ߵ��趨��Ч;<br/>
 * ��ͬ��ͨѶ��,ֻ��ǰ�ߵ��趨��Ч(���趨Ϊ14400BPS,�����������Զ�����Ϊ1200BPS);<br/>
 * ���첽ͨѶ��,���������ʳ�����ǰMODEM��֧�ֵ��������,���������Զ��޶���������֧�ֵ��������
 * </div>
 * <div class="en">describes the modem dial parameters, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array<br/>
 * In the asynchronous communication, SSETUP field contains rate information, AsMode field may also contain rate information;<br/>
 * If the higher 4 bits of AsMode are non-zero, it��s setting is effective. <br/>
 * In the synchronous communication, Only the former set are effective (If setting to 14400BPS, then the driver will automatically adjust to 1200BPS); <br/>
 * In the asynchronous communication, If the set rate is over the highest rate which can be supported by MODEM, the driver will automatically set the rate to a highest rate which can be supported.
 * </div>
 */
public class COMM_PARA {
    /**
     * <div class="zh">
     * ������Ƶ/���岦�� <br/>
     * 0x00 ˫����Ƶ���� <br/>
     * 0x01 ���岦�ŷ�ʽ1(��������10/s;������1.6:1;�ż���>=500ms) <br/>
     * 0x02 ���岦�ŷ�ʽ2(��������10/s;������2:1;�ż���>=600ms) <br/>
     * ����ֵ  ���� <br/>
     * </div>
     * <div class="en">
     * Set tone/pulse dialing<br/>
     * 0x00 DTMF(Dual Tone Multi Frequency) dialing<br/>
     * 0x01 Pulse dialing 1(Pulse rate 10/s;Intermittent proportion 1.6:1;Signal interval>=500ms) <br/>
     * 0x02 Pulse dialing 2(Pulse rate 10/s; Intermittent proportion 2:1; Signal interval >=600ms) <br/>
     * Others Reserved
     * </div>
     */
	public byte DP;
    /**
     * <div class="zh">
     * �Ƿ��Ⲧ����<br/>
     * D0 = 0 ��Ⲧ����<br/>
     * D0 = 1 ����Ⲧ����<br/>
     * D1 = 0 ���в���<br/>
     * D1 = 1 ����/����Ӧ��<br/>
     * D2 = 0 ��Ⲣ�ߵ绰ռ��(���в��š��Ƿ���ת�˹�������ʽʱ)<br/>
     * D2 = 1 ����Ⲣ�ߵ绰ռ��(���в��š��Ƿ���ת�˹�������ʽʱ)<br/>
     * D5 = 0 ����Ӧ��ʱ����AT״̬����ӿ�<br/>
     * D5 = 1 ����Ӧ��ʱ����PAXԶ������״̬����ӿ�<br/>
     * D6 = 0 ά��SSETUP֮����λ��ʾ�ĳ�ʱʱ��<br/>
     * D6 = 1 ��SSETUP֮����λ��ʾ�ĳ�ʱʱ���ӱ�<br/>
     * D7 = 0 �첽2400bps���Ӳ��ñ�׼ITU V.22bis���ӣ��첽1200���÷Ǳ�׼�Ŀ�������<br/>
     * D7 = 1 �첽2400bps���Ӳ��÷Ǳ�׼��FastConnect�������ӷ�ʽ(ȫ����)���첽1200����ITU V.22bis��׼���ӣ���S90��1.35�汾��ʼ֧�֣�<br/>
     * ����λ ����
     * </div>
     * <div class="en">
     * Check dialing tone<br/>
     * D0 = 0 Dial tone detection<br/> 
     * D0 = 1 Does not detect dial tone<br/> 
     * D1 = 0 Caller dialing <br/>
     * D1 = 1 Called/answer the call<br/>
     * D2 = 0 Detect the paralleled telephone occupation (Caller dialing��No assign number switch to artificial answer mode)<br/> 
     * D2 = 1 Not detect the paralleled telephone occupation (Caller dialing��No assign number switch to artificial answer mode) <br/>
     * D5 = 0 Report interfaces with AT status during called application. <br/>
     * D5 = 1 Report interfaces with PAX remote download status during called application.<br/> 
     * D6 = 0 Keep the timeout duration which expressed by the lower three bit of SSETUP. <br/>
     * D6 = 1 Double the timeout duration which expressed by the lower three bit of SSETUP. <br/>
     * D7 = 0 Asynchronous 2400bps connection using standard ITU V.22bis connection; asynchronous 1200bps connection using non-standard FastConnect method.<br/> 
     * D7 = 1 Asynchronous 2400bps connection using non-standard FastConnect method(All the models); Asynchronous 1200bps connection using standard ITU V.22bis connection(it's available only with S90 version 1.35 and later.)<br/> 
     * Others Reserved.
     * </div>
     */	
	public byte CHDT;
    /**
     * <div class="zh">
     * ժ����Ⱥ򲦺������ʱ��(��λ:100ms,��Сֵ��ȱʡֵΪ20,��Ч��Χ20~255);�ڴ��ڼ�,ֻҪ��⵽������,���˳��Ⱥ�
     * </div>
     * <div class="en">
     * The longest time to wait for dial tone when off hook (Unit��100ms,Minimum and default value is 20,valid range 20~255). Exit waiting when the dial tone has been detected during this time.
     * </div>
     */		
	public byte DT1;
    /**
     * <div class="zh">
     * ������ʱ","�ȴ�ʱ��(��λ:100ms);��ֵҪ����ʵ��Ӧ�û���������趨,��Ӧ�ó��������Ҫ�����ֹ����õĽӿ�[��Χ0~255]
     * </div>
     * <div class="en">
     * ","wait time when dial outside line(Unit��100ms). This value will be set up according to the actural application environment, It is better to keep interface of manually setting in the application. (range 0~255)
     * </div>
     */
	public byte DT2;
    /**
     * <div class="zh">
     * ˫�����ŵ�һ���뱣��ʱ��(��λ:1ms,��Ч��Χ50~255)
     * </div>
     * <div class="en">
     * The keep time of two-tone dialing a single number (Unit��1ms,valid range 50~255)
     * </div>
     */
	public byte HT;
    /**
     * <div class="zh">
     * ˫��������������֮��ļ��ʱ��(��λ:10ms,��Ч��Χ5~25)
     * </div>
     * <div class="en">
     * The interval time between two-tone dial-up two numbers (Unit��10ms, valid range 5~25)
     * </div>
     */
	public byte WT;
    /**
     * <div class="zh">
     * ͨѶ�ֽڵ�����<br/>
     * D7���첽ͬ��ѡ�� 0��ͬ��1���첽<br/>
     * D6D5�����ò�����00��1200 01��2400 10��9600 11��14400 <br/>
     * D4D3����·��ʽ(Ӧ������ⷽʽ) 10��BELL(��������1200BPS) 11��CCITT 0X��CCITT <br/>
     * D2D1D0���ȴ�Ӧ�����ĳ�ʱʱ��(0~7) 000Ϊ5��001Ϊ8��010Ϊ11��011 Ϊ14��100 Ϊ17��101 Ϊ20��110 Ϊ23��111 Ϊ26��<br/>
     * </div>
     * <div class="en">
     * Communication bytes setting (including set up synchronization and asynchronous, baud rate, line, answer tone timeout etc.)<br/>
     * D7��asynchronous synchronization options 0��synchronous 1��asynchronous<br/> 
     * D6D5��set the baudrate 00��1200 01��2400 10��9600 11��14400<br/>
     * D4D3��line format(detection mode of answer tone) 10��BELL(only applicable to 1200BPS) 11��CCITT 0X��CCITT<br/> 
     * D2D1D0��Waiting for the timeout of response tone (0~7) 000 is 5s 001 is 8s 010 is 11s 011 is 14s 100 is17s 101 is 20s 110 is 23s 111 is 26s<br/>
     * </div>
     */
	public byte SSETUP;
    /**
     * <div class="zh">
     * ѭ�������ܴ���(��Ϊ0��ת����1),���겦�Ŵ������к���Ϊһ��ѭ��[��Ч��Χ1~255]
     * </div>
     * <div class="en">
     * Redial times,must >=1<br/>
     * The total number of dial-up cycle (convert 0 to 1 if it is 0),Dialing all numbers in dial number string is one cycle (valid range 1~255)
     * </div>
     */
	public byte DTIMES;
    /**
     * <div class="zh">
     * �涨��ʱ����û��Ӧ�ò����ݽ���,MODEM��Ҷ�;��10��Ϊ��λ,Ϊ0ʱ�޳�ʱ;֧��FSK���ܵĻ������ֵΪ900�룻��FSK���ܻ������ֵΪ650��
     * </div>
     * <div class="en">
     * Communication timeout (If there is no data exchange during this time, MODEM will hang up, No timeout when it is 0. Unit��10s)<br/>
     * There is no application-layer data exchange in specified time, MODEM then hang up.Unit 10s, no timeout if it is 0. The maximum timeout of the model which support FSK function is 900s,and the model which has no FSK function is 650s
     * </div>
     */
	public byte TimeOut;
    /**
     * <div class="zh">
     * �첽ͨѶ������(��4λ)���ַ���ʽ(����λ) <br/>
     * D3D2D1D0�� 0 -"8,n,1" 1 -"8,e,1" 2 -"8,o,1" 4 -"7,e,1" 5 -"7,o,1" 8 - FSK ʹ��B202ͨ��Э��9 - FSK ʹ��V23Cͨ��Э�� <br/>
     * D7D6D5D4�� 0 -��SSETUP��D6D5λ����1 -1200 bps 2 -2400 bps 3 -4800 bps 4 -7200 bps 5 -9600 bps 6 -12000 bps 7 -14400 bps 8 -19200 bps 9 -24000 bps 10 -26400 bps 11 -28800 bps 12 -31200 bps 13 -33600 bps 14 -48000 bps 15 -56000 bps <br/>
     * </div>
     * <div class="en">
     * Asynchronous communication (Only valid when asynchronous communication)<br/>
     * The rate of asynchronous communication (higher 4 bit)��Character format(lower 4 bit) <br/>
     * D3D2D1D0�� 0 -"8,n,1" 1 -"8,e,1" 2 -"8,o,1" 4 -"7,e,1" 5 -"7,o,1" 8 - FSK use the B202 communication protocol 9 - FSK use the V23Ccommunication protocol<br/>
     * D7D6D5D4��0 -determined by D6D5 bit of SSETUP 1 -1200 bps 2 -2400 bps 3 -4800 bps 4 -7200 bps 5 -9600 bps 6 -12000 bps 7 -14400 bps 8 -19200 bps 9 -24000 bps 10 -26400 bps 11 -28800 bps 12 -31200 bps 13 -33600 bps 14 -48000 bps 15 -56000 bps
     * </div>
     */
	public byte AsMode;
	
    public COMM_PARA() {
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
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.clear();
        
        ss.put(DP);
        ss.put(CHDT);
        ss.put(DT1);
        ss.put(DT2);
        ss.put(HT);
        ss.put(WT);
        ss.put(SSETUP);
        ss.put(DTIMES);
        ss.put(TimeOut);
        ss.put(AsMode);

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
        ss.order(ByteOrder.BIG_ENDIAN);
        DP = ss.get();
        CHDT = ss.get();
        DT1 = ss.get();
        DT2 = ss.get();
        HT = ss.get();
        WT = ss.get();
        SSETUP = ss.get();
        DTIMES = ss.get();
        TimeOut = ss.get();
        AsMode = ss.get();
    }
}
