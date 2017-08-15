package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述 modem 拨号参数 ,提供接口将数据串行化到数组,或从数组中读出数据<br/>
 * 在异步通讯中,SSETUP字段含有速率信息,AsMode字段也可能含有速率信息;当后者的高4位非0时,则只有后者的设定有效;<br/>
 * 在同步通讯中,只有前者的设定有效(若设定为14400BPS,则驱动程序自动调整为1200BPS);<br/>
 * 在异步通讯中,若所设速率超过当前MODEM所支持的最高速率,驱动程序将自动限定到其所能支持的最高速率
 * </div>
 * <div class="en">describes the modem dial parameters, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array<br/>
 * In the asynchronous communication, SSETUP field contains rate information, AsMode field may also contain rate information;<br/>
 * If the higher 4 bits of AsMode are non-zero, it‘s setting is effective. <br/>
 * In the synchronous communication, Only the former set are effective (If setting to 14400BPS, then the driver will automatically adjust to 1200BPS); <br/>
 * In the asynchronous communication, If the set rate is over the highest rate which can be supported by MODEM, the driver will automatically set the rate to a highest rate which can be supported.
 * </div>
 */
public class COMM_PARA {
    /**
     * <div class="zh">
     * 设置音频/脉冲拨号 <br/>
     * 0x00 双音多频拨号 <br/>
     * 0x01 脉冲拨号方式1(脉冲速率10/s;断续比1.6:1;号间间隔>=500ms) <br/>
     * 0x02 脉冲拨号方式2(脉冲速率10/s;断续比2:1;号间间隔>=600ms) <br/>
     * 其它值  保留 <br/>
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
     * 是否检测拨号音<br/>
     * D0 = 0 检测拨号音<br/>
     * D0 = 1 不检测拨号音<br/>
     * D1 = 0 主叫拨号<br/>
     * D1 = 1 被叫/来铃应答<br/>
     * D2 = 0 检测并线电话占用(主叫拨号、非发号转人工接听方式时)<br/>
     * D2 = 1 不检测并线电话占用(主叫拨号、非发号转人工接听方式时)<br/>
     * D5 = 0 被叫应用时采用AT状态报告接口<br/>
     * D5 = 1 被叫应用时采用PAX远程下载状态报告接口<br/>
     * D6 = 0 维持SSETUP之低三位表示的超时时长<br/>
     * D6 = 1 将SSETUP之低三位表示的超时时长加倍<br/>
     * D7 = 0 异步2400bps连接采用标准ITU V.22bis连接；异步1200采用非标准的快速连接<br/>
     * D7 = 1 异步2400bps连接采用非标准的FastConnect快速连接方式(全机型)；异步1200采用ITU V.22bis标准连接（仅S90，1.35版本开始支持）<br/>
     * 其它位 保留
     * </div>
     * <div class="en">
     * Check dialing tone<br/>
     * D0 = 0 Dial tone detection<br/> 
     * D0 = 1 Does not detect dial tone<br/> 
     * D1 = 0 Caller dialing <br/>
     * D1 = 1 Called/answer the call<br/>
     * D2 = 0 Detect the paralleled telephone occupation (Caller dialing、No assign number switch to artificial answer mode)<br/> 
     * D2 = 1 Not detect the paralleled telephone occupation (Caller dialing、No assign number switch to artificial answer mode) <br/>
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
     * 摘机后等候拨号音的最长时间(单位:100ms,最小值和缺省值为20,有效范围20~255);在此期间,只要检测到拨号音,就退出等候
     * </div>
     * <div class="en">
     * The longest time to wait for dial tone when off hook (Unit：100ms,Minimum and default value is 20,valid range 20~255). Exit waiting when the dial tone has been detected during this time.
     * </div>
     */		
	public byte DT1;
    /**
     * <div class="zh">
     * 拨外线时","等待时间(单位:100ms);此值要根据实际应用环境的情况设定,在应用程序中最好要留出手工设置的接口[范围0~255]
     * </div>
     * <div class="en">
     * ","wait time when dial outside line(Unit：100ms). This value will be set up according to the actural application environment, It is better to keep interface of manually setting in the application. (range 0~255)
     * </div>
     */
	public byte DT2;
    /**
     * <div class="zh">
     * 双音拨号单一号码保持时间(单位:1ms,有效范围50~255)
     * </div>
     * <div class="en">
     * The keep time of two-tone dialing a single number (Unit：1ms,valid range 50~255)
     * </div>
     */
	public byte HT;
    /**
     * <div class="zh">
     * 双音拨号两个号码之间的间隔时间(单位:10ms,有效范围5~25)
     * </div>
     * <div class="en">
     * The interval time between two-tone dial-up two numbers (Unit：10ms, valid range 5~25)
     * </div>
     */
	public byte WT;
    /**
     * <div class="zh">
     * 通讯字节的设置<br/>
     * D7：异步同步选择 0：同步1：异步<br/>
     * D6D5：设置波特率00：1200 01：2400 10：9600 11：14400 <br/>
     * D4D3：线路制式(应答音检测方式) 10：BELL(仅适用于1200BPS) 11：CCITT 0X：CCITT <br/>
     * D2D1D0：等待应答音的超时时间(0~7) 000为5秒001为8秒010为11秒011 为14秒100 为17秒101 为20秒110 为23秒111 为26秒<br/>
     * </div>
     * <div class="en">
     * Communication bytes setting (including set up synchronization and asynchronous, baud rate, line, answer tone timeout etc.)<br/>
     * D7：asynchronous synchronization options 0：synchronous 1：asynchronous<br/> 
     * D6D5：set the baudrate 00：1200 01：2400 10：9600 11：14400<br/>
     * D4D3：line format(detection mode of answer tone) 10：BELL(only applicable to 1200BPS) 11：CCITT 0X：CCITT<br/> 
     * D2D1D0：Waiting for the timeout of response tone (0~7) 000 is 5s 001 is 8s 010 is 11s 011 is 14s 100 is17s 101 is 20s 110 is 23s 111 is 26s<br/>
     * </div>
     */
	public byte SSETUP;
    /**
     * <div class="zh">
     * 循环拨号总次数(若为0则被转换成1),拨完拨号串的所有号码为一次循环[有效范围1~255]
     * </div>
     * <div class="en">
     * Redial times,must >=1<br/>
     * The total number of dial-up cycle (convert 0 to 1 if it is 0),Dialing all numbers in dial number string is one cycle (valid range 1~255)
     * </div>
     */
	public byte DTIMES;
    /**
     * <div class="zh">
     * 规定的时间内没有应用层数据交换,MODEM则挂断;以10秒为单位,为0时无超时;支持FSK功能的机型最大值为900秒；无FSK功能机型最大值为650秒
     * </div>
     * <div class="en">
     * Communication timeout (If there is no data exchange during this time, MODEM will hang up, No timeout when it is 0. Unit：10s)<br/>
     * There is no application-layer data exchange in specified time, MODEM then hang up.Unit 10s, no timeout if it is 0. The maximum timeout of the model which support FSK function is 900s,and the model which has no FSK function is 650s
     * </div>
     */
	public byte TimeOut;
    /**
     * <div class="zh">
     * 异步通讯的速率(高4位)、字符格式(低四位) <br/>
     * D3D2D1D0： 0 -"8,n,1" 1 -"8,e,1" 2 -"8,o,1" 4 -"7,e,1" 5 -"7,o,1" 8 - FSK 使用B202通信协议9 - FSK 使用V23C通信协议 <br/>
     * D7D6D5D4： 0 -由SSETUP的D6D5位决定1 -1200 bps 2 -2400 bps 3 -4800 bps 4 -7200 bps 5 -9600 bps 6 -12000 bps 7 -14400 bps 8 -19200 bps 9 -24000 bps 10 -26400 bps 11 -28800 bps 12 -31200 bps 13 -33600 bps 14 -48000 bps 15 -56000 bps <br/>
     * </div>
     * <div class="en">
     * Asynchronous communication (Only valid when asynchronous communication)<br/>
     * The rate of asynchronous communication (higher 4 bit)、Character format(lower 4 bit) <br/>
     * D3D2D1D0： 0 -"8,n,1" 1 -"8,e,1" 2 -"8,o,1" 4 -"7,e,1" 5 -"7,o,1" 8 - FSK use the B202 communication protocol 9 - FSK use the V23Ccommunication protocol<br/>
     * D7D6D5D4：0 -determined by D6D5 bit of SSETUP 1 -1200 bps 2 -2400 bps 3 -4800 bps 4 -7200 bps 5 -9600 bps 6 -12000 bps 7 -14400 bps 8 -19200 bps 9 -24000 bps 10 -26400 bps 11 -28800 bps 12 -31200 bps 13 -33600 bps 14 -48000 bps 15 -56000 bps
     * </div>
     */
	public byte AsMode;
	
    public COMM_PARA() {
    }

    /**
     * <div class="zh">
     * 将本object中的数据写入byte数组
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	得到的包含本object数据的byte数组.
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
     * 从一个byte数组中读取数据并记录在本object中
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   从此byte数组中取数据到本object中
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
