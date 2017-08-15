package com.pax.mposapi.comm;

import com.pax.mposapi.util.Utils;


public class Cmd {
	public CmdType type; 
	public byte[] code;

	public static final byte CMD = (byte)0x90;
	public static final byte CMD1 = (byte)0x91;
	public static final byte CMD2 = (byte)0x92;      //jason 
	public static final byte CMD_PASSIVE = (byte)0xb0;

	private Cmd(CmdType type, byte[] code){
		this.type = type;
		this.code = code;
	}
	
	public enum CmdType{
		//ped
		PED_GET_VER,
		PED_WRITE_KEY,
		PED_WRITE_TIK,
		PED_MS_GET_PIN,
		PED_MS_GET_MAC,
		PED_MS_CALC_DES,
		PED_DUKPT_GET_PIN,
		PED_DUKPT_GET_MAC,
		PED_ICC_VERIFY_PLAIN_PIN,
		PED_ICC_VERIFY_CIPHER_PIN,
		PED_GET_KCV,
		PED_ERASE,
		PED_SET_KEY_TAG,
		PED_SET_FUNCKEY,
		PED_INJECT_KEY,
		PED_WRITE_RSA_KEY,
		PED_RSA_RECOVER,
		PED_DUKPT_DES,
		PED_DUKPT_GET_KSN,
		PED_DUKPT_INCREASE_KSN,
		
		//port
		PORT_OPEN,
		PORT_CLOSE,
		PORT_RESET,
		PORT_SEND,
		PORT_RECV,
		
		//base system
		BASE_BEEP,
		BASE_SET_DATETIME,
		BASE_GET_DATETIME,
		BASE_READ_SN,
		BASE_READ_EXSN,
		BASE_PING,
		BASE_REBOOT,
		BASE_GET_RANDOM,
		BASE_BATTERY_CHECK,
		
		BASE_READ_VER_INFO,
		BASE_READ_TERM_INFO,
		BASE_BEEF,
/*
		// terminal key enc/dec
		TERMINAL_KEY_ENC,
		TERMINAL_KEY_DEC,		
*/
		//msr
		MSR_OPEN,
		MSR_CLOSE,
		MSR_RESET,
		MSR_IS_SWIPED,
		MSR_READ,
		
		//icc L1
		ICC_INIT,
		ICC_CLOSE,
		ICC_AUTO_RESP,
		ICC_ISOCOMMAND,
		ICC_DETECT,
		
		//picc
		PICC_OPEN,
		PICC_SETUP,
		PICC_DETECT,
		PICC_ISOCOMMAND,
		PICC_REMOVE,
		PICC_CLOSE,
		PICC_M1_AUTH,
		PICC_M1_READ_BLOCK,
		PICC_M1_WRITE_BLOCK,
		PICC_M1_OPERATE,
		PICC_LIGHT,
		PICC_INIT_FELICA,
		
		//ui - lcd
		LCD_CLS,
		LCD_SHOW_TEXT,
		LCD_GET_TEXT_BY_ID,
		LCD_SET_TEXT_BY_ID,
		LCD_SHOW_TEXT_BY_ID,
		LCD_PROCESS_IMAGE,
		LCD_BACKLIGHT,

		//ui - kbd
		KBD_FLUSH,
		KBD_GET_KEY,
		KBD_GET_STRING,
		KBD_GET_HZ_STRING,
		KBD_BACKLIGHT,
		KBD_LOCK,
		KBD_CHECK,
		KBD_MUTE,
		KBD_HIT,
		
		//printer
		PRN_SET_SPACES,
		PRN_SET_INDENT,
		PRN_SET_GRAY,
		PRN_GET_STATUS,
		PRN_STR,
		PRN_IMAGE,
		PRN_FEED,
		PRN_RESET,
		PRN_START,
		
		//EMV
		EMV_GET_PARAM,
		EMV_SET_PARAM,
		EMV_GET_TLV_DATA,
		EMV_SET_TLV_DATA,
		EMV_GET_SCRIPT_RESULT,
		EMV_SET_PCI_MODE_PARAM,
		EMV_READ_VER_INFO,
		EMV_CLEAR_TRANS_LOG,
		EMV_ADD_ICC_TAG,
		EMV_SET_SCRIPT_PROC_METHOD,
		
		EMV_ADD_CAPK,
		EMV_DEL_CAPK,
		EMV_GET_CAPK,
		EMV_CHECK_CAPK,
		
		EMV_ADD_APP,
		EMV_GET_APP,
		EMV_DEL_APP,
		EMV_GET_FINAL_APP_PARA,
		EMV_MOD_FINAL_APP_PARA,
		EMV_GET_LABEL_LIST,
		
		EMV_ADD_REVOC_LIST,
		EMV_DEL_REVOC_LIST,
		EMV_DEL_ALL_REVOC_LIST,
		
		EMV_INIT_TLV_DATA,
		EMV_APP_SELECT,
		EMV_READ_APP_DATA,
		EMV_CARD_AUTH,
		EMV_PROC_TRANS,
		EMV_APP_SELECT_FOR_LOG,
		EMV_READ_LOG_RECORD,
		EMV_GET_LOG_ITEM,
		EMV_GET_MCK_PARAM,
		EMV_SET_MCK_PARAM,
		EMV_SET_TM_ECP_PARAM,
		EMV_GET_CARD_ECB_BALANCE,
		EMV_START_TRANS,
		EMV_COMPLETE_TRANS,
		EMV_SET_CONFIG_FLAG,
		EMV_SWITCH_CLSS,
		EMV_SET_AMOUNT,
		EMV_READ_SINGLE_LOAD_LOG,
		EMV_GET_SINGLE_LOAD_LOG_ITEM,
		EMV_READ_ALL_LOAD_LOGS,
		EMV_DEL_ALL_CAPK,
		EMV_DEL_ALL_APP,
		
		EMV_GET_LOG_DATA,
		
		//modem
		MODEM_RESET,
		MODEM_DIAL,
		MODEM_CHECK,
		MODEM_TXD,
		MODEM_RXD,
		MODEM_ASYNC_GET,
		MODEM_ONHOOK,
		MODEM_HANGOFF,
		MODEM_EX_COMMAND,
		MODEM_PPP_LOGIN,
		MODEM_PPP_LOGOUT,
		MODEM_PPP_CHECK,
		
