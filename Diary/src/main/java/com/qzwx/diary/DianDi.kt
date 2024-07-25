package com.qzwx.diary

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DabblesScreen() {
    // 获取当前上下文
    val context = LocalContext.current

    // 定义点滴页面内容
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // 在页面右下角添加一个悬浮按钮
        FloatingActionButton(
            onClick = {
                // 创建一个 Intent 用于启动 XieRiJi Activity
                val intent = Intent(context, com.qzwx.diary.ui.XieRiJi::class.java)
                // 启动 XieRiJi Activity
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 设置悬浮按钮的位置为右下角
                .padding(16.dp) // 设置悬浮按钮的内边距，以避免其贴边显示
                .size(56.dp) // 设置悬浮按钮大小
        ) {
            // 使用图片作为悬浮按钮的内容
            Icon(
                painter = painterResource(id = R.drawable.svg_diandi), // 指定按钮的图片资源
                contentDescription = "Floating Button" // 设置图片的描述文字（无障碍功能）
            )
        }
    }
}
