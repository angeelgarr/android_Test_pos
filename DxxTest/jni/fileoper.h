#ifndef _PAX_MPOS_FILEOPER_H
#define _PAX_MPOS_FILEOPER_H

#include "Global.h"

int EMVParamInit(unsigned char *szFilePath);
//int EMVInitializeParameter (void);
int EMVInitializeParameter(unsigned char *szFilePath);
int EMVAddAIDParameter(EMV_APPLIST stEMVAppList);
int EMVGetAIDParameter(int iAIDNo, EMV_APPLIST *stEMVAppList);
//int EMVGetTotalAIDNumber(void);
int EMVGetTotalAIDNumber(int *iAidNum);
int EMVDeleteAIDParameter(int iAIDNo);

int EMVSetParameter(EMV_PARAM stEMV_PARAM);	// ´ýÐÞ¸Ä
int EMVGetParameter(EMV_PARAM *stEMV_PARAM);	

int EMVAddCAPK(EMV_CAPK stEMV_CAPK);
//int EMVGetTotalCAPKNumber(void);
int EMVGetTotalCAPKNumber(int *iCAPKNum);
int EMVGetCAPK(int iCAPKNo, EMV_CAPK *stEMV_CAPK);
int EMVDelCAPK(int iCAPKNo);

void BuildTLVString(unsigned char ucTag, unsigned char *ucData, unsigned int uiLen, unsigned char **ucTLVStr, unsigned int *uiTLVLen);
//int PubFileWrite(unsigned char *szFileName, long lngOffset, void *psData, int iDataLen);

int PubFileWriteDelete(unsigned char *szFileName, long lngOffStart, long lngOffEnd);
int PubFileWriteUpdate(unsigned char *szFileName, long lngOffset, void *psData, int iDataLen);
int PubFileWriteAdd(unsigned char *szFileName, long lngOffset, void *psData, int iDataLen);

#endif