		//clss
		CLSS_ENTRY_READ_VER_INFO,
		CLSS_ENTRY_ADD_AID_LIST,
		CLSS_ENTRY_DEL_AID_LIST,
		CLSS_ENTRY_DEL_ALL_AID_LIST,
		CLSS_ENTRY_SET_PRE_PROC_INFO,
		CLSS_ENTRY_DEL_PRE_PROC_INFO,
		CLSS_ENTRY_DEL_ALL_PRE_PROC_INFO,
		CLSS_ENTRY_PRE_TRANS_PROC,
		CLSS_ENTRY_APPSLT,
		CLSS_ENTRY_FINAL_SELECT,
		CLSS_ENTRY_DEL_CUR_CAND_APP,
		CLSS_ENTRY_GET_PRE_PROC_INTER_FLG,
		CLSS_ENTRY_GET_FINAL_SELECT_DATA,
		CLSS_ENTRY_APP_SELECT_UNLOCK_APP,
		CLSS_ENTRY_GET_ERROR_CODE,
		CLSS_ENTRY_SET_MC_VERSION,
		
		CLSS_PBOC_READ_VER_INFO,
		CLSS_PBOC_GET_TLV_DATA,
		CLSS_PBOC_SET_TLV_DATA,
		CLSS_PBOC_SET_EMV_UNKNOWN_TLV_DATA,
		CLSS_PBOC_GET_READER_PARAM,
		CLSS_PBOC_SET_READER_PARAM,
		CLSS_PBOC_SET_AID_PARAM,
		CLSS_PBOC_ADD_CAPK,
		CLSS_PBOC_DEL_CAPK,
		CLSS_PBOC_GET_CAPK,
		CLSS_PBOC_DEL_ALL_CAPK,
		CLSS_PBOC_ADD_REVOC_LIST,
		CLSS_PBOC_DEL_REVOC_LIST,
		CLSS_PBOC_DEL_ALL_REVOC_LIST,
		CLSS_PBOC_SET_FINAL_SELECT_DATA,
		CLSS_PBOC_SET_TRANS_DATA,
		CLSS_PBOC_PROC_TRANS,
		CLSS_PBOC_CARD_AUTH,
		CLSS_PBOC_GET_CVM_TYPE,
		CLSS_PBOC_GET_TRACK1_MAP_DATA,
		CLSS_PBOC_GET_TRACK2_MAP_DATA,
		CLSS_PBOC_GET_GPO_DATA,
		CLSS_PBOC_RESEND_LAST_CMD,
		CLSS_PBOC_GET_DATA_CMD,
		CLSS_PBOC_PROC_TRANS_UNLOCK_APP,
		
		// clss integrate (paypass, paywave...)
		CLSS_INTEGRATE_DOWNLOAD_CAPK,
		CLSS_INTEGRATE_DOWNLOAD_APP,
		CLSS_INTEGRATE_DOWNLOAD_VISA_READER_PARAM,
		CLSS_INTEGRATE_DOWNLOAD_VISA_AID_PARAM,
		CLSS_INTEGRATE_DOWNLOAD_MC_READER_PARAM,
		CLSS_INTEGRATE_DOWNLOAD_MC_AID_PARAM,
		CLSS_INTEGRATE_DOWNLOAD_MC_TERM_CONFIG,
		CLSS_INTEGRATE_DELETE_ALL_CAPK,
		CLSS_INTEGRATE_DELETE_ONE_CAPK,
		CLSS_INTEGRATE_DELETE_ALL_AID,
		CLSS_INTEGRATE_DELETE_ONE_AID,
		CLSS_INTEGRATE_TRANS_INIT,
		CLSS_INTEGRATE_TRANS_CANCEL,
		CLSS_INTEGRATE_WAIT_CARD_STATUS,
		CLSS_INTEGRATE_TRANS_START,
		CLSS_INTEGRATE_TRASN_WAVE_FINISH,
		
		
		//net
		NET_SOCKET,
		NET_CONNECT,
		NET_SEND,
		NET_SENDTO,
		NET_RECV,
		NET_RECVFROM,
		NET_CLOSE_SOCKET,
		NET_IOCTL,
		NET_DEVGET,
		NET_DNS_RESOLVE,
		NET_PING,
	
		
		//P2PE related
		CMD_EXCHANGE_DATA,
		CMD_CALC_CMAC,
		
		//emv callbacks
		EMV_CALLBACK_WAIT_APP_SEL,
		EMV_CALLBACK_INPUT_AMOUNT,
		EMV_CALLBACK_GET_HOLDER_PWD,
		EMV_CALLBACK_REFER_PROC,
		EMV_CALLBACK_ONLINE_PROC,
		EMV_CALLBACK_ADVICE_PROC,
		EMV_CALLBACK_VERIFY_PIN_OK,
		EMV_CALLBACK_UNKNOWN_TLV_DATA,
		EMV_CALLBACK_CERT_VERIFY,
		EMV_CALLBACK_SET_PARAM,
		
		EMV_CALLBACK_PICC_ISO_COMMAND,
		EMV_CALLBACK_ICC_ISO_COMMAND,
		EMV_CALLBACK_PED_VERIFY_PLAIN_PIN,
		EMV_CALLBACK_PED_VERIFY_CIPHER_PIN,
		EMV_CALLBACK_SM2_VERIFY,
		EMV_CALLBACK_SM3,
		
		EMV_CALLBACK_CAND_APP_SEL,
		
		//jason moto api
		MTLA_OPEN, 
		MTLA_CLOSE, 
		MTLA_MENU,
		MTLA_MESSAGE,
		MTLA_ABORT,
		MTLA_SETSESSION_KEY,
		MTLA_READ_CARDDATA,
		MTLA_ENABLE_KEY,
		MTLA_DISABLE_KEY,
		MTLA_GETPIN,
		MTLA_GETEMVTAG,
		MTLA_SETEMVTAG,
		MTLA_GETBATTERY_LEVEL,
		MTLA_GETBATTERY_THRESHOLD,
		MTLA_SETBATTERY_THRESHOLE,
		MTLA_CREATMAC,
		MTLA_VALIDATE_MAC,
		MTLA_EMVOLINE_FINISH,
		MTLA_AUTHORIZE_CARD,
		MTLA_OTHERINFO,
		MTLA_REMOVECARD,
		MTLA_SETPARAMETER,
		MTLA_GETPARAMETER,
		MTLA_DOWNLOAD_APP,
		MTLA_DELETE_ALL_APP,
		MTLA_DOWNLOAD_CAPK,
		MTLA_DELETE_ALL_CAPK,
		MTLA_GET_EMV_PARAMETER,
		MTLA_DOWNLOAD_EMV_PARAM,
		MTLA_DOWNLOAD_FILE,
		MTLA_DOWNLOAD_RSAKEY,
		MTLA_RSAKEY_TEST,
		MTLA_GET_ENCRYPTED_DATA,
		MTLA_GET_DATE_TIME,
		MTLA_SET_DATE_TIME,
		MTLA_GET_STATUS_UPDATE,
		MTLA_READ_CARD_DATA
		

	}
	
