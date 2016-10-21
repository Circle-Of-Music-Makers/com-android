# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sidzi/Developement/android-studio/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.j256.** { *; }
-keep class com.j256.**
-keepclassmembers class com.j256.**
-keep enum com.j256.**
-keepclassmembers enum com.j256.**
-keep interface com.j256.**
-keepclassmembers interface com.j256.**

-keepclassmembers class * {
  public <init>(android.content.Context);
}

-keepattributes *Annotation*