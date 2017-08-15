#include "com_pax_jni_emvparam.h"
#include "fileoper.h"

jint jniSetByteArrayField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned char *abField, int size)
{
	jfieldID fd;
	jbyteArray abVal;
	jbyte *pbVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_BYTE_ARRAY);
	if(NULL == fd)
	{
		return -1;
	}

	abVal = (jbyteArray)(*env)->GetObjectField(env,objID,fd);
	len = (*env)->GetArrayLength(env, abVal);
	len = (len < size) ? len : size;
	pbVal = (*env)->GetByteArrayElements(env,abVal,0);
	memcpy(abField, pbVal, len);
	(*env)->ReleaseByteArrayElements(env, abVal, pbVal, 0);

	return EMV_OK;
}

jint jniGetByteArrayField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned char *abField)
{
	jfieldID fd;
	jbyteArray abVal;
	jbyte *pbVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_BYTE_ARRAY);
	if(NULL == fd)
	{
		return -1;
	}
	len = sizeof(abField);
	abVal = (*env)->NewByteArray(env, len);
	(*env)->SetByteArrayRegion(env, abVal, 0, len, abField);
	(*env)->SetObjectField(env, objID, fd, abVal);

	return EMV_OK;
}

jint jniSetByteField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned char *bField)
{
	jfieldID fd;
	jbyte bVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_BYTE);
	if(NULL == fd)
	{
		return -1;
	}
	bVal = (*env)->GetByteField(env,objID,fd);
	*bField = bVal;

	return EMV_OK;
}

jint jniGetByteField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned char bField)
{
	jfieldID fd;
	jbyte bVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_BYTE);
	if(NULL == fd)
	{
		return -1;
	}
	bVal = bField;
	(*env)->SetByteField(env, objID, fd, bVal);

	return EMV_OK;
}

jint jniSetIntField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned long *iField)
{
	jfieldID fd;
	jint iVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_INT);
	if(NULL == fd)
	{
		return -1;
	}
	iVal = (*env)->GetIntField(env,objID,fd);
	*iField = iVal;

	return EMV_OK;
}

jint jniGetIntField(JNIEnv *env, jobject objID, jclass classID, const char *FieldName, unsigned long iField)
{
	jfieldID fd;
	jint iVal;
	jsize len;
	
	fd = (*env)->GetFieldID(env, classID, FieldName, SIGN_INT);
	if(NULL == fd)
	{
		return -1;
	}
	iVal = iField;
	(*env)->SetIntField(env, objID, fd, iVal);

	return EMV_OK;
}

