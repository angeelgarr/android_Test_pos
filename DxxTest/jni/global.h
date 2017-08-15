#ifndef  _PAX_MPOS_GLOBAL_H_
#define _PAX_MPOS_GLOBAL_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "common.h"
#include "emvlib.h"
#include "util.h"


//#define FILE_EMV_PARAM		"EMVPARA"
#define FILE_EMV_PARAM_TMP	"mnt/sdcard/EMVPARA_TMP.txt"

#define TAG_EMV_CLEAR		0x10
#define TAG_EMV_APP			0x11
#define TAG_EMV_CAPK		0x12
#define TAG_EMV_Parameter	0x13

#define MPOS_EMV_PARAM_TLV_LEN	2
#define MPOS_EMV_TL_LEN			(1 + 2)

#define EMV_APP_MAX_NUM		99
#define EMV_CAPK_MAX_NUM	64

#define FILE_INIT			0x00
#define FILE_READ_TLV		0x01
#define FILE_ADD_TLV		0x02
#define FILE_DEL_TLV		0x03

#define MPOS_BUF_MAX_SIZE		(1024 * 100)
#define MPOS_EMV_TLV_MAX_SIZE	1024 * 2

#define SIGN_BYTE "B"
#define SIGN_BYTE_ARRAY "[B"
#define SIGN_INT "I"
#define SIGN_LONG "J"


#define MAX(a, b)       ( (a)>=(b) ? (a) : (b) )
#define MIN(a, b)       ( (a)<=(b) ? (a) : (b) )

// return code
#if 0
enum enCodeReturn {
	MPOS_OK,

	// FILE
	MPOS_FILE_ERR = -200,
	MPOS_FILE_NOT_EXIST,
	MPOS_FILE_OPEN_ERR,
	MPOS_FILE_SEEK_ERR,
	MPOS_FILE_SEEK_TYPE_ERR,
	MPOS_FILE_TAG_TYPE_ERR,
	MPOS_FILE_DATA_INVALID,
	MPOS_FILE_READ_ERR,
	MPOS_FILE_WRITE_ERR,

	// EMV
	MPOS_EMV_ERR = -300,
	MPOS_EMV_PARAM_ERR,
	MPOS_EMV_NOT_FOUND,
	
	// MEMORY
	MPOS_MALLOC_ERR = -400,

	// PARAMETER
	MPOS_PARAM_INVALID = -500,
};
#else
enum enCodeReturn {
	EMV_OK,

	EMV_FILE_NOT_EXIST = -200,
	EMV_FILE_ERR,
	EMV_FILE_INIT_ERR,
	EMV_OVERFLOW,
	EMV_PARAM_ERR,
};
#endif

typedef struct _tagEMV_Param
{
	unsigned char	ucInitFlag;		// 0-struct not initialized, 1-struct initialized
	//int				iSkipTLV = 1;	// first TLV is 0x10 0x00
	unsigned char	ucSkipTLV[2];
	int				iAppNum;
	EMV_APPLIST		stEMVAppList[EMV_APP_MAX_NUM];
	int				iCAPKNum;
	EMV_CAPK		stEMVCAPK[EMV_CAPK_MAX_NUM];
	int				iParaExist;		// 0-not exist, 1-exist
	EMV_PARAM		stEMVParameter;
}EMV_Param;

EMV_Param	glEMV_Param;
static unsigned char sgEMV_PARA_FilePath[128];

#ifndef NULL
#ifdef __cplusplus
	#define NULL    0
#else
	#define NULL    ((void *)0)
#endif // __cplusplus
#endif // NULL

#endif