	private static final Cmd[] cmds = {
		//ped
		new Cmd(CmdType.PED_GET_VER,					new byte[]{(byte)CMD, (byte)0x0}),
		new Cmd(CmdType.PED_WRITE_KEY,					new byte[]{(byte)CMD, (byte)0x1}),
		new Cmd(CmdType.PED_WRITE_TIK,					new byte[]{(byte)CMD, (byte)0x2}),
		new Cmd(CmdType.PED_MS_GET_PIN,					new byte[]{(byte)CMD, (byte)0x3}),
		new Cmd(CmdType.PED_MS_GET_MAC,					new byte[]{(byte)CMD, (byte)0x4}),
		new Cmd(CmdType.PED_MS_CALC_DES,				new byte[]{(byte)CMD, (byte)0x5}),
		new Cmd(CmdType.PED_DUKPT_GET_PIN,				new byte[]{(byte)CMD, (byte)0x6}),
		new Cmd(CmdType.PED_DUKPT_GET_MAC,				new byte[]{(byte)CMD, (byte)0x7}),
		new Cmd(CmdType.PED_ICC_VERIFY_PLAIN_PIN,		new byte[]{(byte)CMD, (byte)0x9}),
		new Cmd(CmdType.PED_ICC_VERIFY_CIPHER_PIN,		new byte[]{(byte)CMD, (byte)0xa}),
		new Cmd(CmdType.PED_GET_KCV,					new byte[]{(byte)CMD, (byte)0xb}),
		new Cmd(CmdType.PED_ERASE,						new byte[]{(byte)CMD, (byte)0xd}),
		new Cmd(CmdType.PED_SET_KEY_TAG,				new byte[]{(byte)CMD, (byte)0xf}),
		new Cmd(CmdType.PED_SET_FUNCKEY,				new byte[]{(byte)CMD, (byte)0x14}),
		new Cmd(CmdType.PED_INJECT_KEY,					new byte[]{(byte)CMD, (byte)0x15}),
		new Cmd(CmdType.PED_WRITE_RSA_KEY,				new byte[]{(byte)CMD, (byte)0x16}),
		new Cmd(CmdType.PED_RSA_RECOVER,				new byte[]{(byte)CMD, (byte)0x17}),
		new Cmd(CmdType.PED_DUKPT_DES,					new byte[]{(byte)CMD, (byte)0x18}),
		new Cmd(CmdType.PED_DUKPT_GET_KSN,				new byte[]{(byte)CMD, (byte)0x19}),
		new Cmd(CmdType.PED_DUKPT_INCREASE_KSN,			new byte[]{(byte)CMD, (byte)0x20}),
		
		//port
		new Cmd(CmdType.PORT_OPEN, 				new byte[]{(byte)CMD, (byte)0x2a}),
		new Cmd(CmdType.PORT_CLOSE,				new byte[]{(byte)CMD, (byte)0x2b}),
		new Cmd(CmdType.PORT_RESET,				new byte[]{(byte)CMD, (byte)0x2c}),
		new Cmd(CmdType.PORT_SEND, 				new byte[]{(byte)CMD, (byte)0x2d}),
		new Cmd(CmdType.PORT_RECV, 				new byte[]{(byte)CMD, (byte)0x2e}),
		
		//base
		new Cmd(CmdType.BASE_BEEP, 				new byte[]{(byte)CMD, (byte)0x31}),
		new Cmd(CmdType.BASE_SET_DATETIME,		new byte[]{(byte)CMD, (byte)0x32}),
		new Cmd(CmdType.BASE_GET_DATETIME, 		new byte[]{(byte)CMD, (byte)0x33}),
		new Cmd(CmdType.BASE_READ_SN,			new byte[]{(byte)CMD, (byte)0x34}),
		new Cmd(CmdType.BASE_READ_EXSN, 		new byte[]{(byte)CMD, (byte)0x35}),
		new Cmd(CmdType.BASE_PING,		 		new byte[]{(byte)CMD, (byte)0x36}),		
		new Cmd(CmdType.BASE_REBOOT,			new byte[]{(byte)CMD, (byte)0x37}),
		new Cmd(CmdType.BASE_GET_RANDOM, 		new byte[]{(byte)CMD, (byte)0x38}),
		new Cmd(CmdType.BASE_BATTERY_CHECK, 	new byte[]{(byte)CMD, (byte)0x39}),		
		
		new Cmd(CmdType.BASE_READ_VER_INFO,		new byte[]{(byte)CMD, (byte)0x3d}),
		new Cmd(CmdType.BASE_READ_TERM_INFO, 	new byte[]{(byte)CMD, (byte)0x3e}),
		new Cmd(CmdType.BASE_BEEF, 				new byte[]{(byte)CMD, (byte)0x3f}),

		//msr
		new Cmd(CmdType.MSR_OPEN,				new byte[]{(byte)CMD, (byte)0x50}),
		new Cmd(CmdType.MSR_CLOSE,				new byte[]{(byte)CMD, (byte)0x51}),
		new Cmd(CmdType.MSR_RESET,				new byte[]{(byte)CMD, (byte)0x52}),
		new Cmd(CmdType.MSR_IS_SWIPED,			new byte[]{(byte)CMD, (byte)0x53}),
		new Cmd(CmdType.MSR_READ,				new byte[]{(byte)CMD, (byte)0x54}),
		
		//icc L1
		new Cmd(CmdType.ICC_INIT,				new byte[]{(byte)CMD, (byte)0x55}),
		new Cmd(CmdType.ICC_CLOSE,				new byte[]{(byte)CMD, (byte)0x56}),
		new Cmd(CmdType.ICC_AUTO_RESP,			new byte[]{(byte)CMD, (byte)0x57}),
		new Cmd(CmdType.ICC_ISOCOMMAND,			new byte[]{(byte)CMD, (byte)0x58}),
		new Cmd(CmdType.ICC_DETECT,				new byte[]{(byte)CMD, (byte)0x59}),
		
		//picc
		new Cmd(CmdType.PICC_OPEN,				new byte[]{(byte)CMD, (byte)0x60}),
		new Cmd(CmdType.PICC_SETUP,				new byte[]{(byte)CMD, (byte)0x61}),
		new Cmd(CmdType.PICC_DETECT,			new byte[]{(byte)CMD, (byte)0x62}),
		new Cmd(CmdType.PICC_ISOCOMMAND,		new byte[]{(byte)CMD, (byte)0x63}),
		new Cmd(CmdType.PICC_REMOVE,			new byte[]{(byte)CMD, (byte)0x64}),
		new Cmd(CmdType.PICC_CLOSE,				new byte[]{(byte)CMD, (byte)0x65}),
		new Cmd(CmdType.PICC_M1_AUTH,			new byte[]{(byte)CMD, (byte)0x66}),
		new Cmd(CmdType.PICC_M1_READ_BLOCK,		new byte[]{(byte)CMD, (byte)0x67}),
		new Cmd(CmdType.PICC_M1_WRITE_BLOCK,	new byte[]{(byte)CMD, (byte)0x68}),
		new Cmd(CmdType.PICC_M1_OPERATE,		new byte[]{(byte)CMD, (byte)0x69}),
		new Cmd(CmdType.PICC_LIGHT,				new byte[]{(byte)CMD, (byte)0x6a}),
		new Cmd(CmdType.PICC_INIT_FELICA,		new byte[]{(byte)CMD, (byte)0x6b}),	//FIXME! 
		
		//ui - lcd
		new Cmd(CmdType.LCD_CLS,				new byte[]{(byte)CMD, (byte)0x40}),
		new Cmd(CmdType.LCD_SHOW_TEXT,			new byte[]{(byte)CMD, (byte)0x41}),
		new Cmd(CmdType.LCD_GET_TEXT_BY_ID,		new byte[]{(byte)CMD, (byte)0x42}),
		new Cmd(CmdType.LCD_SET_TEXT_BY_ID,		new byte[]{(byte)CMD, (byte)0x43}),
		new Cmd(CmdType.LCD_SHOW_TEXT_BY_ID,	new byte[]{(byte)CMD, (byte)0x44}),
		new Cmd(CmdType.LCD_PROCESS_IMAGE,		new byte[]{(byte)CMD, (byte)0x45}),
		new Cmd(CmdType.LCD_BACKLIGHT,			new byte[]{(byte)CMD, (byte)0x46}),
		//ui - kbd
		new Cmd(CmdType.KBD_FLUSH,				new byte[]{(byte)CMD, (byte)0x47}),
		new Cmd(CmdType.KBD_GET_KEY,			new byte[]{(byte)CMD, (byte)0x48}),
		new Cmd(CmdType.KBD_GET_STRING,			new byte[]{(byte)CMD, (byte)0x49}),
		new Cmd(CmdType.KBD_GET_HZ_STRING,		new byte[]{(byte)CMD, (byte)0x4a}),
		new Cmd(CmdType.KBD_BACKLIGHT,			new byte[]{(byte)CMD, (byte)0x4b}),
		new Cmd(CmdType.KBD_LOCK,				new byte[]{(byte)CMD, (byte)0x4c}),
		new Cmd(CmdType.KBD_CHECK,				new byte[]{(byte)CMD, (byte)0x4d}),
		new Cmd(CmdType.KBD_MUTE,				new byte[]{(byte)CMD, (byte)0x4e}),
		new Cmd(CmdType.KBD_HIT,				new byte[]{(byte)CMD, (byte)0x4f}),
		
		//printer
		new Cmd(CmdType.PRN_SET_SPACES,			new byte[]{(byte)CMD, (byte)0x70}),
		new Cmd(CmdType.PRN_SET_INDENT,			new byte[]{(byte)CMD, (byte)0x71}),
		new Cmd(CmdType.PRN_SET_GRAY,			new byte[]{(byte)CMD, (byte)0x72}),
		new Cmd(CmdType.PRN_GET_STATUS,			new byte[]{(byte)CMD, (byte)0x73}),
		new Cmd(CmdType.PRN_STR,				new byte[]{(byte)CMD, (byte)0x74}),
		new Cmd(CmdType.PRN_IMAGE,				new byte[]{(byte)CMD, (byte)0x75}),
		new Cmd(CmdType.PRN_FEED,				new byte[]{(byte)CMD, (byte)0x76}),
		new Cmd(CmdType.PRN_RESET,				new byte[]{(byte)CMD, (byte)0x77}),
		new Cmd(CmdType.PRN_START,				new byte[]{(byte)CMD, (byte)0x78}),

		//EMV
		new Cmd(CmdType.EMV_GET_PARAM,						new byte[]{(byte)CMD, (byte)0x80}),
		new Cmd(CmdType.EMV_SET_PARAM,						new byte[]{(byte)CMD, (byte)0x81}),
		new Cmd(CmdType.EMV_GET_TLV_DATA,					new byte[]{(byte)CMD, (byte)0x82}),
		new Cmd(CmdType.EMV_SET_TLV_DATA,					new byte[]{(byte)CMD, (byte)0x83}),
		new Cmd(CmdType.EMV_GET_SCRIPT_RESULT,				new byte[]{(byte)CMD, (byte)0x84}),
		new Cmd(CmdType.EMV_SET_PCI_MODE_PARAM,				new byte[]{(byte)CMD, (byte)0x85}),
		new Cmd(CmdType.EMV_READ_VER_INFO,					new byte[]{(byte)CMD, (byte)0x86}),
		new Cmd(CmdType.EMV_CLEAR_TRANS_LOG,				new byte[]{(byte)CMD, (byte)0x87}),
		new Cmd(CmdType.EMV_ADD_ICC_TAG,					new byte[]{(byte)CMD, (byte)0x88}),
		new Cmd(CmdType.EMV_SET_SCRIPT_PROC_METHOD,			new byte[]{(byte)CMD, (byte)0x89}),
		new Cmd(CmdType.EMV_ADD_CAPK,						new byte[]{(byte)CMD, (byte)0x8a}),
		new Cmd(CmdType.EMV_DEL_CAPK,						new byte[]{(byte)CMD, (byte)0x8b}),
		new Cmd(CmdType.EMV_GET_CAPK,						new byte[]{(byte)CMD, (byte)0x8c}),
		new Cmd(CmdType.EMV_CHECK_CAPK,						new byte[]{(byte)CMD, (byte)0x8d}),
		new Cmd(CmdType.EMV_ADD_APP,						new byte[]{(byte)CMD, (byte)0x8e}),
		new Cmd(CmdType.EMV_GET_APP,						new byte[]{(byte)CMD, (byte)0x8f}),
		new Cmd(CmdType.EMV_DEL_APP,						new byte[]{(byte)CMD, (byte)0x90}),
		new Cmd(CmdType.EMV_GET_FINAL_APP_PARA,				new byte[]{(byte)CMD, (byte)0x91}),
		new Cmd(CmdType.EMV_MOD_FINAL_APP_PARA,				new byte[]{(byte)CMD, (byte)0x92}),
		new Cmd(CmdType.EMV_GET_LABEL_LIST,					new byte[]{(byte)CMD, (byte)0x93}),
		new Cmd(CmdType.EMV_ADD_REVOC_LIST,					new byte[]{(byte)CMD, (byte)0x94}),
		new Cmd(CmdType.EMV_DEL_REVOC_LIST,					new byte[]{(byte)CMD, (byte)0x95}),
		new Cmd(CmdType.EMV_DEL_ALL_REVOC_LIST,				new byte[]{(byte)CMD, (byte)0x96}),
		new Cmd(CmdType.EMV_INIT_TLV_DATA,					new byte[]{(byte)CMD, (byte)0x97}),
		new Cmd(CmdType.EMV_APP_SELECT,						new byte[]{(byte)CMD, (byte)0x98}),
		new Cmd(CmdType.EMV_READ_APP_DATA,					new byte[]{(byte)CMD, (byte)0x99}),
		new Cmd(CmdType.EMV_CARD_AUTH,						new byte[]{(byte)CMD, (byte)0x9a}),
		new Cmd(CmdType.EMV_PROC_TRANS,						new byte[]{(byte)CMD, (byte)0x9b}),
		new Cmd(CmdType.EMV_APP_SELECT_FOR_LOG,				new byte[]{(byte)CMD, (byte)0x9c}),
		new Cmd(CmdType.EMV_READ_LOG_RECORD,				new byte[]{(byte)CMD, (byte)0x9d}),
		new Cmd(CmdType.EMV_GET_LOG_ITEM,					new byte[]{(byte)CMD, (byte)0x9e}),
		new Cmd(CmdType.EMV_GET_MCK_PARAM,					new byte[]{(byte)CMD, (byte)0x9f}),
		new Cmd(CmdType.EMV_SET_MCK_PARAM,					new byte[]{(byte)CMD, (byte)0xa0}),
		new Cmd(CmdType.EMV_SET_TM_ECP_PARAM,				new byte[]{(byte)CMD, (byte)0xa1}),
		new Cmd(CmdType.EMV_GET_CARD_ECB_BALANCE,			new byte[]{(byte)CMD, (byte)0xa2}),
		new Cmd(CmdType.EMV_START_TRANS,					new byte[]{(byte)CMD, (byte)0xa3}),
		new Cmd(CmdType.EMV_COMPLETE_TRANS,					new byte[]{(byte)CMD, (byte)0xa4}),
		new Cmd(CmdType.EMV_SET_CONFIG_FLAG,				new byte[]{(byte)CMD, (byte)0xa5}),
		new Cmd(CmdType.EMV_SWITCH_CLSS,					new byte[]{(byte)CMD, (byte)0xa6}),
		new Cmd(CmdType.EMV_SET_AMOUNT,						new byte[]{(byte)CMD, (byte)0xa7}),
		new Cmd(CmdType.EMV_READ_SINGLE_LOAD_LOG,			new byte[]{(byte)CMD, (byte)0xa8}),
		new Cmd(CmdType.EMV_GET_SINGLE_LOAD_LOG_ITEM,		new byte[]{(byte)CMD, (byte)0xa9}),
		new Cmd(CmdType.EMV_READ_ALL_LOAD_LOGS,				new byte[]{(byte)CMD, (byte)0xaa}),
		
		new Cmd(CmdType.EMV_DEL_ALL_CAPK,					new byte[]{(byte)CMD, (byte)0xab}),
		new Cmd(CmdType.EMV_DEL_ALL_APP,					new byte[]{(byte)CMD, (byte)0xac}),

		new Cmd(CmdType.EMV_GET_LOG_DATA,					new byte[]{(byte)CMD, (byte)0xad}),

		//modem
		new Cmd(CmdType.MODEM_RESET,						new byte[]{(byte)CMD, (byte)0xb0}),
		new Cmd(CmdType.MODEM_DIAL,							new byte[]{(byte)CMD, (byte)0xb1}),
		new Cmd(CmdType.MODEM_CHECK,						new byte[]{(byte)CMD, (byte)0xb2}),
		new Cmd(CmdType.MODEM_TXD,							new byte[]{(byte)CMD, (byte)0xb3}),
		new Cmd(CmdType.MODEM_RXD,							new byte[]{(byte)CMD, (byte)0xb4}),
		new Cmd(CmdType.MODEM_ASYNC_GET,					new byte[]{(byte)CMD, (byte)0xb5}),
		new Cmd(CmdType.MODEM_ONHOOK,						new byte[]{(byte)CMD, (byte)0xb6}),
		new Cmd(CmdType.MODEM_HANGOFF,						new byte[]{(byte)CMD, (byte)0xb7}),
		new Cmd(CmdType.MODEM_EX_COMMAND,					new byte[]{(byte)CMD, (byte)0xb8}),
		new Cmd(CmdType.MODEM_PPP_LOGIN,					new byte[]{(byte)CMD, (byte)0xb9}),
		new Cmd(CmdType.MODEM_PPP_LOGOUT,					new byte[]{(byte)CMD, (byte)0xba}),
		new Cmd(CmdType.MODEM_PPP_CHECK,					new byte[]{(byte)CMD, (byte)0xbb}),
		
		//clss
		new Cmd(CmdType.CLSS_ENTRY_READ_VER_INFO,			new byte[]{(byte)CMD, (byte)0xc0}),
		new Cmd(CmdType.CLSS_ENTRY_ADD_AID_LIST,			new byte[]{(byte)CMD, (byte)0xc1}),
		new Cmd(CmdType.CLSS_ENTRY_DEL_AID_LIST,			new byte[]{(byte)CMD, (byte)0xc2}),
		new Cmd(CmdType.CLSS_ENTRY_DEL_ALL_AID_LIST,		new byte[]{(byte)CMD, (byte)0xc3}),
		new Cmd(CmdType.CLSS_ENTRY_SET_PRE_PROC_INFO,		new byte[]{(byte)CMD, (byte)0xc4}),
		new Cmd(CmdType.CLSS_ENTRY_DEL_PRE_PROC_INFO,		new byte[]{(byte)CMD, (byte)0xc5}),
		new Cmd(CmdType.CLSS_ENTRY_DEL_ALL_PRE_PROC_INFO,	new byte[]{(byte)CMD, (byte)0xc6}),
		new Cmd(CmdType.CLSS_ENTRY_PRE_TRANS_PROC,			new byte[]{(byte)CMD, (byte)0xc7}),
		new Cmd(CmdType.CLSS_ENTRY_APPSLT,					new byte[]{(byte)CMD, (byte)0xc8}),
		new Cmd(CmdType.CLSS_ENTRY_FINAL_SELECT,			new byte[]{(byte)CMD, (byte)0xc9}),
		new Cmd(CmdType.CLSS_ENTRY_DEL_CUR_CAND_APP,		new byte[]{(byte)CMD, (byte)0xca}),
		new Cmd(CmdType.CLSS_ENTRY_GET_PRE_PROC_INTER_FLG,	new byte[]{(byte)CMD, (byte)0xcb}),
		new Cmd(CmdType.CLSS_ENTRY_GET_FINAL_SELECT_DATA,	new byte[]{(byte)CMD, (byte)0xcc}),
		new Cmd(CmdType.CLSS_ENTRY_APP_SELECT_UNLOCK_APP,	new byte[]{(byte)CMD, (byte)0xcd}),
		new Cmd(CmdType.CLSS_ENTRY_GET_ERROR_CODE,			new byte[]{(byte)CMD, (byte)0xce}),
		new Cmd(CmdType.CLSS_ENTRY_SET_MC_VERSION,			new byte[]{(byte)CMD, (byte)0xcf}),
		
		new Cmd(CmdType.CLSS_PBOC_READ_VER_INFO,			new byte[]{(byte)CMD, (byte)0xe0}),
		new Cmd(CmdType.CLSS_PBOC_GET_TLV_DATA,				new byte[]{(byte)CMD, (byte)0xe1}),
		new Cmd(CmdType.CLSS_PBOC_SET_TLV_DATA,				new byte[]{(byte)CMD, (byte)0xe2}),
		new Cmd(CmdType.CLSS_PBOC_SET_EMV_UNKNOWN_TLV_DATA,	new byte[]{(byte)CMD, (byte)0xe3}),
		new Cmd(CmdType.CLSS_PBOC_GET_READER_PARAM,			new byte[]{(byte)CMD, (byte)0xe4}),
		new Cmd(CmdType.CLSS_PBOC_SET_READER_PARAM,			new byte[]{(byte)CMD, (byte)0xe5}),
		new Cmd(CmdType.CLSS_PBOC_SET_AID_PARAM,			new byte[]{(byte)CMD, (byte)0xe6}),
		new Cmd(CmdType.CLSS_PBOC_ADD_CAPK,					new byte[]{(byte)CMD, (byte)0xe7}),
		new Cmd(CmdType.CLSS_PBOC_DEL_CAPK,					new byte[]{(byte)CMD, (byte)0xe8}),
		new Cmd(CmdType.CLSS_PBOC_GET_CAPK,					new byte[]{(byte)CMD, (byte)0xe9}),
		new Cmd(CmdType.CLSS_PBOC_DEL_ALL_CAPK,				new byte[]{(byte)CMD, (byte)0xea}),
		new Cmd(CmdType.CLSS_PBOC_ADD_REVOC_LIST,			new byte[]{(byte)CMD, (byte)0xeb}),
		new Cmd(CmdType.CLSS_PBOC_DEL_REVOC_LIST,			new byte[]{(byte)CMD, (byte)0xec}),
		new Cmd(CmdType.CLSS_PBOC_DEL_ALL_REVOC_LIST,		new byte[]{(byte)CMD, (byte)0xed}),
		new Cmd(CmdType.CLSS_PBOC_SET_FINAL_SELECT_DATA,	new byte[]{(byte)CMD, (byte)0xee}),
		new Cmd(CmdType.CLSS_PBOC_SET_TRANS_DATA,			new byte[]{(byte)CMD, (byte)0xef}),
		new Cmd(CmdType.CLSS_PBOC_PROC_TRANS,				new byte[]{(byte)CMD, (byte)0xf0}),
		new Cmd(CmdType.CLSS_PBOC_CARD_AUTH,				new byte[]{(byte)CMD, (byte)0xf1}),
		new Cmd(CmdType.CLSS_PBOC_GET_CVM_TYPE,				new byte[]{(byte)CMD, (byte)0xf2}),
		new Cmd(CmdType.CLSS_PBOC_GET_TRACK1_MAP_DATA,		new byte[]{(byte)CMD, (byte)0xf3}),
		new Cmd(CmdType.CLSS_PBOC_GET_TRACK2_MAP_DATA,		new byte[]{(byte)CMD, (byte)0xf4}),
		new Cmd(CmdType.CLSS_PBOC_GET_GPO_DATA,				new byte[]{(byte)CMD, (byte)0xf5}),
		new Cmd(CmdType.CLSS_PBOC_RESEND_LAST_CMD,			new byte[]{(byte)CMD, (byte)0xf6}),
		new Cmd(CmdType.CLSS_PBOC_GET_DATA_CMD,				new byte[]{(byte)CMD, (byte)0xf7}),
		new Cmd(CmdType.CLSS_PBOC_PROC_TRANS_UNLOCK_APP,	new byte[]{(byte)CMD, (byte)0xf8}),
		
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_CAPK,		new byte[]{(byte)CMD1, (byte)0x30}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_APP,		new byte[]{(byte)CMD1, (byte)0x31}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_VISA_READER_PARAM,	new byte[]{(byte)CMD1, (byte)0x32}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_VISA_AID_PARAM,		new byte[]{(byte)CMD1, (byte)0x33}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_READER_PARAM,	new byte[]{(byte)CMD1, (byte)0x34}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_AID_PARAM,		new byte[]{(byte)CMD1, (byte)0x35}),
		new Cmd(CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_TERM_CONFIG,		new byte[]{(byte)CMD1, (byte)0x36}),
		new Cmd(CmdType.CLSS_INTEGRATE_DELETE_ALL_CAPK,		new byte[]{(byte)CMD1, (byte)0x37}),
		new Cmd(CmdType.CLSS_INTEGRATE_DELETE_ONE_CAPK,		new byte[]{(byte)CMD1, (byte)0x38}),
		new Cmd(CmdType.CLSS_INTEGRATE_DELETE_ALL_AID,		new byte[]{(byte)CMD1, (byte)0x39}),
		new Cmd(CmdType.CLSS_INTEGRATE_DELETE_ONE_AID,		new byte[]{(byte)CMD1, (byte)0x3A}),
		new Cmd(CmdType.CLSS_INTEGRATE_TRANS_INIT,			new byte[]{(byte)CMD1, (byte)0x3B}),
		new Cmd(CmdType.CLSS_INTEGRATE_TRANS_CANCEL,		new byte[]{(byte)CMD1, (byte)0x3C}),
		new Cmd(CmdType.CLSS_INTEGRATE_WAIT_CARD_STATUS,	new byte[]{(byte)CMD1, (byte)0x3D}),
		new Cmd(CmdType.CLSS_INTEGRATE_TRANS_START,			new byte[]{(byte)CMD1, (byte)0x3E}),
		new Cmd(CmdType.CLSS_INTEGRATE_TRASN_WAVE_FINISH,	new byte[]{(byte)CMD1, (byte)0x3F}),
		
