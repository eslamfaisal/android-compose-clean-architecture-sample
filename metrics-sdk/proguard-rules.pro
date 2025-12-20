# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools proguard files.

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
