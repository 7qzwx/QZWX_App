package com.qzwx.qzwxapp.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.exyte.animatednavbar.*
import com.exyte.animatednavbar.animation.indendshape.*
import com.exyte.animatednavbar.items.dropletbutton.*
import com.qzwx.qzwxapp.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("HomePage", R.drawable.svg_my, "首页")
    object Music : BottomNavItem("MusicPage", R.drawable.svg_music, "音乐")
    object Profile : BottomNavItem("MyPage", R.drawable.svg_my, "我的")
}

@Composable
fun QZWXBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Music,
        BottomNavItem.Profile
    )
    
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    
    AnimatedNavigationBar(
        modifier = Modifier
            .height(64.dp)
            .shadow(8.dp),
        selectedIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0,
        barColor = MaterialTheme.colorScheme.primaryContainer,
        ballColor = MaterialTheme.colorScheme.surface,
        indentAnimation = StraightIndent(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(1000)
        ),
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = item.route == currentRoute
            
            DropletButton(
                isSelected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // 防止导航栏重复点击创建多个相同目标页面
                            popUpTo(0) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = item.icon,
                contentDescription = item.title,
                iconColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                dropletColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                size = 24.dp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


