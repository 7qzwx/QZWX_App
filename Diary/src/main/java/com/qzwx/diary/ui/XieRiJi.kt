package com.qzwx.diary.ui

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.diary.R
import com.qzwx.diary.data.DiaryEntry
import com.qzwx.diary.data.DiaryViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class XieRiJi : ComponentActivity() {
    private val diaryViewModel : DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_AppTheme {
                DiaryScreen(diaryViewModel)
            }
        }
    }
}

@Composable
fun rememberImeState() : State<Boolean> {
    val imeState = remember { mutableStateOf(false) }
    val view = LocalView.current

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            imeState.value =
                ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime())
                    ?: true
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return imeState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(diaryViewModel : DiaryViewModel) {
    val context = LocalContext.current
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDate()) }
    var diaryTitle by remember { mutableStateOf("") }
    var diaryContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var focusState by remember { mutableStateOf(false) }
    val size by animateFloatAsState(targetValue = if (focusState) 1f else 0.5f)
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF7A8FDA))

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000)
        }
    }
    val backgroundImage = painterResource(id = R.drawable.xieriji_bg)

    Box(modifier = Modifier.fillMaxSize()) {
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
                .verticalScroll(scrollState)
        ) {
//            // 显示当前日期和时间
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 12.dp, start = 20.dp, end = 20.dp)
//                    .border(BorderStroke(2.dp, Color.Black), shape = MaterialTheme.shapes.medium)
//                    .padding(7.dp)
//            ) {
//                Text(
//                    text = "$currentDate",
//                    fontSize = 16.sp,
//                    color = Color.Black,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//                Text(
//                    text = currentTime,
//                    fontSize = 20.sp,
//                    color = Color.Black,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
            // 作者寄语
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
//            // 日记标题输入框
//            OutlinedTextField(
//                value = diaryTitle,
//                onValueChange = { diaryTitle = it },
//                label = { Text("日记标题") },
//                singleLine = true,
//                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily(Font(R.font.qzwx_cuti))),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 7.dp, start = 7.dp, end = 7.dp)
//                    .scale(size)
//                    .onFocusChanged { focusState = it.isFocused },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    containerColor = Color(0x63A5A8EF),
//                    focusedLabelColor = Color.Black,
//                    unfocusedLabelColor = Color.Black
//                )
//            )
            // 日记内容输入框
            OutlinedTextField(
                value = diaryContent,
                onValueChange = { diaryContent = it },
                label = { Text("日记内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .verticalScroll(scrollState),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily(Font(R.font.qzwx_geziti))),
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )
        }
        // 保存按钮
        FloatingActionButton(
            onClick = {
                val diaryEntry = DiaryEntry(
                    riqi = currentDate,
                    timestamp = currentTime,
                    title = diaryTitle,
                    zishutongji = diaryContent.length,
                    content = diaryContent
                )
                diaryViewModel.insertDiaryEntry(diaryEntry)
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(50.dp),
            contentColor = Color.White
        ) {
            Text(
                text = "保存",
                style = TextStyle(color = Color(0xFF238692), fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(8.dp)
            )
        }
//        // 显示对话框
//        if (showDialog) {
//            Dialog(onDismissRequest = { showDialog = false }) {
//                Surface(
//                    shape = MaterialTheme.shapes.medium,
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text("恭喜您添加了一篇日记")
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = {
//                            showDialog = false
//                            context.startActivity(Intent(context, MainActivity::class.java))
//                        }) {
//                            Text("确定")
//                        }
//                    }
//                }
//            }
//        }
    }
}

// 获取当前时间
fun getCurrentTime() : String {
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return format.format(Date())
}

// 获取当前日期
fun getCurrentDate() : String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd  EEEE", Locale.getDefault())
    return dateFormat.format(Date())
}

