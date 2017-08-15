package com.pax.mposapi.comm;

public class RespCode {
	public static final int OK = 0;
	public int code;
	public byte cmd;
	public byte subCmd;

	public RespCode(){
		this.code = 0;
	}
	
	public void setCode(int code){
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	public boolean isOk(){
		return (code == OK);
	}
}
