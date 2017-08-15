/*****************************************************/
/* Emvlib.h                                          */
/* Define the Application Program Interface          */
/* of EMV2 for all PAX POS terminals     		     */
/* Created by ZengYun at 20/01/2005                  */
/*****************************************************/

#ifndef _EMV_LIB_H_
#define _EMV_LIB_H_

#define MAX_APP_NUM       99         //EMV��Ӧ���б����ɴ洢��Ӧ����
#define MAX_APP_ITEMS     17         //��16�޸�Ϊ17  modify by nt 20091218
#define MAX_KEY_NUM       64         //EMV����֤���Ĺ�Կ�����ɴ洢�Ĺ�Կ��

//���״����������붨��
//#define EMV_OK             0         //�ɹ�

//�ص��������������������붨��
//#define EMV_SUM_ERR       -15        //��֤������ԿУ��ʹ���
//#define EMV_NOT_FOUND     -16        //û���ҵ�ָ�������ݻ�Ԫ��
//#define EMV_NO_DATA       -17        //ָ��������Ԫ��û������
//#define EMV_OVERFLOW      -18        //�ڴ����
//#define EMV_PARAM_ERR     -30        // 20081008 liuxl

#ifdef WIN32
#pragma warning(disable:4103)
#pragma pack(4)//�趨Ϊ1�ֽڶ���
// #else
// 	__attribute__((__packed__))
#endif


typedef struct{
	unsigned char AppName[33];       //����Ӧ��������'\x00'��β���ַ���
	unsigned char AID[17];           //Ӧ�ñ�־
	unsigned char AidLen;            //AID�ĳ���
    unsigned char SelFlag;           //ѡ���־(PART_MATCH ����ƥ��  FULL_MATCH ȫƥ��)      
	unsigned char Priority;          //���ȼ���־
	unsigned char TargetPer;         //Ŀ��ٷֱ���
	unsigned char MaxTargetPer;      //���Ŀ��ٷֱ���
	unsigned char FloorLimitCheck;   //�Ƿ�������޶�
	unsigned char RandTransSel;      //�Ƿ�����������ѡ��
	unsigned char VelocityCheck;     //�Ƿ����Ƶ�ȼ��
    unsigned long FloorLimit;        //����޶�
	unsigned long Threshold;         //��ֵ
	unsigned char TACDenial[6];      //�ն���Ϊ����(�ܾ�)
	unsigned char TACOnline[6];      //�ն���Ϊ����(����)
	unsigned char TACDefault[6];     //�ն���Ϊ����(ȱʡ)
    unsigned char AcquierId[6];      //�յ��б�־
	unsigned char dDOL[256];         //�ն�ȱʡDDOL
	unsigned char tDOL[256];         //�ն�ȱʡTDOL
	unsigned char Version[3];        //Ӧ�ð汾
	unsigned char RiskManData[10];   //���չ�������
}EMV_APPLIST;
typedef struct {

	unsigned char RID[5];            //Ӧ��ע�������ID
	unsigned char KeyID;             //��Կ����
	unsigned char HashInd;           //HASH�㷨��־
	unsigned char ArithInd;          //RSA�㷨��־
	unsigned char ModulLen;          //ģ����
	unsigned char Modul[248];        //ģ
	unsigned char ExponentLen;       //ָ������
	unsigned char Exponent[3];       //ָ��
	unsigned char ExpDate[3];        //��Ч��(YYMMDD)
	unsigned char CheckSum[20];      //��ԿУ���
}EMV_CAPK;

typedef struct{
	unsigned char MerchName[256];    //�̻���
	unsigned char MerchCateCode[2];  //�̻������(ûҪ��ɲ�����)
	unsigned char MerchId[15];       //�̻���־(�̻���)
	unsigned char TermId[8];         //�ն˱�־(POS��)
	unsigned char TerminalType;      //�ն�����
	unsigned char Capability[3];     //�ն�����
	unsigned char ExCapability[5];   //�ն���չ����
	unsigned char TransCurrExp;      //���׻��Ҵ���ָ��
	unsigned char ReferCurrExp;      //�ο�����ָ��
	unsigned char ReferCurrCode[2];  //�ο����Ҵ���
	unsigned char CountryCode[2];    //�ն˹��Ҵ���
	unsigned char TransCurrCode[2];  //���׻��Ҵ���
	unsigned long ReferCurrCon;      //�ο����Ҵ���ͽ��״����ת��ϵ��(���׻��ҶԲο����ҵĻ���*1000)
	unsigned char TransType;         //��ǰ��������(EMV_CASH EMV_GOODS EMV_SERVICE EMV_GOODS&EMV_CASHBACK EMV_SERVICE&EMV_CASHBACK)
	unsigned char ForceOnline;       //�̻�ǿ������(1 ��ʾ������������)
	unsigned char GetDataPIN;        //������ǰ�Ƿ�����Դ���
	unsigned char SurportPSESel;     //�Ƿ�֧��PSEѡ��ʽ
}EMV_PARAM;



#endif
