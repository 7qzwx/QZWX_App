package com.qzwx.myapplication

import BottomNavItem
import CustomBottomNavigationBar
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
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.myapplication.navigation.NavGraph

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 启用边到边的布局
        setContent {
            QZWX_AppTheme {
                MyApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    // 定义底部导航栏的选项
    val items = listOf(
        BottomNavItem("主页", R.drawable.svg_all, "home"),
        BottomNavItem("音乐", R.drawable.svg_music1, "music"),
        BottomNavItem("我的", R.drawable.svg_my, "profile")
    )
    // 系统状态栏修改
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color(0xFF310952) // 设置为透明，或你需要的颜色
    )
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(
                items = items,
                navController = navController
            )
        }
    ) {
        NavGraph(
            navController = navController
        )
    }
}

