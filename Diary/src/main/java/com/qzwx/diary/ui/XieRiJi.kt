package com.qzwx.diary.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.window.Dialog
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.diary.MainActivity
import com.qzwx.diary.R
import com.qzwx.diary.data.DiaryEntry
import com.qzwx.diary.data.DiaryViewModel
import com.qzwx.diary.theme.QZWX_APPTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class XieRiJi : ComponentActivity() {
    private val diaryViewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_APPTheme {
                DiaryScreen(diaryViewModel)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(diaryViewModel: DiaryViewModel) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var diaryTitle by remember { mutableStateOf("") }
    var diaryContent by remember { mutableStateOf("") }
    var keyboardHeight by remember { mutableStateOf(0) }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color(0xFF7A8FDA) // 系统状态栏颜色
    )
//【1开始】获取时间代码
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000)
        }
    }
//【1结束】
    val scrollState = rememberScrollState()
    val view = LocalView.current
    val density = LocalDensity.current.density
//    【2开始】获取键盘，保证不会遮挡页面
    DisposableEffect(view) {
        val callback = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardHeight = (keypadHeight / density).toInt().coerceAtLeast(0)
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(callback)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(callback)
        }
    }
//【2结束】
    val backgroundImage = painterResource(id = R.drawable.xieriji_bg)     //整个页面背景
//【A开始】下面是整个页面ui代码
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = keyboardHeight.dp)
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
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .border(BorderStroke(2.dp, Color.Black), shape = MaterialTheme.shapes.medium)
                    .padding(15.dp)
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
//【2开始】作者寄语组件
            Text(
                text = "---愿有一天，当你翻阅此篇日记之际，能够唤起那刻时光的点点滴滴---",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.qzwx_kaiti))
                ),
                color = Color(0xFF59D7E7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 8.dp),
                textAlign = TextAlign.Center
            )
//【2结束】【3开始】日记标题组件
            OutlinedTextField(
                value = diaryTitle,
                onValueChange = { diaryTitle = it },
                label = { Text("日记标题") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.qzwx_cuti))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0x63A5A8EF),
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
//【3结束】【4开始】日记正文组件
            OutlinedTextField(
                value = diaryContent,
                onValueChange = { diaryContent = it },
                label = { Text("日记内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .verticalScroll(scrollState),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.qzwx_geziti))
                ),
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
        }
//【4结束】【1开始】保存按钮组件
        FloatingActionButton(
            onClick = {
                // 创建一个 DiaryEntry 对象并插入数据库
                val diaryEntry = DiaryEntry(
                    timestamp = currentTime,
                    title = diaryTitle,
                    content = diaryContent
                )
                diaryViewModel.insertDiaryEntry(diaryEntry)
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(50.dp),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Text(
                text = "保存",
                style = TextStyle(
                    color = Color(0xFF238692),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("恭喜您添加了一篇日记")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            showDialog = false
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }) {
                            Text("确定")
                        }
                    }}}}

//【1结束】
    }
}
//【A结束】
//【7开始】这是获取正确时间代码
fun getCurrentTime(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(Date())
}  //【7结束】

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QZWX_APPTheme {
        DiaryScreen(DiaryViewModel(Application()))
    }
}
