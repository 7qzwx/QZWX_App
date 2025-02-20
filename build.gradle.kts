// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.24" apply false   //添加此代码，注意version版本需要根据你的kotlin版本号修改，我这个意思是kotlin版本为1.9.0，ksp版本为1.0.13，具体更改的版本号请查询github的ksp仓库：https://github.com/google/ksp/releases?page=4
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

