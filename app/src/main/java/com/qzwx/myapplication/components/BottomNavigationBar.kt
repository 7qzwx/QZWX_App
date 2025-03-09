package com.qzwx.myapplication.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Straight
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius

// 底部导航栏项的数据类
data class BottomNavItem(
    val label : String,
    val iconResId : Int,
    val route : String
)

// 底部导航栏的 Composable 函数
@Composable
fun CustomBottomNavigationBar(
    items : List<BottomNavItem>,
    navController : NavHostController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    val barColor = if (isSystemInDarkTheme()) {
        // 深色模式下的条形颜色
        MaterialTheme.colorScheme.onPrimary.copy(0.1f)
    } else {
        // 浅色模式下的条形颜色
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.01f)
    }
    AnimatedNavigationBar(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
            .height(68.dp),
        selectedIndex = selectedIndex,
        ballColor = MaterialTheme.colorScheme.primary,
        barColor = barColor,
        cornerRadius = shapeCornerRadius(25.dp),
        ballAnimation = Straight(
            spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessVeryLow)
        )
    ) {
        items.forEachIndexed { index, item ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = item.iconResId),
                    contentDescription = item.label,
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                )
            }
        }
    }
}