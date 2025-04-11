package com.qzwx.qzwxapp.navigation

import android.os.*
import androidx.annotation.*
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.qzwx.qzwxapp.*
import com.qzwx.qzwxapp.ui.*

//导航逻辑
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(navController : NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "SplashPage"
    ) {
        composable("HomePage") {
            HomePage(navController)
        }
        composable("MusicPage") {
            MusicPage(navController)
        }
        composable("MyPage") {
            MyPage(navController)
        }
        composable("SplashPage") {
            SplashPage(navController)
        }
    }
}