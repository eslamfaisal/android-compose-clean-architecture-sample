plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.eslam.bakingapp.core.security"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        // NDK Configuration - specify which ABIs to build for
        ndk {
            // Include all major architectures
            // arm64-v8a: Most modern Android devices (64-bit ARM)
            // armeabi-v7a: Older Android devices (32-bit ARM)
            // x86_64: Emulators and some Chromebooks
            // x86: Older emulators
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }
        
        // External native build arguments
        externalNativeBuild {
            cmake {
                // C++ flags for all build types
                cppFlags += listOf(
                    "-std=c++17",
                    "-fvisibility=hidden"
                )
                // Additional arguments
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_TOOLCHAIN=clang"
                )
            }
        }
    }

    buildTypes {
        debug {
            // Keep debug symbols for debugging native code
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=Debug"
                }
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Strip symbols and optimize for release
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=Release"
                    cppFlags += listOf("-O3", "-DNDEBUG")
                }
            }
        }
    }
    
    // CMake build configuration
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    // Enable prefab for native library publishing (optional, for multi-module native deps)
    buildFeatures {
        prefab = false
    }

    // NDK required for Android 15+ (16KB page alignment)
    ndkVersion = libs.versions.ndk.get()
}

dependencies {
    // Module dependencies
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    
    // Security
    api(libs.androidx.security.crypto)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
}
