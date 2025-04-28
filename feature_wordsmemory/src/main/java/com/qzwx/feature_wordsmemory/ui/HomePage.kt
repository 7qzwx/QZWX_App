package com.qzwx.feature_wordsmemory.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier : Modifier = Modifier,
    navController : NavController,
    viewModel : WordViewModel
) {
    // Collect state from ViewModel using collectAsState
    val allWordsCount by viewModel.allWordsCount.collectAsState(initial = 0)
    val newWordsCount by viewModel.newWordsCount.collectAsState(initial = 0)
    val familiarWordsCount by viewModel.familiarWordsCount.collectAsState(initial = 0)
    val masteredWordsCount by viewModel.masteredWordsCount.collectAsState(initial = 0)
    val toLearnWordsCount by viewModel.toLearnWordsCount.collectAsState(initial = 0)
    // Get current time for greeting
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 6..11  -> "早上好,开始新的一天吧!"
        in 12..17 -> "下午好,背背单词休息会!"
        in 18..23 -> "晚上好,回忆一下单词吧!"
        else      -> "凌晨了，早点休息吧！"
    }
    // Load data when the component is first composed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadWordCounts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addnewwordspage") },
                containerColor = MaterialTheme.colorScheme.primary,      // 设置背景色为主色
                contentColor = MaterialTheme.colorScheme.onPrimary       // 设置图标颜色为 onPrimary
            ) {
                Icon(Icons.Default.Add,
                    contentDescription = "Add Word")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title Section
                Text(
                    text = "单词统计",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                )
                // Info Cards Section - Each item in a separate row
                InfoCardRow(label = "全部单词", count = allWordsCount)
                InfoCardRow(label = "生疏单词", count = newWordsCount)
                InfoCardRow(label = "熟悉单词", count = familiarWordsCount)
                InfoCardRow(label = "掌握单词", count = masteredWordsCount)
                InfoCardRow(label = "待学习单词", count = toLearnWordsCount)

                Spacer(modifier = Modifier.height(32.dp))
                // Start Learning Button - Centered and styled
                Button(
                    onClick = { navController.navigate("reviewpage") },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "开始学习",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCardRow(label : String, count : Int) {
    // A card that displays label and count with padding and rounded corners
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(greeting : String) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = greeting,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Filter action */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}