plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

android {
    namespace = "com.qzwx.feature_wordsmemory"
    compileSdk = 35

    defaultConfig {
        minSdk = 29
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    ksp(libs.androidxRoomCompiler)
    implementation("com.canopas.compose-animated-navigationbar:bottombar:1.0.1")  //底部导航栏
    implementation(project(":core"))
}