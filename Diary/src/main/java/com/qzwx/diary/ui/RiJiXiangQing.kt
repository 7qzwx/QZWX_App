package com.qzwx.diary.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.diary.R
import com.qzwx.diary.data.DiaryViewModel

class RiJiXiangQing : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diaryId = intent.getIntExtra("DIARY_ID", -1) // 获取传递的日记 ID

        setContent {
            // 获取 ViewModel
            val diaryViewModel: DiaryViewModel by viewModels()

            // 展示日记详情页面
            DiaryDetailScreen(diaryId, diaryViewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(diaryId: Int, viewModel: DiaryViewModel) {
    // 获取上下文
    val context = LocalContext.current // 确保在此处获取上下文

    // 设置顶部系统状态栏
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFFACB8F4)) // 设置状态栏颜色

    // 定义自定义字体
    val qzwx_shouxie = FontFamily(Font(R.font.qzwx_shouxie, FontWeight.Normal))
    val qzwx_geziti = FontFamily(Font(R.font.qzwx_geziti, FontWeight.Normal))
    val qzwx_cuti = FontFamily(Font(R.font.qzwx_cuti, FontWeight.Normal))
    val qzwx_kaiti = FontFamily(Font(R.font.qzwx_kaiti, FontWeight.Normal))

    // 获取具体的日记条目
    val diaryEntry by viewModel.getDiaryEntryById(diaryId).observeAsState()

    // 选择字体按钮控件
    var selectedFont by remember { mutableStateOf(qzwx_kaiti) }
    var showFontDialog by remember { mutableStateOf(false) }

    // 悬浮更多按钮的状态
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text(text = "字体选择！", fontFamily = qzwx_kaiti) },
            text = {
                Column {
                    // 添加字体选择项
                    val fontOptions = listOf(qzwx_shouxie to "手写", qzwx_geziti to "日记", qzwx_cuti to "粗体", qzwx_kaiti to "楷体")
                    fontOptions.forEach { (font, name) ->
                        TextButton(onClick = {
                            selectedFont = font
                            showFontDialog = false
                        }) {
                            Text(text = name, fontFamily = qzwx_kaiti)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) {
                    Text("不选择了，就这样挺好看的", fontFamily = qzwx_kaiti)
                }
            },
            containerColor = Color(0xFFACB8F4) // 设置对话框背景颜色
        )
    }

    // 展示日记详情
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMoreOptions = !showMoreOptions },
                modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(50)) // 添加阴影
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00838F), Color(0xFFAD1457)) // 渐变背景
                        ),
                        shape = RoundedCornerShape(50) // 圆形背景
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.svg_gengduo), // 使用你的图标资源
                    contentDescription = "更多选项", // 图标描述
                )
            }
        }
    ) {
        diaryEntry?.let { entry ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFACB8F4)) // 设置背景颜色在这里
                    .padding(16.dp), // 在这里添加内边距，确保内容不紧贴边缘
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .align(Alignment.TopCenter), // 将内容对齐到顶部中心
                    horizontalAlignment = Alignment.CenterHorizontally // 确保内容水平居中
                ) {
                    Text(
                        text = entry.timestamp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) // 展示日记时间
                    Text(
                        fontFamily = qzwx_cuti,
                        text = entry.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) // 展示日记标题
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = selectedFont),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) // 展示日记内容
                }

                // 显示更多选项
                if (showMoreOptions) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        // 字体选择按钮
                        Button(onClick = { showFontDialog = true }) {
                            Text("选择字体")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // 保留编辑图标但不实现点击事件
                        Button(onClick = { /* 编辑功能已被删除 */ }) {
                            Text("修改")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // 分享按钮
                        Button(onClick = { /* TODO: 实现分享功能 */ }) {
                            Text("分享")
                        }
                    }
                }
            }
        }
    }
}
