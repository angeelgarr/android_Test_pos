#ifndef _PAX_MPOS_UTIL_H_
#define _PAX_MPOS_UTIL_H_

unsigned long HexToULong(unsigned char *psHex);
void ULongToHex(unsigned long ulData, unsigned char *psOutHex);

void PubAsc2Bcd(unsigned char *psIn, unsigned int uiLength, unsigned char *psOut);

#ifdef __cplusplus
extern "C"{
#endif

#endif
