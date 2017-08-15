package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;

/**
 * <div class="zh">
 * <b><font color=red>注意: D180,D200,D800 不支持此功能</font></b><br/>
 * PrinterManager 用于操作打印机, 只对有打印机配置的机型有效<br/>
 * </div>
 * <div class="en">
 * <b><font color=red>NOTE: this module is not applicable for D180,D200,D800</font></b><br/>
 * PrinterManager is used to controll the printer, only usable for terminal with printer.<br/>
 * </div>
 *
 */
public class PrinterManager {
    public static final byte PRN_STATUS_OK 			= 0x00;
    public static final byte PRN_STATUS_BUSY 		= 0x01;
    public static final byte PRN_STATUS_NO_PAPER 	= 0x02;
    public static final byte PRN_STATUS_DATA_FORMAT_ERR = 0x03;
    public static final byte PRN_STATUS_FAULT	 	= 0x04;
    public static final byte PRN_STATUS_OVERHEATED 	= 0x08;
    public static final byte PRN_STATUS_UNFINISHED 	= (byte)0xf0;
    public static final byte PRN_STATUS_NO_SUCH_FONT= (byte)0xfc;
    public static final byte PRN_STATUS_DATA_TOO_LONG=(byte)0xfe;

    /*
    public static final byte PRN_IMAGE_TYPE_MONO_BMP 	= 0;
    public static final byte PRN_IMAGE_TYPE_24BIT_BMP 	= 1;
    public static final byte PRN_IMAGE_TYPE_PNG 		= 2;
    public static final byte PRN_IMAGE_TYPE_JPG 		= 3;
    */
    
	public static final int PRN_DOTS_PER_LINE = 384; 
	
    private static final String TAG = "PrinterManager";
    private final Proto proto;
    private static PrinterManager instance;
    
    /**
     * <div class="zh">
     * 打印字符串的编码, 默认为 "gb2312"
     * </div>
     * <div class="en">
     * encoding of the string to print, default to "gb2312"
     * </div>
     */
    public String encoding = "gb2312";

    /**
     * <div class="zh">
     * 使用指定的Context构造出PrinterManager对象
     * </div>
     * <div class="en">
     * Create a PrinterManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */
    private PrinterManager(Context context) {
    	proto = Proto.getInstance(context);
    }

