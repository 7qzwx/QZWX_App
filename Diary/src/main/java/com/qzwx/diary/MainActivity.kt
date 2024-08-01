package com.qzwx.diary

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.diary.data.DiaryViewModel
import com.qzwx.diary.ui.XieRiJi
import com.qzwx.diary.theme.QZWX_APPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: DiaryViewModel = ViewModelProvider(this, DiaryViewModel.DiaryViewModelFactory(application)).get(DiaryViewModel::class.java)  //调用数据库在页面上可视初始化

        setContent {
            QZWX_APPTheme {
                MainScreen() // 加载主屏幕内容
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 记录当前选中的导航项，初始值为 0（点滴）
    var selectedItem by remember { mutableIntStateOf(0) }
    // 控制浮动按钮的显示
    var isFabExpanded by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color(0xFFACB8F4) // 设置为透明，或你需要的颜色
    )

    // 使用 Scaffold 布局，包含底部导航栏和内容区
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    if (index == 2) {
                        // 点击 + 按钮时切换浮动按钮的显示状态
                        isFabExpanded = !isFabExpanded
                    } else {
                        // 选择其他导航项时切换页面
                        selectedItem = index
                        isFabExpanded = false // 隐藏浮动按钮
                    }
                }
            )
        },
        // 中间悬浮＋号
        floatingActionButton = {
            if (isFabExpanded) {
                // 获取当前上下文
                val context = LocalContext.current

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 增加点滴的悬浮按钮
                    FloatingActionButton(
                        onClick = {
                            // 创建一个 Intent 用于启动 XieRiJi Activity
                            val intent = Intent(context, XieRiJi::class.java)
                            // 启动 XieRiJi Activity
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.svg_diandi),
                            contentDescription = "增加点滴",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // 增加便签的悬浮按钮
                    FloatingActionButton(
                        onClick = {
                            // 创建一个 Intent 用于启动 XieBianQian Activity
                            val intent = Intent(context, XieBianQian::class.java)
                            // 启动 XieBianQian Activity
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.svg_bianqian),
                            contentDescription = "增加便签",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },

        floatingActionButtonPosition = FabPosition.Center,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // 根据当前选中的导航项显示不同的内容
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> DianDiScreen(DiaryViewModel(Application())) // 显示点滴内容
                1 -> CalendarScreen() // 显示日历内容
                3 -> NotesScreen() // 显示便签内容
                4 -> FavoritesScreen() // 显示收藏内容
            }
        }
    }
}


@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    // 创建底部导航栏
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(painterResource(R.drawable.svg_diandi), contentDescription = "点滴", modifier = Modifier.size(24.dp)) },
            label = { Text("点滴") }
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(painterResource(R.drawable.svg_rili), contentDescription = "日历", modifier = Modifier.size(24.dp)) },
            label = { Text("日历") }
        )
        // Special + button in the middle
        NavigationBarItem(
            selected = false, // 不需要选中状态
            onClick = { onItemSelected(2) }, // 点击时显示/隐藏浮动按钮
            icon = { Icon(painterResource(R.drawable.svg_tianjia), contentDescription = "添加", modifier = Modifier.size(24.dp)) },
            label = { Text("") }, // 空标签
            alwaysShowLabel = false
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = { Icon(painterResource(R.drawable.svg_bianqian), contentDescription = "便签", modifier = Modifier.size(24.dp)) },
            label = { Text("便签") }
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = { onItemSelected(4) },
            icon = { Icon(painterResource(R.drawable.svg_shoucang), contentDescription = "收藏", modifier = Modifier.size(24.dp)) },
            label = { Text("收藏") }
        )
    }
}



@Composable
fun CalendarScreen() {
    // 定义日历页面内容
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("这是日历页面内容")
    }
}



@Composable
fun FavoritesScreen() {
    // 定义收藏页面内容
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("这是收藏页面内容")
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MainScreenPreview() {
    // 预览主屏幕，包括底部导航栏和内容
    QZWX_APPTheme {
        MainScreen()
    }
}
