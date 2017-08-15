#ifndef	_PAX_MPOS_COMMON_H_
#define _PAX_MPOS_COMMON_H_

#if !defined(WIN32) && !defined(WINDOWS)
#include <jni.h>
#include "android/log.h"
#endif // WINDOWS

#ifndef NULL
#ifdef __cplusplus
	#define NULL    0
#else
	#define NULL    ((void *)0)
#endif // __cplusplus
#endif // NULL

#define PAX_MPOS_LOG
#define DEBUG

#ifdef PAX_MPOS_LOG
static const char *TAG = "C-TAG";
#if !defined(WIN32) && !defined(WINDOWS) && defined(DEBUG)
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt,##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt,##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN,  TAG, fmt,##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt,##args)
#else
#define LOGI(fmt, args...)
#define LOGD(fmt, args...)
#define LOGW(fmt, args...)
#define LOGE(fmt, args...)
#endif // WINDOWS

#endif

#endif
