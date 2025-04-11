package com.qzwx.feature_accountbook.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Straight
import com.exyte.animatednavbar.animation.indendshape.StraightIndent
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.lerp
import com.exyte.animatednavbar.utils.noRippleClickable
import com.exyte.animatednavbar.utils.toDp
import com.qzwx.feature_accountbook.R

@Composable
fun ColorButtonNavBar(modifier : Modifier, navController : NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    var prevSelectedIndex by remember { mutableStateOf(0) }
    // 添加对导航状态变化的监听
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 根据当前路由更新选中的导航项
            selectedItem = when (destination.route) {
                "HomePage"     -> 0
                "CalendarPage" -> 1
                "WalletPage"   -> 2
                "ChartPage"    -> 3
                "MyPage"       -> 4
                else           -> selectedItem
            }
        }
    }
    // 定义导航按钮的图标和对应的路由
    val colorButtons =
        listOf(
            ButtonBackground(icon = R.drawable.ico_home) to "HomePage",
            ButtonBackground(icon = R.drawable.ic_calendar) to "CalendarPage",
            ButtonBackground(icon = R.drawable.ico_wallet) to "WalletPage",
            ButtonBackground(icon = R.drawable.ico_chart) to "ChartPage",
            ButtonBackground(icon = R.drawable.ico_my) to "MyPage"
        )
    val animationTypes = List(colorButtons.size) { DefaultAnimationType() }
    val barColor = if (isSystemInDarkTheme()) {
        // 深色模式下的条形颜色
        MaterialTheme.colorScheme.onPrimary.copy(0.1f)
    } else {
        // 浅色模式下的条形颜色
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.01f)
    }
    AnimatedNavigationBar(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .height(85.dp),
        selectedIndex = selectedItem,
        ballColor = MaterialTheme.colorScheme.primary,
        barColor = barColor,
        cornerRadius = shapeCornerRadius(25.dp),
        ballAnimation =
        Straight(spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessVeryLow)),
        indentAnimation =
        StraightIndent(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(1000)
        )
    ) {
        colorButtons.forEachIndexed { index, (background, route) ->
            ColorButton(
                modifier = Modifier.fillMaxSize(),
                prevSelectedIndex = prevSelectedIndex,
                selectedIndex = selectedItem,
                index = index,
                onClick = {
                    prevSelectedIndex = selectedItem
                    selectedItem = index
                    // 检查当前页面是否已经是目标页面，避免重复压栈
                    if (navController.currentDestination?.route != route) {
                        navController.navigate(route) {
                            // 设置 launchSingleTop 为 true，避免重复压栈
                            launchSingleTop = true
                            // 添加 popUpTo 以清除重复的栈
                            popUpTo(
                                navController
                                    .graph
                                    .startDestinationId
                            ) { saveState = true }
                        }
                    }
                },
                icon = background.icon,
                contentDescription = null,
                background = background,
                animationType = animationTypes[index]
            )
        }
    }
}

data class ButtonBackground(@DrawableRes val icon : Int, val offset : DpOffset = DpOffset.Zero)

class DefaultAnimationType : ColorButtonAnimation() {
    @Composable
    override fun AnimatingIcon(
        modifier : Modifier,
        isSelected : Boolean,
        isFromLeft : Boolean,
        icon : Int
    ) {
        val iconColor = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Gray.copy(0.5f)
        }
        // 实现动画效果，保持你的逻辑或根据需要调整
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor,
            modifier = modifier
        )
    }
}

@Stable
abstract class ColorButtonAnimation(
    open val animationSpec : FiniteAnimationSpec<Float> = tween(10000),
) {
    @Composable
    abstract fun AnimatingIcon(
        modifier : Modifier,
        isSelected : Boolean,
        isFromLeft : Boolean,
        icon : Int,
    )
}

@Composable
fun ColorButton(
    modifier : Modifier = Modifier,
    index : Int,
    selectedIndex : Int,
    prevSelectedIndex : Int,
    onClick : () -> Unit,
    @DrawableRes icon : Int,
    contentDescription : String? = null,
    background : ButtonBackground,
    backgroundAnimationSpec : AnimationSpec<Float> = remember {
        tween(300, easing = LinearEasing)
    },
    animationType : ColorButtonAnimation,
    maxBackgroundOffset : Dp = 25.dp
) {
    val isSelected = remember(selectedIndex, index) { selectedIndex == index }
    val iconColor = if (isSelected) {
        // 这里设置选中时的特殊颜色，可根据需求修改
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray
    }

    Box(modifier = modifier.noRippleClickable { onClick() }) {
        val isSelected = remember(selectedIndex, index) { selectedIndex == index }
        val fraction =
            animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = backgroundAnimationSpec,
                label = "fractionAnimation"
            )
        val density = LocalDensity.current
        val maxOffset =
            remember(maxBackgroundOffset) {
                with(density) { maxBackgroundOffset.toPx() }
            }
        val isFromLeft =
            remember(prevSelectedIndex, index, selectedIndex) {
                (prevSelectedIndex < index) || (selectedIndex > index)
            }
        val offset by
        remember(isSelected, isFromLeft) {
            derivedStateOf {
                calculateBackgroundOffset(
                    isSelected = isSelected,
                    isFromLeft = isFromLeft,
                    maxOffset = maxOffset,
                    fraction = fraction.value
                )
            }
        }

        Icon(
            modifier = Modifier
                .offset(
                    x = background.offset.x + offset.toDp(),
                    y = background.offset.y
                )
                .scale(fraction.value)
                .align(Alignment.Center),
            tint = iconColor,
            painter = painterResource(id = background.icon),
            contentDescription = contentDescription
        )

        animationType.AnimatingIcon(
            modifier = Modifier.align(Alignment.Center),
            isSelected = isSelected,
            isFromLeft = isFromLeft,
            icon = icon
        )
    }
}

private fun calculateBackgroundOffset(
    isSelected : Boolean,
    isFromLeft : Boolean,
    fraction : Float,
    maxOffset : Float
) : Float {
    val offset = if (isFromLeft) -maxOffset else maxOffset
    return if (isSelected) {
        lerp(offset, 0f, fraction)
    } else {
        lerp(-offset, 0f, fraction)
    }
}
