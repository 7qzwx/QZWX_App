package com.qzwx.myapplication.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // 获取当前选中的项的索引
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }
    // 新增变量，用于记录上一次选中的项的索引
    var prevSelectedIndex by remember { mutableStateOf(0) }
    // 使用第三方库的 AnimatedNavigationBar
    AnimatedNavigationBar(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp) // 添加了上下内边距
            .height(68.dp), // 设置了导航栏的高度
        selectedIndex = selectedIndex,
        ballColor = MaterialTheme.colorScheme.primary, // 设置球的颜色
        barColor = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
        cornerRadius = shapeCornerRadius(25.dp), // 设置导航栏的圆角为25dp
        ballAnimation = Straight(
            spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessVeryLow)
        ) // 设置凹陷动画
    ) {
        items.forEachIndexed { index, item ->
            // 修改了 Box 的内容
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize() // 修改为填满整个区域
                    .clickable(
                        indication = null, // 禁用默认的涟漪效果
                        interactionSource = MutableInteractionSource(), // 可选：禁用交互源
                    ) {
                        // 修改了点击事件
                        prevSelectedIndex = selectedIndex // 更新上一次选中的索引
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
                    tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}