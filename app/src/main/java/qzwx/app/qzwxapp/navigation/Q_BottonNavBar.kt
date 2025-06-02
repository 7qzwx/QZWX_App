package qzwx.app.qzwxapp.navigation

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
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
    val icon: ImageVector,
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
            icon = Icons.Rounded.Home,
            route = "HomePage"
        ),
        NavigationItem(
            icon = Icons.Rounded.Favorite,
            route = "MusicPage"
        ),
        NavigationItem(
            icon = Icons.Rounded.Person,
            route = "MyPage"
        )
    ),
    content: @Composable (Int) -> Unit
) {
    // 获取当前导航路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 根据当前路由设置选中的索引
    val initialPage = when (currentRoute) {
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
        val newIndex = when (currentRoute) {
            "HomePage" -> 0
            "MusicPage" -> 1
            "MyPage" -> 2
            else -> null
        }

        if (newIndex != null && newIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(newIndex)
        }
    }

    // 使用Surface确保整个屏幕应用正确的背景颜色
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 内容区域 - 添加底部padding以避免内容被导航栏遮挡
            HorizontalPager(
                state = pagerState,
                // 添加底部padding，确保内容不会被导航栏遮挡
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp)
            ) { page ->
                content(page)
            }

            // 底部导航栏 - 缩小宽度，调整padding使其紧贴按钮
            BlurredBottomNavigation(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(270.dp)  // 缩小导航栏宽度
                    .height(65.dp)  // 控制导航栏高度
                    .align(Alignment.BottomCenter),
                pagerState = pagerState,
                items = navigationItems
            ) { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        }
    }
}

/**
 * 带高斯模糊效果的底部导航栏
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BlurredBottomNavigation(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    items: List<NavigationItem>,
    onItemClick: (Int) -> Unit
) {
    val selectedIndex = pagerState.currentPage
    val density = LocalDensity.current
    val dcolor0f = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val dcolor1f = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    val hcolor0f = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    // 高斯模糊背景的导航栏
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .graphicsLayer {
                this.alpha = 0.9f  // 增加一点透明度效果
            }
            // 创建模糊效果背景
            .drawWithCache {
                onDrawWithContent {
                    // 创建带梯度的背景，模拟模糊效果
                    val gradient = Brush.verticalGradient(
                        0f to dcolor0f,
                        1f to dcolor1f,
                        tileMode = TileMode.Clamp
                    )

                    // 绘制渐变背景
                    drawRect(gradient)

                    // 添加高光效果增强视觉体感
                    val highlightGradient = Brush.radialGradient(
                        0f to hcolor0f,
                        1f to Color.Transparent,
                        radius = size.width * 0.8f,
                        center = center.copy(y = 0f)
                    )
                    drawRect(highlightGradient, blendMode = BlendMode.SrcOver)

                    // 绘制内容
                    drawContent()
                }
            }
            .padding(horizontal = 8.dp, vertical = 8.dp)  // 减小内边距，使导航栏更紧凑
    ) {
        // 导航项容器
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,  // 改为SpaceEvenly使按钮均匀紧凑排列
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                NavItem(
                    item = item,
                    isSelected = index == selectedIndex,
                    onClick = { onItemClick(index) }
                )
            }
        }
    }
}

/**
 * 单个导航项
 */
@Composable
fun NavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 移除缩放动画，只保留透明度动画
    val textAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(300),
        label = "text_alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(60.dp)  // 减小导航项宽度
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // 图标
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // 选中状态的背景光效
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)  // 减小背景光效大小
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                )
            }

            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(22.dp)  // 减小图标大小
            )
        }

    }
}

/**
 * 旋转修饰符
 */
fun Modifier.rotate(degrees: Float) = graphicsLayer(rotationZ = degrees)

