# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Firebase Analytics classes
-keep class com.google.android.datatransport.** { *; }
-keep class com.google.firebase.analytics.** { *; }

# Keep Firebase Firestore models (Only if using Firestore)
-keep class com.google.firebase.firestore.** { *; }

# Keep Firebase Messaging classes (Only if using Firebase Cloud Messaging)
-keep class com.google.firebase.messaging.** { *; }

# Keep Firebase Database classes (Only if using Firebase Realtime Database)
-keep class com.google.firebase.database.** { *; }

# Keep Firebase Auth classes (Only if using Firebase Authentication)
-keep class com.google.firebase.auth.** { *; }


# Keep serialized classes to prevent stripping Gson, Jackson, or other JSON parsers
-keepattributes *Annotation*
-keep class com.google.** { *; }
-keepnames class * { *; }
-dontwarn com.google.**

