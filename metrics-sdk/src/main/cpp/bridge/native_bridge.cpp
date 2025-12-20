#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/bitmap.h>

#include "session/session_manager.h"
#include "metrics/metrics_aggregator.h"
#include "events/event_detector.h"
#include "imaging/image_processor.h"

#define LOG_TAG "MetricsSDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace metrics;

// Global references for callbacks
static JavaVM* g_javaVM = nullptr;
static jobject g_callbackObject = nullptr;
static jmethodID g_onEventMethod = nullptr;

extern "C" {

// ==================== JNI OnLoad ====================

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_javaVM = vm;
    LOGI("MetricsSDK native library loaded");
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_OK && env != nullptr) {
        if (g_callbackObject != nullptr) {
            env->DeleteGlobalRef(g_callbackObject);
            g_callbackObject = nullptr;
        }
    }
    LOGI("MetricsSDK native library unloaded");
}

// ==================== Initialization ====================

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeInit(
    JNIEnv* env,
    jobject thiz,
    jstring storagePath
) {
    const char* path = env->GetStringUTFChars(storagePath, nullptr);
    ImageProcessor::getInstance().setStorageDirectory(path);
    env->ReleaseStringUTFChars(storagePath, path);
    
    LOGI("Native SDK initialized with storage: %s", path);
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeSetEventCallback(
    JNIEnv* env,
    jobject thiz,
    jobject callback
) {
    if (g_callbackObject != nullptr) {
        env->DeleteGlobalRef(g_callbackObject);
    }
    
    g_callbackObject = env->NewGlobalRef(callback);
    
    jclass callbackClass = env->GetObjectClass(callback);
    g_onEventMethod = env->GetMethodID(
        callbackClass, 
        "onEvent", 
        "(ILjava/lang/String;Ljava/lang/String;J)V"
    );
    
    // Set native callback
    EventDetector::getInstance().setEventCallback([](const EventDetector::Event& event) {
        if (g_javaVM == nullptr || g_callbackObject == nullptr || g_onEventMethod == nullptr) {
            return;
        }
        
        JNIEnv* env = nullptr;
        bool needsDetach = false;
        
        int status = g_javaVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
        if (status == JNI_EDETACHED) {
            if (g_javaVM->AttachCurrentThread(&env, nullptr) != JNI_OK) {
                return;
            }
            needsDetach = true;
        }
        
        jstring name = env->NewStringUTF(event.name.c_str());
        jstring metadata = env->NewStringUTF(event.metadata.c_str());
        
        env->CallVoidMethod(
            g_callbackObject,
            g_onEventMethod,
            static_cast<jint>(event.type),
            name,
            metadata,
            static_cast<jlong>(event.timestampMs)
        );
        
        env->DeleteLocalRef(name);
        env->DeleteLocalRef(metadata);
        
        if (needsDetach) {
            g_javaVM->DetachCurrentThread();
        }
    });
    
    LOGD("Event callback registered");
}

// ==================== Session Management ====================

JNIEXPORT jstring JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeStartSession(
    JNIEnv* env,
    jobject thiz
) {
    std::string sessionId = SessionManager::getInstance().startSession();
    return env->NewStringUTF(sessionId.c_str());
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeEndSession(
    JNIEnv* env,
    jobject thiz
) {
    SessionManager::getInstance().endSession();
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativePauseSession(
    JNIEnv* env,
    jobject thiz
) {
    SessionManager::getInstance().pauseSession();
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeResumeSession(
    JNIEnv* env,
    jobject thiz
) {
    SessionManager::getInstance().resumeSession();
}

JNIEXPORT jstring JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeGetSessionId(
    JNIEnv* env,
    jobject thiz
) {
    std::string sessionId = SessionManager::getInstance().getSessionId();
    return env->NewStringUTF(sessionId.c_str());
}

JNIEXPORT jlong JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeGetSessionDuration(
    JNIEnv* env,
    jobject thiz
) {
    return static_cast<jlong>(SessionManager::getInstance().getSessionDurationMs());
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeSetUserInfo(
    JNIEnv* env,
    jobject thiz,
    jstring userId,
    jstring email
) {
    const char* userIdStr = env->GetStringUTFChars(userId, nullptr);
    const char* emailStr = email != nullptr ? env->GetStringUTFChars(email, nullptr) : "";
    
    SessionManager::getInstance().setUserInfo(userIdStr, emailStr);
    
    env->ReleaseStringUTFChars(userId, userIdStr);
    if (email != nullptr) {
        env->ReleaseStringUTFChars(email, emailStr);
    }
}

// ==================== Metrics ====================

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeRecordMemoryMetrics(
    JNIEnv* env,
    jobject thiz,
    jlong totalMb,
    jlong usedMb,
    jlong availableMb
) {
    MetricsAggregator::getInstance().recordMemoryMetrics(totalMb, usedMb, availableMb);
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeRecordCpuMetrics(
    JNIEnv* env,
    jobject thiz,
    jfloat usagePercentage,
    jint coreCount
) {
    MetricsAggregator::getInstance().recordCpuMetrics(usagePercentage, coreCount);
}

JNIEXPORT jboolean JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeIsMemorySpike(
    JNIEnv* env,
    jobject thiz
) {
    return static_cast<jboolean>(MetricsAggregator::getInstance().isMemorySpike());
}

// ==================== Events ====================

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeStartWatchdog(
    JNIEnv* env,
    jobject thiz
) {
    EventDetector::getInstance().startWatchdog();
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeStopWatchdog(
    JNIEnv* env,
    jobject thiz
) {
    EventDetector::getInstance().stopWatchdog();
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativePingWatchdog(
    JNIEnv* env,
    jobject thiz
) {
    EventDetector::getInstance().pingWatchdog();
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeRecordEvent(
    JNIEnv* env,
    jobject thiz,
    jint eventType,
    jstring name,
    jstring metadata
) {
    const char* nameStr = env->GetStringUTFChars(name, nullptr);
    const char* metadataStr = env->GetStringUTFChars(metadata, nullptr);
    
    EventDetector::getInstance().recordEvent(
        static_cast<EventDetector::EventType>(eventType),
        nameStr,
        metadataStr
    );
    
    env->ReleaseStringUTFChars(name, nameStr);
    env->ReleaseStringUTFChars(metadata, metadataStr);
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeRecordHeavyAction(
    JNIEnv* env,
    jobject thiz,
    jstring name,
    jstring metadata
) {
    const char* nameStr = env->GetStringUTFChars(name, nullptr);
    const char* metadataStr = env->GetStringUTFChars(metadata, nullptr);
    
    EventDetector::getInstance().recordHeavyAction(nameStr, metadataStr);
    
    env->ReleaseStringUTFChars(name, nameStr);
    env->ReleaseStringUTFChars(metadata, metadataStr);
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeRecordCrash(
    JNIEnv* env,
    jobject thiz,
    jstring stackTrace
) {
    const char* stackTraceStr = env->GetStringUTFChars(stackTrace, nullptr);
    EventDetector::getInstance().recordCrash(stackTraceStr);
    env->ReleaseStringUTFChars(stackTrace, stackTraceStr);
}

// ==================== Image Processing ====================

JNIEXPORT jstring JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeProcessBitmap(
    JNIEnv* env,
    jobject thiz,
    jobject bitmap,
    jstring outputPath
) {
    const char* pathStr = env->GetStringUTFChars(outputPath, nullptr);
    std::string path(pathStr);
    env->ReleaseStringUTFChars(outputPath, pathStr);
    
    auto result = ImageProcessor::getInstance().processAndSaveBitmap(env, bitmap, path);
    
    if (result.success) {
        return env->NewStringUTF(result.filePath.c_str());
    } else {
        LOGE("Image processing failed: %s", result.errorMessage.c_str());
        return nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeSetImageConfig(
    JNIEnv* env,
    jobject thiz,
    jint targetWidth,
    jint targetHeight,
    jint quality,
    jboolean useWebP
) {
    ImageProcessor::ProcessingConfig config{
        targetWidth,
        targetHeight,
        quality,
        static_cast<bool>(useWebP)
    };
    ImageProcessor::getInstance().setDefaultConfig(config);
}

JNIEXPORT jboolean JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeIsLowMemory(
    JNIEnv* env,
    jobject thiz
) {
    return static_cast<jboolean>(ImageProcessor::getInstance().isLowMemory());
}

// ==================== Cleanup ====================

JNIEXPORT void JNICALL
Java_com_eslam_metrics_internal_bridge_NativeBridge_nativeReset(
    JNIEnv* env,
    jobject thiz
) {
    SessionManager::getInstance().endSession();
    MetricsAggregator::getInstance().reset();
    EventDetector::getInstance().reset();
    LOGI("Native SDK reset");
}

} // extern "C"
