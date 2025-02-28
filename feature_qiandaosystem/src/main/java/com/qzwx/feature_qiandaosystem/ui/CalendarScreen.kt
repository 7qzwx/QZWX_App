package com.qzwx.feature_qiandaosystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.qzwx.core.room.room_qiandaosystem.CheckInHistory
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(modifier : Modifier = Modifier, viewModel : CheckInViewModel) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { java.time.DayOfWeek.MONDAY }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    // 用于存储当前选中的日期及其签到记录
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var checkInHistory by remember { mutableStateOf<List<CheckInHistory>>(emptyList()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("签到一览") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp) // 设置页面边距
        ) {
            // 日历部分
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    DayContent(day, viewModel, onDayClick = { date ->
                        selectedDate = date
                    })
                },
                modifier = Modifier.weight(1f) // 设置日历占满剩余空间
            )
            // 当选中的日期改变时，获取签到记录
            LaunchedEffect(selectedDate) {
                if (selectedDate != null) {
                    checkInHistory = viewModel.getCheckInHistoryByDate(selectedDate!!).first()
                }
            }
            // 显示签到记录
            if (selectedDate != null && checkInHistory.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // 限制 LazyColumn 的高度，避免遮挡日历
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    item {
                        Text(
                            text = "签到记录: ${selectedDate.toString()}",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(checkInHistory.size) { index ->
                        val history = checkInHistory[index]
                        Text(
                            text = " ${history.checkInName}  获得  经验值 ${history.experience}",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (index < checkInHistory.size - 1) {
                            // 在每个签到记录之间添加分隔线
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayContent(day: CalendarDay, viewModel: CheckInViewModel, onDayClick: (LocalDate) -> Unit) {
    val checkInHistoryByDate = remember { mutableStateOf<List<CheckInHistory>>(emptyList()) }

    LaunchedEffect(day.date) {
        viewModel.getCheckInHistoryByDate(day.date).collect { history ->
            checkInHistoryByDate.value = history
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = day.position == DayPosition.MonthDate) {
                // 捕获 checkInHistoryByDate 的值
                val history = checkInHistoryByDate.value
                if (history.isNotEmpty()) {
                    onDayClick(day.date)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f),
            fontSize = 16.sp
        )

        // 检查是否有签到记录
        if (checkInHistoryByDate.value.isNotEmpty()) {
            // 检查是否有系统签到
            val hasSystemCheckIn = checkInHistoryByDate.value.any { it.checkInName == "签到" }
            // 检查是否有其他签到系统
            val hasOtherCheckIn = checkInHistoryByDate.value.any { it.checkInName != "签到" }

            // 显示系统签到标志
            if (hasSystemCheckIn) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.Green, shape = CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            // 显示其他签到系统的横线
            if (hasOtherCheckIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // 设置横线宽度为日期格子宽度的 80%
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}