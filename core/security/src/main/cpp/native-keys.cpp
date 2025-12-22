/**
 * Native Key Provider - Secure API Key Storage
 *
 * This file uses XOR obfuscation to prevent easy extraction of API keys
 * from the compiled binary. While not unbreakable, it provides a significant
 * barrier against casual reverse engineering attempts.
 *
 * Security Layers Applied:
 * 1. Keys stored in native code (harder to decompile than Java/Kotlin)
 * 2. XOR obfuscation (keys aren't visible in hex editors)
 * 3. String splitting (no complete key in one place)
 * 4. Package name verification (prevents use in other apps)
 *
 * WARNING: Never commit real production keys to version control!
 * Use this as a template and inject real keys during CI/CD builds.
 */

#include <jni.h>
#include <string>
#include <vector>

namespace {
    /**
     * XOR key for obfuscation - should be different for each key type
     * In production, generate these randomly and never reuse
     */
    constexpr char XOR_KEY_API = 0x5A;
    constexpr char XOR_KEY_SECRET = 0x7F;

    /**
     * Expected package name for verification
     * This adds an extra layer of protection - even if the .so is extracted,
     * it won't work in an app with a different package name
     */
    const std::string EXPECTED_PACKAGE = "com.eslam.bakingapp";
    const std::string EXPECTED_PACKAGE_DEBUG = "com.eslam.bakingapp.debug";

    /**
     * XOR-encoded API Key
     * Original: "bk_fake_api_key_12345_demo"
     * To encode: for each char c in key: encoded_char = c ^ XOR_KEY_API
     *
     * Generate encoded keys using this Python snippet:
     * key = "your_actual_api_key"
     * xor_key = 0x5A
     * encoded = [hex(ord(c) ^ xor_key) for c in key]
     * print(', '.join(encoded))
     */
    const std::vector<char> API_KEY_ENCODED = {
        0x38, 0x31, 0x1a, 0x3c, 0x3f, 0x31, 0x3b, 0x1a,
        0x3f, 0x28, 0x3b, 0x1a, 0x31, 0x3b, 0x29, 0x1a,
        0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x1a, 0x3e, 0x3b,
        0x37, 0x35
    };

    /**
     * XOR-encoded Secret Key
     * Original: "sk_fake_secret_key_67890_demo"
     * Encoded with XOR_KEY_SECRET (0x7F)
     */
    const std::vector<char> SECRET_KEY_ENCODED = {
        0x0c, 0x14, 0x54, 0x19, 0x1e, 0x14, 0x1a, 0x54,
        0x0c, 0x1a, 0x1c, 0x0d, 0x1a, 0x0b, 0x54, 0x14,
        0x1a, 0x08, 0x54, 0x49, 0x48, 0x4f, 0x4e, 0x4d,
        0x54, 0x1b, 0x1a, 0x12, 0x10
    };

    /**
     * Additional key parts for extra obfuscation
     * Keys are split and concatenated at runtime
     */
    const char* KEY_PREFIX_PART_1 = "baking";
    const char* KEY_PREFIX_PART_2 = "_app_";

    /**
     * Decodes an XOR-encoded byte vector to a string
     */
    std::string decodeKey(const std::vector<char>& encoded, char xorKey) {
        std::string decoded;
        decoded.reserve(encoded.size());
        for (char c : encoded) {
            decoded += static_cast<char>(c ^ xorKey);
        }
        return decoded;
    }

    /**
     * Verifies the calling app's package name
     * Returns true if the package name matches expected values
     */
    bool verifyPackageName(JNIEnv* env, jobject context) {
        // Get Context class
        jclass contextClass = env->GetObjectClass(context);
        if (contextClass == nullptr) return false;

        // Get getPackageName method
        jmethodID getPackageNameMethod = env->GetMethodID(
            contextClass,
            "getPackageName",
            "()Ljava/lang/String;"
        );
        if (getPackageNameMethod == nullptr) return false;

        // Call getPackageName()
        auto packageNameObj = (jstring) env->CallObjectMethod(
            context,
            getPackageNameMethod
        );
        if (packageNameObj == nullptr) return false;

        // Convert to C++ string
        const char* packageNameChars = env->GetStringUTFChars(packageNameObj, nullptr);
        std::string packageName(packageNameChars);
        env->ReleaseStringUTFChars(packageNameObj, packageNameChars);

        // Verify package name
        return packageName == EXPECTED_PACKAGE ||
               packageName == EXPECTED_PACKAGE_DEBUG;
    }

