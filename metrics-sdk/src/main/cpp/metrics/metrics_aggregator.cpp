#include "metrics_aggregator.h"
#include "session/session_manager.h"
#include <android/log.h>

#define LOG_TAG "MetricsSDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace metrics {

MetricsAggregator& MetricsAggregator::getInstance() {
    static MetricsAggregator instance;
    return instance;
}

MetricsAggregator::MetricsAggregator() {
    LOGI("MetricsAggregator initialized");
}

MetricsAggregator::~MetricsAggregator() {
    LOGI("MetricsAggregator destroyed");
}

int64_t MetricsAggregator::getCurrentTimeMs() const {
    auto now = std::chrono::system_clock::now();
    auto duration = now.time_since_epoch();
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
}

void MetricsAggregator::recordMemoryMetrics(int64_t totalMb, int64_t usedMb, int64_t availableMb) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    float usagePercentage = totalMb > 0 ? (static_cast<float>(usedMb) / totalMb) * 100.0f : 0.0f;
    
    latestMemory_ = {
        totalMb,
        usedMb,
        availableMb,
        usagePercentage,
        getCurrentTimeMs()
    };
    
    // Store in history
    memoryHistory_.push_back(latestMemory_);
    if (memoryHistory_.size() > MAX_HISTORY_SIZE) {
        memoryHistory_.erase(memoryHistory_.begin());
    }
    
    LOGD("Memory recorded: %lldMB / %lldMB (%.1f%%)", usedMb, totalMb, usagePercentage);
    
    if (usagePercentage > memoryThreshold_.load()) {
        LOGW("Memory spike detected: %.1f%% > %.1f%%", usagePercentage, memoryThreshold_.load());
    }
}

void MetricsAggregator::recordCpuMetrics(float usagePercentage, int coreCount) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    latestCpu_ = {
        usagePercentage,
        coreCount,
        getCurrentTimeMs()
    };
    
    // Store in history
    cpuHistory_.push_back(latestCpu_);
    if (cpuHistory_.size() > MAX_HISTORY_SIZE) {
        cpuHistory_.erase(cpuHistory_.begin());
    }
    
    LOGD("CPU recorded: %.1f%% (%d cores)", usagePercentage, coreCount);
    
    if (usagePercentage > cpuThreshold_.load()) {
        LOGW("CPU spike detected: %.1f%% > %.1f%%", usagePercentage, cpuThreshold_.load());
    }
}

MetricsAggregator::MemoryMetrics MetricsAggregator::getLatestMemoryMetrics() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return latestMemory_;
}

MetricsAggregator::CpuMetrics MetricsAggregator::getLatestCpuMetrics() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return latestCpu_;
}

MetricsAggregator::PerformanceSnapshot MetricsAggregator::getSnapshot() const {
    std::lock_guard<std::mutex> lock(mutex_);
    
    PerformanceSnapshot snapshot;
    snapshot.memory = latestMemory_;
    snapshot.cpu = latestCpu_;
    snapshot.sessionId = SessionManager::getInstance().getSessionId();
    snapshot.sessionDurationMs = SessionManager::getInstance().getSessionDurationMs();
    
    return snapshot;
}

void MetricsAggregator::setMemoryThresholdPercentage(float threshold) {
    memoryThreshold_.store(threshold);
    LOGD("Memory threshold set to: %.1f%%", threshold);
}

void MetricsAggregator::setCpuThresholdPercentage(float threshold) {
    cpuThreshold_.store(threshold);
    LOGD("CPU threshold set to: %.1f%%", threshold);
}

bool MetricsAggregator::isMemorySpike() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return latestMemory_.usagePercentage > memoryThreshold_.load();
}

bool MetricsAggregator::isCpuSpike() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return latestCpu_.cpuUsagePercentage > cpuThreshold_.load();
}

void MetricsAggregator::reset() {
    std::lock_guard<std::mutex> lock(mutex_);
    latestMemory_ = {};
    latestCpu_ = {};
    memoryHistory_.clear();
    cpuHistory_.clear();
    LOGI("MetricsAggregator reset");
}

} // namespace metrics
