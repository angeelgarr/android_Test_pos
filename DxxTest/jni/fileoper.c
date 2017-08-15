#include "Fileoper.h"

/*
 * 
 */
void EMVParamReset(void)
{
	memset(&glEMV_Param, 0, sizeof(EMV_Param));
	glEMV_Param.ucSkipTLV[0] = 0x10;
	glEMV_Param.ucSkipTLV[1] = 0x00;;
}

///
/// Check whether the file exist
/// return value: 0-not exist, 1-exist
///
int CheckFileExist(unsigned char *szFileName)
{
	int		iRet = 0;
	FILE	*fp = NULL;

	fp = fopen(szFileName, "rb");
	if(fp != NULL)
	{
		iRet = 1;

		fclose(fp);
		fp = NULL;
	}

	return iRet;
}

/*
 * init EMV_Param struct
 */
int EMVParamInit(unsigned char *szFileName)
{
	int		iRet = 0, iReadBytes = 0, iStartBytes = 0;
	FILE 	*fp = NULL;
	unsigned char	ucTag;
	unsigned char	ucLen[MPOS_EMV_PARAM_TLV_LEN];
	unsigned char	ucVal[1024];
	unsigned int	uiLen = 0;

	if(NULL == szFileName)
	{
#ifdef PAX_MPOS_LOG
		LOGE("filename is null");
#endif
		return EMV_FILE_NOT_EXIST;
	}

	EMVParamReset();

	//glEMV_Param
	fp = fopen(szFileName, "rb");
	if( NULL == fp )
	{
#ifdef PAX_MPOS_LOG
		LOGE("file open error: [%s], errno:[%d]", szFileName);
#endif
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_NOT_EXIST;
	}

	iStartBytes = sizeof(glEMV_Param.ucSkipTLV);
	iRet = fseek(fp, (long)iStartBytes, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		return EMV_FILE_ERR;
	}

	while(NULL != fp && !feof(fp))
	{
		// tag
		ucTag = '\0';
		iReadBytes = fread(&ucTag, sizeof(unsigned char), 1, fp);
		if(NULL == fp || feof(fp))
		{
			break;
		}

		// length
		memset(ucLen, 0, MPOS_EMV_PARAM_TLV_LEN);
		iReadBytes = fread(&ucLen, sizeof(unsigned char), MPOS_EMV_PARAM_TLV_LEN, fp);
		if(NULL == fp || feof(fp))
		{
			fclose(fp);
			fp = NULL;
			//return MPOS_FILE_DATA_INVALID;
			//return EMV_FILE_INIT_ERR;
			return EMV_PARAM_ERR;
		}
		uiLen = ucLen[0]*256+ucLen[1];
		if(uiLen > sizeof(EMV_APPLIST)
			&& uiLen > sizeof(EMV_CAPK)
			&& uiLen > sizeof(EMV_PARAM))
		{
			fclose(fp);
			fp = NULL;
			//return MPOS_FILE_DATA_INVALID;
			//return EMV_FILE_INIT_ERR;
			return EMV_PARAM_ERR;
		}

		// value
		memset(ucVal, 0, sizeof(ucVal));
		iReadBytes = fread(ucVal, sizeof(unsigned char), uiLen, fp);
		if(NULL == fp || feof(fp))
		{
			fclose(fp);
			fp = NULL;
			//return MPOS_FILE_DATA_INVALID;
			//return EMV_FILE_INIT_ERR;
			return EMV_PARAM_ERR;
		}

		if(iReadBytes != uiLen)
		{
			fclose(fp);
			fp = NULL;
			//return MPOS_FILE_READ_ERR;
			return EMV_PARAM_ERR;
		}

		switch(ucTag)
		{
		case TAG_EMV_CLEAR:
			memcpy(&glEMV_Param.ucSkipTLV, ucVal, uiLen);
			break;

		case TAG_EMV_APP:
			memcpy(&glEMV_Param.stEMVAppList[glEMV_Param.iAppNum], ucVal, uiLen);
			glEMV_Param.iAppNum ++;
			break;

		case TAG_EMV_CAPK:
			memcpy(&glEMV_Param.stEMVCAPK[glEMV_Param.iCAPKNum], ucVal, uiLen);
			glEMV_Param.iCAPKNum ++;
			break;

		case TAG_EMV_Parameter:
			memcpy(&glEMV_Param.stEMVParameter, ucVal, uiLen);
			glEMV_Param.iParaExist = 1;
			break;

		default:
			fclose(fp);
			fp = NULL;
			return EMV_PARAM_ERR;
		}
	}

	fclose(fp);
	fp = NULL;

	if(glEMV_Param.iAppNum > MAX_APP_NUM || glEMV_Param.iCAPKNum > MAX_KEY_NUM)
	{
		return EMV_PARAM_ERR;
	}

	glEMV_Param.ucInitFlag = 1;

	return iRet;
}

