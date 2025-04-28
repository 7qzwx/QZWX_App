plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.qzwx.qzwxapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.qzwx.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 5
        versionName = "1.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    ksp(libs.androidxRoomCompiler)
    implementation(project(":core"))
    implementation(project(":Feature_Diary"))
    implementation(project(":Feature_AccountBook"))
    implementation(project(":feature_qiandaosystem"))
    implementation(project(":feature_wordsmemory"))
}