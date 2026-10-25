# ProGuard rules for Haftalik Xarajat
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class com.example.haftalikharajat.data.model.** { *; }
