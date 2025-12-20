# Keep the public API
-keep class com.eslam.metrics.api.** { *; }
-keep class com.eslam.metrics.MetricsSDK { *; }

# Keep JNI methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Room entities
-keep class com.eslam.metrics.data.room.** { *; }

# Keep Moshi adapters
-keep class com.eslam.metrics.data.**JsonAdapter { *; }
