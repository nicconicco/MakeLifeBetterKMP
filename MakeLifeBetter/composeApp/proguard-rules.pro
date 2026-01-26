# Keep Compose
-dontwarn androidx.compose.**

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.carlosnicolaugalves.makelifebetter.**$$serializer { *; }
-keepclassmembers class com.carlosnicolaugalves.makelifebetter.** {
    *** Companion;
}
-keepclasseswithmembers class com.carlosnicolaugalves.makelifebetter.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keep class dev.gitlive.firebase.** { *; }

# Keep Google Maps
-keep class com.google.android.gms.maps.** { *; }
