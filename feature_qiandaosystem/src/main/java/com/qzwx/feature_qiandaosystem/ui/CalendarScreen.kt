package com.qzwx.feature_qiandaosystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.qzwx.core.room.room_qiandaosystem.CheckInHistory
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(modifier : Modifier = Modifier, viewModel : CheckInViewModel) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusYears(1).withMonth(6) }
    val endMonth = remember { currentMonth.plusYears(1).withMonth(6) }
    val daysOfWeek = daysOfWeek() // Available in the library
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var checkInHistory by remember { mutableStateOf<List<CheckInHistory>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("签到一览") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp), // 设置页面边距
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 热力日历视图
            item {
                HeatMapCalendarView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    viewModel = viewModel,
                    startMonth = startMonth,
                    endMonth = endMonth,
                    currentMonth = currentMonth,
                )
            }
            // 月份标题
            item {
                SimpleCalendarTitle(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                    currentMonth = state.firstVisibleMonth.yearMonth,
                    goToPrevious = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                        }
                    },
                    goToNext = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                        }
                    },
                    goToCurrent = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(currentMonth)
                        }
                    },
                )
            }
            // 日历部分
            item {
                HorizontalCalendar(
                    modifier = Modifier.aspectRatio(1f),
                    state = state,
                    dayContent = { day ->
                        DayContent(day, viewModel, onDayClick = { date ->
                            selectedDate = date
                        }, selectedDate = selectedDate)
                    },
                    monthHeader = {
                        DaysOfWeekTitle(daysOfWeek = daysOfWeek) // Use the title as month header
                    },
                    monthContainer = { _, container ->
                        val configuration = LocalConfiguration.current
                        val screenWidth = configuration.screenWidthDp.dp
                        Box(
                            modifier = Modifier
                                .width(screenWidth * 0.9f)
                                .padding(8.dp)
                                .clip(shape = RoundedCornerShape(8.dp))
                                .border(
                                    color = Color.Black,
                                    width = 1.dp,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            container() // Render the provided container!
                        }
                    }
                )
            }
            // 签到记录显示部分
            item {
                // 当选中的日期改变时，获取签到记录
                LaunchedEffect(selectedDate) {
                    if (selectedDate != null) {
                        checkInHistory = viewModel.getCheckInHistoryByDate(selectedDate!!).first()
                    } else {
                        checkInHistory = emptyList()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp, // 边框宽度
                            color = Color.Black, // 边框颜色
                            shape = RoundedCornerShape(8.dp) // 边框形状
                        )
                        .padding(16.dp) // 内边距
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (selectedDate != null) {
                            if (checkInHistory.isNotEmpty()) {
                                Text(
                                    text = "签到记录: ${selectedDate.toString()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Divider(
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                                Column {
                                    checkInHistory.forEachIndexed { index, history ->
                                        Text(modifier = Modifier.padding(top = 10.dp),
                                            text = " ${history.checkInName}  获得  Exp ${history.experience}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (index < checkInHistory.size - 1) {
                                            Divider(
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.1f),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "今天似乎没有打卡!",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Text(
                                text = "请选择一个日期查看签到记录",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeatMapCalendarView(
    modifier : Modifier,
    viewModel : CheckInViewModel,
    startMonth : YearMonth,
    endMonth : YearMonth,
    currentMonth : YearMonth,
) {
    val state = rememberHeatMapCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    val checkInData = remember { mutableStateOf<Map<LocalDate, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        checkInData.value = viewModel.getCheckInData(startMonth, endMonth)
    }

    HeatMapCalendar(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(end = 6.dp),
        dayContent = { day, _ ->
            val checkInCount = checkInData.value[day.date] ?: 0
            val level = getLevelFromCount(checkInCount)
            LevelBox(level.color)
        },
        weekHeader = { WeekHeader(it) },
        monthHeader = { calendarMonth ->
            MonthHeader(calendarMonth, LocalDate.now(), state)
        },
    )
}

@Composable
private fun LevelBox(color : Color) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = color)
    )
}

@Composable
private fun WeekHeader(dayOfWeek : DayOfWeek) {
    Box(
        modifier = Modifier
            .height(18.dp)
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 10.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthHeader(
    calendarMonth : CalendarMonth,
    endDate : LocalDate,
    state : HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstFullyVisibleMonth by remember {
        derivedStateOf { getMonthWithYear(state.layoutInfo, 18.dp, density) }
    }
    val month = calendarMonth.yearMonth
    val title = if (month == firstFullyVisibleMonth) {
        month.displayText(short = false)
    } else {
        month.displayText(short = true)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp, start = 2.dp),
    ) {
        Text(text = title, fontSize = 10.sp)
    }

}

private fun getMonthWithYear(
    layoutInfo : CalendarLayoutInfo,
    daySize : Dp,
    density : Density,
) : YearMonth? {
    val visibleItemsInfo = layoutInfo.visibleMonthsInfo
    return when {
        visibleItemsInfo.isEmpty()    -> null
        visibleItemsInfo.count() == 1 -> visibleItemsInfo.first().month.yearMonth
        else                          -> {
            val firstItem = visibleItemsInfo.first()
            val daySizePx = with(density) { daySize.toPx() }
            if (
                firstItem.size < daySizePx * 3 ||
                firstItem.offset < layoutInfo.viewportStartOffset &&
                (layoutInfo.viewportStartOffset - firstItem.offset > daySizePx)
            ) {
                visibleItemsInfo[1].month.yearMonth
            } else {
                firstItem.month.yearMonth
            }
        }
    }
}

@Composable
private fun SimpleCalendarTitle(
    modifier : Modifier,
    currentMonth : YearMonth,
    goToPrevious : () -> Unit,
    goToNext : () -> Unit,
    goToCurrent : () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = goToPrevious) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Previous Month"
            )
        }
        Text(
            text = currentMonth.displayText(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row {
            IconButton(onClick = goToCurrent) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Go to Current Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = goToNext) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next Month"
                )
            }
        }
    }
}

@Composable
fun DayContent(
    day : CalendarDay,
    viewModel : CheckInViewModel,
    onDayClick : (LocalDate) -> Unit,
    selectedDate : LocalDate?,
) {
    val checkInHistoryByDate = remember { mutableStateOf<List<CheckInHistory>>(emptyList()) }
    val today = remember { LocalDate.now() }

    LaunchedEffect(day.date) {
        viewModel.getCheckInHistoryByDate(day.date).collect { history ->
            checkInHistoryByDate.value = history
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (day.date == selectedDate) MaterialTheme.colorScheme.primary.copy(0.5f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = day.position == DayPosition.MonthDate) {
                onDayClick(day.date)
            },
        contentAlignment = Alignment.Center
    ) {
        if (day.date == today) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .align(Alignment.Center)
            )
        }

        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.onSurface else Color.Gray.copy(
                alpha = 0.5f
            ),
            fontSize = 16.sp
        )

        if (checkInHistoryByDate.value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(10.dp)
                    .background(Color.Green, shape = CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek : List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

fun YearMonth.displayText(short : Boolean = false) : String {
    return if (short) {
        "${this.monthValue}月"
    } else {
        "${this.year}年${this.monthValue}月"
    }
}

private enum class Level(val color : Color) {
    Zero(Color(0xDDD4D6D9)),
    One(Color(0xFF9BE9A8)),
    Two(Color(0xFF40C463)),
    Three(Color(0xFF30A14E)),
    Four(Color(0xFF216E3A)),
}

private fun getLevelFromCount(count : Int) : Level {
    return when {
        count == 0 -> Level.Zero
        count <= 3 -> Level.One
        count <= 6 -> Level.Two
        count <= 9 -> Level.Three
        else       -> Level.Four
    }
}