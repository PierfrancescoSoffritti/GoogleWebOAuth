# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Pierfrancesco\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# retrolambda
-dontwarn java.lang.invoke.*

-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential$RequestHandler
-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException
-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
-dontwarn com.google.common.cache.Striped64
-dontwarn com.google.common.cache.Striped64$1
-dontwarn com.google.common.cache.Striped64$Cell
-dontwarn com.google.common.primitives.UnsignedBytes$LexicographicalComparatorHolder$UnsafeComparator
-dontwarn com.google.common.primitives.UnsignedBytes$LexicographicalComparatorHolder$UnsafeComparator$1