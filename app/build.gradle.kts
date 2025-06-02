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
    namespace = "qzwx.app.qzwxapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "qzwx.app.qzwxapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 8
        versionName = "1.2.2"

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
                        "QZWX_App_${variant.versionName}.apk"
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
    // 第三方库
    implementation(libs.dotlottie.android) //lottie动画
    implementation(libs.swipe)  //左右滑动效果
    implementation(libs.composeneumorphism) //按钮悬浮与塌陷效果
    implementation(libs.exyte.animated.navigation.bar) // 动画导航栏
    implementation(libs.composereorderable.reorderable) // 可重排序的Compose组件
    implementation(libs.mpandroidchart) // MPAndroidChart图表库
    implementation(libs.plot) // Plot图表库
    implementation(libs.breens.mbaka.beetablescompose) // 自定义表格
    implementation(libs.calendar.compose) // Compose日历组件
    implementation(libs.accompanist.systemuicontroller.v0301) // Accompanist系统UI控制器
    implementation(libs.accompanist.pager.indicators) // Accompanist分页指示器

    // DataStore 依赖
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-preferences-core:1.1.7")

    // AndroidX库
    implementation(libs.constraintlayout.compose)  //约束布局
    implementation(libs.androidx.material.icons.extended) // Material图标扩展
    implementation(libs.androidx.navigation.navigation.compose3) // Compose导航
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose) // Compose ViewModel
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx) // LiveData KTX
    implementation(libs.androidxRoomRuntime) // Room数据库运行时
    ksp(libs.androidxRoomCompiler) // Room编译器（KSP）
    implementation(libs.androidxRoomKtx) // Room KTX扩展
    implementation(libs.androidxRoomRxjava2) // Room RxJava2支持
    implementation(libs.androidxRoomRxjava3) // Room RxJava3支持
    implementation(libs.androidxRoomGuava) // Room Guava支持
    implementation(libs.androidxRoomTesting) // Room测试支持
    implementation(libs.androidxRoomPaging) // Room分页支持
    implementation(libs.androidx.ui.text.google.fonts) // Compose Google字体
    implementation(libs.androidx.core.ktx) // AndroidX Core KTX
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle Runtime KTX
    implementation(libs.androidx.activity.compose) // Compose Activity
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM（物料清单）
    implementation(libs.androidx.ui) // Compose UI
    implementation(libs.androidx.ui.graphics) // Compose图形
    implementation(libs.androidx.ui.tooling.preview) // Compose工具预览
    implementation(libs.androidx.material3) // Material 3组件
    implementation(libs.androidx.ui.text.android) // Compose Android文本
    implementation(libs.androidx.junit) // AndroidX JUnit测试
    implementation(libs.androidx.espresso.core) // Espresso核心测试
    implementation(libs.androidx.ui.test.junit4) // Compose JUnit4测试
    implementation(libs.androidx.ui.tooling) // Compose工具
    implementation(libs.androidx.ui.test.manifest) // Compose测试清单
    implementation(libs.androidx.appcompat) // AppCompat库
    implementation(libs.material) // Material Design库
    implementation(libs.androidx.activity) // AndroidX Activity
    implementation(libs.androidx.constraintlayout) // ConstraintLayout
    implementation(libs.androidx.gridlayout) // GridLayout
    implementation(libs.androidx.navigation.runtime.ktx) // Navigation运行时KTX
    // 测试库
    implementation(libs.junit) // JUnit测试框架
}

