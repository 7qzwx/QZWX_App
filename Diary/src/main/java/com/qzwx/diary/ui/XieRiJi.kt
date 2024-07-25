package com.qzwx.diary.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qzwx.diary.R
import com.qzwx.diary.ui.theme.QZWX_APPTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class XieRiJi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_APPTheme {
                DiaryScreen()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000)
        }
    }

    var diaryTitle by remember { mutableStateOf("") }
    var diaryContent by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val backgroundImage: Painter = painterResource(id = R.drawable.xieriji_bg)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "---愿有一天，当你翻阅此篇日记之际，能够唤起那刻时光的点点滴滴。---",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = diaryTitle,
            onValueChange = { diaryTitle = it },
            label = { Text("日记标题") },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = colorResource(id = R.color.XieRiJi_lvse),
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 让内容框占据剩余空间
                .padding(top = 16.dp)
                .verticalScroll(scrollState) // 使内容框支持滚动
        ) {
            OutlinedTextField(
                value = diaryContent,
                onValueChange = { diaryContent = it },
                label = { Text("日记内容") },
                modifier = Modifier
                    .fillMaxSize() // 确保内容框填充整个 Column
                    .padding(16.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                maxLines = Int.MAX_VALUE, // 允许内容显示更多行
                singleLine = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = colorResource(id = R.color.XieRiJi_lvse),
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
        }

        // 底部按钮组
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = colorResource(id = R.color.XieRiJi_lvse)) // 背景颜色与页面一致
                .padding(vertical = 8.dp), // 为按钮组提供内边距
            horizontalArrangement = Arrangement.End // 使按钮在水平上靠右对齐
        ) {
            IconButton(onClick = { /* TODO: Handle Mood button click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.svg_shoucang),
                    contentDescription = "心情",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp)) // 添加间隔
            IconButton(onClick = { /* TODO: Handle Weather button click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.svg_bianqian),
                    contentDescription = "天气",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp)) // 添加间隔
            IconButton(onClick = { /* TODO: Handle Save button click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.svg_emo),
                    contentDescription = "保存",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("M月d日  HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

@Preview(showBackground = true)
@Composable
fun PreviewXieRiJi() {
    QZWX_APPTheme {
        DiaryScreen()
    }
}