/*
 * build TLV string with (tag, length, data)
 */
void BuildTLVString(unsigned char ucTag, unsigned char *ucData, unsigned int uiLen, unsigned char **ucTLVStr, unsigned int *uiTLVLen)
{
	unsigned char	ucTmp[MPOS_EMV_TLV_MAX_SIZE], ucLenTmp = '\0';
	int				i = 0;

	if(uiLen < 0)
	{
		return;
	}

	memset(ucTmp, 0, MPOS_EMV_TLV_MAX_SIZE);
	*uiTLVLen = 0;
	//ucTmp = *ucTLVStr;

	// tag
	ucTmp[i++] = ucTag;

	// length
	ucLenTmp = (unsigned char)((uiLen >> 8) & 0xff);
	ucTmp[i++] = ucLenTmp;
	ucLenTmp = (unsigned char)(uiLen & 0xff);
	ucTmp[i++] = ucLenTmp;
	
	// value
	if(uiLen > 0)
	{
		memcpy(&ucTmp[i], ucData, uiLen);
		i += uiLen;
	}

	memcpy(ucTLVStr, ucTmp, i);
	*uiTLVLen = i;
}

/*
* delete data in file
*/
int PubFileWriteDelete(unsigned char *szFileName, long lngOffStart, long lngOffEnd)
{
	int				iRet = EMV_OK, iReadBytes = 0, iWriteBytes = 0, iBuffLen = 0;
	FILE			*fp = NULL, *fpTmp = NULL;
	unsigned char	ucBuff[MPOS_BUF_MAX_SIZE];
	long			lngPos = 0, lngPosTmp = 0, lngFileSize = 0, lngWriteSize = 0, lngWriteSizeTmp = 0;

	if(lngOffStart > lngOffEnd)
	{
		return EMV_PARAM_ERR;
	}

	fp = fopen(szFileName, "rb+");
	if( NULL == fp )
	{
		//LOGE("[PubFileWrite]: open file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_ERR;
	}

	fpTmp = fopen(FILE_EMV_PARAM_TMP, "wb+");
	if( NULL == fpTmp)
	{
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_ERR;
	}

	// get file size
	iRet = fseek(fp, 0, SEEK_END);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fp = NULL;
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}
	lngFileSize = ftell(fp);

	if(lngOffStart > lngFileSize || lngOffEnd > lngFileSize)
	{
		return EMV_PARAM_ERR;
	}

	iRet = fseek(fp, 0, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fp = NULL;
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}

	iRet = fseek(fpTmp, 0, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fp = NULL;
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}

	lngWriteSize = lngOffStart;
	lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? lngWriteSize : MPOS_BUF_MAX_SIZE;
	while(lngWriteSizeTmp > 0)
	{
		memset(ucBuff, 0, MPOS_BUF_MAX_SIZE);
		iReadBytes = fread(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fp);
		if(iReadBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fp = NULL;
			//return MPOS_FILE_READ_ERR;
			return EMV_FILE_ERR;
		}

		iWriteBytes = fwrite(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fpTmp);
		if(iWriteBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fp = NULL;
			//return MPOS_FILE_WRITE_ERR;
			return EMV_FILE_ERR;
		}

		lngWriteSize = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
		lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
	}

	iRet = fseek(fp, lngOffEnd, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fp = NULL;
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}

	lngWriteSize = lngFileSize - lngOffEnd;
	lngWriteSizeTmp = lngWriteSize;
	while(lngWriteSizeTmp > 0)
	{
		lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? lngWriteSize : MPOS_BUF_MAX_SIZE;
		memset(ucBuff, 0, MPOS_BUF_MAX_SIZE);
		iReadBytes = fread(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fp);
		if(iReadBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fp = NULL;
			//return MPOS_FILE_READ_ERR;
			return EMV_FILE_ERR;
		}

		iWriteBytes = fwrite(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fpTmp);
		if(iWriteBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fp = NULL;
			//return MPOS_FILE_WRITE_ERR;
			return EMV_FILE_ERR;
		}

		lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
	}

	fclose(fp);
	fp = NULL;
	fclose(fpTmp);
	fp = NULL;

	remove(szFileName);
	rename(FILE_EMV_PARAM_TMP, szFileName);

	return iRet;
}

