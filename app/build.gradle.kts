import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        versionCode = 6
        versionName = "1.1.6"

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
        //自定义打包apk名称
 applicationVariants.all {
        val variant = this
        variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName =
                    "QZWX_App${variant.versionName}_${
                        SimpleDateFormat(
                            "MMdd",
                            Locale.getDefault()
                        ).format(Date())
                    }.apk"
                output.outputFileName = outputFileName
            }
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