    /**
     * Create a PrinterManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static PrinterManager getInstance(Context context) {
        if (instance == null) {
        	instance = new PrinterManager(context);
        }
        return instance;
    }
                
    /**
     * <div class="zh">
     * 设置打印字间距、行间距。
     * </div>
     * <div class="en">
     * Set up printing character/ line spaces.
     * </div>
     * 
     * @param xSpace 
     * <div class="zh">字间距</div>
     * <div class="en">character space</div>
     * 
     * @param ySpace 
     * <div class="zh">行间距</div>
     * <div class="en">line space</div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnSetSpaces(byte xSpace, byte ySpace) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = xSpace;
    	req[1] = ySpace;
    	
    	proto.sendRecv(Cmd.CmdType.PRN_SET_SPACES, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * 设置字符打印左边界。
     * </div>
     * <div class="en">
     * Set character printing left boundary.
     * </div>
     * 
     * @param indent 
     * <div class="zh">左边界空白像素点,范围：0~300。</div>
     * <div class="en">dots in left boundary, range: 0~300</div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnSetIndent(int indent) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = (byte)(indent / 256);
    	req[1] = (byte)(indent % 256);
    	
    	proto.sendRecv(Cmd.CmdType.PRN_SET_INDENT, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 设置打印黑度等级。
     * </div>
     * <div class="en">
     * Set printing gray level
     * </div>
     * 
     * @param grayLevel 
     * <div class="zh">
     * 		<ul>
     *            <li>1 : 缺省黑度,即普通打印单 
     *            <li>2 : 保留 
     *            <li>3 : 双层热敏打印 
     *            <li>4 : 双层热敏打印,比3的黑度更高 
     *            <li>50~500 : 黑度按照缺省黑度百分比进行设置,如50是把黑度设置为默认值的50%,500则把黑度设置为500%
     *            <li>其它值 : 保留并且更改无效
     *      </ul>
     * </div> 
     * <div class="en">
     * 		<ul>     
     *            <li>1 : default level, normal print slip
     *            <li>2 : reserved
     *            <li>3 : two-layer thermal printing
     *            <li>4 : two-layer thermal printing, higher gray level than 3
     *            <li>50~500 : gray level is set according to percentage of default
     *            value. If it is 50, then set gray level to be 50% of default
     *            value; if it is 500, then set grey level to be 500% of default
     *            value.
     *            <li>Others : reserved and modification is invalid
     *      </ul>
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnSetGray(int grayLevel) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = (byte)(grayLevel / 256);
    	req[1] = (byte)(grayLevel % 256);
    	
    	proto.sendRecv(Cmd.CmdType.PRN_SET_GRAY, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * 查询当前打印状态
     * </div>
     * <div class="en">
     * Inquire current printer status.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 			<ul>
     * 				<li>{@link #PRN_STATUS_OK}:正常
     * 				<li>{@link #PRN_STATUS_BUSY}:打印机忙
     * 				<li>{@link #PRN_STATUS_NO_PAPER}:打印机缺纸
     * 				<li>{@link #PRN_STATUS_DATA_FORMAT_ERR}:打印数据包格式错
     * 				<li>{@link #PRN_STATUS_FAULT}:打印机故障
     * 				<li>{@link #PRN_STATUS_OVERHEATED}:打印机过热
     * 				<li>{@link #PRN_STATUS_UNFINISHED}:打印未完成
     * 				<li>{@link #PRN_STATUS_NO_SUCH_FONT}:打印机未装字库
     * 				<li>{@link #PRN_STATUS_DATA_TOO_LONG}:数据包过长
     * 			</ul>
     * </div> 
     * <div class="en">     
     * 			<ul>
     * 				<li>{@link #PRN_STATUS_OK}: OK
     * 				<li>{@link #PRN_STATUS_BUSY}: busy
     * 				<li>{@link #PRN_STATUS_NO_PAPER}: out of paper
     * 				<li>{@link #PRN_STATUS_DATA_FORMAT_ERR}: print data format error
     * 				<li>{@link #PRN_STATUS_FAULT}: printer fault
     * 				<li>{@link #PRN_STATUS_OVERHEATED}: overheated
     * 				<li>{@link #PRN_STATUS_UNFINISHED}: unfinished
     * 				<li>{@link #PRN_STATUS_NO_SUCH_FONT}: lack of font
     * 				<li>{@link #PRN_STATUS_DATA_TOO_LONG}: data too long
     * 			</ul>
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public byte prnStatus() throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode(); 
    	byte[] resp = new byte[1];
    	proto.sendRecv(Cmd.CmdType.PRN_GET_STATUS, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		//success
    		return resp[0];
    	} else {
        	throw new PrinterException(rc.code);  		
    	}
    }
    
    /**
     * <div class="zh">
     * 打印字符, 注意此接口只是将数据送至打印缓冲区, 必须调用 {@link PrinterManager#prnStart} 开始打印, 打印机的一行总点数为384点 
     * </div>
     * <div class="en">
     * print a tring. Note that this function doesn't print the string immediately, 
     * it only transmits the string to the printing buffer, call {@link PrinterManager#prnStart} to start printing.
     * Total dots per line is 384.
     * </div>
     * 
     * @param str
     * <div class="zh">
     *           要打印的字符.<br/>
     *           支持\r\n\f<br/>
     *           另外支持约定的控制字符如下
     *           <ul>
     *           
     *           <li>%Ff, 字体控制, f表示字体大小, 取值范围0~7, 英文和中文各8种字体.<br/>
     *           	英文   0: 8*16, 1: 16*24, 2: 8*32, 3: 16*48,
     *           	4: 16*16, 5: 32*24, 6: 16*32, 7:　32*48.<br/>
     *           	中文   0: 16*16, 1: 24*24, 2: 16*32, 3: 24*48,
     *           	4: 32*16, 5: 48*24, 6: 32*32, 7: 48*48.
     *           <li>%Rr, 反显控制, r表示是否反色打印. 0 表示正常, 1表示反色
     *           </ul>
     * </div> 
     * <div class="en">     
     *           the string to print<br/>
     *           support\r\n\f<br/>
     *           support extra control as follows:
     *           <ul>
     *           <li>%Ff, Font control, f is font size, ranged from 0 to 7, 8 fonts for english and chinese each.<br/>
     *           	For English,  0: 8*16, 1: 16*24, 2: 8*32, 3: 16*48,
     *           	4: 16*16, 5: 32*24, 6: 16*32, 7: 32*48.<br/>
     *           	For Chinese,  0: 16*16, 1: 24*24, 2: 16*32, 3: 24*48,
     *           	4: 32*16, 5: 48*24, 6: 32*32, 7: 48*48.
     *           <li>%Rr, Inversion control, r toggles inversion. 0 is normal, 1 is invert.
     *           </ul>           
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnStr(String str) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] encoded = str.getBytes(encoding); 
    	
    	byte[] req = new byte[2 + encoded.length];
    	req[0] = (byte)(encoded.length / 256);
    	req[1] = (byte)(encoded.length % 256);
    	System.arraycopy(encoded, 0, req, 2, encoded.length);
    	
    	proto.sendRecv(Cmd.CmdType.PRN_STR, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * 打印图像. 注意此接口只是将数据送至打印缓冲区. 必须调用 {@link PrinterManager#prnStart}开始打印 
     * </div>
     * <div class="en">
     * print image. Note that this function doesn't print the image immediately, 
     * it only transmits the image to the printing buffer, call {@link PrinterManager#prnStart} to start printing.
     * </div>
     * 
     * @param name
     * <div class="zh">
	 *		图像文件名称
     * </div> 
     * <div class="en">     
     *      image file name
     * </div>
     * @param type
     * <div class="zh">
     * 		图像类型  
     * 	<ul>
     * 		<li>0: 单色 bmp  {@link #PRN_IMAGE_TYPE_MONO_BMP}
     * 		<li>1: 24-位色 bmp {@link #PRN_IMAGE_TYPE_24BIT_BMP}
     * 		<li>2: png  {@link #PRN_IMAGE_TYPE_PNG}
     * 		<li>3: jpg {@link #PRN_IMAGE_TYPE_JPG}
     *  </ul>
     * </div> 
     * <div class="en">     
     *      image type
     *   <ul>
     * 		<li>0: mono bmp {@link #PRN_IMAGE_TYPE_MONO_BMP}
     * 		<li>1: 24-bit bmp {@link #PRN_IMAGE_TYPE_24BIT_BMP}
     * 		<li>2: png {@link #PRN_IMAGE_TYPE_PNG}
     * 		<li>3: jpg {@link #PRN_IMAGE_TYPE_JPG}
     * 	</ul>
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     */
    /*
    public void prnImage(String name, byte type) throws PrinterException, IOException, ProtoException {
    	RespCode rc = new RespCode();
    	byte[] encodedName = name.getBytes(encoding); 
    	    	
    	byte[] req = new byte[2 + encodedName.length];
    	req[0] = type;
    	req[1] = (byte)(encodedName.length);
    	System.arraycopy(encodedName, 0, req, 2, encodedName.length);
    	
    	proto.sendRecv(Cmd.CmdType.PRN_IMAGE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }
*/    
    
