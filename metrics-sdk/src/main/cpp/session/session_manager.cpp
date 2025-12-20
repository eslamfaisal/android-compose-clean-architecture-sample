#include "session_manager.h"
#include <android/log.h>
#include <random>
#include <sstream>
#include <iomanip>

#define LOG_TAG "MetricsSDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace metrics {

SessionManager& SessionManager::getInstance() {
    static SessionManager instance;
    return instance;
}

SessionManager::SessionManager() {
    LOGI("SessionManager initialized");
}

SessionManager::~SessionManager() {
    LOGI("SessionManager destroyed");
}

std::string SessionManager::generateUUID() {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, 15);
    std::uniform_int_distribution<> dis2(8, 11);

    std::stringstream ss;
    ss << std::hex;

    for (int i = 0; i < 8; i++) ss << dis(gen);
    ss << "-";
    for (int i = 0; i < 4; i++) ss << dis(gen);
    ss << "-4"; // UUID version 4
    for (int i = 0; i < 3; i++) ss << dis(gen);
    ss << "-";
    ss << dis2(gen);
    for (int i = 0; i < 3; i++) ss << dis(gen);
    ss << "-";
    for (int i = 0; i < 12; i++) ss << dis(gen);

    return ss.str();
}

int64_t SessionManager::getCurrentTimeMs() const {
    auto now = std::chrono::system_clock::now();
    auto duration = now.time_since_epoch();
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
}

std::string SessionManager::startSession() {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (state_ == State::ACTIVE) {
        LOGD("Session already active: %s", sessionId_.c_str());
        return sessionId_;
    }

    sessionId_ = generateUUID();
    startTimeMs_ = getCurrentTimeMs();
    state_ = State::ACTIVE;

    LOGI("Session started: %s", sessionId_.c_str());
    return sessionId_;
}

void SessionManager::endSession() {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (state_ == State::IDLE || state_ == State::TERMINATED) {
        LOGD("No active session to end");
        return;
    }

    int64_t duration = getCurrentTimeMs() - startTimeMs_;
    LOGI("Session ended: %s, duration: %lldms", sessionId_.c_str(), duration);
    
    state_ = State::TERMINATED;
    sessionId_.clear();
    startTimeMs_ = 0;
    pauseTimeMs_ = 0;
}

void SessionManager::pauseSession() {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (state_ != State::ACTIVE) {
        LOGD("Cannot pause: session not active");
        return;
    }

    pauseTimeMs_ = getCurrentTimeMs();
    state_ = State::BACKGROUND;
    LOGD("Session paused: %s", sessionId_.c_str());
}

void SessionManager::resumeSession() {
    std::lock_guard<std::mutex> lock(mutex_);
    
    if (state_ != State::BACKGROUND) {
        LOGD("Cannot resume: session not in background");
        return;
    }

    int64_t pauseDuration = getCurrentTimeMs() - pauseTimeMs_;
    
    // Check grace period
    if (pauseDuration > gracePeriodMs_) {
        LOGI("Grace period exceeded (%lldms > %lldms), ending session", 
             pauseDuration, gracePeriodMs_);
        state_ = State::TERMINATED;
        sessionId_.clear();
        return;
    }

    state_ = State::ACTIVE;
    pauseTimeMs_ = 0;
    LOGD("Session resumed: %s", sessionId_.c_str());
}

std::string SessionManager::getSessionId() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return sessionId_;
}

SessionManager::State SessionManager::getState() const {
    return state_.load();
}

int64_t SessionManager::getSessionDurationMs() const {
    std::lock_guard<std::mutex> lock(mutex_);
    if (state_ == State::IDLE || startTimeMs_ == 0) {
        return 0;
    }
    return getCurrentTimeMs() - startTimeMs_;
}

bool SessionManager::isActive() const {
    return state_ == State::ACTIVE;
}

void SessionManager::setUserInfo(const std::string& userId, const std::string& email) {
    std::lock_guard<std::mutex> lock(mutex_);
    userId_ = userId;
    userEmail_ = email;
    LOGD("User info set: %s", userId.c_str());
}

std::string SessionManager::getUserId() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return userId_;
}

std::string SessionManager::getUserEmail() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return userEmail_;
}

void SessionManager::setGracePeriodMs(int64_t gracePeriodMs) {
    std::lock_guard<std::mutex> lock(mutex_);
    gracePeriodMs_ = gracePeriodMs;
    LOGD("Grace period set to: %lldms", gracePeriodMs);
}

} // namespace metrics
