package com.qzwx.qzwxapp.navigation

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

/**
 * 导航项数据类
 */
data class NavigationItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val description: String,
    val route: String
)

/**
 * 含滑动功能的主页内容
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(
    navController: NavController,
    navigationItems: List<NavigationItem> = listOf(
        NavigationItem(
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            description = "首页",
            route = "HomePage"
        ),
        NavigationItem(
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
            description = "收藏",
            route = "MusicPage"
        ),
        NavigationItem(
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            description = "个人",
            route = "MyPage"
        )
    ),
    content: @Composable (Int) -> Unit
) {
    // 获取当前导航路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 根据当前路由设置选中的索引
    val initialPage = when(currentRoute) {
        "HomePage" -> 0
        "MusicPage" -> 1
        "MyPage" -> 2
        else -> 0
    }
    
    // 使用PagerState管理页面状态
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { navigationItems.size }
    )
    
    // 使用协程处理页面切换
    val coroutineScope = rememberCoroutineScope()
    
    // 处理返回按钮直接退出应用
    DisposableEffect(Unit) {
        val activity = navController.context as? ComponentActivity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        
        activity?.onBackPressedDispatcher?.addCallback(callback)
        
        onDispose {
            callback.remove()
        }
    }
    
    // 当导航变化时，更新Pager页面
    LaunchedEffect(currentRoute) {
        val newIndex = when(currentRoute) {
            "HomePage" -> 0
            "MusicPage" -> 1
            "MyPage" -> 2
            else -> null
        }
        
        if (newIndex != null && newIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(newIndex)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // 滑动页面
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                content(page)
            }
        }
        
        // 底部导航栏
        ModernBottomNavigation(
            pagerState = pagerState,
            navigationItems = navigationItems,
            onTabSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
                
                // 移除路由导航，仅保留页面滑动切换
                // navController.navigate(navigationItems[index].route) {
                //     popUpTo(navController.graph.startDestinationId) {
                //         saveState = true
                //     }
                //     launchSingleTop = true
                //     restoreState = true
                // }
            }
        )
    }
}

/**
 * 现代风格底部导航栏
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernBottomNavigation(
    pagerState: PagerState,
    navigationItems: List<NavigationItem>,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEachIndexed { index, item ->
                ModernNavItem(
                    item = item,
                    isSelected = pagerState.currentPage == index,
                    onSelected = { onTabSelected(index) }
                )
            }
        }
    }
}

/**
 * 现代风格导航项
 */
@Composable
fun ModernNavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    else
                        Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.description,
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
            )
        }
        
        // 只在选中时显示文字
        AnimatedVisibility(visible = isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * 可点击修饰符（无涟漪效果）
 */
@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.noRippleClickable(
    onClick: () -> Unit
) = clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)

/**
 * 旋转修饰符
 */
fun Modifier.rotate(degrees: Float) = graphicsLayer(rotationZ = degrees)

/**
 * 示例用法：
 *
 * @Composable
 * fun MyApp() {
 *     MainContent()
 * }
 */