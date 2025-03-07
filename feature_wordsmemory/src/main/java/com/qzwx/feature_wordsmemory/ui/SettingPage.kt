package com.qzwx.feature_wordsmemory.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.components.BottomBar

@Composable
fun SettingPage(navController : NavController) {
    // 页面内容
    Scaffold(
    ) { innerPadding ->
        Text(modifier = Modifier.padding(innerPadding), text = "这是设置页面")
        // 页面布局
    }
}