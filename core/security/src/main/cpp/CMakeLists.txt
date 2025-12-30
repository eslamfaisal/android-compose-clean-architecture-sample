# CMakeLists.txt for Native Key Provider
# This builds the native library for secure API key storage

cmake_minimum_required(VERSION 3.22.1)

# Define the project
project("bakingapp-security" VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Enable security hardening flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -ffunction-sections -fdata-sections")

# Strip symbols in release builds (makes reverse engineering harder)
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG")
set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} -Wl,--gc-sections -s")

# Create the shared library
add_library(
    # Library name - must match System.loadLibrary() call in Kotlin
    native-keys
    
    # Type: SHARED = .so file
    SHARED
    
    # Source files
    native-keys.cpp
)

# Find and link required libraries
find_library(
    log-lib
    log
)

# Link libraries
target_link_libraries(
    native-keys
    ${log-lib}
)

# 16 KB page size alignment for Android 15+ compatibility
target_link_options(native-keys PRIVATE "-Wl,-z,max-page-size=16384")

# Optional: Add additional compiler warnings for development
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    target_compile_options(native-keys PRIVATE
        -Wall
        -Wextra
        -Wpedantic
    )
endif()