		//net
		new Cmd(CmdType.NET_SOCKET,							new byte[]{(byte)CMD1, (byte)0x00}),
		new Cmd(CmdType.NET_CONNECT,						new byte[]{(byte)CMD1, (byte)0x01}),
		new Cmd(CmdType.NET_SEND,							new byte[]{(byte)CMD1, (byte)0x02}),
		new Cmd(CmdType.NET_SENDTO,							new byte[]{(byte)CMD1, (byte)0x03}),
		new Cmd(CmdType.NET_RECV,							new byte[]{(byte)CMD1, (byte)0x04}),
		new Cmd(CmdType.NET_RECVFROM,						new byte[]{(byte)CMD1, (byte)0x05}),
		new Cmd(CmdType.NET_CLOSE_SOCKET,					new byte[]{(byte)CMD1, (byte)0x06}),
		new Cmd(CmdType.NET_IOCTL,							new byte[]{(byte)CMD1, (byte)0x07}),
		new Cmd(CmdType.NET_DEVGET,							new byte[]{(byte)CMD1, (byte)0x08}),
		new Cmd(CmdType.NET_DNS_RESOLVE,					new byte[]{(byte)CMD1, (byte)0x09}),
		new Cmd(CmdType.NET_PING,							new byte[]{(byte)CMD1, (byte)0x0a}),
		
