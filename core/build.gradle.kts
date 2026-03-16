import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.library)
}

extensions.configure<LibraryExtension>  {
    namespace = "com.dalmuina.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    coreLibraryDesugaring(libs.desugar.jdk.libs)
    //Unit test
    implementation(libs.koin.android)
}
