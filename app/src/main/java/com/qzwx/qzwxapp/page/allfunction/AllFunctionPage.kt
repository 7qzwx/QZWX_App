package com.qzwx.qzwxapp.page.allfunction

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.qzwx.feature_accountbook.AccountBookActivity
import com.qzwx.feature_diary.DiaryActivity
import com.qzwx.feature_wordsmemory.WordsMemoryActivity
import com.qzwx.qzwxapp.JiSuanQi

// 记录上次Toast显示时间
private var lastToastTime = 0L

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllFunctionPage(navController: NavController) {
    val context = LocalContext.current
    
    // Material3 颜色获取
    val colorScheme = MaterialTheme.colorScheme
    
    // 当前展示模式
    var currentDisplayMode by remember { mutableStateOf(DisplayMode.GRID) }
    
    // 显示Toast的函数，防止短时间内重复显示
    val showToastWithDebounce = { message: String ->
        val currentTime = System.currentTimeMillis()
        // 如果距离上次显示超过2000毫秒，才显示新的Toast
        if (currentTime - lastToastTime > 2000) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            lastToastTime = currentTime
        }
    }
    
    // 构建应用数据列表
    val appItems = buildAppItemsList(context, colorScheme, showToastWithDebounce, navController)
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            // 使用提取的TopBar组件
            FunctionTopBar(
                currentDisplayMode = currentDisplayMode,
                onModeChange = { newMode -> currentDisplayMode = newMode },
                colorScheme = colorScheme
            )
        }
    ) { innerPadding ->
        // 使用提取的内容区域组件
        FunctionContent(
            currentDisplayMode = currentDisplayMode,
            appItems = appItems,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

/**
 * 构建应用数据列表
 */
private fun buildAppItemsList(
    context: Context,
    colorScheme: ColorScheme,
    showToastWithDebounce: (String) -> Unit,
    navController: NavController
): List<AppItem> {
    return listOf(
        AppItem(
            title = "计算器",
            color = colorScheme.primaryContainer,
            textColor = colorScheme.onPrimaryContainer,
            description = "简单快捷的计算工具",
            onClick = {
                val intent = Intent(context, JiSuanQi::class.java)
                context.startActivity(intent)
            }
        ),
        AppItem(
            title = "日记本",
            color = colorScheme.secondaryContainer,
            textColor = colorScheme.onSecondaryContainer,
            description = "记录生活的点滴",
            onClick = {
                val intent = Intent(context, DiaryActivity::class.java)
                context.startActivity(intent)
            }
        ),
        AppItem(
            title = "记账本",
            color = colorScheme.tertiaryContainer,
            textColor = colorScheme.onTertiaryContainer,
            description = "管理您的财务状况",
            onClick = {
                val intent = Intent(context, AccountBookActivity::class.java)
                context.startActivity(intent)
            }
        ),
        AppItem(
            title = "签到系统",
            color = colorScheme.errorContainer,
            textColor = colorScheme.onErrorContainer,
            description = "养成打卡好习惯",
           onClick = {
                try {
                    // 使用Action启动外部应用
                    val intent = Intent("com.qzwx.qcheckin.LAUNCH")
                    context.startActivity(intent)
                    
                } catch (e: Exception) {
                    // 更新提示内容，使用防重复函数
                    showToastWithDebounce("无法打开，请确定是否下载QCheckIn，可前往GitHub下载！")
                }
            }
        ),
        AppItem(
            title = "单词本",
            color = colorScheme.inversePrimary,
            textColor = colorScheme.primary,
            description = "学习与记忆单词",
            onClick = {
                val intent = Intent(context, WordsMemoryActivity::class.java)
                context.startActivity(intent)
            }
        ),
        AppItem(
            title = "Todo",
            color = colorScheme.surfaceVariant,
            textColor = colorScheme.onSurfaceVariant,
            description = "任务清单管理",
            onClick = {
                try {
                    // 使用Action启动外部应用
                    val intent = Intent("qzwx.app.qtodo.LAUNCH")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // 更新提示内容，使用防重复函数
                    showToastWithDebounce("无法打开，请确定是否下载QTodo，可前往GitHub下载！")
                }
            }
        ),
        AppItem(
            title = "全部网站",
            color = colorScheme.inverseSurface,
            textColor = colorScheme.inverseOnSurface,
            description = "浏览收藏的网站",
            onClick = {
                navController.navigate("AllWebPage")
            }
        )
    )
}

