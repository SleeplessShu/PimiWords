# ===== KOTLIN =====
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# ===== ENUMS — все enum классы проекта =====
-keepnames enum com.sleeplessdog.pimi.** { *; }
-keep enum com.sleeplessdog.pimi.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    *;
}

# ===== MODELS — data классы и модели =====
-keep class com.sleeplessdog.pimi.games.presentation.models.** { *; }
-keep class com.sleeplessdog.pimi.games.domain.models.** { *; }
-keep class com.sleeplessdog.pimi.settings.** { *; }
-keep class com.sleeplessdog.pimi.score.presentation.models.** { *; }
-keep class com.sleeplessdog.pimi.score.models.** { *; }
-keep class com.sleeplessdog.pimi.score.domain.models.** { *; }
-keep class com.sleeplessdog.pimi.dictionary.group_screen.** { *; }
-keep class com.sleeplessdog.pimi.dictionary.models.** { *; }
-keep class com.sleeplessdog.pimi.dictionary.word_packs.WordPack { *; }
-keep class com.sleeplessdog.pimi.endGame.** { *; }
-keep class com.sleeplessdog.pimi.gameSelect.LandingConditions { *; }
-keep class com.sleeplessdog.pimi.gameSelect.LandingKeys { *; }
-keep class com.sleeplessdog.pimi.games.presentation.states.** { *; }

# ===== NAVIGATION — Safe Args =====
-keep class com.sleeplessdog.pimi.**.*Args { *; }
-keep class com.sleeplessdog.pimi.**.*Directions { *; }
-keep class com.sleeplessdog.pimi.**.*FragmentArgs { *; }

# ===== ROOM — БД =====
-keep class com.sleeplessdog.pimi.database.** { *; }
-keep class com.sleeplessdog.pimi.database.user.** { *; }
-keep class com.sleeplessdog.pimi.database.global.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers @androidx.room.TypeConverters class * { *; }

# ===== KOIN =====
-keepnames class com.sleeplessdog.pimi.di.** { *; }
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# ===== FIREBASE =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ===== GSON — для WordPacks парсинга =====
-keep class com.sleeplessdog.pimi.dictionary.word_packs.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ===== COROUTINES =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ===== LOTTIE =====
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ===== COMPOSE =====
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ===== VIEWMODEL =====
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ===== FRAGMENTS =====
-keep class * extends androidx.fragment.app.Fragment { *; }
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    <init>(...);
}

# ===== SERIALIZATION =====
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# ===== BILLING =====
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# ===== CRASHLYTICS =====
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**