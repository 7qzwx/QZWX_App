package qzwx.app.qzwxapp.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.flowOf
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// 这些颜色定义保留，但现在主要使用ColorManager
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff00574A),
    background = Color(0xFF141C25),
    surface = Color(0xFF222A33),
    onSurface = Color(0xFFF2F2F2),
)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xff48AB93),
    background = Color(0xFFF2F2F2),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun QZWX_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current


    // 设置系统状态栏颜色
    val systemUiController = rememberSystemUiController()
    val syscolor = colorScheme.background
    val darkiconscolor = !darkTheme

    SideEffect {
        systemUiController.setStatusBarColor(
            color = syscolor, // 状态栏背景颜色
            darkIcons = darkiconscolor, // 根据主题设置图标颜色
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

