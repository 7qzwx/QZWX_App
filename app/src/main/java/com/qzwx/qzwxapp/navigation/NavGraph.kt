package com.qzwx.qzwxapp.navigation

import android.os.*
import androidx.annotation.*
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.qzwx.qzwxapp.*
import com.qzwx.qzwxapp.page.allfunction.AllFunctionPage
import com.qzwx.qzwxapp.page.allweb.AllWebPage
import com.qzwx.qzwxapp.page.home.HomePage
import com.qzwx.qzwxapp.ui.*
import com.qzwx.qzwxapp.viewmodel.LinkViewModel
import com.qzwx.qzwxapp.data.WebAppDatabase

//导航逻辑
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "SplashPage"
    ) {
        // 启动页
        composable("SplashPage") {
            SplashPage(navController)
        }
        
        // 主屏幕容器 - 包含底部导航和所有主要页面
        composable("MainScreen") {
            MainScreen(navController)
        }
        
        // 全部网站页面
        composable("AllWebPage") {
            val context = navController.context
            val linkDao = WebAppDatabase.getDatabase(context).linkDao()
            val linkViewModel = LinkViewModel(linkDao)
            AllWebPage(linkViewModel, navController)
        }
        
        // 深层链接或外部导航的目标路由
        // 注意：这些路由通常不会从MainScreen内部导航到
        // 而是从外部或通知等入口进入
        composable("DetailPage/{id}") { backStackEntry ->
            // DetailPage(navController, backStackEntry.arguments?.getString("id") ?: "")
            // TODO: 实现具体细节页面
        }
        // 其他可能需要的路由...
    }
}

//主页容器，结合滑动导航
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(navController: NavHostController) {
    MainContent(
        navController = navController
    ) { page ->
        when (page) {
            0 -> HomePage()
            1 -> AllFunctionPage(navController)
            2 -> MyPage(navController)
        }
    }
}