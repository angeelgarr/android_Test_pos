#include "util.h"


unsigned long HexToULong(unsigned char *psHex)
{
	unsigned long ulData = 0;
	unsigned long ulTmp  = 0;

	ulData = 0;
	ulTmp  = 1;
	ulData += psHex[3]*ulTmp;
	ulTmp  *= 256;
	ulData += psHex[2]*ulTmp;
	ulTmp  *= 256;
	ulData += psHex[1]*ulTmp;
	ulTmp  *= 256;
	ulData += psHex[0]*ulTmp;

	return ulData;
}

void ULongToHex(unsigned long ulData, unsigned char *psOutHex)
{
	unsigned short nHigh, nLow;
	nHigh = (unsigned short)(ulData/65536);
	nLow  = (unsigned short)(ulData%65536);
	psOutHex[0] = nHigh/256;
	psOutHex[1] = nHigh%256;
	psOutHex[2] = nLow/256;
	psOutHex[3] = nLow %256;
}

//将可读的16进制串合并成其一半长度的二进制串,"12AB"-->0x12AB
//Convert readable HEX string to BIN string, which only half length of HEX string. "12AB"-->0x12AB
void PubAsc2Bcd(unsigned char *psIn, unsigned int uiLength, unsigned char *psOut)
{
    unsigned char tmp;
    unsigned int i;
	
    if ((psIn == NULL) || (psOut == NULL))
    {
        return;
    }
	
    for(i = 0; i < uiLength; i += 2)
    {
        tmp = psIn[i];
		
        if ( tmp > '9' )
        {
            tmp = (unsigned char)toupper((int)tmp) - 'A' + 0x0A;
        }
        else
        {
            tmp &= 0x0F;
        }
        psOut[i / 2] = (tmp << 4);
		
        tmp = psIn[i + 1];
		
        if( tmp > '9' )
        {
            tmp = toupper((char)tmp) - 'A' + 0x0A;
        }
        else
        {
            tmp &= 0x0F;
        }
        psOut[i / 2] |= tmp;
    }
}
