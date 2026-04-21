# ===== ALL =====
-keep class com.sleeplessdog.pimi.** { *; }

# ===== KOTLIN =====
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# ===== ROOM =====
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# ===== FIREBASE =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ===== GSON =====
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# ===== COROUTINES =====
-dontwarn kotlinx.coroutines.**

# ===== LOTTIE =====
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# ===== COMPOSE =====
-dontwarn androidx.compose.**

# ===== BILLING =====
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# ===== CRASHLYTICS =====
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

-keep class com.google.common.reflect.TypeToken { *; }
-keep class * extends com.google.common.reflect.TypeToken { *; }