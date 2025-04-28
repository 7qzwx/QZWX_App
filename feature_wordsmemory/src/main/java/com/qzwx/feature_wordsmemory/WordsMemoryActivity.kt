package com.qzwx.feature_wordsmemory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle
import com.example.bottombar.model.ItemStyle
import com.example.bottombar.model.VisibleItem
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_wordsmemory.data.AppDatabase
import com.qzwx.feature_wordsmemory.data.WordRepository
import com.qzwx.feature_wordsmemory.navigation.NavGraph
import com.qzwx.feature_wordsmemory.ui.handleWordsImportResult
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModelFactory

class WordsMemoryActivity : ComponentActivity() {
    // 注册文件选择结果的处理器，使用ActivityResultContracts
    private val importWordsLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri : Uri? ->
            if (uri != null) {
                // 创建一个包含URI的Intent，传递给处理函数
                val intent = Intent().apply { data = uri }
                handleWordsImportResult(this, intent)
            } else {
                Toast.makeText(this, "未选择文件", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = WordRepository(database.wordDao())

        setContent {
            QZWX_AppTheme {
                val viewModel : WordViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = WordViewModelFactory(repository)
                )
                WMsystem(viewModel = viewModel)
            }
        }
    }

    // 启动文件选择器，选择 CSV 文件
    fun importWordsDatabase() {
        try {
            // 显示启动导入的Toast提示
            Toast.makeText(this, "请选择备份的CSV文件", Toast.LENGTH_SHORT).show()
            importWordsLauncher.launch("text/*") // 启动文件选择器选择 CSV 文件
        } catch (e : Exception) {
            Toast.makeText(this, "打开文件选择器失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun WMsystem(viewModel : WordViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Triple("主页", R.drawable.nav_home, "homepage"),
        Triple("单词库", R.drawable.nav_allwards, "vocabularypage"),
        Triple("统计", R.drawable.nav_chart, "statisticpage"),
        Triple("设置", R.drawable.nav_setting, "settingpage")
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedItemIndex = remember { mutableStateOf(0) }

    LaunchedEffect(currentRoute) {
        selectedItemIndex.value = items.indexOfFirst { it.third == currentRoute }
    }
    // 只在 `homepage`, `vocabularypage`, `statisticpage`, `settingpage` 这些页面显示底部导航栏
    val shouldShowBottomBar = currentRoute in items.map { it.third }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) { // 只有在允许的页面才显示底部栏
                AnimatedBottomBar(
                    selectedItem = selectedItemIndex.value,
                    indicatorStyle = IndicatorStyle.DOT,
                    containerShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    indicatorColor = MaterialTheme.colorScheme.onSurface,
                    indicatorShape = RoundedCornerShape(25.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    items.forEachIndexed { index, (label, icon, route) ->
                        val selected = index == selectedItemIndex.value
                        BottomBarItem(
                            selected = selected,
                            onClick = {
                                selectedItemIndex.value = index
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            imageVector = ImageVector.vectorResource(id = icon), // 使用 vectorResource 加载图标
                            label = label,
                            visibleItem = VisibleItem.ICON,
                            itemStyle = ItemStyle.STYLE1
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navController = navController, viewModel = viewModel)
        }
    }
}