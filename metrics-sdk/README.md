# MetricsSDK - Session Recording & Performance Metrics

A high-performance, lightweight Native Android Plugin (SDK) for session recording and performance metrics. Built with C++ (NDK) for core logic and Kotlin for API/OS hooks.

## 🎯 Philosophy: "Zero-Lag Observability"

This SDK acts as a "Black Box" recorder for Android apps:
- **Primary Goal:** Capture user sessions, performance bottlenecks (ANR, Crash, Memory), and "visual context" (screenshots) without **ever** blocking the UI thread.
- **Tech Stack:** C++ (NDK) for core logic/heavy lifting, Kotlin for API/OS hooks, JNI for bridging, Room/SQLite for persistence.
- **Philosophy:** "If it's heavy, do it in C++. If it blocks, do it in the background."

## 📋 Features

- ✅ **Automatic Session Management** - Sessions start/end based on app lifecycle
- ✅ **ANR Detection** - Native watchdog detects main thread blocks > 5s
- ✅ **Memory Monitoring** - Detects memory spikes and low memory conditions
- ✅ **Crash Reporting** - Global UncaughtExceptionHandler captures crashes
- ✅ **Screenshot Capture** - Hardware-accelerated PixelCopy for zero UI lag
- ✅ **Native Image Processing** - C++ downscaling and compression
- ✅ **Room Database** - Persistent storage for sessions and events

## 🏗️ Architecture

### Native Layer (`/src/main/cpp`) - C++
- `session/` - Session state machine (start/stop logic)
- `metrics/` - Aggregators for CPU, Memory usage
- `events/` - Logic to detect "Heavy Actions"
- `imaging/` - Bitmap compression logic
- `bridge/` - JNI interface (native_bridge.cpp)

### Android Layer (`/src/main/java`) - Kotlin
- `api/` - Public facing API (MetricsSDK.kt)
- `internal/`
  - `lifecycle/` - ProcessLifecycleOwner hooks
  - `capture/` - ScreenshotManager (PixelCopy implementation)
  - `detection/` - ANR, Crash, Memory monitors
  - `bridge/` - JNI Wrapper classes
- `data/`
  - `room/` - Entities, DAO, Database
  - `repository/` - Repository pattern implementation

## 🚀 Quick Start

### 1. Add Dependency

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":metrics-sdk"))
}
```

### 2. Initialize in Application

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Simple initialization
        MetricsSDK.init(this)
        
        // Or with custom config
        val config = MetricsConfig.builder()
            .enableAnrDetection(true)
            .enableMemoryMonitoring(true)
            .enableCrashReporting(true)
            .enableScreenshots(true)
            .screenshotQuality(40)
            .gracePeriodMs(5000)
            .debugLogging(BuildConfig.DEBUG)
            .build()
        
        MetricsSDK.init(this, config)
    }
}
```

### 3. Set User Info

```kotlin
// After user login
MetricsSDK.setUserInfo("user123", "user@example.com")
```

### 4. Track Actions

```kotlin
// Track simple actions
MetricsSDK.trackAction("button_clicked")

// Track heavy actions with metadata (triggers screenshot)
MetricsSDK.trackHeavyAction(
    "checkout_completed",
    mapOf(
        "orderId" to "12345",
        "amount" to 99.99
    )
)
```

### 5. Get Session ID

```kotlin
val sessionId = MetricsSDK.getSessionId()
```

## 🔧 Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| `gracePeriodMs` | 5000 | Grace period before session ends when app goes to background |
| `enableAnrDetection` | true | Enable ANR detection |
| `enableMemoryMonitoring` | true | Enable memory spike detection |
| `enableCrashReporting` | true | Enable crash reporting |
| `enableScreenshots` | true | Enable screenshot capture on events |
| `memoryThresholdPercent` | 80.0 | Memory usage threshold for spike detection |
| `screenshotQuality` | 40 | Screenshot quality (0-100) |
| `screenshotWidth` | 360 | Target screenshot width |
| `screenshotHeight` | 640 | Target screenshot height |
| `dataRetentionDays` | 7 | Maximum age of stored data |
| `debugLogging` | false | Enable debug logging |

## 📊 Event Types

| Type | Description | Auto-detected |
|------|-------------|---------------|
| ANR | Main thread blocked > 5s | ✅ Yes |
| MEMORY_SPIKE | Memory usage exceeds threshold | ✅ Yes |
| CRASH | Uncaught exception | ✅ Yes |
| HEAVY_ACTION | User-defined heavy action | Manual |
| CUSTOM | Simple user action | Manual |

## 🔒 Constraints & Safety

- **No UI Lag:** All image processing runs off the Main Thread
- **Memory Safety:** Handles OutOfMemory gracefully, skips screenshots in low memory
- **Privacy:** Architecture supports future masking features
- **Google Play Policy:** No abusive background services

## 📁 Database Schema

### SessionTable
- `session_id` (UUID)
- `user_id`, `user_email`
- `device_model`, `os_version`, `app_version`
- `start_time`, `end_time`, `duration_ms`
- `event_count`, `is_crashed`

### EventTable
- `session_id` (FK)
- `event_type`, `event_name`
- `timestamp`, `metadata_json`
- `screenshot_path`
- `memory_usage_mb`, `cpu_usage_percent`

## 🛠️ Building

The SDK uses CMake for native code compilation. Make sure you have NDK installed.

```bash
# Build the module
./gradlew :metrics-sdk:build

# Run tests
./gradlew :metrics-sdk:test
```

## 📝 License

See the [LICENSE](../LICENSE) file for details.