int PubFileWriteUpdate(unsigned char *szFileName, long lngOffset, void *psData, int iDataLen)
{
	int		iRet = EMV_OK, iReadBytes = 0, iWriteBytes;
	FILE	*fp = NULL;
	long	lngPos = 0, lngPosTmp = 0;

	fp = fopen(szFileName, "rb+");
	if( NULL == fp )
	{
		//LOGE("[PubFileWrite]: open file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_ERR;
	}

	iRet = fseek(fp, lngOffset, SEEK_SET);

	iWriteBytes = fwrite(psData, sizeof(unsigned char), iDataLen, fp);
	if(iWriteBytes != iDataLen)
	{
		fclose(fp);
		fp = NULL;
		//return MPOS_FILE_WRITE_ERR;
		return EMV_FILE_ERR;
	}

	fclose(fp);
	fp = NULL;

	return iRet;
}

/*
 * write file
 * currenty version; the in-parameter iDataLen could not be larger than MPOS_BUF_MAX_SIZE
 * if iDataLen > MPOS_BUF_MAX_SIZE, then split psData into pieces outside
 */
int PubFileWriteAdd(unsigned char *szFileName, long lngOffset, void *psData, int iDataLen)
{
	int				iRet = EMV_OK, iReadBytes = 0, iWriteBytes = 0, iBuffLen = 0;
	FILE			*fp = NULL, *fpTmp = NULL;
	unsigned char	ucBuff[MPOS_BUF_MAX_SIZE];
	long			lngPos = 0, lngPosTmp = 0, lngFileSize = 0, lngWriteSize = 0, lngWriteSizeTmp = 0;

	fp = fopen(szFileName, "rb+");
	if( NULL == fp )
	{
		//LOGE("[PubFileWrite]: open file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_ERR;
	}

	fpTmp = fopen(FILE_EMV_PARAM_TMP, "wb+");
	if( NULL == fpTmp)
	{
		//return MPOS_FILE_OPEN_ERR;
		return EMV_FILE_ERR;
	}

	// get file size
	iRet = fseek(fp, 0, SEEK_END);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fpTmp = NULL;
		remove(FILE_EMV_PARAM_TMP);
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}
	lngFileSize = ftell(fp);
	if(lngOffset > lngFileSize + 1)
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fpTmp = NULL;
		remove(FILE_EMV_PARAM_TMP);
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return EMV_PARAM_ERR;
		return EMV_FILE_ERR;
	}

	iRet = fseek(fpTmp, 0, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fpTmp = NULL;
		remove(FILE_EMV_PARAM_TMP);
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}

	iRet = fseek(fp, 0, SEEK_SET);
	if( iRet < 0 )
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fpTmp = NULL;
		remove(FILE_EMV_PARAM_TMP);
		//LOGE("[PubFileWrite]: seek file %s error! errno: %d.", szFileName, perror());
		//return MPOS_FILE_SEEK_ERR;
		return EMV_FILE_ERR;
	}

	lngWriteSize = lngOffset - 1;
	lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? lngWriteSize : MPOS_BUF_MAX_SIZE;
	while(lngWriteSizeTmp > 0)
	{
		memset(ucBuff, 0, MPOS_BUF_MAX_SIZE);
		iReadBytes = fread(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fp);
		if(iReadBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fpTmp = NULL;
			remove(FILE_EMV_PARAM_TMP);
			//printf("MPOS_FILE_WRITE_ERR: 1");
			//return MPOS_FILE_READ_ERR;
			return EMV_FILE_ERR;
		}

		iWriteBytes = fwrite(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fpTmp);
		if(iWriteBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fpTmp = NULL;
			remove(FILE_EMV_PARAM_TMP);
			//printf("MPOS_FILE_WRITE_ERR: 2");
			//return MPOS_FILE_WRITE_ERR;
			return EMV_FILE_ERR;
		}

		lngWriteSize = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
		lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
	}

	iWriteBytes = fwrite(psData, sizeof(unsigned char), iDataLen, fpTmp);
	if(iWriteBytes != iDataLen)
	{
		fclose(fp);
		fp = NULL;
		fclose(fpTmp);
		fpTmp = NULL;
		remove(FILE_EMV_PARAM_TMP);
		//printf("MPOS_FILE_WRITE_ERR: 2");
		//return MPOS_FILE_WRITE_ERR;
		return EMV_FILE_ERR;
	}

	lngWriteSize = lngFileSize - (lngOffset - 1);
	lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? lngWriteSize : MPOS_BUF_MAX_SIZE;
	while(lngWriteSizeTmp > 0)
	{		
		memset(ucBuff, 0, MPOS_BUF_MAX_SIZE);
		iReadBytes = fread(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fp);
		if(iReadBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fpTmp = NULL;
			remove(FILE_EMV_PARAM_TMP);
			//printf("MPOS_FILE_WRITE_ERR: 5");
			//return MPOS_FILE_READ_ERR;
			return EMV_FILE_ERR;
		}

		iWriteBytes = fwrite(ucBuff, sizeof(unsigned char), lngWriteSizeTmp, fpTmp);
		if(iWriteBytes != lngWriteSizeTmp)
		{
			fclose(fp);
			fp = NULL;
			fclose(fpTmp);
			fpTmp = NULL;
			remove(FILE_EMV_PARAM_TMP);
			//printf("MPOS_FILE_WRITE_ERR: 6");
			//return MPOS_FILE_WRITE_ERR;
			return EMV_FILE_ERR;
		}

		lngWriteSize = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
		lngWriteSizeTmp = lngWriteSize < MPOS_BUF_MAX_SIZE ? 0 : lngWriteSize - MPOS_BUF_MAX_SIZE;
	}

	fclose(fp);
	fp = NULL;
	fclose(fpTmp);
	fpTmp = NULL;

	remove(szFileName);
	rename(FILE_EMV_PARAM_TMP, szFileName);

	return iRet;
}

