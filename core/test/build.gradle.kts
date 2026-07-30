import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ktlint)
}

extensions.configure<LibraryExtension> {
    namespace = "com.dalmuina.core.test"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:data"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(libs.mockk)
    implementation(libs.junit)
}
