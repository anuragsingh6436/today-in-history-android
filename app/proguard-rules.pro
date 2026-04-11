# Retrofit + kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.bajrangi.todayinhistory.**$$serializer { *; }
-keepclassmembers class com.bajrangi.todayinhistory.** {
    *** Companion;
}
-keepclasseswithmembers class com.bajrangi.todayinhistory.** {
    kotlinx.serialization.KSerializer serializer(...);
}
