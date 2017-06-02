//
// Created by 苏力(平安科技核心信贷系统开发部房交所分组) on 2017/5/25.
//
#include <stdio.h>
#include <jni.h>

#define TARGET_CLASS "com/suli/libsdk/Hello"
#define TARGET_SAY_HELLO "sayHello"
#define TARGET_SAY_HELLO_SIG "()Ljava/lang/String;"


jstring sayHello(JNIEnv *env) {
    return (*env)->NewStringUTF(env, "native string");
}

static const JNINativeMethod gMethods[] = {
        {TARGET_SAY_HELLO, TARGET_SAY_HELLO_SIG, (void *) sayHello}
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    jclass clazz = (*env)->FindClass(env, TARGET_CLASS);
    if (!clazz) {
        return -1;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods,
                                sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
        return -1;
    }

    return JNI_VERSION_1_4;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return;
    }
}