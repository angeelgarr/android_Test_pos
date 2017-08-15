package com.pax.mposapi.comm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.R.bool;
import android.content.Context;
import android.widget.Toast;

import com.pax.mposapi.CommonException;
import com.pax.mposapi.ConfigManager;
import com.pax.mposapi.EmvManager;
import com.pax.mposapi.EmvManager.EmvCallbackHandler;
import com.pax.mposapi.ProtoException;
import com.pax.mposapi.model.EMV_APPLIST;
import com.pax.mposapi.model.EMV_CANDLIST;
import com.pax.mposapi.util.MyLog;
import com.pax.mposapi.util.Utils;

public class Proto {

	
	//锟斤拷锟斤拷锟斤拷锟斤拷拇锟斤拷锟�
	private static final String TAG = "Proto";
	
	private static final int TRIES = 50;
	private static final byte STX = (byte)2;
	//private static final byte ETX = (byte)3;
	private static final byte ACK = (byte)6;
	private static final byte NAK = (byte)0x15;

	private static Context context;
	
	public static boolean isBTConnected = false;

	//private static Comm comm;
	public static Comm comm;   //By Jim 2014.09.11
	
	public static int tries = TRIES;

	
	//public static ConfigManager cfg;
	private static ConfigManager cfg;
	private static final int ADDITIONAL_TIMEOUT_FOR_GETTING_HOLD_PWD = 60000;

	private static EmvCallbackHandler emvCallbackHandler = null;
	
	public static boolean GetBtStatus(){
		return isBTConnected;
	}
	
	private static enum RecvRespState {
		STATE_INITIAL,
//		STATE_STX_RECVED,
//		STATE_CMD_RECVED,
//		STATE_SUBCMD_RECVED,
		STATE_LEN_RECVED,
		STATE_DATA_RECVED,
		STATE_LRC_RECVED
	};
	
	private static Proto proto;
	
	private Proto(Context context) {
		comm = Comm.getInstance(context);
		this.context = context;
		cfg = ConfigManager.getInstance(context);
	}

	public static Proto getInstance(Context context) {
		if (proto == null){
			proto = new Proto(context);
		}
		return proto;
	}
	
	public void setEmvCallbackHandler(EmvCallbackHandler handler) {
		emvCallbackHandler = handler;
	}
	
	private byte lrc(byte[] data, int offset, int len) {
		byte lrc = 0;
		for (int i = 0; i < len; i ++) {
			lrc ^= data[i + offset];
		}
		return lrc;
	}
	public static boolean setisBTConnected(){
		return isBTConnected = true;
	}
	
	private void sendCmd(Cmd.CmdType cmd, byte[]data) throws IOException {
		byte[] code = Cmd.getCmdCode(cmd);//Ocean  code=鍙戝嚭鐨勫懡浠�
		final byte[] buf = new byte[data.length + 6];//buf鏁扮粍鐨勯暱搴�=鍙傛暟闀垮害+6
		buf[0] = STX;    //02
		buf[1] = code[0];   //92
		buf[2] = code[1];   //1E
		buf[3] = (byte)(data.length / 256);   //鏁版嵁闀垮害   涓轰粈涔坉ata.length?
		buf[4] = (byte)(data.length % 256);  //鏁版嵁闀垮害
		System.arraycopy(data, 0, buf, 5, data.length);//杩欓噷鍙兘鏈夐棶棰�
		buf[5 + data.length] = lrc(buf, 1, 4 + data.length);
		MyLog.i(TAG, ">>>> " + cmd + ": " + Utils.byte2HexStr(buf, 0, buf.length));
		MyLog.i(TAG, ">>>> " + "data:"+new String(data));
		comm.send(buf);//鍙戦�佸懡浠�
	}
	
