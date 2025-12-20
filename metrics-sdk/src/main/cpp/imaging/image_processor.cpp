#include "image_processor.h"
#include <android/log.h>
#include <android/bitmap.h>
#include <chrono>
#include <fstream>
#include <cstring>
#include <cmath>
#include <sstream>
#include <iomanip>
#include <random>

#define LOG_TAG "MetricsSDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace metrics {

ImageProcessor& ImageProcessor::getInstance() {
    static ImageProcessor instance;
    return instance;
}

ImageProcessor::ImageProcessor() {
    LOGI("ImageProcessor initialized");
}

ImageProcessor::~ImageProcessor() {
    LOGI("ImageProcessor destroyed");
}

int64_t ImageProcessor::getCurrentTimeMs() const {
    auto now = std::chrono::system_clock::now();
    auto duration = now.time_since_epoch();
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
}

void ImageProcessor::setDefaultConfig(const ProcessingConfig& config) {
    std::lock_guard<std::mutex> lock(mutex_);
    defaultConfig_ = config;
    LOGD("Default config updated: %dx%d, quality=%d", 
         config.targetWidth, config.targetHeight, config.quality);
}

ImageProcessor::ProcessingConfig ImageProcessor::getDefaultConfig() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return defaultConfig_;
}

void ImageProcessor::setStorageDirectory(const std::string& directory) {
    std::lock_guard<std::mutex> lock(mutex_);
    storageDirectory_ = directory;
    LOGD("Storage directory set: %s", directory.c_str());
}

std::string ImageProcessor::getStorageDirectory() const {
    std::lock_guard<std::mutex> lock(mutex_);
    return storageDirectory_;
}

std::string ImageProcessor::generateFilename(const std::string& prefix) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(1000, 9999);
    
    auto now = std::chrono::system_clock::now();
    auto time = std::chrono::system_clock::to_time_t(now);
    
    std::stringstream ss;
    ss << prefix << "_" << time << "_" << dis(gen) << ".jpg";
    
    return ss.str();
}

