#include "event_detector.h"
#include "session/session_manager.h"
#include <android/log.h>

#define LOG_TAG "MetricsSDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace metrics {

EventDetector& EventDetector::getInstance() {
    static EventDetector instance;
    return instance;
}

EventDetector::EventDetector() {
    LOGI("EventDetector initialized");
}

EventDetector::~EventDetector() {
    stopWatchdog();
    LOGI("EventDetector destroyed");
}

int64_t EventDetector::getCurrentTimeMs() const {
    auto now = std::chrono::system_clock::now();
    auto duration = now.time_since_epoch();
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
}

std::string EventDetector::eventTypeToString(EventType type) const {
    switch (type) {
        case EventType::ANR: return "ANR";
        case EventType::MEMORY_SPIKE: return "MEMORY_SPIKE";
        case EventType::CPU_SPIKE: return "CPU_SPIKE";
        case EventType::CRASH: return "CRASH";
        case EventType::HEAVY_ACTION: return "HEAVY_ACTION";
        case EventType::CUSTOM: return "CUSTOM";
        default: return "UNKNOWN";
    }
}

void EventDetector::startWatchdog() {
    std::lock_guard<std::mutex> lock(watchdogMutex_);
    
    if (watchdogRunning_.load()) {
        LOGD("Watchdog already running");
        return;
    }

    watchdogRunning_.store(true);
    lastPingTimeMs_.store(getCurrentTimeMs());
    
    watchdogThread_ = std::thread(&EventDetector::watchdogLoop, this);
    LOGI("Watchdog started with threshold: %lldms", anrThresholdMs_.load());
}

void EventDetector::stopWatchdog() {
    watchdogRunning_.store(false);
    
    if (watchdogThread_.joinable()) {
        watchdogThread_.join();
    }
    
    LOGI("Watchdog stopped");
}

void EventDetector::pingWatchdog() {
    lastPingTimeMs_.store(getCurrentTimeMs());
}

void EventDetector::watchdogLoop() {
    while (watchdogRunning_.load()) {
        std::this_thread::sleep_for(std::chrono::milliseconds(WATCHDOG_CHECK_INTERVAL_MS));
        
        if (!watchdogRunning_.load()) break;
        
        int64_t currentTime = getCurrentTimeMs();
        int64_t lastPing = lastPingTimeMs_.load();
        int64_t elapsed = currentTime - lastPing;
        
        if (elapsed > anrThresholdMs_.load()) {
            LOGE("ANR detected! Main thread blocked for %lldms", elapsed);
            recordEvent(EventType::ANR, "MainThreadBlocked", 
                       "{\"blockedDurationMs\":" + std::to_string(elapsed) + "}");
        }
    }
}

void EventDetector::recordEvent(EventType type, const std::string& name, const std::string& metadata) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    Event event{
        type,
        name,
        metadata,
        getCurrentTimeMs(),
        SessionManager::getInstance().getSessionId(),
        false // screenshot taken later
    };
    
    events_.push_back(event);
    if (events_.size() > MAX_EVENTS) {
        events_.erase(events_.begin());
    }
    
    LOGI("Event recorded: %s - %s", eventTypeToString(type).c_str(), name.c_str());
    
    // Notify callback
    if (eventCallback_) {
        eventCallback_(event);
    }
}

void EventDetector::recordHeavyAction(const std::string& name, const std::string& metadata) {
    recordEvent(EventType::HEAVY_ACTION, name, metadata);
}

void EventDetector::recordCrash(const std::string& stackTrace) {
    recordEvent(EventType::CRASH, "AppCrash", stackTrace);
}

void EventDetector::setEventCallback(EventCallback callback) {
    std::lock_guard<std::mutex> lock(mutex_);
    eventCallback_ = std::move(callback);
    LOGD("Event callback registered");
}

void EventDetector::setAnrThresholdMs(int64_t thresholdMs) {
    anrThresholdMs_.store(thresholdMs);
    LOGD("ANR threshold set to: %lldms", thresholdMs);
}

std::vector<EventDetector::Event> EventDetector::getRecentEvents(size_t count) const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (count >= events_.size()) {
        return events_;
    }
    
    return std::vector<Event>(events_.end() - count, events_.end());
}

size_t EventDetector::getEventCount() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return events_.size();
}

void EventDetector::reset() {
    std::lock_guard<std::mutex> lock(mutex_);
    events_.clear();
    LOGI("EventDetector reset");
}

} // namespace metrics