	private void processPassiveCmd(Cmd.CmdType cmd, byte[]resp) throws ProtoException, IOException, CommonException {
		MyLog.w(TAG, "processPassiveCmd");
		int ret = 0;
		byte[] req = new byte[4];
		
		switch (cmd) {
		case EMV_CALLBACK_WAIT_APP_SEL:	
		{
			int tryCnt = resp[0];
			int appNum = resp[1];
			int appLen = new EMV_APPLIST().serialToBuffer().length;
			
			EMV_APPLIST[] apps = new EMV_APPLIST[appNum];
			MyLog.i(TAG, "cnt: " + tryCnt + " appNum : " + appNum + " appLen: " + appLen);
			for (int i = 0; i < appNum; i++) {
				apps[i] = new EMV_APPLIST();
				apps[i].serialFromBuffer(resp, 2 + i * appLen);
			}
			
			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onWaitAppSel(tryCnt, appNum, apps);
			} else {
				ret = EmvManager.EMV_USER_CANCEL;
			}
			MyLog.i(TAG, "onWaitAppSel returns: " + ret);
		}
			break;
		case EMV_CALLBACK_CAND_APP_SEL:	
		{
			int tryCnt = resp[0];
			int appNum = resp[1];
			int appLen = new EMV_CANDLIST().serialToBuffer().length;
			
			EMV_CANDLIST[] apps = new EMV_CANDLIST[appNum];
			MyLog.i(TAG, "cnt: " + tryCnt + " appNum : " + appNum + " appLen: " + appLen);
			for (int i = 0; i < appNum; i++) {
				apps[i] = new EMV_CANDLIST();
				apps[i].serialFromBuffer(resp, 2 + i * appLen);
			}
			
			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onCandAppSel(tryCnt, appNum, apps);
			} else {
				ret = EmvManager.EMV_USER_CANCEL;
			}
			MyLog.i(TAG, "onCandAppSel returns: " + ret);
		}
			break;
		case EMV_CALLBACK_INPUT_AMOUNT:
			boolean needCashback = (resp[0] == 1);
			