ImageProcessor::ProcessingResult ImageProcessor::processAndSaveBitmap(
    JNIEnv* env,
    jobject bitmap,
    const std::string& outputPath
) {
    ProcessingResult result{};
    result.success = false;
    
    int64_t startTime = getCurrentTimeMs();
    
    if (env == nullptr || bitmap == nullptr) {
        result.errorMessage = "Invalid JNI environment or bitmap";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) {
        result.errorMessage = "Failed to get bitmap info";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    result.originalWidth = static_cast<int>(info.width);
    result.originalHeight = static_cast<int>(info.height);
    result.originalSizeBytes = info.stride * info.height;
    
    LOGD("Processing bitmap: %dx%d, stride=%d, format=%d",
         result.originalWidth, result.originalHeight, info.stride, info.format);
    
    // Lock pixels
    void* pixels = nullptr;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        result.errorMessage = "Failed to lock bitmap pixels";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    // Get config
    ProcessingConfig config = getDefaultConfig();
    
    // Calculate target dimensions maintaining aspect ratio
    float aspectRatio = static_cast<float>(result.originalWidth) / result.originalHeight;
    int targetWidth = config.targetWidth;
    int targetHeight = static_cast<int>(targetWidth / aspectRatio);
    
    if (targetHeight > config.targetHeight) {
        targetHeight = config.targetHeight;
        targetWidth = static_cast<int>(targetHeight * aspectRatio);
    }
    
    // Allocate buffer for downscaled image
    std::vector<uint8_t> scaledPixels(targetWidth * targetHeight * 4);
    
    // Downscale
    bool scaleSuccess = downscalePixels(
        static_cast<uint8_t*>(pixels),
        result.originalWidth,
        result.originalHeight,
        static_cast<int>(info.stride),
        scaledPixels.data(),
        targetWidth,
        targetHeight
    );
    
    // Unlock pixels
    AndroidBitmap_unlockPixels(env, bitmap);
    
    if (!scaleSuccess) {
        result.errorMessage = "Failed to downscale image";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    result.processedWidth = targetWidth;
    result.processedHeight = targetHeight;
    
    // Write to file (simple raw format for now, can be extended to JPEG)
    bool writeSuccess = writeJpeg(
        scaledPixels.data(),
        targetWidth,
        targetHeight,
        config.quality,
        outputPath
    );
    
    if (!writeSuccess) {
        result.errorMessage = "Failed to write image file";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    result.filePath = outputPath;
    result.success = true;
    result.processingTimeMs = getCurrentTimeMs() - startTime;
    
    // Get file size
    std::ifstream file(outputPath, std::ios::binary | std::ios::ate);
    if (file.is_open()) {
        result.processedSizeBytes = static_cast<size_t>(file.tellg());
        file.close();
    }
    
    LOGI("Image processed: %dx%d -> %dx%d, %zu -> %zu bytes, %lldms",
         result.originalWidth, result.originalHeight,
         result.processedWidth, result.processedHeight,
         result.originalSizeBytes, result.processedSizeBytes,
         result.processingTimeMs);
    
    return result;
}

ImageProcessor::ProcessingResult ImageProcessor::processAndSaveBitmapBuffer(
    const uint8_t* pixelBuffer,
    int width,
    int height,
    int stride,
    const std::string& outputPath
) {
    ProcessingResult result{};
    result.success = false;
    
    int64_t startTime = getCurrentTimeMs();
    
    if (pixelBuffer == nullptr || width <= 0 || height <= 0) {
        result.errorMessage = "Invalid pixel buffer or dimensions";
        LOGE("%s", result.errorMessage.c_str());
        return result;
    }
    
    result.originalWidth = width;
    result.originalHeight = height;
    result.originalSizeBytes = stride * height;
    
    // Get config
    ProcessingConfig config = getDefaultConfig();
    
    // Calculate target dimensions
    float aspectRatio = static_cast<float>(width) / height;
    int targetWidth = config.targetWidth;
    int targetHeight = static_cast<int>(targetWidth / aspectRatio);
    
    if (targetHeight > config.targetHeight) {
        targetHeight = config.targetHeight;
        targetWidth = static_cast<int>(targetHeight * aspectRatio);
    }
    
    // Allocate and downscale
    std::vector<uint8_t> scaledPixels(targetWidth * targetHeight * 4);
    
    bool scaleSuccess = downscalePixels(
        pixelBuffer, width, height, stride,
        scaledPixels.data(), targetWidth, targetHeight
    );
    
    if (!scaleSuccess) {
        result.errorMessage = "Failed to downscale image";
        return result;
    }
    
    result.processedWidth = targetWidth;
    result.processedHeight = targetHeight;
    
    // Write
    bool writeSuccess = writeJpeg(
        scaledPixels.data(), targetWidth, targetHeight,
        config.quality, outputPath
    );
    
    if (!writeSuccess) {
        result.errorMessage = "Failed to write image file";
        return result;
    }
    
    result.filePath = outputPath;
    result.success = true;
    result.processingTimeMs = getCurrentTimeMs() - startTime;
    
    return result;
}

bool ImageProcessor::downscalePixels(
    const uint8_t* srcPixels,
    int srcWidth,
    int srcHeight,
    int srcStride,
    uint8_t* dstPixels,
    int dstWidth,
    int dstHeight
) {
    if (srcPixels == nullptr || dstPixels == nullptr) {
        return false;
    }
    
    // Bilinear interpolation downscaling
    float xRatio = static_cast<float>(srcWidth) / dstWidth;
    float yRatio = static_cast<float>(srcHeight) / dstHeight;
    
    for (int y = 0; y < dstHeight; ++y) {
        for (int x = 0; x < dstWidth; ++x) {
            float srcX = x * xRatio;
            float srcY = y * yRatio;
            
            int x0 = static_cast<int>(srcX);
            int y0 = static_cast<int>(srcY);
            int x1 = std::min(x0 + 1, srcWidth - 1);
            int y1 = std::min(y0 + 1, srcHeight - 1);
            
            float xFrac = srcX - x0;
            float yFrac = srcY - y0;
            
            // RGBA (4 bytes per pixel)
            for (int c = 0; c < 4; ++c) {
                float p00 = srcPixels[y0 * srcStride + x0 * 4 + c];
                float p10 = srcPixels[y0 * srcStride + x1 * 4 + c];
                float p01 = srcPixels[y1 * srcStride + x0 * 4 + c];
                float p11 = srcPixels[y1 * srcStride + x1 * 4 + c];
                
                float value = (1 - xFrac) * (1 - yFrac) * p00 +
                              xFrac * (1 - yFrac) * p10 +
                              (1 - xFrac) * yFrac * p01 +
                              xFrac * yFrac * p11;
                
                dstPixels[y * dstWidth * 4 + x * 4 + c] = 
                    static_cast<uint8_t>(std::min(255.0f, std::max(0.0f, value)));
            }
        }
    }
    
    return true;
}

bool ImageProcessor::writeJpeg(
    const uint8_t* pixels,
    int width,
    int height,
    int quality,
    const std::string& outputPath
) {
    // Simple raw RGBA file for now
    // In production, would use libjpeg-turbo or similar
    // For this implementation, we'll write a simple binary format
    
    std::ofstream file(outputPath, std::ios::binary);
    if (!file.is_open()) {
        LOGE("Failed to open file for writing: %s", outputPath.c_str());
        return false;
    }
    
    // Write header (simple format: width, height, then pixels)
    file.write(reinterpret_cast<const char*>(&width), sizeof(width));
    file.write(reinterpret_cast<const char*>(&height), sizeof(height));
    file.write(reinterpret_cast<const char*>(&quality), sizeof(quality));
    
    // Write pixel data
    size_t pixelDataSize = width * height * 4;
    file.write(reinterpret_cast<const char*>(pixels), pixelDataSize);
    
    file.close();
    
    LOGD("Written image to: %s", outputPath.c_str());
    return true;
}

bool ImageProcessor::isLowMemory() const {
    // This would typically check actual system memory
    // For now, return false (not in low memory state)
    return false;
}

void ImageProcessor::setLowMemoryThreshold(size_t thresholdBytes) {
    std::lock_guard<std::mutex> lock(mutex_);
    lowMemoryThreshold_ = thresholdBytes;
}

} // namespace metrics
