plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.eslam.bakingapp.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        // BuildConfig fields for API configuration
        buildConfigField("String", "BASE_URL", "\"https://api.bakingapp.com/\"")
        buildConfigField("long", "CONNECT_TIMEOUT", "30L")
        buildConfigField("long", "READ_TIMEOUT", "30L")
        buildConfigField("long", "WRITE_TIMEOUT", "30L")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Module dependencies
    implementation(project(":core:common"))
    
    // Networking
    api(libs.retrofit)
    api(libs.retrofit.moshi)
    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.coroutines.test)
}

