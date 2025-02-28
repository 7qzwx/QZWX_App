package com.qzwx.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.qzwx.myapplication.ui.HomeScreen
import com.qzwx.myapplication.ui.MusicScreen
import com.qzwx.myapplication.ui.ProfileScreen

//定义所有页面
object NavDestinations {
    const val HOME = "home"
    const val MUSIC = "music"
    const val PROFILE = "profile"
}

//导航逻辑
@Composable
fun NavGraph(navController : NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.HOME
    ) {
        composable(NavDestinations.HOME) {
            HomeScreen()
        }
        composable(NavDestinations.MUSIC) {
            MusicScreen()
        }
        composable(NavDestinations.PROFILE) {
            ProfileScreen()
        }
    }
}