		//P2PE related, can be in diff Manager
		new Cmd(CmdType.CMD_EXCHANGE_DATA,					new byte[]{(byte)CMD1, (byte)0x10}),	// in base system manager
		new Cmd(CmdType.CMD_CALC_CMAC,						new byte[]{(byte)CMD1, (byte)0x11}),	// in base system manager

/*
		// terminal key enc/dec
		new Cmd(CmdType.TERMINAL_KEY_ENC,					new byte[]{(byte)CMD1, (byte)0x20}),
		new Cmd(CmdType.TERMINAL_KEY_DEC,					new byte[]{(byte)CMD1, (byte)0x21}),
*/
		
		
		//emv callbacks
		new Cmd(CmdType.EMV_CALLBACK_WAIT_APP_SEL,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb0}),
		new Cmd(CmdType.EMV_CALLBACK_INPUT_AMOUNT,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb1}),
		new Cmd(CmdType.EMV_CALLBACK_GET_HOLDER_PWD,		new byte[]{(byte)CMD_PASSIVE, (byte)0xb2}),
		new Cmd(CmdType.EMV_CALLBACK_REFER_PROC,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb3}),
		new Cmd(CmdType.EMV_CALLBACK_ONLINE_PROC,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb4}),
		new Cmd(CmdType.EMV_CALLBACK_ADVICE_PROC,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb5}),
		new Cmd(CmdType.EMV_CALLBACK_VERIFY_PIN_OK,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb6}),
		new Cmd(CmdType.EMV_CALLBACK_UNKNOWN_TLV_DATA,		new byte[]{(byte)CMD_PASSIVE, (byte)0xb7}),
		new Cmd(CmdType.EMV_CALLBACK_CERT_VERIFY,			new byte[]{(byte)CMD_PASSIVE, (byte)0xb8}),
		new Cmd(CmdType.EMV_CALLBACK_SET_PARAM,				new byte[]{(byte)CMD_PASSIVE, (byte)0xb9}),		
		
		new Cmd(CmdType.EMV_CALLBACK_PICC_ISO_COMMAND,		new byte[]{(byte)CMD_PASSIVE, (byte)0xba}),		
		new Cmd(CmdType.EMV_CALLBACK_ICC_ISO_COMMAND,		new byte[]{(byte)CMD_PASSIVE, (byte)0xbb}),		
		new Cmd(CmdType.EMV_CALLBACK_PED_VERIFY_PLAIN_PIN,	new byte[]{(byte)CMD_PASSIVE, (byte)0xbc}),		
		new Cmd(CmdType.EMV_CALLBACK_PED_VERIFY_CIPHER_PIN,	new byte[]{(byte)CMD_PASSIVE, (byte)0xbd}),		
		new Cmd(CmdType.EMV_CALLBACK_SM2_VERIFY,			new byte[]{(byte)CMD_PASSIVE, (byte)0xbe}),		
		new Cmd(CmdType.EMV_CALLBACK_SM3,					new byte[]{(byte)CMD_PASSIVE, (byte)0xbf}),		
		
		new Cmd(CmdType.EMV_CALLBACK_CAND_APP_SEL,			new byte[]{(byte)CMD_PASSIVE, (byte)0xc0}),
		
		//jason moto command
		new Cmd(CmdType.MTLA_OPEN, 					new byte[]{(byte)CMD2, (byte)0x01}),
		new Cmd(CmdType.MTLA_CLOSE, 				new byte[]{(byte)CMD2, (byte)0x02}),
		new Cmd(CmdType.MTLA_MENU, 					new byte[]{(byte)CMD2, (byte)0x03}),
		new Cmd(CmdType.MTLA_MESSAGE, 				new byte[]{(byte)CMD2, (byte)0x04}),
		new Cmd(CmdType.MTLA_ABORT, 				new byte[]{(byte)CMD2, (byte)0x05}),
		new Cmd(CmdType.MTLA_SETSESSION_KEY, 		new byte[]{(byte)CMD2, (byte)0x14}),
		new Cmd(CmdType.MTLA_READ_CARDDATA, 		new byte[]{(byte)CMD2, (byte)0x06}),
		new Cmd(CmdType.MTLA_ENABLE_KEY, 			new byte[]{(byte)CMD2, (byte)0x07}),
		new Cmd(CmdType.MTLA_DISABLE_KEY, 			new byte[]{(byte)CMD2, (byte)0x08}),
		new Cmd(CmdType.MTLA_GETPIN, 				new byte[]{(byte)CMD2, (byte)0x09}),
		new Cmd(CmdType.MTLA_GETEMVTAG,             new byte[]{(byte)CMD2, (byte)0x0A}),
		new Cmd(CmdType.MTLA_SETEMVTAG,         	new byte[]{(byte)CMD2, (byte)0x0B}),
		new Cmd(CmdType.MTLA_GETBATTERY_LEVEL,      new byte[]{(byte)CMD2, (byte)0x11}),
		new Cmd(CmdType.MTLA_GETBATTERY_THRESHOLD,  new byte[]{(byte)CMD2, (byte)0x12}),
		new Cmd(CmdType.MTLA_SETBATTERY_THRESHOLE,  new byte[]{(byte)CMD2, (byte)0x13}),
		new Cmd(CmdType.MTLA_CREATMAC,         	    new byte[]{(byte)CMD2, (byte)0x0C}),
		new Cmd(CmdType.MTLA_VALIDATE_MAC,         	new byte[]{(byte)CMD2, (byte)0x0D}),
		new Cmd(CmdType.MTLA_EMVOLINE_FINISH,       new byte[]{(byte)CMD2, (byte)0x0E}),
		new Cmd(CmdType.MTLA_AUTHORIZE_CARD,        new byte[]{(byte)CMD2, (byte)0x0F}),
		new Cmd(CmdType.MTLA_OTHERINFO,         	new byte[]{(byte)CMD2, (byte)0x10}),
		new Cmd(CmdType.MTLA_REMOVECARD,         	new byte[]{(byte)CMD2, (byte)0x15}),
		new Cmd(CmdType.MTLA_SETPARAMETER,         	new byte[]{(byte)CMD2, (byte)0x16}),
		new Cmd(CmdType.MTLA_GETPARAMETER,         	new byte[]{(byte)CMD2, (byte)0x17}),
		new Cmd(CmdType.MTLA_DOWNLOAD_APP,         	new byte[]{(byte)CMD2, (byte)0x1F}),	
		new Cmd(CmdType.MTLA_DELETE_ALL_APP,        new byte[]{(byte)CMD2, (byte)0x19}),	
		new Cmd(CmdType.MTLA_DOWNLOAD_CAPK,         new byte[]{(byte)CMD2, (byte)0x1A}),	
		new Cmd(CmdType.MTLA_DELETE_ALL_CAPK,       new byte[]{(byte)CMD2, (byte)0x1B}),	
		new Cmd(CmdType.MTLA_GET_EMV_PARAMETER,     new byte[]{(byte)CMD2, (byte)0x1C}),	
		new Cmd(CmdType.MTLA_DOWNLOAD_EMV_PARAM,    new byte[]{(byte)CMD2, (byte)0x1D}),
		new Cmd(CmdType.MTLA_DOWNLOAD_FILE,   		new byte[]{(byte)CMD2, (byte)0x18}),
		new Cmd(CmdType.MTLA_DOWNLOAD_RSAKEY,       new byte[]{(byte)CMD2, (byte)0x19}),
		new Cmd(CmdType.MTLA_RSAKEY_TEST,           new byte[]{(byte)CMD2, (byte)0x21}),
		new Cmd(CmdType.MTLA_GET_ENCRYPTED_DATA,    new byte[]{(byte)CMD2, (byte)0x1A}),
		new Cmd(CmdType.MTLA_GET_DATE_TIME,   		new byte[]{(byte)CMD2, (byte)0x1B}),
		new Cmd(CmdType.MTLA_SET_DATE_TIME,   	    new byte[]{(byte)CMD2, (byte)0x1C}),
		new Cmd(CmdType.MTLA_GET_STATUS_UPDATE,     new byte[]{(byte)CMD2, (byte)0x1D}),
		new Cmd(CmdType.MTLA_READ_CARD_DATA,        new byte[]{(byte)CMD2, (byte)0x1E}),
		
	};
	
	public static byte[] getCmdCode(CmdType type){
		for (int i = 0; i < cmds.length; i++){
			if ((cmds[i]).type.equals(type)){
				return cmds[i].code;
			}
		}
		return null;
	}
	
	public static CmdType getCmdType(byte[] code) {
		for (int i = 0; i < cmds.length; i++){
			if (Utils.cmpByteArray(cmds[i].code, 0, code, 0, 2)) {
				return cmds[i].type;
			}
		}
		return null;
	}

	public static boolean isCmdPassive(byte[] code) {
		return (code[0] == (byte)CMD_PASSIVE); 
	}

	public static boolean mayRecvPassiveCmd(byte[] code) {
		return ((code[0] != (byte)CMD_PASSIVE) && (code[1] >= (byte)0x80) && (code[1] <= (byte)0xf8));
	}
	
}
