# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep AutoGLM classes
-keep class com.aipaly.autoglm.** { *; }

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Accessibility Service
-keep class android.accessibilityservice.** { *; }

# Keep JSON classes
-keep class org.json.** { *; }
