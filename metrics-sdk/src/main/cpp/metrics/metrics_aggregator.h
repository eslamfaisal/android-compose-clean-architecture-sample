#ifndef METRICS_SDK_METRICS_AGGREGATOR_H
#define METRICS_SDK_METRICS_AGGREGATOR_H

#include <string>
#include <mutex>
#include <vector>
#include <chrono>
#include <atomic>

namespace metrics {

/**
 * MetricsAggregator - Native performance metrics collector
 * 
 * Aggregates CPU, memory, and custom performance metrics.
 * Thread-safe operations for background collection.
 */
class MetricsAggregator {
public:
    struct MemoryMetrics {
        int64_t totalMemoryMb;
        int64_t usedMemoryMb;
        int64_t availableMemoryMb;
        float usagePercentage;
        int64_t timestampMs;
    };

    struct CpuMetrics {
        float cpuUsagePercentage;
        int coreCount;
        int64_t timestampMs;
    };

    struct PerformanceSnapshot {
        MemoryMetrics memory;
        CpuMetrics cpu;
        int64_t sessionDurationMs;
        std::string sessionId;
    };

    static MetricsAggregator& getInstance();

    // Prevent copying
    MetricsAggregator(const MetricsAggregator&) = delete;
    MetricsAggregator& operator=(const MetricsAggregator&) = delete;

    // Recording
    void recordMemoryMetrics(int64_t totalMb, int64_t usedMb, int64_t availableMb);
    void recordCpuMetrics(float usagePercentage, int coreCount);

    // Getters
    MemoryMetrics getLatestMemoryMetrics() const;
    CpuMetrics getLatestCpuMetrics() const;
    PerformanceSnapshot getSnapshot() const;

    // Thresholds
    void setMemoryThresholdPercentage(float threshold);
    void setCpuThresholdPercentage(float threshold);
    bool isMemorySpike() const;
    bool isCpuSpike() const;

    // Reset
    void reset();

private:
    MetricsAggregator();
    ~MetricsAggregator();

    int64_t getCurrentTimeMs() const;

    mutable std::mutex mutex_;
    
    MemoryMetrics latestMemory_{};
    CpuMetrics latestCpu_{};
    
    std::atomic<float> memoryThreshold_{80.0f}; // 80% default
    std::atomic<float> cpuThreshold_{90.0f};     // 90% default
    
    std::vector<MemoryMetrics> memoryHistory_;
    std::vector<CpuMetrics> cpuHistory_;
    
    static constexpr size_t MAX_HISTORY_SIZE = 100;
};

} // namespace metrics

#endif // METRICS_SDK_METRICS_AGGREGATOR_H
