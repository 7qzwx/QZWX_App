package com.qzwx.feature_wordsmemory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModelFactory

class WordsMemoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = WordRepository(database.wordDao())

        setContent {
            QZWX_AppTheme {
                val viewModel : WordViewModel = viewModel(
                    factory = WordViewModelFactory(repository)
                )
                WMsystem(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WMsystem(viewModel : WordViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Triple("主页", Icons.Default.Home, "homepage"),
        Triple("复习", Icons.Default.Reviews, "reviewpage"),
        Triple("统计", Icons.Default.EvStation, "statisticpage"),
        Triple("设置", Icons.Default.Settings, "settingpage")
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedItemIndex = remember { mutableStateOf(0) }

    LaunchedEffect(currentRoute) {
        selectedItemIndex.value = items.indexOfFirst { it.third == currentRoute }
    }

    Scaffold(
        bottomBar = {
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
                        imageVector = icon,
                        label = label,
                        visibleItem = VisibleItem.ICON,
                        itemStyle = ItemStyle.STYLE1
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navController = navController, viewModel = viewModel) // 传入 ViewModel
        }
    }
}