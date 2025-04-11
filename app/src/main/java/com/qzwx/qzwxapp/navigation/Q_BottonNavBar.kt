package com.qzwx.qzwxapp.navigation


import android.annotation.*
import androidx.activity.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*

/**
 * 一个独立的动画导航按钮示例
 */
@Composable
fun AnimatedNavigationBarExample(navController: NavController) {
    // 获取当前导航路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 根据当前路由设置选中的索引
    val selectedIndex = when(currentRoute) {
        "HomePage" -> 0
        "MusicPage" -> 1
        "MyPage" -> 2
        else -> 0
    }
    var prevSelectedIndex by remember { mutableIntStateOf(selectedIndex) }

    // 处理返回按钮直接退出应用
    DisposableEffect(Unit) {
        val activity = navController.context as? ComponentActivity
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    activity?.finish()
            }
        }
        
        // 添加回调到OnBackPressedDispatcher
        activity?.onBackPressedDispatcher?.addCallback(callback)
        
        onDispose {
            callback.remove()
        }
    }

    // 导航项数据
    val navigationItems = remember {
        listOf(
            NavigationItem(
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                description = "首页"
            ),
            NavigationItem(
                selectedIcon = Icons.Filled.Favorite,
                unselectedIcon = Icons.Outlined.Favorite,
                description = "收藏"
            ),
            NavigationItem(
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                description = "个人"
            )
        )
    }

    // 主容器
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // 导航栏
            AnimatedNavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    prevSelectedIndex = selectedIndex
                    
                    // 根据选择的索引进行导航
                    when(index) {
                        0 -> navController.navigate("HomePage") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        1 -> navController.navigate("MusicPage") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        2 -> navController.navigate("MyPage") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                items = navigationItems,
                prevSelectedIndex = prevSelectedIndex
            )
        }
    }
}

/**
 * 导航项数据类
 */
data class NavigationItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val description: String
)

/**
 * 自定义动画导航栏
 */
@Composable
fun AnimatedNavigationBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    prevSelectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<NavigationItem>,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
    ) {
        // 绘制指示器
        Box(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.Center)
        ) {
            items.forEachIndexed { index, _ ->
                // 计算每个项的位置
                val itemWidth = modifier
                    .fillMaxWidth()
                    .then(Modifier.padding(0.dp))
                    .fillMaxWidth()
                    .size(1.dp)
                    .fillMaxWidth()
                    .size(1.dp)
                    .size(1.dp)

                if (index == selectedIndex) {
                    // 动画指示器
                    AnimatedIndicator(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(64.dp),
                        index = index,
                        selectedIndex = selectedIndex,
                        prevSelectedIndex = prevSelectedIndex,
                        indicatorColor = indicatorColor
                    )
                }
            }
        }

        // 导航项
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // 均匀分布按钮
            Row(items, selectedIndex, onItemSelected, contentColor, unselectedContentColor)
        }
    }
}

/**
 * 动画指示器
 */
@Composable
private fun AnimatedIndicator(
    modifier: Modifier,
    index: Int,
    selectedIndex: Int,
    prevSelectedIndex: Int,
    indicatorColor: Color
) {
    // 指示器动画
    val animationSpec = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessLow
    )

    // 移动动画
    val scale by animateFloatAsState(
        targetValue = if (selectedIndex == index) 1f else 0f,
        animationSpec = animationSpec,
        label = "scaleAnimation"
    )

    // 绘制动画指示器
    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(indicatorColor.copy(alpha = 0.3f))
    )
}

/**
 * 导航项行
 */
@Composable
private fun Row(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    contentColor: Color,
    unselectedContentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, item ->
            NavItem(
                item = item,
                isSelected = selectedIndex == index,
                onSelected = { onItemSelected(index) },
                contentColor = contentColor,
                unselectedContentColor = unselectedContentColor
            )
        }
    }
}

/**
 * 单个导航项
 */
@Composable
private fun NavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onSelected: () -> Unit,
    contentColor: Color,
    unselectedContentColor: Color
) {
    // 颜色动画
    val color by animateColorAsState(
        targetValue = if (isSelected) contentColor else unselectedContentColor,
        label = "colorAnimation"
    )

    // 图标动画
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    // 旋转动画（仅对设置图标应用）
    val rotation by animateFloatAsState(
        targetValue = if (isSelected && (item.selectedIcon == Icons.Filled.Settings)) 30f else 0f,
        animationSpec = tween(500, easing = LinearEasing),
        label = "rotationAnimation"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .noRippleClickable(onClick = onSelected)
            .padding(12.dp)
            .size(42.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.description,
            tint = color,
            modifier = Modifier
                .scale(scale)
                .rotate(rotation)
                .size(26.dp)
        )
    }
}

/**
 * 可点击修饰符（无涟漪效果）
 */
@SuppressLint("SuspiciousModifierThen")
@Composable
fun Modifier.noRippleClickable(
    onClick: () -> Unit
) = this.then(
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
)

/**
 * 旋转修饰符
 */
fun Modifier.rotate(degrees: Float) = this.then(
    Modifier.graphicsLayer(rotationZ = degrees)
)

/**
 * 示例用法：
 *
 * @Composable
 * fun MyApp() {
 *     AnimatedNavigationBarExample()
 * }
 */