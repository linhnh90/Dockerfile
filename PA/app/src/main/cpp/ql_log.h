//
// Created by zhoukai on 17-1-3.
//

#ifndef MODEMTOOLTEST_QL_LOG_H
#define MODEMTOOLTEST_QL_LOG_H

#include <android/log.h>

#define DTAG    "ModemTool"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,DTAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,DTAG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,DTAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,DTAG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,DTAG,__VA_ARGS__)

#endif //MODEMTOOLTEST_QL_LOG_H
