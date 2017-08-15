LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-8
LOCAL_MODULE    := PaxJniEMVParam
LOCAL_LDLIBS    := -lm -llog 
LOCAL_SRC_FILES := com_pax_jni_emvparam.c fileoper.c

include $(BUILD_SHARED_LIBRARY)