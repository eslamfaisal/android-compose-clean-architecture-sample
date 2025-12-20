#ifndef METRICS_SDK_IMAGE_PROCESSOR_H
#define METRICS_SDK_IMAGE_PROCESSOR_H

#include <jni.h>
#include <string>
#include <mutex>
#include <cstdint>

namespace metrics {

/**
 * ImageProcessor - Native image processing engine
 * 
 * Handles bitmap downscaling and compression for screenshots.
 * Processes images off the main thread to prevent UI lag.
 */
class ImageProcessor {
public:
    struct ProcessingConfig {
        int targetWidth;
        int targetHeight;
        int quality;           // 0-100 for JPEG/WebP
        bool useWebP;          // true for WebP, false for JPEG
    };

    struct ProcessingResult {
        bool success;
        std::string filePath;
        int originalWidth;
        int originalHeight;
        int processedWidth;
        int processedHeight;
        size_t originalSizeBytes;
        size_t processedSizeBytes;
        int64_t processingTimeMs;
        std::string errorMessage;
    };

    static ImageProcessor& getInstance();

    // Prevent copying
    ImageProcessor(const ImageProcessor&) = delete;
    ImageProcessor& operator=(const ImageProcessor&) = delete;

    // Configuration
    void setDefaultConfig(const ProcessingConfig& config);
    ProcessingConfig getDefaultConfig() const;

    // Processing
    ProcessingResult processAndSaveBitmap(
        JNIEnv* env,
        jobject bitmap,
        const std::string& outputPath
    );

    ProcessingResult processAndSaveBitmapBuffer(
        const uint8_t* pixelBuffer,
        int width,
        int height,
        int stride,
        const std::string& outputPath
    );

    // Utilities
    void setStorageDirectory(const std::string& directory);
    std::string getStorageDirectory() const;
    std::string generateFilename(const std::string& prefix);

    // Memory management
    bool isLowMemory() const;
    void setLowMemoryThreshold(size_t thresholdBytes);

private:
    ImageProcessor();
    ~ImageProcessor();

    bool downscalePixels(
        const uint8_t* srcPixels,
        int srcWidth,
        int srcHeight,
        int srcStride,
        uint8_t* dstPixels,
        int dstWidth,
        int dstHeight
    );

    bool writeJpeg(
        const uint8_t* pixels,
        int width,
        int height,
        int quality,
        const std::string& outputPath
    );

    int64_t getCurrentTimeMs() const;

    mutable std::mutex mutex_;
    ProcessingConfig defaultConfig_{360, 640, 40, false}; // 360p, 40% quality, JPEG
    std::string storageDirectory_;
    size_t lowMemoryThreshold_{50 * 1024 * 1024}; // 50MB default
};

} // namespace metrics

#endif // METRICS_SDK_IMAGE_PROCESSOR_H
