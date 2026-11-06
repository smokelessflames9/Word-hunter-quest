# Add project specific ProGuard / R8 rules here.
# Preserve line number information for Play Console crash & ANR deobfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

# Keep Firestore data models (required for reflection-based Snapshot.toObject deserialization)
-keep class com.ttws.wordhunterquest.data.firestore.** { *; }
-keepnames class com.ttws.wordhunterquest.data.firestore.** { *; }

# Keep game data models
-keep class com.ttws.wordhunterquest.data.** { *; }

# Keep Google Mobile Ads & Firebase Auth/Credential Manager classes
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.errorprone.annotations.**
