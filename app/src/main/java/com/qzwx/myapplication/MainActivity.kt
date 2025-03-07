package com.qzwx.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.myapplication.components.BottomNavItem
import com.qzwx.myapplication.components.CustomBottomNavigationBar
import com.qzwx.myapplication.navigation.NavDestinations
import com.qzwx.myapplication.navigation.NavGraph
import com.qzwx.myapplication.notification.NotificationChannels

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationChannels.createNotificationChannels(this)
        enableEdgeToEdge()

        setContent {
            QZWX_AppTheme {
                Surface(modifier = Modifier.systemBarsPadding()) {
                    MyApp()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    val items = listOf(
        BottomNavItem("主页", R.drawable.svg_all, NavDestinations.HOME),
        BottomNavItem("音乐", R.drawable.svg_music1, NavDestinations.MUSIC),
        BottomNavItem("我的", R.drawable.svg_my, NavDestinations.PROFILE)
    )
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in listOf(NavDestinations.HOME,
                    NavDestinations.MUSIC,
                    NavDestinations.PROFILE)) {
                CustomBottomNavigationBar(
                    items = items,
                    navController = navController
                )
            }
        }
    ) {
        NavGraph(navController = navController)
    }
}