package com.qzwx.core.theme

import android.os.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.google.accompanist.systemuicontroller.*

// 定义亮色主题
private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
//    secondaryContainer = secondaryContainerLight,
//    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
//    tertiaryContainer = tertiaryContainerLight,
//    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
//    errorContainer = errorContainerLight,
//    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight
)

// 定义暗色主题
private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
//    secondaryContainer = secondaryContainerDark,
//    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
//    tertiaryContainer = tertiaryContainerDark,
//    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
//    errorContainer = errorContainerDark,
//    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark
)

@Composable
fun QZWX_AppTheme(
    darkTheme : Boolean = isSystemInDarkTheme(),
    dynamicColor : Boolean = false,  // 是否根据主题变化颜色
    content : @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme                                                      -> DarkColorScheme
        else                                                           -> LightColorScheme
    }
    // 设置系统 状态栏颜色跟背景一致
    val systemUiController = rememberSystemUiController()
    val syscolor = if (isSystemInDarkTheme()) {
        backgroundDark
    } else {
        backgroundLight
    }
    val darkiconscolor = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = syscolor, // 状态栏
            darkIcons = darkiconscolor, // 根据主题设置图标颜色
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}