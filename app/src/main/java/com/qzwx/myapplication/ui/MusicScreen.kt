package com.qzwx.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // 创建 Intent 并跳转到目标 Activity
            val intent =
                Intent(context, com.qzwx.feature_todoanddone.TodoAndDoneActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Todo页面")
        }
        Text(text = "Music,陪伴每一天!", style = MaterialTheme.typography.headlineMedium)
    }
}
