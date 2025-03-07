package com.qzwx.feature_wordsmemory.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomBar(navController : NavController) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("首页") },
            selected = navController.currentDestination?.route == "homepage",
            onClick = { navController.navigate("homepage") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Reviews, contentDescription = "Review") },
            label = { Text("复习") },
            selected = navController.currentDestination?.route == "reviewpage",
            onClick = { navController.navigate("reviewpage") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.StarRate, contentDescription = "Statistic") },
            label = { Text("统计") },
            selected = navController.currentDestination?.route == "statisticpage",
            onClick = { navController.navigate("statisticpage") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("设置") },
            selected = navController.currentDestination?.route == "settingpage",
            onClick = { navController.navigate("settingpage") }
        )
    }
}