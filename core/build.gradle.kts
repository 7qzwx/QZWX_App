plugins {
    id("com.android.library")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

android {
    namespace = "com.qzwx.core"
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
    // 第三方库
    api(libs.dotlottie.android) //lottie动画
    api(files("libs/compose-charts-android-0.1.2.aar"))
    api(libs.swipe)  //左右滑动效果
    api(libs.composeneumorphism) //按钮悬浮与塌陷效果
    api(libs.exyte.animated.navigation.bar) // 动画导航栏
    api(libs.composereorderable.reorderable) // 可重排序的Compose组件
    api(libs.mpandroidchart) // MPAndroidChart图表库
    api(libs.plot) // Plot图表库
    api(libs.breens.mbaka.beetablescompose) // 自定义表格
    api(libs.calendar.compose) // Compose日历组件
    api(libs.accompanist.systemuicontroller.v0301) // Accompanist系统UI控制器
    api(libs.accompanist.pager.indicators) // Accompanist分页指示器
    // AndroidX库
    api(libs.constraintlayout.compose)  //约束布局
    api(libs.androidx.material.icons.extended) // Material图标扩展
    api(libs.androidx.navigation.navigation.compose3) // Compose导航
    api(libs.androidx.lifecycle.lifecycle.viewmodel.compose) // Compose ViewModel
    api(libs.androidx.lifecycle.lifecycle.livedata.ktx) // LiveData KTX
    api(libs.androidxRoomRuntime) // Room数据库运行时
    ksp(libs.androidxRoomCompiler) // Room编译器（KSP）
    api(libs.androidxRoomKtx) // Room KTX扩展
    api(libs.androidxRoomRxjava2) // Room RxJava2支持
    api(libs.androidxRoomRxjava3) // Room RxJava3支持
    api(libs.androidxRoomGuava) // Room Guava支持
    api(libs.androidxRoomTesting) // Room测试支持
    api(libs.androidxRoomPaging) // Room分页支持
    api(libs.androidx.ui.text.google.fonts) // Compose Google字体
    api(libs.androidx.core.ktx) // AndroidX Core KTX
    api(libs.androidx.lifecycle.runtime.ktx) // Lifecycle Runtime KTX
    api(libs.androidx.activity.compose) // Compose Activity
    api(platform(libs.androidx.compose.bom)) // Compose BOM（物料清单）
    api(libs.androidx.ui) // Compose UI
    api(libs.androidx.ui.graphics) // Compose图形
    api(libs.androidx.ui.tooling.preview) // Compose工具预览
    api(libs.androidx.material3) // Material 3组件
    api(libs.androidx.ui.text.android) // Compose Android文本
    api(libs.androidx.junit) // AndroidX JUnit测试
    api(libs.androidx.espresso.core) // Espresso核心测试
    api(libs.androidx.ui.test.junit4) // Compose JUnit4测试
    api(libs.androidx.ui.tooling) // Compose工具
    api(libs.androidx.ui.test.manifest) // Compose测试清单
    api(libs.androidx.appcompat) // AppCompat库
    api(libs.material) // Material Design库
    api(libs.androidx.activity) // AndroidX Activity
    api(libs.androidx.constraintlayout) // ConstraintLayout
    api(libs.androidx.gridlayout) // GridLayout
    api(libs.androidx.navigation.runtime.ktx) // Navigation运行时KTX
    // 测试库
    api(libs.junit) // JUnit测试框架
}