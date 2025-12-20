#ifndef METRICS_SDK_SESSION_MANAGER_H
#define METRICS_SDK_SESSION_MANAGER_H

#include <string>
#include <mutex>
#include <chrono>
#include <atomic>

namespace metrics {

/**
 * SessionManager - Native session state machine
 * 
 * Manages session lifecycle with thread-safe operations.
 * Handles session start/stop logic with configurable grace period.
 */
class SessionManager {
public:
    enum class State {
        IDLE,
        ACTIVE,
        BACKGROUND,
        TERMINATED
    };

    static SessionManager& getInstance();

    // Prevent copying
    SessionManager(const SessionManager&) = delete;
    SessionManager& operator=(const SessionManager&) = delete;

    // Session lifecycle
    std::string startSession();
    void endSession();
    void pauseSession();
    void resumeSession();

    // Getters
    std::string getSessionId() const;
    State getState() const;
    int64_t getSessionDurationMs() const;
    bool isActive() const;

    // User info
    void setUserInfo(const std::string& userId, const std::string& email);
    std::string getUserId() const;
    std::string getUserEmail() const;

    // Configuration
    void setGracePeriodMs(int64_t gracePeriodMs);

private:
    SessionManager();
    ~SessionManager();

    std::string generateUUID();
    int64_t getCurrentTimeMs() const;

    mutable std::mutex mutex_;
    std::atomic<State> state_{State::IDLE};
    
    std::string sessionId_;
    std::string userId_;
    std::string userEmail_;
    
    int64_t startTimeMs_{0};
    int64_t pauseTimeMs_{0};
    int64_t gracePeriodMs_{5000}; // 5 seconds default
};

} // namespace metrics

#endif // METRICS_SDK_SESSION_MANAGER_H
