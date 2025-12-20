#ifndef METRICS_SDK_EVENT_DETECTOR_H
#define METRICS_SDK_EVENT_DETECTOR_H

#include <string>
#include <mutex>
#include <vector>
#include <functional>
#include <chrono>
#include <thread>
#include <atomic>

namespace metrics {

/**
 * EventDetector - Native event detection engine
 * 
 * Detects heavy actions, ANR conditions, and triggers events.
 * Uses native watchdog for main thread monitoring.
 */
class EventDetector {
public:
    enum class EventType {
        ANR,
        MEMORY_SPIKE,
        CPU_SPIKE,
        CRASH,
        HEAVY_ACTION,
        CUSTOM
    };

    struct Event {
        EventType type;
        std::string name;
        std::string metadata;
        int64_t timestampMs;
        std::string sessionId;
        bool screenshotTaken;
    };

    using EventCallback = std::function<void(const Event&)>;

    static EventDetector& getInstance();

    // Prevent copying
    EventDetector(const EventDetector&) = delete;
    EventDetector& operator=(const EventDetector&) = delete;

    // Event detection
    void startWatchdog();
    void stopWatchdog();
    void pingWatchdog(); // Called from main thread

    // Event recording
    void recordEvent(EventType type, const std::string& name, const std::string& metadata);
    void recordHeavyAction(const std::string& name, const std::string& metadata);
    void recordCrash(const std::string& stackTrace);

    // Callback registration
    void setEventCallback(EventCallback callback);

    // Configuration
    void setAnrThresholdMs(int64_t thresholdMs);
    
    // Getters
    std::vector<Event> getRecentEvents(size_t count) const;
    size_t getEventCount() const;

    // Reset
    void reset();

private:
    EventDetector();
    ~EventDetector();

    void watchdogLoop();
    int64_t getCurrentTimeMs() const;
    std::string eventTypeToString(EventType type) const;

    mutable std::mutex mutex_;
    std::mutex watchdogMutex_;
    
    std::vector<Event> events_;
    EventCallback eventCallback_;
    
    std::atomic<bool> watchdogRunning_{false};
    std::atomic<int64_t> lastPingTimeMs_{0};
    std::atomic<int64_t> anrThresholdMs_{5000}; // 5 seconds default
    
    std::thread watchdogThread_;
    
    static constexpr size_t MAX_EVENTS = 500;
    static constexpr int64_t WATCHDOG_CHECK_INTERVAL_MS = 500;
};

} // namespace metrics

#endif // METRICS_SDK_EVENT_DETECTOR_H
