#include <string>
#include <stdio.h>
#include <jni.h>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_example_andrew_maps_MainActivity_stringFromJNI( JNIEnv* env, jobject){

//    JavaVM *vm;
//    JNIEnv *env;
//    JavaVMInitArgs vm_args;
//    vm_args.version = JNI_VERSION_1_2;
//    vm_args.nOptions = 0;
//    vm_args.ignoreUnrecognized = 1;
//
//    // Construct a VM
//    jint res = JNI_CreateJavaVM(&vm, (void **)&env, &vm_args);

    std::string hello = "Hello from JNI!";
    return env->NewStringUTF(hello.c_str());
}