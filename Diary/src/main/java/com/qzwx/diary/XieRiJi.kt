package com.qzwx.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.qzwx.diary.ui.theme.QZWX_APPTheme

class XieRiJi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_APPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    XieRiJiScreen()
                }
            }
        }
    }
}

@Composable
fun XieRiJiScreen() {
    // 定义 XieRiJi 界面的内容
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("XieRiJi 页面", style = MaterialTheme.typography.headlineMedium)
    }
}
