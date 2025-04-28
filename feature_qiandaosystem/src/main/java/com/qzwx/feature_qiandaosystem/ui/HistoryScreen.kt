package com.qzwx.feature_qiandaosystem.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.breens.beetablescompose.BeeTablesCompose
import com.qzwx.feature_qiandaosystem.data.CheckInHistory
import com.qzwx.feature_qiandaosystem.data.CheckInRepository
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModelFactory
/** 历史记录页面,用户点击页面的历史按钮会跳转到该页面   */
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    checkInName : String,
    checkInRepository : CheckInRepository,
    viewModel : CheckInViewModel = viewModel(factory = CheckInViewModelFactory(checkInRepository))
) {
    // 显式指定泛型类型参数
    val allHistory by viewModel.checkInHistory.collectAsState(initial = emptyList<CheckInHistory>())
    // 显式声明转换类型
    val historyItems by remember {
        derivedStateOf<List<HistoryItem>> {
            allHistory.map { history ->
                // 格式化日期
                val formattedDate = formatDate(history.date)
                HistoryItem(
                    checkInCount = history.checkInCount,
                    experience = history.experience,
                    level = history.level,
                    lastCheckInDate = formattedDate
                )
            }
        }
    }

    LaunchedEffect(checkInName) {
        viewModel.loadCheckInHistory(checkInName) // ✅ 在协程作用域调用
    }

    if (historyItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "暂无历史记录", fontSize = 32.sp)
        }
        return
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()) {
            Text(
                text = checkInName,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    BeeTablesCompose(
                        data = historyItems,
                        enableTableHeaderTitles = true,
                        headerTableTitles = listOf("次数", "经验", "等级", "日期"),
                        headerTitlesTextStyle = MaterialTheme.typography.bodyLarge
                        // 其他参数...
                    )
                }
            }
        }
    }
}

// 日期格式化函数
private fun formatDate(dateString : String) : String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("MM-dd")
    return LocalDate.parse(dateString, inputFormatter).format(outputFormatter)
}

data class RecordItem(
    val name : String,
    val experience : Int,
    val dayCount : Int,
    val level : Int,
    val lastCheckInDate : String
)

data class HistoryItem(
    val checkInCount : Int,
    val experience : Int,
    val level : Int,
    val lastCheckInDate : String
)