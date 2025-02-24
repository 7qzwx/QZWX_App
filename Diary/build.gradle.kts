import org.gradle.api.JavaVersion.VERSION_11

plugins {
    id("com.android.library") // 库模块插件
    kotlin("android")
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.0-1.0.24"
}

android {
    namespace = "com.qzwx.diary" // 使用命名空间替代 applicationId
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35

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
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11
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
    implementation ("com.google.accompanist:accompanist-webview:0.35.0-alpha")
    implementation ("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc") //日期选择器

    implementation ("com.google.android.material:material:1.9.0") // 或者更新的版本

    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.text.google.fonts)
    val room_version = "2.6.1"
        implementation("androidx.room:room-runtime:$room_version")
        annotationProcessor("androidx.room:room-compiler:$room_version")

        // To use Kotlin Symbol Processing (KSP)
        ksp("androidx.room:room-compiler:$room_version")

        // optional - Kotlin Extensions and Coroutines support for Room
        implementation("androidx.room:room-ktx:$room_version")

        // optional - RxJava2 support for Room
        implementation("androidx.room:room-rxjava2:$room_version")

        // optional - RxJava3 support for Room
        implementation("androidx.room:room-rxjava3:$room_version")

        // optional - Guava support for Room, including Optional and ListenableFuture
        implementation("androidx.room:room-guava:$room_version")

        // optional - Test helpers
        testImplementation("androidx.room:room-testing:$room_version")

        // optional - Paging 3 Integration
        implementation("androidx.room:room-paging:$room_version")



    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation ("androidx.compose.material:material-icons-extended")
    // Google Material Components
    implementation("com.google.android.material:material:1.9.0")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.0.0")

    // Accompanist libraries for additional Compose functionality
    implementation("com.google.accompanist:accompanist-placeholder-material:0.28.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.0-alpha")

    // Jetpack Compose libraries
    implementation("androidx.compose.ui:ui:1.6.6")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.6")
    implementation("androidx.compose.foundation:foundation:1.6.6")
    implementation("androidx.activity:activity-compose:1.8.2")

    // AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