/*
 * Function:  EMV parameter file initialization..
 * Class:     com_pax_jni_emvparam
 * Method:    EMVInitializeParameter
 * Signature: (java.lang.String;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVInitializeParameter (JNIEnv *env, jobject obj, jstring strFilePath)
{
	jint jiRet = EMV_OK;
	
	unsigned char* szStr = (char*)(*env)->GetStringUTFChars(env, strFilePath, NULL);
	jiRet = EMVInitializeParameter(szStr);
	(*env)->ReleaseStringUTFChars(env, strFilePath, szStr);

	return jiRet;
}


/*
 * Function:  Add an EMV application and its related parameter into the parameter file
 * Class:     com_pax_jni_emvparam
 * Method:    EMVAddAIDParameter
 * Signature: (Lcom/pax/emv/EMV_APPLIST;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVAddAIDParameter (JNIEnv *env, jobject obj, jobject objEMV_APPList)
{
	jint jiRet = EMV_OK;

	EMV_APPLIST stEMVAppList;
	memset(&stEMVAppList, 0, sizeof(EMV_APPLIST));
	
	jclass class_EMV_APPList = (*env)->GetObjectClass(env, objEMV_APPList);
	if(NULL == class_EMV_APPList)
    {
        return EMV_PARAM_ERR;
    }

	//AppName
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AppName", 
								stEMVAppList.AppName, sizeof(stEMVAppList.AppName));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AID
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AID", 
								stEMVAppList.AID, sizeof(stEMVAppList.AID));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AidLen
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "AidLen", &stEMVAppList.AidLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//SelFlag
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "SelFlag", &stEMVAppList.SelFlag);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Priority
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "Priority", &stEMVAppList.Priority);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TargetPer
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "TargetPer", &stEMVAppList.TargetPer);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MaxTargetPer
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "MaxTargetPer", &stEMVAppList.MaxTargetPer);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//FloorLimitCheck
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "FloorLimitCheck", &stEMVAppList.FloorLimitCheck);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//RandTransSel
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "RandTransSel", &stEMVAppList.RandTransSel);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//VelocityCheck
	jiRet = jniSetByteField(env, objEMV_APPList, class_EMV_APPList, "VelocityCheck", &stEMVAppList.VelocityCheck);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//FloorLimit
	jiRet = jniSetIntField(env, objEMV_APPList, class_EMV_APPList, "FloorLimit", &stEMVAppList.FloorLimit);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Threshold
	jiRet = jniSetIntField(env, objEMV_APPList, class_EMV_APPList, "Threshold", &stEMVAppList.Threshold);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACDenial
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACDenial", 
								stEMVAppList.TACDenial, sizeof(stEMVAppList.TACDenial));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACOnline
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACOnline", 
								stEMVAppList.TACOnline, sizeof(stEMVAppList.TACOnline));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACDefault
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACDefault", 
								stEMVAppList.TACDefault, sizeof(stEMVAppList.TACDefault));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AcquierId
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AcquierId", 
								stEMVAppList.AcquierId, sizeof(stEMVAppList.AcquierId));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//dDOL
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "dDOL", 
								stEMVAppList.dDOL, sizeof(stEMVAppList.dDOL));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//tDOL
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "tDOL", 
								stEMVAppList.tDOL, sizeof(stEMVAppList.tDOL));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Version
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "Version", 
								stEMVAppList.Version, sizeof(stEMVAppList.Version));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//RiskManData
	jiRet = jniSetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "RiskManData", 
								stEMVAppList.RiskManData, sizeof(stEMVAppList.RiskManData));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	jiRet = EMVAddAIDParameter(stEMVAppList);

	return jiRet;
}

/*
 * Function:  Get an application from the terminal application.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVGetAIDParameter
 * Signature: (ILcom/pax/emv/EMV_CAPK;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVGetAIDParameter (JNIEnv *env, jobject obj, jint jiAIDNo, jobject objEMV_APPList)
{
	jint jiRet = EMV_OK;

	int AIDNo = jiAIDNo;
	EMV_APPLIST stEMVAppList;
	memset(&stEMVAppList, 0, sizeof(EMV_APPLIST));
	jiRet = EMVGetAIDParameter(AIDNo, &stEMVAppList);
	if(EMV_OK != jiRet)
	{
		return jiRet;
	}

	jclass class_EMV_APPList = (*env)->GetObjectClass(env, objEMV_APPList);
	if(NULL == class_EMV_APPList)
    {
        return EMV_PARAM_ERR;
    }

	//AppName
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AppName", stEMVAppList.AppName);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AID
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AID", stEMVAppList.AID);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AidLen
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "AidLen", stEMVAppList.AidLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//SelFlag
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "SelFlag", stEMVAppList.SelFlag);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Priority
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "Priority", stEMVAppList.Priority);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TargetPer
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "TargetPer", stEMVAppList.TargetPer);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MaxTargetPer
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "MaxTargetPer", stEMVAppList.MaxTargetPer);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//FloorLimitCheck
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "FloorLimitCheck", stEMVAppList.FloorLimitCheck);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//RandTransSel
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "RandTransSel", stEMVAppList.RandTransSel);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//VelocityCheck
	jiRet = jniGetByteField(env, objEMV_APPList, class_EMV_APPList, "VelocityCheck", stEMVAppList.VelocityCheck);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//FloorLimit
	jiRet = jniGetIntField(env, objEMV_APPList, class_EMV_APPList, "FloorLimit", stEMVAppList.FloorLimit);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Threshold
	jiRet = jniGetIntField(env, objEMV_APPList, class_EMV_APPList, "Threshold", stEMVAppList.Threshold);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACDenial
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACDenial", stEMVAppList.TACDenial);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACOnline
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACOnline", stEMVAppList.TACOnline);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TACDefault
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "TACDefault", stEMVAppList.TACDefault);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//AcquierId
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "AcquierId", stEMVAppList.AcquierId);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//dDOL
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "dDOL", stEMVAppList.dDOL);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//tDOL
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "tDOL", stEMVAppList.tDOL);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Version
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "Version", stEMVAppList.Version);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//RiskManData
	jiRet = jniGetByteArrayField(env, objEMV_APPList, class_EMV_APPList, "RiskManData", stEMVAppList.RiskManData);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	return jiRet;
}

/*
 * Function:  Get the total number of AID that has been added
 * Class:     com_pax_jni_emvparam
 * Method:    EMVGetTotalAIDNumber
 * Signature: (Ljava/lang/Integer;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVGetTotalAIDNumber (JNIEnv *env, jobject obj, jobject objAIDNum)
{
	jint jiRet = EMV_OK;
	
	int No;
	jiRet = EMVGetTotalAIDNumber(&No);
	if(EMV_OK != jiRet)
	{
		return jiRet;
	}

	jclass class_AIDNum = (*env)->GetObjectClass(env, objAIDNum);
	if(NULL == class_AIDNum)
    {
        return EMV_PARAM_ERR;
    }
	jfieldID fd = (*env)->GetFieldID(env, class_AIDNum, "value", "I");
	if(NULL == fd)
    {
        return EMV_PARAM_ERR;
    }
	(*env)->SetIntField(env, objAIDNum, fd, No);

	return jiRet;
}

/*
 * Function:  Delete an application from the application.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVDeleteAIDParameter
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVDeleteAIDParameter (JNIEnv *env, jobject obj, jint jiAIDNo)
{
	jint jiRet = EMV_OK;

	int AIDNo = jiAIDNo;
	jiRet = EMVDeleteAIDParameter(AIDNo);

	return jiRet;
}

/*
 * Function:  Get terminal parameter.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVGetParameter
 * Signature: ()Lcom/pax/emv/EMVPARA;
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVGetParameter (JNIEnv *env, jobject obj, jobject objEMV_PARAM)
{
	jint jiRet = EMV_OK;

	EMV_PARAM stEMV_PARAM;
	memset(&stEMV_PARAM, 0, sizeof(EMV_PARAM));
	jiRet = EMVGetParameter(&stEMV_PARAM);
	if(EMV_OK != jiRet)
	{
		return jiRet;
	}

	jclass class_EMV_PARAM = (*env)->GetObjectClass(env, objEMV_PARAM);
	if(NULL == class_EMV_PARAM)
    {
        return EMV_PARAM_ERR;
    }

	//MerchName
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchName", stEMV_PARAM.MerchName);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MerchCateCode
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchCateCode", stEMV_PARAM.MerchCateCode);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MerchId
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchId", stEMV_PARAM.MerchId);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TermId
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "TermId", stEMV_PARAM.TermId);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TerminalType
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TerminalType", stEMV_PARAM.TerminalType);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Capability
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "Capability", stEMV_PARAM.Capability);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExCapability
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "ExCapability", stEMV_PARAM.ExCapability);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransCurrExp
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TransCurrExp", stEMV_PARAM.TransCurrExp);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrExp
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrExp", stEMV_PARAM.ReferCurrExp);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrCode
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrCode", stEMV_PARAM.ReferCurrCode);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//CountryCode
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "CountryCode", stEMV_PARAM.CountryCode);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransCurrCode
	jiRet = jniGetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "TransCurrCode", stEMV_PARAM.TransCurrCode);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrCon
	jiRet = jniGetIntField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrCon", stEMV_PARAM.ReferCurrCon);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransType
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TransType", stEMV_PARAM.TransType);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ForceOnline
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "ForceOnline", stEMV_PARAM.ForceOnline);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//GetDataPIN
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "GetDataPIN", stEMV_PARAM.GetDataPIN);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//SurportPSESel
	jiRet = jniGetByteField(env, objEMV_PARAM, class_EMV_PARAM, "SurportPSESel", stEMV_PARAM.SurportPSESel);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	return jiRet;
}

/*
 * Function:  Set terminal parameter
 * Class:     com_pax_jni_emvparam
 * Method:    EMVSetParameter
 * Signature: (Lcom/pax/emv/EMV_Parameter;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVSetParameter (JNIEnv *env, jobject obj, jobject objEMV_PARAM)
{
	jint jiRet = EMV_OK;

	EMV_PARAM stEMV_PARAM;
	memset(&stEMV_PARAM, 0, sizeof(EMV_PARAM));

	jclass class_EMV_PARAM = (*env)->GetObjectClass(env, objEMV_PARAM);
	if(NULL == class_EMV_PARAM)
    {
        return EMV_PARAM_ERR;
    }

	//MerchName
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchName", 
								stEMV_PARAM.MerchName, sizeof(stEMV_PARAM.MerchName));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MerchCateCode
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchCateCode", 
								stEMV_PARAM.MerchCateCode, sizeof(stEMV_PARAM.MerchCateCode));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//MerchId
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "MerchId", 
								stEMV_PARAM.MerchId, sizeof(stEMV_PARAM.MerchId));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TermId
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "TermId", 
								stEMV_PARAM.TermId, sizeof(stEMV_PARAM.TermId));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TerminalType
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TerminalType", &stEMV_PARAM.TerminalType);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Capability
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "Capability", 
								stEMV_PARAM.Capability, sizeof(stEMV_PARAM.Capability));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExCapability
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "ExCapability", 
								stEMV_PARAM.ExCapability, sizeof(stEMV_PARAM.ExCapability));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransCurrExp
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TransCurrExp", &stEMV_PARAM.TransCurrExp);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrExp
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrExp", &stEMV_PARAM.ReferCurrExp);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrCode
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrCode", 
								stEMV_PARAM.ReferCurrCode, sizeof(stEMV_PARAM.ReferCurrCode));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//CountryCode
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "CountryCode", 
								stEMV_PARAM.CountryCode, sizeof(stEMV_PARAM.CountryCode));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransCurrCode
	jiRet = jniSetByteArrayField(env, objEMV_PARAM, class_EMV_PARAM, "TransCurrCode", 
								stEMV_PARAM.TransCurrCode, sizeof(stEMV_PARAM.TransCurrCode));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ReferCurrCon
	jiRet = jniSetIntField(env, objEMV_PARAM, class_EMV_PARAM, "ReferCurrCon", &stEMV_PARAM.ReferCurrCon);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//TransType
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "TransType", &stEMV_PARAM.TransType);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ForceOnline
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "ForceOnline", &stEMV_PARAM.ForceOnline);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//GetDataPIN
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "GetDataPIN", &stEMV_PARAM.GetDataPIN);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//SurportPSESel
	jiRet = jniSetByteField(env, objEMV_PARAM, class_EMV_PARAM, "SurportPSESel", &stEMV_PARAM.SurportPSESel);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	jiRet = EMVSetParameter(stEMV_PARAM);

	return jiRet;
}

/*
 * Function:  Add a new CA public key
 * Class:     com_pax_jni_emvparam
 * Method:    EMVAddCAPK
 * Signature: (Lcom/pax/emv/EMV_CAPK;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVAddCAPK (JNIEnv *env, jobject obj, jobject objEmvCapk)
{
	jint jiRet = EMV_OK;

	EMV_CAPK stEMVCAPK;
	memset(&stEMVCAPK, 0, sizeof(EMV_CAPK));
	
	jclass class_EMV_CAPK = (*env)->GetObjectClass(env, objEmvCapk);
	if(NULL == class_EMV_CAPK)
    {
        return EMV_PARAM_ERR;
    }

	//RID
	jiRet = jniSetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "RID", 
								stEMVCAPK.RID, sizeof(stEMVCAPK.RID));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//KeyID
	jiRet = jniSetByteField(env, objEmvCapk, class_EMV_CAPK, "KeyID", &stEMVCAPK.KeyID);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//HashInd
	jiRet = jniSetByteField(env, objEmvCapk, class_EMV_CAPK, "HashInd", &stEMVCAPK.HashInd);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ArithInd
	jiRet = jniSetByteField(env, objEmvCapk, class_EMV_CAPK, "ArithInd", &stEMVCAPK.ArithInd);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ModulLen
	jiRet = jniSetByteField(env, objEmvCapk, class_EMV_CAPK, "ModulLen", &stEMVCAPK.ModulLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Modul
	jiRet = jniSetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "Modul", 
								stEMVCAPK.Modul, sizeof(stEMVCAPK.Modul));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExponentLen
	jiRet = jniSetByteField(env, objEmvCapk, class_EMV_CAPK, "ExpLen", &stEMVCAPK.ExponentLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Exponent
	jiRet = jniSetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "Exp",
								stEMVCAPK.Exponent, sizeof(stEMVCAPK.Exponent));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExpDate
	jiRet = jniSetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "ExpDate", 
								stEMVCAPK.ExpDate, sizeof(stEMVCAPK.ExpDate));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//CheckSum
	jiRet = jniSetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "CheckSum", 
								stEMVCAPK.CheckSum, sizeof(stEMVCAPK.CheckSum));
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	jiRet = EMVAddCAPK(stEMVCAPK);

	return jiRet;
}

/*
 * Function:  Gets the total number of EMV CAPK.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVGetTotalCAPKNumber
 * Signature: (Ljava/lang/Integer;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVGetTotalCAPKNumber (JNIEnv *env, jobject obj, jobject objCAPKNum)
{
	jint jiRet = EMV_OK;
	
	int No;
	jiRet = EMVGetTotalCAPKNumber(&No);
	if(EMV_OK != jiRet)
	{
		return jiRet;
	}

	jclass class_CAPKNum = (*env)->GetObjectClass(env, objCAPKNum);
	if(NULL == class_CAPKNum)
    {
        return EMV_PARAM_ERR;
    }
	jfieldID fd = (*env)->GetFieldID(env, class_CAPKNum, "value", "I");
	if(NULL == fd)
    {
        return EMV_PARAM_ERR;
    }
	(*env)->SetIntField(env, objCAPKNum, fd, No);

	return jiRet;
}

/*
 * Function:  Get a CA public key.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVGetCAPK
 * Signature: (ILcom/pax/emv/EMV_CAPK;)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVGetCAPK (JNIEnv *env, jobject obj, jint jiCapkNo, jobject objEmvCapk)
{
	jint jiRet = EMV_OK;

	int CAPKNo = jiCapkNo;
	EMV_CAPK stEMVCAPK;
	memset(&stEMVCAPK, 0, sizeof(EMV_CAPK));
	jiRet = EMVGetCAPK(CAPKNo, &stEMVCAPK);
	if(EMV_OK != jiRet)
	{
		return jiRet;
	}

	jclass class_EMV_CAPK = (*env)->GetObjectClass(env, objEmvCapk);
	if(NULL == class_EMV_CAPK)
    {
        return EMV_PARAM_ERR;
    }

	//RID
	jiRet = jniGetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "RID", stEMVCAPK.RID);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//KeyID
	jiRet = jniGetByteField(env, objEmvCapk, class_EMV_CAPK, "KeyID", stEMVCAPK.KeyID);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//HashInd
	jiRet = jniGetByteField(env, objEmvCapk, class_EMV_CAPK, "HashInd", stEMVCAPK.HashInd);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ArithInd
	jiRet = jniGetByteField(env, objEmvCapk, class_EMV_CAPK, "ArithInd", stEMVCAPK.ArithInd);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ModulLen
	jiRet = jniGetByteField(env, objEmvCapk, class_EMV_CAPK, "ModulLen", stEMVCAPK.ModulLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Modul
	jiRet = jniGetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "Modul", stEMVCAPK.Modul);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExponentLen
	jiRet = jniGetByteField(env, objEmvCapk, class_EMV_CAPK, "ExpLen", stEMVCAPK.ExponentLen);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//Exponent
	jiRet = jniGetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "Exp", stEMVCAPK.Exponent);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//ExpDate
	jiRet = jniGetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "ExpDate", stEMVCAPK.ExpDate);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	//CheckSum
	jiRet = jniGetByteArrayField(env, objEmvCapk, class_EMV_CAPK, "CheckSum", stEMVCAPK.CheckSum);
	if(EMV_OK != jiRet)
    {
        return EMV_PARAM_ERR;
    }

	return jiRet;

}

/*
 * Function:  Delete a CA public key.
 * Class:     com_pax_jni_emvparam
 * Method:    EMVDelCAPK
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pax_jni_emvparam_EMVDelCAPK (JNIEnv *env, jobject obj, jint jiCapkNo)
{
	jint jiRet = EMV_OK;

	int CAPKNo = jiCapkNo;
	jiRet = EMVDelCAPK(CAPKNo);

	return jiRet;
}