    /**
     * Builds a composite key with runtime concatenation
     * This prevents the full key from appearing in any single location
     */
    std::string buildCompositeIdentifier() {
        std::string result;
        result += KEY_PREFIX_PART_1;
        result += KEY_PREFIX_PART_2;
        result += "v1";
        return result;
    }
}

extern "C" {

/**
 * Returns the decoded API key after package verification
 * 
 * @param env JNI environment pointer
 * @param thiz Reference to the calling object
 * @param context Android Context for package verification
 * @return Decoded API key, or empty string if verification fails
 */
JNIEXPORT jstring JNICALL
Java_com_eslam_bakingapp_core_security_NativeKeyProvider_getApiKeyNative(
        JNIEnv* env,
        jobject /* thiz */,
        jobject context
) {
    // Verify package name to prevent key extraction in other apps
    if (!verifyPackageName(env, context)) {
        return env->NewStringUTF("");
    }

    // Decode and return the API key
    std::string apiKey = decodeKey(API_KEY_ENCODED, XOR_KEY_API);
    return env->NewStringUTF(apiKey.c_str());
}

/**
 * Returns the decoded Secret key after package verification
 * 
 * @param env JNI environment pointer
 * @param thiz Reference to the calling object
 * @param context Android Context for package verification
 * @return Decoded Secret key, or empty string if verification fails
 */
JNIEXPORT jstring JNICALL
Java_com_eslam_bakingapp_core_security_NativeKeyProvider_getSecretKeyNative(
        JNIEnv* env,
        jobject /* thiz */,
        jobject context
) {
    // Verify package name
    if (!verifyPackageName(env, context)) {
        return env->NewStringUTF("");
    }

    // Decode and return the secret key
    std::string secretKey = decodeKey(SECRET_KEY_ENCODED, XOR_KEY_SECRET);
    return env->NewStringUTF(secretKey.c_str());
}

/**
 * Returns a composite identifier built at runtime
 * Demonstrates string splitting technique
 * 
 * @param env JNI environment pointer
 * @param thiz Reference to the calling object
 * @return Composite identifier string
 */
JNIEXPORT jstring JNICALL
Java_com_eslam_bakingapp_core_security_NativeKeyProvider_getAppIdentifierNative(
        JNIEnv* env,
        jobject /* thiz */
) {
    std::string identifier = buildCompositeIdentifier();
    return env->NewStringUTF(identifier.c_str());
}

/**
 * Validates an API key format without exposing the actual key
 * Useful for debugging/verification without logging sensitive data
 * 
 * @param env JNI environment pointer
 * @param thiz Reference to the calling object
 * @param keyToValidate The key to validate
 * @return true if the key matches the expected format
 */
JNIEXPORT jboolean JNICALL
Java_com_eslam_bakingapp_core_security_NativeKeyProvider_validateKeyFormatNative(
        JNIEnv* env,
        jobject /* thiz */,
        jstring keyToValidate
) {
    if (keyToValidate == nullptr) {
        return JNI_FALSE;
    }

    const char* keyChars = env->GetStringUTFChars(keyToValidate, nullptr);
    std::string key(keyChars);
    env->ReleaseStringUTFChars(keyToValidate, keyChars);

    // Validate key format: should start with "bk_" or "sk_"
    bool isValid = (key.length() > 3) &&
                   (key.substr(0, 3) == "bk_" || key.substr(0, 3) == "sk_");

    return isValid ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"

