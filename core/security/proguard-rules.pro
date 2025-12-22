# ProGuard rules for core:security module
# These rules protect native method declarations from obfuscation

# ============================================================================
# Native Key Provider Rules
# ============================================================================
# Keep the NativeKeyProvider class and all its native methods
# JNI method names must match exactly, so they cannot be obfuscated
-keep class com.eslam.bakingapp.core.security.NativeKeyProvider {
    native <methods>;
    # Keep the companion object for library loading
    public static ** Companion;
}

# Keep the NativeKeyProvider companion object
-keep class com.eslam.bakingapp.core.security.NativeKeyProvider$Companion {
    *;
}

# Prevent R8 from removing the init block that loads the native library
-keepclassmembers class com.eslam.bakingapp.core.security.NativeKeyProvider {
    static <clinit>;
}

# ============================================================================
# Exception Classes
# ============================================================================
# Keep exception class names for meaningful stack traces
-keep class com.eslam.bakingapp.core.security.NativeLibraryNotLoadedException {
    *;
}

# ============================================================================
# Interface and Implementation Classes
# ============================================================================
# Keep ApiKeyProvider interface methods for Hilt injection
-keep interface com.eslam.bakingapp.core.security.ApiKeyProvider {
    *;
}

-keep class com.eslam.bakingapp.core.security.DefaultApiKeyProvider {
    *;
}

# ============================================================================
# Hilt Generated Classes
# ============================================================================
# Hilt generates code that references these classes
-keep class com.eslam.bakingapp.core.security.di.SecurityModule {
    *;
}

-keep class com.eslam.bakingapp.core.security.di.SecurityModule_* {
    *;
}

# ============================================================================
# Optimization Exceptions
# ============================================================================
# Don't optimize native method lookups
-keepattributes Signature
-keepattributes *Annotation*

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