    //transform image pixels into 1bpp
    private byte[][] prnTranformImagePixels(Bitmap bitmap) {
    	int pixel;
    	int R = 0, G = 0, B = 0;
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
    	
    	int bytesCount = (width + 7) / 8; 
    	byte[][] formatted = new byte[height][];
    	
    	for (int y = 0; y < height; y++) {
    		formatted[y] = new byte[bytesCount];
    		
    		for (int x = 0; x < width; x++) {
    			pixel = bitmap.getPixel(x, y);
    			R = Color.red(pixel);
    			G = Color.green(pixel);
    			B = Color.blue(pixel);
    			
    			int gray = (int)(0.299 * R + 0.587 * G + 0.114 * B);
    			if (gray < 128) {
    				formatted[y][x / 8] |= ((1 << (7 - x % 8)) & 0xff);
    			}
    		}
    	}
    	return formatted;
    }
    
    /**
     * <div class="zh">
     * 打印图像, 注意此接口只是将数据送至打印缓冲区, 必须调用 {@link PrinterManager#prnStart}开始打印, 注意横向点数最多384,超出范围的像素会被截除
     * </div>
     * <div class="en">
     * print image. Note that this function doesn't print the image immediately, 
     * it only transmits the image to the printing buffer, call {@link PrinterManager#prnStart} to start printing.
     * Note that the pixels outside the paper will be truncated.
     * </div>
     * 
     * @param bitmap
     * <div class="zh">
	 *		参考  {@link android.graphics.Bitmap}
     * </div> 
     * <div class="en">     
	 *		see {@link android.graphics.Bitmap}
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnImage(Bitmap bitmap) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	final int linesPerPage = 40;
    	
    	byte[][] formattedPixels = prnTranformImagePixels(bitmap);
    	int pages = (formattedPixels.length + linesPerPage - 1)/ linesPerPage;
    	
    	byte[] req;
    	int offset = 0;
    	int totalLinesPrinted = 0;
    	final int MAX_BYTES_NEEDED_PER_LINE = PRN_DOTS_PER_LINE / 8; 
    	
    	for (int i = 0; i < pages; i++) {
    		int linesOfThisPage = linesPerPage;
    		
    		//last page
    		if (i == (pages - 1)) {
    			linesOfThisPage = formattedPixels.length % linesPerPage;
    		}

    		//max 48 bytes needed.
    		int neededPixBytesPerLine = formattedPixels[0].length > MAX_BYTES_NEEDED_PER_LINE ? MAX_BYTES_NEEDED_PER_LINE : formattedPixels[0].length;
    		req = new byte[1 + (2 + neededPixBytesPerLine) * linesOfThisPage];
    		
    		offset = 0;
    		req[offset++] = (byte)linesOfThisPage;
    		for (int j = 0; j < linesOfThisPage; j++) {
    			int line = totalLinesPrinted + j;
    			req[offset++] = (byte)(neededPixBytesPerLine / 256);
    			req[offset++] = (byte)(neededPixBytesPerLine % 256);
    			System.arraycopy(formattedPixels[line], 0, req, offset, neededPixBytesPerLine);
    			offset += neededPixBytesPerLine;
    		}
    		
	    	proto.sendRecv(Cmd.CmdType.PRN_IMAGE, req, rc, new byte[0]);
	    	if (rc.code == 0) {
	    		//success
	    	} else {
	        	throw new PrinterException(rc.code);    		
	    	}
	    	
	    	totalLinesPrinted += linesOfThisPage;
    	}
    }
    
    
    /**
     * <div class="zh">
     * 走纸, 注意此接口不会立即走纸, 必须调用 {@link PrinterManager#prnStart}开始走纸 
     * </div>
     * <div class="en">
     * feed paper. Note that this function doesn't feed paper immediately, 
     * call {@link PrinterManager#prnStart} to start feeding.
     * </div>
     * 
     * @param dots
     * <div class="zh">
	 *		走纸总点数
     * </div> 
     * <div class="en">     
     *      dots to feed
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnFeed(int dots) throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = (byte)(dots / 256);
    	req[1] = (byte)(dots % 256);
    	
    	proto.sendRecv(Cmd.CmdType.PRN_FEED, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }    
    
    /**
     * <div class="zh">
     * 恢复打印机的缺省设置, 清空打印缓冲区的数据
     * </div>
     * <div class="en">
     * restore the default setting, reset the printing buffer.
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnReset() throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.PRN_RESET, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }        

    /**
     * <div class="zh">
     * 开始打印, 注意本操作不会自动清除打印缓冲区, 重复调用此接口将打印多份当前打印缓冲区中的内容 
     * 如果需要清除缓冲区,请调用 {@link PrinterManager#prnReset()}
     * </div>
     * <div class="en">
     * start printing. 
     * Note that this function doesn't clean the printing buffer, i.e. calling this function
     * multiple times will result in printing the same content multiple times.
     * If you want to clear the printing buffer, call  {@link PrinterManager#prnReset()}
     * </div>
     * 
     * @throws PrinterException
     * <div class="zh">打印机错误</div>
     * <div class="en">printer error</div>
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
    public void prnStart() throws PrinterException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.PRN_START, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PrinterException(rc.code);    		
    	}
    }        

}
