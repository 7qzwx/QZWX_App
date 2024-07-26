package com.qzwx.diary.ui

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.diary.R
import com.qzwx.diary.theme.QZWX_APPTheme
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
    var diaryTitle by remember { mutableStateOf("") }
    var diaryContent by remember { mutableStateOf("") }
    var keyboardHeight by remember { mutableStateOf(0) }
    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = Color(0xFFACB8F4) // 设置为透明，或你需要的颜色
    )

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000)
        }
    }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val density = LocalDensity.current.density

    // 添加观察器以检测键盘可见性并调整填充
    DisposableEffect(view) {
        val callback = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardHeight = (keypadHeight / density).toInt().coerceAtLeast(0) // 确保非负
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(callback)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(callback)
        }
    }

    val backgroundImage = painterResource(id = R.drawable.xieriji_bg)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = keyboardHeight.dp) // 使用非负填充
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "背景图",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 时间文本，带边框
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .border(BorderStroke(2.dp, Color.Black), shape = MaterialTheme.shapes.medium) // 设置边框宽度、颜色和形状
                    .padding(15.dp) // 内边距
            ) {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Text(
                text = "---愿有一天，当你翻阅此篇日记之际，能够唤起那刻时光的点点滴滴---",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.qzwx_kaiti)) // 替换为你的字体资源ID
                ),
                color = Color(0xFF59D7E7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 8.dp),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = diaryTitle,
                onValueChange = { diaryTitle = it },
                label = { Text("日记标题") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.qzwx_cuti)) // 替换为你的字体资源ID
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0x63A5A8EF),
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.AddReaction, // 使用Compose默认的心情图标
                    contentDescription = "心情",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color(0xFF5AE4BD)
                )

                Icon(
                    imageVector = Icons.Default.WbSunny, // 使用Compose默认的天气图标
                    contentDescription = "天气",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color(0xFF5AE4BD)
                )
            }

            OutlinedTextField(
                value = diaryContent,
                onValueChange = { diaryContent = it },
                label = { Text("日记内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp) // 设置最小高度以避免塌陷
                    .verticalScroll(scrollState),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.qzwx_geziti)) // 替换为你的字体资源ID
                ),
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
        }
        FloatingActionButton(
            onClick = { /* TODO: Handle Save action */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(50.dp),
            contentColor = Color.White, // 设置文字颜色
            elevation = FloatingActionButtonDefaults.elevation(8.dp) // 设置阴影高度
        ) {
            Text(
                text = "保存",
                style = TextStyle(
                    color = Color(0xFF238692),
                    fontWeight = FontWeight.Bold, // 字体粗细
                    textAlign = TextAlign.Center // 文字居中
                ),
                modifier = Modifier.padding(8.dp) // 内边距
            )
        }



    }
}



fun getCurrentTime(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return format.format(Date())
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QZWX_APPTheme {
        DiaryScreen()
    }
}
