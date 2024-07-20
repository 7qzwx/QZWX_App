package com.qzwx.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.qzwx.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 启用边到边的布局
        setContent {
            MyApplicationTheme {
                MyApp() // 设置应用的主要内容
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyApp() {
    // 定义底部导航栏的选项
    val items = listOf(
        BottomNavItem("主页", R.drawable.svg_all), // 自定义图标资源 ID
        BottomNavItem("音乐", R.drawable.svg_music), // 自定义图标资源 ID
        BottomNavItem("我的", R.drawable.svg_my) // 自定义图标资源 ID
    )

    // 创建PagerState，用于页面左右切换效果
    val pagerState = rememberPagerState()

    Scaffold(
        modifier = Modifier.fillMaxSize(), // 设置Scaffold填满整个屏幕
        bottomBar = {
            BottomNavigationBar(items, pagerState)
        },
        content = { paddingValues ->
            HorizontalPager(
                count = items.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // 设置Pager填满整个屏幕并应用填充
            ) { page ->
                when (page) {
                    0 -> HomeScreen()
                    1 -> MusicScreen()
                    2 -> ProfileScreen()
                }
            }
        }
    )
}

// 底部导航栏项的数据类
data class BottomNavItem(val label: String, val iconResId: Int)

// 底部导航栏的Composable函数
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()

    NavigationBar(
        modifier = Modifier
            .height(56.dp) // 设置底部导航栏的高度
            .fillMaxWidth(), // 设置底部导航栏填满宽度
        containerColor = Color.Gray, // 设置底部导航栏的背景颜色
        tonalElevation = 5.dp // 设置导航栏的阴影高度
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    // 使用painterResource加载自定义图标
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp) // 设置图标的大小，24.dp为示例值
                    )
                },
                selected = pagerState.currentPage == index,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // 设置选中时图标的颜色
                    unselectedIconColor = Color.Gray, // 设置未选中时图标的颜色
                    selectedTextColor = Color.Transparent, // 隐藏选中时的文字颜色
                    unselectedTextColor = Color.Transparent // 隐藏未选中时的文字颜色
                ),
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}


// 音乐页内容的Composable函数
@Composable
fun MusicScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "音乐", style = MaterialTheme.typography.headlineMedium)
        // 可以在这里添加更多音乐页的组件
    }
}

// 我的页面内容的Composable函数
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "我的", style = MaterialTheme.typography.headlineMedium)
        // 可以在这里添加更多我的页面的组件
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApplicationTheme {
        MyApp() // 预览MyApp的内容
    }
}
