/*****************************************************/
/* Emvlib.h                                          */
/* Define the Application Program Interface          */
/* of EMV2 for all PAX POS terminals     		     */
/* Created by ZengYun at 20/01/2005                  */
/*****************************************************/

#ifndef _EMV_LIB_H_
#define _EMV_LIB_H_

#define MAX_APP_NUM       99         //EMV库应用列表最多可存储的应用数
#define MAX_APP_ITEMS     17         //由16修改为17  modify by nt 20091218
#define MAX_KEY_NUM       64         //EMV库认证中心公钥表最多可存储的公钥数

//交易处理函数返回码定义
//#define EMV_OK             0         //成功

//回调函数或其他函数返回码定义
//#define EMV_SUM_ERR       -15        //认证中心密钥校验和错误
//#define EMV_NOT_FOUND     -16        //没有找到指定的数据或元素
//#define EMV_NO_DATA       -17        //指定的数据元素没有数据
//#define EMV_OVERFLOW      -18        //内存溢出
//#define EMV_PARAM_ERR     -30        // 20081008 liuxl

#ifdef WIN32
#pragma warning(disable:4103)
#pragma pack(4)//设定为1字节对齐
// #else
// 	__attribute__((__packed__))
#endif


typedef struct{
	unsigned char AppName[33];       //本地应用名，以'\x00'结尾的字符串
	unsigned char AID[17];           //应用标志
	unsigned char AidLen;            //AID的长度
    unsigned char SelFlag;           //选择标志(PART_MATCH 部分匹配  FULL_MATCH 全匹配)      
	unsigned char Priority;          //优先级标志
	unsigned char TargetPer;         //目标百分比数
	unsigned char MaxTargetPer;      //最大目标百分比数
	unsigned char FloorLimitCheck;   //是否检查最低限额
	unsigned char RandTransSel;      //是否进行随机交易选择
	unsigned char VelocityCheck;     //是否进行频度检测
    unsigned long FloorLimit;        //最低限额
	unsigned long Threshold;         //阀值
	unsigned char TACDenial[6];      //终端行为代码(拒绝)
	unsigned char TACOnline[6];      //终端行为代码(联机)
	unsigned char TACDefault[6];     //终端行为代码(缺省)
    unsigned char AcquierId[6];      //收单行标志
	unsigned char dDOL[256];         //终端缺省DDOL
	unsigned char tDOL[256];         //终端缺省TDOL
	unsigned char Version[3];        //应用版本
	unsigned char RiskManData[10];   //风险管理数据
}EMV_APPLIST;
typedef struct {

	unsigned char RID[5];            //应用注册服务商ID
	unsigned char KeyID;             //密钥索引
	unsigned char HashInd;           //HASH算法标志
	unsigned char ArithInd;          //RSA算法标志
	unsigned char ModulLen;          //模长度
	unsigned char Modul[248];        //模
	unsigned char ExponentLen;       //指数长度
	unsigned char Exponent[3];       //指数
	unsigned char ExpDate[3];        //有效期(YYMMDD)
	unsigned char CheckSum[20];      //密钥校验和
}EMV_CAPK;

typedef struct{
	unsigned char MerchName[256];    //商户名
	unsigned char MerchCateCode[2];  //商户类别码(没要求可不设置)
	unsigned char MerchId[15];       //商户标志(商户号)
	unsigned char TermId[8];         //终端标志(POS号)
	unsigned char TerminalType;      //终端类型
	unsigned char Capability[3];     //终端性能
	unsigned char ExCapability[5];   //终端扩展性能
	unsigned char TransCurrExp;      //交易货币代码指数
	unsigned char ReferCurrExp;      //参考货币指数
	unsigned char ReferCurrCode[2];  //参考货币代码
	unsigned char CountryCode[2];    //终端国家代码
	unsigned char TransCurrCode[2];  //交易货币代码
	unsigned long ReferCurrCon;      //参考货币代码和交易代码的转换系数(交易货币对参考货币的汇率*1000)
	unsigned char TransType;         //当前交易类型(EMV_CASH EMV_GOODS EMV_SERVICE EMV_GOODS&EMV_CASHBACK EMV_SERVICE&EMV_CASHBACK)
	unsigned char ForceOnline;       //商户强制联机(1 表示总是联机交易)
	unsigned char GetDataPIN;        //密码检测前是否读重试次数
	unsigned char SurportPSESel;     //是否支持PSE选择方式
}EMV_PARAM;



#endif
