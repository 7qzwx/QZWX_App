package com.qzwx.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qzwx.diary.ui.theme.QZWX_APPTheme
import java.text.SimpleDateFormat
import java.util.*

class XieRiJi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_APPTheme {
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
    var moodDialogVisible by remember { mutableStateOf(false) }
    var weatherDialogVisible by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var selectedWeather by remember { mutableStateOf<String?>(null) }
    var diaryContent by remember { mutableStateOf("") }

    // 获取当前时间（小时和分钟）
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = timeFormat.format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题输入框
        var title by remember { mutableStateOf("") }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("标题") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 当前时间
        Text(
            text = "当前时间: $currentTime",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 心情和天气按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { moodDialogVisible = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1)), // 粉橙色背景
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "心情: ${selectedMood ?: "未选择"}",
                    color = if (selectedMood != null) Color(0xFF00695C) else Color.Unspecified // 选中时的颜色
                )
            }
            Button(
                onClick = { weatherDialogVisible = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1)), // 粉橙色背景
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "天气: ${selectedWeather ?: "未选择"}",
                    color = if (selectedWeather != null) Color(0xFF00695C) else Color.Unspecified // 选中时的颜色
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 富文本输入框
        OutlinedTextField(
            value = diaryContent,
            onValueChange = { diaryContent = it },
            label = { Text("日记正文") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = Int.MAX_VALUE,
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 保存按钮
        Button(
            onClick = { /* 保存操作 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1)), // 粉橙色背景
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Text("保存", color = Color.White)
        }

        // 显示心情对话框
        if (moodDialogVisible) {
            AlertDialog(
                onDismissRequest = { moodDialogVisible = false },
                title = { Text("选择心情") },
                text = {
                    Column {
                        MoodOption("开心", selectedMood) { selectedMood = it }
                        MoodOption("正常", selectedMood) { selectedMood = it }
                        MoodOption("emo", selectedMood) { selectedMood = it }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { moodDialogVisible = false }) {
                        Text("确定")
                    }
                },
                containerColor = Color(0xFFFFE4E1) // 浅粉色背景
            )
        }

        // 显示天气对话框
        if (weatherDialogVisible) {
            AlertDialog(
                onDismissRequest = { weatherDialogVisible = false },
                title = { Text("选择天气") },
                text = {
                    Column {
                        WeatherOption("晴天", selectedWeather) { selectedWeather = it }
                        WeatherOption("多云", selectedWeather) { selectedWeather = it }
                        WeatherOption("雨天", selectedWeather) { selectedWeather = it }
                        WeatherOption("阴天", selectedWeather) { selectedWeather = it }
                        WeatherOption("雪天", selectedWeather) { selectedWeather = it }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { weatherDialogVisible = false }) {
                        Text("确定")
                    }
                },
                containerColor = Color(0xFFFFE4E1) // 浅粉色背景
            )
        }
    }
}

// 心情选项组件
@Composable
fun MoodOption(mood: String, selectedMood: String?, onClick: (String) -> Unit) {
    val isSelected = mood == selectedMood
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(mood) }
            .padding(16.dp)
    ) {
        Text(
            text = mood,
            color = if (isSelected) Color(0xFFFF6F61) else Color.Unspecified, // 选中文本的颜色
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// 天气选项组件
@Composable
fun WeatherOption(weather: String, selectedWeather: String?, onClick: (String) -> Unit) {
    val isSelected = weather == selectedWeather
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(weather) }
            .padding(16.dp)
    ) {
        // 图标
        Icon(
            painter = painterResource(id = when (weather) {
                "晴天" -> R.drawable.svg_diandi
                "多云" -> R.drawable.svg_bianqian
                "雨天" -> R.drawable.svg_rili
                "阴天" -> R.drawable.svg_shoucang
                "雪天" -> R.drawable.svg_diandi
                else -> R.drawable.svg_diandi
            }),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = weather,
            color = if (isSelected) Color(0xFF1AC08B) else Color.Unspecified, // 选中文本的颜色
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewXieRiJiScreen() {
    QZWX_APPTheme {
        XieRiJiScreen()
    }
}