			String[] amts = new String[2];
			if (needCashback) {
				amts[1] = "";	//amts[1] non-null means need cashback
			}
			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onInputAmount(amts);
			} else {
				ret = EmvManager.EMV_USER_CANCEL;
			}
			MyLog.i(TAG, "onInputAmt returns: " + ret);

			if (ret == 0) {
				if (needCashback) {
					int cashBackAmt = 0;
					req = new byte[12];
					if (amts[1] == null) {
						MyLog.w(TAG, "You should provide cashback, assuming 0!");
					}
					else {
						try {
							cashBackAmt = Integer.parseInt(amts[1]);
						} catch (NumberFormatException ne) {
							ne.printStackTrace();
							MyLog.e(TAG, "cashback amount number format error, assuming 0!");
						}
					}					
					Utils.int2ByteArray(cashBackAmt, req, 8);
				} else {
					req = new byte[8];
				}
				
				//0~3 are 0s
				int authAmt = 0;
				if (amts[0] == null) {
					MyLog.w(TAG, "You should provide auth amount, assuming 0!");
				}
				else {
					try {
						authAmt = Integer.parseInt(amts[0]);
					} catch (NumberFormatException ne) {
						ne.printStackTrace();
						MyLog.e(TAG, "auth amount number format error, assuming 0!");
					}
				}
				Utils.int2ByteArray(authAmt, req, 4);
			}
			break;
		case EMV_CALLBACK_GET_HOLDER_PWD:

			int pinFlag = resp[0];
			int tryFlag = 0;
			int remainCnt = 0;
			int pinStatus = 0;
			
			if (pinFlag == EmvManager.EMV_PIN_FLAG_OFFLINE) {
				tryFlag = resp[1];
				remainCnt = resp[2];
				pinStatus = resp[3];
				
				//offline pin && first time
				if (tryFlag == 0 && pinStatus == 0) {
					cfg.receiveTimeout += ADDITIONAL_TIMEOUT_FOR_GETTING_HOLD_PWD;
					MyLog.i(TAG, "receiveTimeout set to " + cfg.receiveTimeout);
				}
			}

			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onGetHolderPwd(pinFlag, tryFlag, remainCnt, pinStatus);
			} else {
				ret = EmvManager.EMV_USER_CANCEL;
			}
			MyLog.i(TAG, "onGetHolderPwd returns: " + ret);
			break;
		case EMV_CALLBACK_REFER_PROC:

			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onReferProc();
			} else {
				ret = EmvManager.EMV_REFER_DENIAL;
			}
			MyLog.i(TAG, "onReferProc returns: " + ret);	
			break;
		case EMV_CALLBACK_ONLINE_PROC:

			//the lengths are from the EMV source code
			byte[] respCode = new byte[2];
			byte[] authCode = new byte[4 + 6];
			byte[] authData = new byte[4 + 16];
			byte[] script = new byte[4 + 300];
			
			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onOnlineProc(respCode, authCode, authData, script);
			} else {
				ret = EmvManager.EMV_ONLINE_DENIAL;
			}
			MyLog.i(TAG, "onOnlineProc returns: " + ret);

			if (ret == EmvManager.EMV_ONLINE_APPROVE) {
				int authCodeLen = authCode[0];
				int authDataLen = Utils.intFromByteArray(authData, 0);
				int scriptLen = Utils.intFromByteArray(script, 0);
				
				req = new byte[4 + 2 + 1 + authCodeLen + 4 + authDataLen + 4 + scriptLen];
				System.arraycopy(respCode, 0, req, 4, 2);
				System.arraycopy(authCode, 0, req, 6, 1 + authCodeLen);
				System.arraycopy(authData, 0, req, 7 + authCodeLen, 4 + authDataLen);
				System.arraycopy(script, 0, req, 7 + authCodeLen + 4 + authDataLen, 4 + scriptLen);
			} else {
				req = new byte[4 + 2 + 1 + 4 + 4];	//all 0s
			}
			break;			
		case EMV_CALLBACK_ADVICE_PROC:

			if (emvCallbackHandler != null) {
				emvCallbackHandler.onAdviceProc();
			}
			break;
		case EMV_CALLBACK_VERIFY_PIN_OK:

			if (emvCallbackHandler != null) {
				emvCallbackHandler.onVerifyPinOk();
				if (cfg.receiveTimeout > ADDITIONAL_TIMEOUT_FOR_GETTING_HOLD_PWD) {
					cfg.receiveTimeout -= ADDITIONAL_TIMEOUT_FOR_GETTING_HOLD_PWD;
					MyLog.i(TAG, "receive timeout set back to :" + cfg.receiveTimeout);
				}
			}
			break;
		case EMV_CALLBACK_UNKNOWN_TLV_DATA:

			short tag = Utils.shortFromByteArray(resp, 0); 
			int len = Utils.intFromByteArray(resp, 2); 
			byte[] value = new byte[len];
			
			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onUnknownTLVData(tag, len, value);
			} else {
				ret = -1;
			}
			MyLog.i(TAG, "onUnknownTLVData returns: " + ret);

			if (ret == 0) {
				req = new byte[4 + len];
				System.arraycopy(value, 0, req, 4, len);
			}
			break;
		case EMV_CALLBACK_CERT_VERIFY:

			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onCertVerify();
			} else {
				ret = -1;	//verify error
			}
			MyLog.i(TAG, "onCertVerify returns: " + ret);

			break;
		case EMV_CALLBACK_SET_PARAM:

			if (emvCallbackHandler != null) {
				ret = emvCallbackHandler.onSetParam();
			} else {
				ret = -1;	//cancel/abort
			}
			MyLog.i(TAG, "onSetParam returns: " + ret);

			break;
		default:
			throw new RuntimeException("Invalid passive cmd: " + cmd + "!");
		}
		
		Utils.int2ByteArray(ret, req, 0);
    	
    	sendCmdWaitAck(cmd, req);    	
	}
	
	private int processResponse(Cmd.CmdType cmd, RespCode respCode, byte[]resp) throws ProtoException, IOException {
		MyLog.i(TAG, "waiting for response...");			
		byte[] respTemp = new byte[resp.length + 10]; 
		int respDataLen = 0;		
		
		//String s1=new String(resp);
	//	System.out.println(resp);
		//MyLog.i(TAG, "resp:"+resp);
		
		//int tries = TRIES;
		int recvLen;
		boolean mayReceivePassiveCmd = Cmd.mayRecvPassiveCmd(Cmd.getCmdCode(cmd));
		boolean success = false;
		Toast toast;
		
		tries = TRIES;
		toast=Toast.makeText(this.context,"Test -------0", Toast.LENGTH_LONG);
		toast.show();
		
	//	MyLog.i(TAG, "respTemp:"+respTemp+"resp:"+resp);
Tries: 	while (tries-- > 0) {
			MyLog.i(TAG, "process response... countdown: " + (tries + 1));
			respDataLen = 0;
			try {
					RecvRespState state = RecvRespState.STATE_INITIAL;
					// will break out either by throwing exception or successfully received the package 
					while (true)
					{
						MyLog.i(TAG, "state " + state.toString());
						
						
						
						if (state == RecvRespState.STATE_INITIAL) {
							recvLen = comm.recv(respTemp, 0, 5);
							if(Cmd.CmdType.MTLA_READ_CARDDATA == cmd)
							{
								if(0x1E == respTemp[2])
								{
									cmd = Cmd.CmdType.MTLA_READ_CARD_DATA;
								}
							}
							else if(Cmd.CmdType.MTLA_READ_CARD_DATA == cmd)
							{
								if(0x06 == respTemp[2])
								{
									cmd = Cmd.CmdType.MTLA_READ_CARDDATA;
								}
							}
							MyLog.e(TAG, "CMD1: " + Cmd.getCmdCode(cmd)[1] + " CMD2: " + Cmd.getCmdCode(Cmd.CmdType.MTLA_READ_CARD_DATA)[1]);
							//MyLog.i(TAG, "recvLen: " + String.valueOf(recvLen));
							//MyLog.i(TAG, "debug Data: " + Utils.byte2HexStr(respTemp, 0, respTemp.length));
							// to ensure at least 10000ms to receive during INITIAL state
							if ((recvLen == 0) && (cfg.receiveTimeout < 10000)) {
								continue Tries;
							}
							if (recvLen == 5){
								if (respTemp[0] != STX) {
									MyLog.e(TAG, "data format error1, recved " + Utils.byte2HexStr(respTemp, 0, 3));							
									throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT1);
								} 
								else if (respTemp[1] == Cmd.CMD || respTemp[1] == Cmd.CMD1|| respTemp[1] == Cmd.CMD2) //jason  add com2
								{
									byte[] code = Cmd.getCmdCode(cmd);
									//MyLog.e(TAG, "CMD1: " + Cmd.getCmdCode(cmd)[1] + " CMD2: " + Cmd.getCmdCode(Cmd.CmdType.MTLA_READ_CARD_DATA)[1]);
									if (respTemp[2] != code[1]) {
										MyLog.e(TAG, "code " + Utils.byte2HexStr(code, 0, 2));
										MyLog.e(TAG, "data format error2, recved " + Utils.byte2HexStr(respTemp, 0, 3));							
										throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT2);
									}
								} 
								else if (respTemp[1] == Cmd.CMD_PASSIVE) {
									if (!mayReceivePassiveCmd) {
										MyLog.e(TAG, "should NOT receive passive Cmd for cmd: " + cmd);
										throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT3);
									}
									//received passive command!!!
								} else {
									MyLog.e(TAG, "data format error3, recved " + Utils.byte2HexStr(respTemp, 0, 3));							
									throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT4);									
								}
								respDataLen = Utils.shortFromByteArray(respTemp, 3); 
								state = RecvRespState.STATE_LEN_RECVED;
							} 
							else {
								toast=Toast.makeText(this.context,"Test -------1", Toast.LENGTH_LONG);
								toast.show();
								throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA1);						
							}
						} else if (state == RecvRespState.STATE_LEN_RECVED) {
							//len field erorr! minimum 4 bytes
							if (((respTemp[1] == Cmd.CMD) || (respTemp[1] == Cmd.CMD1)|| (respTemp[1] == Cmd.CMD2)) && (respDataLen < 4)) {
								MyLog.w(TAG, "resp data len too short " + respDataLen);
								toast=Toast.makeText(this.context,"Test -------2", Toast.LENGTH_LONG);
								toast.show();
								throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA2);						
							} else {
								MyLog.i(TAG, "try to recv len " + respDataLen + " of data...");
								if (respDataLen > respTemp.length - 5 - 1) {	//-1 more is for lrc later
									MyLog.e(TAG, "resp data len is :" + respDataLen + " but we have only " + (respDataLen - 5 - 1) + " for data!");
									throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT5);
								}
								recvLen = comm.recv(respTemp, 5, respDataLen);				
								if (recvLen != respDataLen){
									MyLog.w(TAG, "recved data len " + recvLen + " !=" + respDataLen);
									toast=Toast.makeText(this.context,"Test -------3", Toast.LENGTH_LONG);
									toast.show();
									throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA4);						
								}
								else
								{
									state = RecvRespState.STATE_DATA_RECVED;
								}
							}
						} else if (state == RecvRespState.STATE_DATA_RECVED) {
							recvLen = comm.recv(respTemp, 5 + respDataLen, 1);
							if (recvLen == 1) {
								byte expLrc;
								expLrc = lrc(respTemp, 1, 4 + respDataLen);
								//MyLog.i(TAG, "debug Data: " + Utils.byte2HexStr(respTemp, 0, respTemp.length));
								if (respTemp[5 + respDataLen] != expLrc)
								{
									MyLog.i(TAG, "resp lrc should be: " + expLrc + ", but recved: " + respTemp[5 + respDataLen]);
									throw new ProtoException(ProtoException.PROTO_ERR_CHKSUM);
								}
								else
								{
									if ((respTemp[1] == Cmd.CMD) || (respTemp[1] == Cmd.CMD1)) {
										respCode.setCode(Utils.intFromByteArray(respTemp, 5));
										System.arraycopy(respTemp, 9, resp, 0, respDataLen - 4);
									} else {	//CMD_PASSIVE
										System.arraycopy(respTemp, 9, resp, 0, respDataLen- 4);  //jason
									}

									respCode.cmd = respTemp[1];
									respCode.subCmd = respTemp[2];
									
									//SUCCESS!
									state = RecvRespState.STATE_LRC_RECVED;
									MyLog.i(TAG, "recv success");
									MyLog.i(TAG, "<<<< Recv Data: " + Utils.byte2HexStr(respTemp, 0, 5 + respDataLen + 1));
									break;
								}
							} else {
								toast=Toast.makeText(this.context,"Test -------4", Toast.LENGTH_LONG);
								toast.show();
								throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA5);
							}
						} else {
							throw new RuntimeException("Invalid state, check you code!");
						}
					}//while (true)
					
					success = true;
					//success, break

					break;
			} catch (ProtoException pe) {
				if (tries != 0) {
					MyLog.e(TAG, "proto err, send nak and retry...");
					byte[] nak = new byte[1];
					nak[0] = NAK;
					comm.reset();
					comm.send(nak);
				} else {
					MyLog.e(TAG, "retried for 5 times, close...");
					throw pe;
				}
			}
			
		}//while (tries-- > 0)

		if (!success) {
			toast=Toast.makeText(this.context,"Test -------5", Toast.LENGTH_LONG);
			toast.show();
			throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA6);
		}
		
		//send ack
		byte[] ack = new byte[1];
		ack[0] = ACK;
		comm.send(ack);		
		if ((respTemp[1] == Cmd.CMD) || (respTemp[1] == Cmd.CMD1) || (respTemp[1] == Cmd.CMD2)) {
			return respDataLen - 4;
		} else {
			return respDataLen;
			
		}

	}
	
	private void sendCmdWaitAck(Cmd.CmdType cmd, byte[]req) throws ProtoException, IOException {
		comm.reset();

		int recvLen;
		int tries = TRIES;
		byte[] ack = new byte[1];
		boolean ok = false;
		
		while (tries-- > 0) {
			if (tries < TRIES - 1) {
				MyLog.w(TAG, "re-sending cmd... countdown: " + (tries + 1));		
			} else {
				MyLog.i(TAG, "sending cmd... countdown: " + (tries + 1));												
			}
			sendCmd(cmd, req);
			MyLog.i(TAG, "req1:"+req);	
			MyLog.i(TAG, "waiting for ack/nak.....");							
			recvLen = comm.recv(ack, 0, 1);
			if (recvLen == 1)
			{
				MyLog.e(TAG, "ack[0]:" + ack[0]);	
				
				if (ack[0] == NAK) {
					MyLog.w(TAG, "NAK recved!");
					if (tries > 0) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					tries++;
					comm.reset();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
					//throw new ProtoException(ProtoException.PROTO_ERR_NAKED);							
				} else if (ack[0] != ACK) {
					MyLog.w(TAG, "not ACK/NAK?!" + Utils.byte2HexStrUnFormatted(ack, 0, 1));
//					throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				} else {
					ok = true;
					break;
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA);
				//recv nothing, try again...
			}
		}
		
		if (!ok) {
			throw new IOException("recv timeout");
		}
	}
	
	private void sendCmdWaitAck1(Cmd.CmdType cmd, byte[]req) throws ProtoException, IOException {
		comm.reset();

		int recvLen;
		byte[] ack = new byte[1];
		boolean ok = false;
		

			
			sendCmd(cmd, req);
			MyLog.i(TAG, "req1:"+req);	
			MyLog.i(TAG, "waiting for ack/nak.....");							
			recvLen = comm.recv1(ack, 0, 1);
			if (recvLen == 1)
			{
				if (ack[0] == NAK) {
					MyLog.w(TAG, "NAK recved!");
				
					tries++;
					comm.reset();
					
					//throw new ProtoException(ProtoException.PROTO_ERR_NAKED);							
				} else if (ack[0] != ACK) {
					MyLog.w(TAG, "not ACK/NAK?!" + Utils.byte2HexStrUnFormatted(ack, 0, 1));
//					throw new ProtoException(ProtoException.PROTO_ERR_DATA_FORMAT);
					
					
					
				} else {
					ok = true;
				}
			} else {
				//throw new ProtoException(ProtoException.PROTO_ERR_NO_ENOUGH_DATA);
				//recv nothing, try again...
			}
		
		
		if (!ok) {
			throw new IOException("recv timeout");
		}
	}
	public synchronized int sendRecvOPen(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp) throws ProtoException, IOException, CommonException {
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		//int bakTimeout = 2000;
		/*if(cmd == Cmd.CmdType.MTLA_ABORT)
			bakTimeout = 2000;
		else
			bakTimeout = cfg.receiveTimeout;
			*/
		MyLog.i(TAG, "enter sendRecv");
		isBTConnected = true;

		comm.connect();
		
		
		try {
			cfg.receiveTimeout = 10000;
			sendCmdWaitAck(cmd, req);
			cfg.receiveTimeout = bakTimeout;

			int dataLen = 0;
			respCode.cmd = (byte)00;
			respCode.subCmd = (byte)0xFF;
			byte[] code = Cmd.getCmdCode(cmd);
			while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
				dataLen = processResponse(cmd, respCode, resp);
				//process passive cmd
				if (respCode.cmd == Cmd.CMD_PASSIVE) {
					byte[] passive = new byte[2];
					passive[0] = Cmd.CMD_PASSIVE;
					passive[1] = respCode.subCmd;
					processPassiveCmd(Cmd.getCmdType(passive), resp);
				}
			}

			MyLog.i(TAG, "sendRecv: " + cmd + " success");
			return dataLen;
			
		} catch (IOException e) {
			MyLog.e(TAG, "IOException, close...");
			comm.close();
			throw e;
		} catch (ProtoException e) {
			MyLog.e(TAG, "ProtoException, close...");
			//comm.close();
			//throw e;			
			byte[] tmp = "BT is not connected".getBytes();
			System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
			return -900;
		}
		finally {
			cfg.receiveTimeout = bakTimeout;
		}
	}
	
	public synchronized int sendRecvClose(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp) throws ProtoException, IOException, CommonException {
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		//int bakTimeout = 2000;
		/*if(cmd == Cmd.CmdType.MTLA_ABORT)
			bakTimeout = 2000;
		else
			bakTimeout = cfg.receiveTimeout;
			*/
		MyLog.i(TAG, "enter sendRecv");
	//	comm.connect();
		
		
		if(!isBTConnected)
		{
			byte[] tmp = "BT is not connected".getBytes();
			System.arraycopy(tmp, 0, resp, 0, tmp.length);
			return -900;
		}
		else {
		
				try {
					cfg.receiveTimeout = 2000;
					sendCmdWaitAck(cmd, req);
					cfg.receiveTimeout = bakTimeout;
		
					int dataLen = 0;
					respCode.cmd = (byte)00;
					respCode.subCmd = (byte)0xFF;
					byte[] code = Cmd.getCmdCode(cmd);
					while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
						dataLen = processResponse(cmd, respCode, resp);
						//process passive cmd
						if (respCode.cmd == Cmd.CMD_PASSIVE) {
							byte[] passive = new byte[2];
							passive[0] = Cmd.CMD_PASSIVE;
							passive[1] = respCode.subCmd;
							processPassiveCmd(Cmd.getCmdType(passive), resp);
						}
					}
		
					MyLog.i(TAG, "sendRecv: " + cmd + " success");
					return dataLen;
					
				} catch (IOException e) {
					MyLog.e(TAG, "IOException, close...");
					//comm.close();
					//throw e;
					byte[] tmp = "BT is not connected".getBytes();
					System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
					return -900;
				} catch (ProtoException e) {
					MyLog.e(TAG, "ProtoException, close...");
					//comm.close();
					//throw e;	
					byte[] tmp = "BT is not connected".getBytes();
					System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
					return -900;
				}
				finally {
					cfg.receiveTimeout = bakTimeout;
					isBTConnected = false;
				}
		}
	}
	public synchronized int sendRecvClose1(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp) throws ProtoException, IOException, CommonException {
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		//int bakTimeout = 2000;
		/*if(cmd == Cmd.CmdType.MTLA_ABORT)
			bakTimeout = 2000;
		else
			bakTimeout = cfg.receiveTimeout;
			*/
		MyLog.i(TAG, "enter sendRecv");
	//	comm.connect();
		
		
		if(!isBTConnected)
		{
			byte[] tmp = "BT is not connected".getBytes();
			System.arraycopy(tmp, 0, resp, 0, tmp.length);
			return -900;
		}
		else {
		
				try {
					cfg.receiveTimeout = 2000;
					sendCmdWaitAck(cmd, req);
					cfg.receiveTimeout = bakTimeout;
		
					int dataLen = 0;
					respCode.cmd = (byte)00;
					respCode.subCmd = (byte)0xFF;
					byte[] code = Cmd.getCmdCode(cmd);
					while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
						dataLen = processResponse(cmd, respCode, resp);
						//process passive cmd
						if (respCode.cmd == Cmd.CMD_PASSIVE) {
							byte[] passive = new byte[2];
							passive[0] = Cmd.CMD_PASSIVE;
							passive[1] = respCode.subCmd;
							processPassiveCmd(Cmd.getCmdType(passive), resp);
						}
					}
			
					MyLog.i(TAG, "sendRecv: " + cmd + " success");
					return dataLen;
					
				} catch (IOException e) {
					MyLog.e(TAG, "IOException, close...");
//					comm.close();
//					throw e;
					byte[] tmp = "BT is not connected".getBytes();
					System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
					return -900;
				} catch (ProtoException e) {
					MyLog.e(TAG, "ProtoException, close...");
				//	comm.close();
				//	throw e;		
					byte[] tmp = "BT is not connected".getBytes();
					System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
					return -900;
				}
				finally {
					cfg.receiveTimeout = bakTimeout;
					isBTConnected = false;
				}
		}
	}


	public synchronized int sendRecv(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp) throws ProtoException, IOException, CommonException {
		
		MyLog.i(TAG, "req:"+new String(req));
		if(null == cfg)
		{
			System.out.println("1");
		}
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		//int bakTimeout = 2000;
		/*if(cmd == Cmd.CmdType.MTLA_ABORT)
			bakTimeout = 2000;
		else
			bakTimeout = cfg.receiveTimeout;
			*/
		int dataLen = 0;
		MyLog.i(TAG, "enter sendRecv");
	//	comm.connect();
		if(!isBTConnected)
		{
			byte[] tmp = "BT is not connected!!!1".getBytes();
			System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
			return -900;
		}
		else {
			try {
				//cfg.receiveTimeout = 2000;
				sendCmdWaitAck(cmd, req);
				//cfg.receiveTimeout = bakTimeout;

			
				respCode.cmd = (byte)00;
				respCode.subCmd = (byte)0xFF;
				byte[] code = Cmd.getCmdCode(cmd);
				while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
					dataLen = processResponse(cmd, respCode, resp);
					//process passive cmd
					if (respCode.cmd == Cmd.CMD_PASSIVE) {
						byte[] passive = new byte[2];
						passive[0] = Cmd.CMD_PASSIVE;
						passive[1] = respCode.subCmd;
						processPassiveCmd(Cmd.getCmdType(passive), resp);
					}
					
					if (0 == respCode.code)
					{
						break;
					}
				}
				MyLog.i(TAG, "sendRecv: " + cmd + " success");
				return dataLen;
				
			} catch (IOException e) {
				MyLog.e(TAG, "IOException, close...");
			//	comm.close();
				//throw e;
				byte[] tmp = "BT is not connected!!!2".getBytes();
				System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
				return -900;
			} catch (ProtoException e) {
				MyLog.e(TAG, "ProtoException, close");
//				comm.close();
				throw e;			
			}
			finally {
				cfg.receiveTimeout = bakTimeout;
			}

		}
		
			
	}
	
	public synchronized int sendRecv1(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp) throws ProtoException, IOException, CommonException {
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		//int bakTimeout = 2000;
		/*if(cmd == Cmd.CmdType.MTLA_ABORT)
			bakTimeout = 2000;
		else
			bakTimeout = cfg.receiveTimeout;
			*/
//		proto.sta = true;
		MyLog.i(TAG, "enter sendRecv1");
		//com.pax.mposapi.comm.Comm.clientTemp.close();
		//proto.comm.close();
		if(!isBTConnected)
		{
			byte[] tmp = "BT is not connected".getBytes();
			System.arraycopy(tmp, 0, resp, 0, tmp.length);
			return -900;
		}
		
		
		//isBTConnected = true;
//		if(!isBTConnected)
//		{
//			byte[] tmp = "BT is not connected".getBytes(); 
//			System.arraycopy(tmp, 0, resp, 0, tmp.length);
//			return -900;
//		}
		else {
			
			try {
//				comm.connect();
				//cfg.receiveTimeout = 2000;
				sendCmdWaitAck1(cmd, req);
				//cfg.receiveTimeout = bakTimeout;
	
				int dataLen = 0;
				respCode.cmd = (byte)00;
				respCode.subCmd = (byte)0xFF;
				byte[] code = Cmd.getCmdCode(cmd);
				while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
					dataLen = processResponse(cmd, respCode, resp);
					//process passive cmd
					if (respCode.cmd == Cmd.CMD_PASSIVE) {
						byte[] passive = new byte[2];
						passive[0] = Cmd.CMD_PASSIVE;
						passive[1] = respCode.subCmd;
						processPassiveCmd(Cmd.getCmdType(passive), resp);
					}
				}
	
				MyLog.i(TAG, "sendRecv: " + cmd + " success");
				return dataLen;
				
			} catch (IOException e) {
				MyLog.e(TAG, "IOException, close..1");
			//	comm.close();
			//	throw e;
				byte[] tmp = "Current command is not able to be aborted1".getBytes();
				System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
				return -900;
			} catch (ProtoException e) {
				MyLog.e(TAG, "ProtoException, close..");
			//	comm.close();
				throw e;			
			}
			finally {
				cfg.receiveTimeout = bakTimeout;
			}
		}
	}
	
	public synchronized int sendRecvStep(Cmd.CmdType cmd, byte[]req, RespCode respCode, byte[]resp, boolean bRecv) throws ProtoException, IOException, CommonException {
		if(null == cfg)
		{
			System.out.println("1");
		}
		int bakTimeout = cfg.receiveTimeout;//锟斤拷bakTimeout锟斤拷值
		int dataLen = 0;
		MyLog.i(TAG, "enter sendRecv");

		if(!isBTConnected)
		{
			byte[] tmp = "BT is not connected".getBytes();
			System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
			return -900;
		}
		else {
			try {
				
				//cfg.receiveTimeout = 2000;
				sendCmdWaitAck(cmd, req);
				//cfg.receiveTimeout = bakTimeout;
				
				if(bRecv){
	
					respCode.cmd = (byte)00;
					respCode.subCmd = (byte)0xFF;
					byte[] code = Cmd.getCmdCode(cmd);
					while (!((respCode.cmd == code[0]) && (respCode.subCmd == code[1]))) {
						dataLen = processResponse(cmd, respCode, resp);
						//process passive cmd
						if (respCode.cmd == Cmd.CMD_PASSIVE) {
							byte[] passive = new byte[2];
							passive[0] = Cmd.CMD_PASSIVE;
							passive[1] = respCode.subCmd;
							processPassiveCmd(Cmd.getCmdType(passive), resp);
						}
					}
	
					MyLog.i(TAG, "sendRecv: " + cmd + " success");
					return dataLen;
				}
				else{
					return 0;
				}
				
			} catch (IOException e) {
				MyLog.e(TAG, "IOException, close...");
				comm.close();
				//throw e;
				byte[] tmp = "BT is not connected".getBytes();
				System.arraycopy(tmp, 0, resp, 0, (resp.length < tmp.length)? resp.length: tmp.length);
				return -900;
			} catch (ProtoException e) {
				MyLog.e(TAG, "ProtoException, close...");
				comm.close();
				throw e;			
			}
			finally {
				cfg.receiveTimeout = bakTimeout;
			}

		}
		
			
	}
	

}
