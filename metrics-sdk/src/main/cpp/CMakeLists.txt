# CMakeLists.txt for MetricsSDK Native Layer
# Minimum required version of CMake
cmake_minimum_required(VERSION 3.22.1)

# Project name
project("metrics_sdk" LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable position independent code
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Add compile definitions
add_compile_definitions(
    ANDROID
    LOG_TAG="MetricsSDK"
)

# Compiler flags for optimization
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -O2")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -O0")

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)

# Header files directories
include_directories(
    ${CMAKE_SOURCE_DIR}
    ${CMAKE_SOURCE_DIR}/session
    ${CMAKE_SOURCE_DIR}/metrics
    ${CMAKE_SOURCE_DIR}/events
    ${CMAKE_SOURCE_DIR}/imaging
    ${CMAKE_SOURCE_DIR}/bridge
)

# Source files
set(SESSION_SOURCES
    session/session_manager.cpp
)

set(METRICS_SOURCES
    metrics/metrics_aggregator.cpp
)

set(EVENTS_SOURCES
    events/event_detector.cpp
)

set(IMAGING_SOURCES
    imaging/image_processor.cpp
)

set(BRIDGE_SOURCES
    bridge/native_bridge.cpp
)

# Create shared library
add_library(metrics_sdk SHARED
    ${SESSION_SOURCES}
    ${METRICS_SOURCES}
    ${EVENTS_SOURCES}
    ${IMAGING_SOURCES}
    ${BRIDGE_SOURCES}
)

# Link libraries
target_link_libraries(metrics_sdk
    ${log-lib}
    ${android-lib}
    ${jnigraphics-lib}
)

# Set library properties
set_target_properties(metrics_sdk PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}"
)