int WriteLength(FILE *fp,unsigned int uiLen)
{
	int				iRet = EMV_OK, iWriteBytes = 0;
	unsigned char	ucTmp=0;

	if(fp == NULL)
	{
		return EMV_FILE_ERR;
	}

	ucTmp=(unsigned char)((uiLen >> 8) & 0xff);
	iWriteBytes = fwrite(&ucTmp,sizeof(unsigned char),1,fp);
	if(iWriteBytes != 1)
	{
		return EMV_FILE_ERR;
	}

	ucTmp=(unsigned char)(uiLen & 0xff);
	iWriteBytes = fwrite(&ucTmp,sizeof(unsigned char),1,fp);
	if(iWriteBytes != 1)
	{
		return EMV_FILE_ERR;
	}

	return iRet;
}

int AddDataToFile(FILE *fp,unsigned char ucTag, void * pData)
{
	int				iRet = EMV_OK, iWriteBytes = 0;
	unsigned int	uiLen = 0;
	unsigned char	ucTmp=0;

	if(fp == NULL)
	{
		return EMV_FILE_ERR;
	}

	switch(ucTag)
	{
		case 0x10:
			iWriteBytes = fwrite(&ucTag,sizeof(unsigned char),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			ucTag=0x00;
			iWriteBytes = fwrite(&ucTag,sizeof(unsigned char),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			break;

		case 0x11:
			iWriteBytes = fwrite(&ucTag,sizeof(unsigned char),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}

			uiLen=sizeof(EMV_APPLIST);
			iRet = WriteLength(fp,uiLen);
			if(iRet != EMV_OK)
			{
				return iRet;
			}
			iWriteBytes = fwrite((EMV_APPLIST *)pData,sizeof(EMV_APPLIST),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			break;

		case 0x12:
			iWriteBytes = fwrite(&ucTag,sizeof(unsigned char),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}

			uiLen=sizeof(EMV_CAPK);
			iRet = WriteLength(fp,uiLen);
			if(iRet != EMV_OK)
			{
				return iRet;
			}
			iWriteBytes = fwrite((EMV_CAPK *)pData,sizeof(EMV_CAPK),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			break;

		case 0x13:
			iWriteBytes = fwrite(&ucTag,sizeof(unsigned char),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			uiLen=sizeof(EMV_PARAM);
			iRet = WriteLength(fp,uiLen);
			if(iRet != EMV_OK)
			{
				return iRet;
			}
			
			iWriteBytes = fwrite((EMV_PARAM *)pData,sizeof(EMV_PARAM),1,fp);
			if(iWriteBytes != 1)
			{
				return EMV_FILE_ERR;
			}
			break;

		default:
			break;
	}

	return iRet;
}

/*
 * EMV parameter file initialization..
 */
//int EMVInitializeParameter (void)
int EMVInitializeParameter(unsigned char *szFilePath)
{
	int iRet = EMV_OK;
	FILE *fp = NULL;

	if(NULL == szFilePath)
	{
		return EMV_FILE_NOT_EXIST;
	}

	memset(sgEMV_PARA_FilePath, 0, sizeof(sgEMV_PARA_FilePath));
	strcpy(sgEMV_PARA_FilePath, szFilePath);
	
	iRet = EMVParamInit(szFilePath);
	if( iRet == EMV_FILE_NOT_EXIST)	// file not exist, create file
	{
		fp = fopen(szFilePath, "wb+");
		if( NULL == fp)
		{
			return EMV_FILE_ERR;
		}

		iRet = fseek(fp, 0, SEEK_SET);
		if(iRet < 0)
		{
			fclose(fp);
			fp = NULL;
			return EMV_FILE_ERR;
		}

 		iRet = AddDataToFile(fp, TAG_EMV_CLEAR, NULL);

		fclose(fp);
		fp = NULL;
	}

	return iRet;
}

/*
 * Add an EMV application and its related parameter into the parameter file.
 * If the application already exists, the new application will replace the old one.
 */
int EMVAddAIDParameter(EMV_APPLIST stEMVAppList)
{
	int				iRet = EMV_OK, iIdx = 0;
	unsigned int	uiTLVLen = 0;
	unsigned char	ucTLVStr[MPOS_EMV_TLV_MAX_SIZE], ucExist = 0;
	long			lngOffset = 0;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	for(iIdx=0; iIdx<glEMV_Param.iAppNum; iIdx++)
	{
		if(memcmp(glEMV_Param.stEMVAppList[iIdx].AID, stEMVAppList.AID, 17) == 0)
		{
			// if AID exist	in EMVPARA file, then update the AID
			ucExist = 1;
			break;
		}
	}

	if( ucExist == 0 && glEMV_Param.iAppNum >= MAX_APP_NUM)
	{
		return EMV_OVERFLOW;
	}

	lngOffset = sizeof(glEMV_Param.ucSkipTLV);

	memset(ucTLVStr, 0, MPOS_EMV_TLV_MAX_SIZE);
	BuildTLVString((unsigned char)TAG_EMV_APP, (unsigned char *)&stEMVAppList, sizeof(EMV_APPLIST), (unsigned char **)&ucTLVStr, &uiTLVLen);
	
	if(ucExist == 0)	// not exist, add
	{
		lngOffset += glEMV_Param.iAppNum * MPOS_EMV_TL_LEN + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + 1;;

		// write
		iRet = PubFileWriteAdd(sgEMV_PARA_FilePath, lngOffset, ucTLVStr, uiTLVLen);
	}
	else	// already exist in the EMVPARA file, update
	{	
		lngOffset += iIdx * MPOS_EMV_TL_LEN + iIdx * sizeof(EMV_APPLIST) ;

		// udpate
		iRet = PubFileWriteUpdate(sgEMV_PARA_FilePath, lngOffset, ucTLVStr, uiTLVLen);
	}
	if(iRet == EMV_OK)
	{
		glEMV_Param.ucInitFlag = 0;	// re-init
	}

	return iRet;
}

/*
 * add capk
 * If the public key already exists, the new key will replace the old one.
 */
int EMVAddCAPK(EMV_CAPK stEMV_CAPK)
{
	int iRet = EMV_OK, iIdx=0;

	unsigned int	uiTLVLen = 0;
	unsigned char	ucTLVStr[MPOS_EMV_TLV_MAX_SIZE], ucExist = 0;
	long			lngOffset;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	for(iIdx=0; iIdx<glEMV_Param.iCAPKNum; iIdx++)
	{
		if(memcmp(glEMV_Param.stEMVCAPK[iIdx].RID, stEMV_CAPK.RID, 5) == 0
			&& glEMV_Param.stEMVCAPK[iIdx].KeyID == stEMV_CAPK.KeyID )
		{
			ucExist = 1;
			break;
		}
	}

	if( ucExist == 0 && glEMV_Param.iCAPKNum >= MAX_KEY_NUM)
	{
		return EMV_OVERFLOW;
	}

	lngOffset = sizeof(glEMV_Param.ucSkipTLV);

	memset(ucTLVStr, 0 ,MPOS_EMV_TLV_MAX_SIZE);
	BuildTLVString(TAG_EMV_CAPK, (unsigned char *)&stEMV_CAPK, sizeof(EMV_CAPK), (unsigned char **)&ucTLVStr, &uiTLVLen);
	
	if( ucExist == 0 )
	{
		lngOffset += (glEMV_Param.iAppNum + glEMV_Param.iCAPKNum ) * MPOS_EMV_TL_LEN + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + glEMV_Param.iCAPKNum * sizeof(EMV_CAPK) + 1;

		// write
		iRet = PubFileWriteAdd(sgEMV_PARA_FilePath, lngOffset, ucTLVStr, uiTLVLen);
	}
	else
	{
		lngOffset += (glEMV_Param.iAppNum + iIdx ) * MPOS_EMV_TL_LEN  + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + iIdx * sizeof(EMV_CAPK) ;
		// update
		iRet = PubFileWriteUpdate(sgEMV_PARA_FilePath, lngOffset, ucTLVStr, uiTLVLen);
	}
	if(iRet == EMV_OK)
	{
		glEMV_Param.ucInitFlag = 0;
	}

	return iRet;
}

int EMVSetParameter(EMV_PARAM stEMV_PARAM)
{
	int				iRet = EMV_OK;
	unsigned int	uiTLVLen = 0;
	unsigned char	ucTLVStr[MPOS_EMV_TLV_MAX_SIZE];
	long			lngOffset;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	lngOffset = sizeof(glEMV_Param.ucSkipTLV);;

	memset(ucTLVStr, 0, MPOS_EMV_TLV_MAX_SIZE);
	BuildTLVString(TAG_EMV_Parameter, (unsigned char *)&stEMV_PARAM, sizeof(EMV_PARAM), (unsigned char **)&ucTLVStr, &uiTLVLen);

	lngOffset += (glEMV_Param.iAppNum + glEMV_Param.iCAPKNum ) * MPOS_EMV_TL_LEN + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + glEMV_Param.iCAPKNum * sizeof(EMV_CAPK);
	
	// write
	iRet = PubFileWriteUpdate(sgEMV_PARA_FilePath, lngOffset, ucTLVStr, uiTLVLen);
	if(iRet == EMV_OK)
	{
		glEMV_Param.ucInitFlag = 0;
	}

	return iRet;
}

/*
 * get AID parameter with AID number
 */
int EMVGetAIDParameter(int iAIDNo, EMV_APPLIST *stEMVAppList)
{
	int iRet = EMV_OK;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	if(iAIDNo < 0 || iAIDNo >= glEMV_Param.iAppNum)
	{
		return EMV_PARAM_ERR;
	}

	memcpy(stEMVAppList, &glEMV_Param.stEMVAppList[iAIDNo], sizeof(EMV_APPLIST));

	return iRet;
}

/*
 * get CAPK with CAPK Number
 */
int EMVGetCAPK(int iCAPKNo, EMV_CAPK *stEMV_CAPK)
{
	int iRet = EMV_OK;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	if(iCAPKNo < 0 || iCAPKNo >= glEMV_Param.iCAPKNum)
	{
		return EMV_PARAM_ERR;
	}

	memcpy(stEMV_CAPK, &glEMV_Param.stEMVCAPK[iCAPKNo], sizeof(EMV_CAPK));

	return iRet;
}

/*
 * get emv parameter
 */
int EMVGetParameter(EMV_PARAM *stEMV_PARAM)
{
	int iRet = EMV_OK;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	if( glEMV_Param.iParaExist == 0)
	{
		return EMV_PARAM_ERR;
	}

	memcpy(stEMV_PARAM, &glEMV_Param.stEMVParameter, sizeof(EMV_PARAM));

	return iRet;
}

/*
 * Get the total number of AID that has been added
 */
//int EMVGetTotalAIDNumber(void)
int EMVGetTotalAIDNumber(int *iAidNum)
{
	int iRet = 0;

	*iAidNum = 0;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	*iAidNum = glEMV_Param.iAppNum;

	return iRet;
}

/*
 * Gets the total number of EMV CAPK.
 */
//int EMVGetTotalCAPKNumber(void)
int EMVGetTotalCAPKNumber(int *iCAPKNum)
{
	int iRet = 0;

	*iCAPKNum = 0;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	*iCAPKNum = glEMV_Param.iCAPKNum;

	return iRet;
}



/*
*	delete AID parameter with AIDNo
*/
int EMVDeleteAIDParameter(int iAIDNo)
{
	int iRet = EMV_OK;
	long	lngOffStart = sizeof(glEMV_Param.ucSkipTLV);
	long	lngOffEnd = sizeof(glEMV_Param.ucSkipTLV);

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	if(iAIDNo < 0 || iAIDNo > glEMV_Param.iAppNum - 1)
	{
		return EMV_PARAM_ERR;
	}

	lngOffStart = sizeof(glEMV_Param.ucSkipTLV);
	lngOffEnd = sizeof(glEMV_Param.ucSkipTLV);
	
	lngOffStart += iAIDNo * MPOS_EMV_TL_LEN + iAIDNo * sizeof(EMV_APPLIST);
	lngOffEnd += (iAIDNo + 1) * MPOS_EMV_TL_LEN + (iAIDNo + 1) * sizeof(EMV_APPLIST);

	iRet = PubFileWriteDelete(sgEMV_PARA_FilePath, lngOffStart, lngOffEnd);
	if(iRet == EMV_OK)
	{
		glEMV_Param.ucInitFlag = 0;
	}

	return iRet;
}

/*
*	delete CAPK with iCAPKNo
*/
int EMVDelCAPK(int iCAPKNo)
{
	int iRet = EMV_OK;
	long	lngOffStart;
	long	lngOffEnd;

	/*if( 0 == glEMV_Param.ucInitFlag )
	{*/
		iRet = EMVParamInit(sgEMV_PARA_FilePath);
		if( iRet != EMV_OK)
		{
			return iRet;
		}
	/*}
	else
	{
		if(CheckFileExist(sgEMV_PARA_FilePath) == 0)
		{
			return EMV_FILE_NOT_EXIST;
		}
	}*/

	if(iCAPKNo < 0 || iCAPKNo > glEMV_Param.iCAPKNum - 1)
	{
		return EMV_PARAM_ERR;
	}

	lngOffStart = sizeof(glEMV_Param.ucSkipTLV);
	lngOffEnd = sizeof(glEMV_Param.ucSkipTLV);

	lngOffStart += (glEMV_Param.iAppNum + iCAPKNo ) * MPOS_EMV_TL_LEN + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + iCAPKNo * sizeof(EMV_CAPK);
	lngOffEnd += (glEMV_Param.iAppNum + iCAPKNo + 1 ) * MPOS_EMV_TL_LEN + glEMV_Param.iAppNum * sizeof(EMV_APPLIST) + (iCAPKNo + 1) * sizeof(EMV_CAPK);

	iRet = PubFileWriteDelete(sgEMV_PARA_FilePath, lngOffStart, lngOffEnd);
	if(iRet == EMV_OK)
	{
		glEMV_Param.ucInitFlag = 0;
	}

	return iRet;
}
