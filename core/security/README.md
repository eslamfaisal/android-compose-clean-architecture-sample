# Core Security Module

This module provides secure storage and retrieval of sensitive data, including API keys stored in native (C++) code for enhanced security.

## 🔐 Native Key Provider

The `NativeKeyProvider` stores API keys in native C++ code with XOR obfuscation, making reverse engineering significantly harder than storing keys in Java/Kotlin code or BuildConfig.

### Security Layers

1. **Native Code Storage** - Keys stored in ARM/x86 assembly after compilation
2. **XOR Obfuscation** - Keys not visible in hex editors or string dumps
3. **String Splitting** - No complete key appears in one location
4. **Package Verification** - Keys only work with the correct package name

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                     │
├─────────────────────────────────────────────────────────┤
│  @Inject lateinit var apiKeyProvider: ApiKeyProvider    │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              Hilt Dependency Injection                   │
├─────────────────────────────────────────────────────────┤
│  SecurityModule.provideApiKeyProvider()                 │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                  ApiKeyProvider                          │
│                   (Interface)                            │
├─────────────────────────────────────────────────────────┤
│  + getApiKey(): String                                  │
│  + getSecretKey(): String                               │
│  + getAppIdentifier(): String                           │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│               NativeKeyProvider                          │
│                 (Kotlin)                                 │
├─────────────────────────────────────────────────────────┤
│  System.loadLibrary("native-keys")                      │
│  external fun getApiKeyNative(context): String          │
└────────────────────────┬────────────────────────────────┘
                         │ JNI
┌────────────────────────▼────────────────────────────────┐
│               native-keys.cpp                            │
│                  (C++)                                   │
├─────────────────────────────────────────────────────────┤
│  XOR-encoded keys                                       │
│  Package name verification                              │
│  Runtime decoding                                       │
└─────────────────────────────────────────────────────────┘
```

## 📱 Usage

### Basic Usage

```kotlin
@AndroidEntryPoint
class MyActivity : AppCompatActivity() {
    
    @Inject
    lateinit var apiKeyProvider: ApiKeyProvider
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get API key securely
        val apiKey = apiKeyProvider.getApiKey()
        val secretKey = apiKeyProvider.getSecretKey()
        
        // Initialize your SDK
        SomeSDK.initialize(apiKey, secretKey)
    }
}
```

### Safe Access (Nullable)

```kotlin
val apiKey = apiKeyProvider.getApiKeyOrNull() ?: run {
    Log.e(TAG, "API key not available")
    return
}
```

### Checking Availability

```kotlin
if (apiKeyProvider.isAvailable()) {
    // Keys are available
} else {
    // Handle unavailable keys (e.g., native library not loaded)
}
```

## 🔧 Encoding New Keys

Use the provided Python script to encode your keys:

```bash
# Encode an API key
cd core/security/scripts
python encode_keys.py "your_actual_api_key" 0x5A --verify

# Example output:
# Original key: your_actual_api_key
# Key length: 18 characters
# XOR key: 0x5A
#
# C++ vector initializer:
# const std::vector<char> YOUR_KEY_ENCODED = {
#     0x2f, 0x35, 0x31, 0x28, 0x1a, 0x3f, 0x3d, 0x28
#     ...
# };
```

Then paste the output into `native-keys.cpp`.

## ⚠️ Important Security Notes

1. **Never commit real production keys** to version control
2. **Use CI/CD** to inject real keys during build
3. **Rotate keys** periodically
4. **Apply server-side restrictions**:
   - Restrict by package name
   - Restrict by SHA-1 certificate
   - Limit API scope

## 🏗️ Build Requirements

- **NDK**: Android NDK (install via SDK Manager)
- **CMake**: Version 3.22.1 or higher

The build is configured in `build.gradle.kts` and `CMakeLists.txt`.

### Supported ABIs

- `arm64-v8a` (64-bit ARM, most modern devices)
- `armeabi-v7a` (32-bit ARM, older devices)
- `x86_64` (Emulators, some Chromebooks)
- `x86` (Older emulators)

## 📁 File Structure

```
core/security/
├── src/main/
│   ├── cpp/
│   │   ├── CMakeLists.txt         # CMake build configuration
│   │   └── native-keys.cpp        # Native key storage
│   └── java/.../security/
│       ├── ApiKeyProvider.kt      # Public interface
│       ├── NativeKeyProvider.kt   # JNI bridge
│       ├── SecureTokenManager.kt  # Token management
│       └── di/
│           └── SecurityModule.kt  # Hilt module
├── scripts/
│   └── encode_keys.py             # Key encoding utility
├── proguard-rules.pro             # ProGuard rules
├── consumer-rules.pro             # Consumer ProGuard rules
└── README.md                      # This file
```

## 🧪 Testing

For testing, you can create a mock implementation:

```kotlin
class FakeApiKeyProvider : ApiKeyProvider {
    override fun isAvailable() = true
    override fun getApiKey() = "test_api_key"
    override fun getSecretKey() = "test_secret_key"
    override fun getAppIdentifier() = "test_app_v1"
    override fun validateKeyFormat(key: String) = true
    override fun getApiKeyOrNull() = getApiKey()
    override fun getSecretKeyOrNull() = getSecretKey()
}
```

## 📚 Further Reading

- [Android NDK Documentation](https://developer.android.com/ndk)
- [JNI Tips](https://developer.android.com/training/articles/perf-jni)
- [ProGuard and Native Code](https://developer.android.com/studio/build/shrink-code)

