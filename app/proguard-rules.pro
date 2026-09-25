# Project ProGuard / R8 rules. R8 full mode is on (AGP default).
# See http://developer.android.com/guide/developing/tools/proguard.html
#
# Libraries in use that ship their own consumer/R8 rules (no manual rules needed):
#   kotlinx.serialization (META-INF/.../kotlinx-serialization-r8.pro), Room,
#   Koin, kotlinx-coroutines, Jetpack Compose, WorkManager, Ktor.

# --- Crashlytics: keep readable, de-obfuscatable stack traces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Navigation3 NavKeys ---
# Route + the two *Mode sealed hierarchies are @Serializable and are encoded /
# decoded by Navigation3 when the back stack is saved and restored. Keep them
# (and their nested subclasses) intact so restoration after process death works.
-keep,includedescriptorclasses class com.dalmuina.deckflow.navigation.Route { *; }
-keep,includedescriptorclasses class com.dalmuina.deckflow.navigation.Route$* { *; }
-keep,includedescriptorclasses class com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorMode { *; }
-keep,includedescriptorclasses class com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorMode$* { *; }
-keep,includedescriptorclasses class com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorMode { *; }
-keep,includedescriptorclasses class com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorMode$* { *; }
