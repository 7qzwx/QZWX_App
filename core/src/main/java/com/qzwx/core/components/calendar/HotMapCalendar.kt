package com.qzwx.core.components.calendar

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
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 热力日历组件的数据类
 * @param title 活动标题
 * @param value 活动值（如：经验值、金额、单词数等）
 * @param date 活动日期
 */
data class ActivityRecord(
    val title : String,
    val value : Int,
    val date : LocalDate
)

/**
 * 日历数据提供接口
 */
interface CalendarDataProvider {
    /**
     * 获取指定日期范围内的活动数据
     * @param startMonth 开始月份
     * @param endMonth 结束月份
     * @return 日期到活动次数的映射
     */
    suspend fun getActivityData(startMonth : YearMonth, endMonth : YearMonth) : Map<LocalDate, Int>

    /**
     * 获取指定日期的活动记录
     * @param date 日期
     * @return 活动记录的流
     */
    fun getActivityRecordsByDate(date : LocalDate) : Flow<List<ActivityRecord>>
}

/**
 * 热力日历视图
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatMapCalendarView(
    modifier : Modifier = Modifier,
    dataProvider : CalendarDataProvider,
    startMonth : YearMonth = YearMonth.now().minusYears(1),
    endMonth : YearMonth = YearMonth.now(),
    currentMonth : YearMonth = YearMonth.now(),
) {
    val state = rememberHeatMapCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    val activityData = remember { mutableStateOf<Map<LocalDate, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        activityData.value = dataProvider.getActivityData(startMonth, endMonth)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        HeatMapCalendar(
            modifier = modifier,
            state = state,
            contentPadding = PaddingValues(end = 6.dp),
            dayContent = { day, _ ->
                val activityCount = activityData.value[day.date] ?: 0
                val level = getLevelFromCount(activityCount)
                LevelBox(level.color)
            },
            weekHeader = { WeekHeader(it) },
            monthHeader = { calendarMonth ->
                MonthHeader(calendarMonth, LocalDate.now(), state)
            },
        )
        CalendarInfo(modifier = Modifier
            .align(Alignment.End)
            .padding(end = 20.dp))
    }
}

/**
 * 完整日历组件，包括热力图、月历和活动详情
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCalendar(
    modifier : Modifier = Modifier,
    dataProvider : CalendarDataProvider,
    title : String = "活动记录",
    showTopBar : Boolean = true,
    detailsTitle : String = "详细记录",
    emptyDetailsMessage : String = "请选择一个日期查看详细记录",
    noRecordsMessage : String = "此日期没有记录"
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(11) }
    val endMonth = remember { currentMonth }
    val daysOfWeek = daysOfWeek()
    
    var visibleMonth by remember { mutableStateOf(currentMonth) }
    
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    
    // 监听日历状态变化
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleMonth }.collect { month ->
            visibleMonth = month.yearMonth
        }
    }
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var activityRecords by remember { mutableStateOf<List<ActivityRecord>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 热力日历视图
            item {
                HeatMapCalendarView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    dataProvider = dataProvider,
                    startMonth = startMonth,
                    endMonth = endMonth,
                    currentMonth = visibleMonth,  // 使用当前可见月份
                )
            }
            // 月份标题
            item {
                MonthNavigationBar(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                    currentMonth = visibleMonth,
                    goToPrevious = {
                        coroutineScope.launch {
                            val prevMonth = visibleMonth.minusMonths(1)
                            if (prevMonth >= startMonth) {
                                state.animateScrollToMonth(prevMonth)
                            }
                        }
                    },
                    goToNext = {
                        coroutineScope.launch {
                            val nextMonth = visibleMonth.plusMonths(1)
                            if (nextMonth <= endMonth) {
                                state.animateScrollToMonth(nextMonth)
                            }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    state = state,
                    dayContent = { day ->
                        DayContent(
                            day = day,
                            dataProvider = dataProvider,
                            onDayClick = { date ->
                                selectedDate = date
                            },
                            selectedDate = selectedDate
                        )
                    },
                    monthHeader = {
                        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
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
                                    width = 2.dp, // 边框宽度
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFA8CABA), // #a8caba
                                            Color(0xFF5D4157)  // #5d4157
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp) // 边框圆角
                                )
                        ) {
                            container()
                        }
                    }
                )
            }
            // 活动记录显示部分
            item {
                LaunchedEffect(selectedDate) {
                    if (selectedDate != null) {
                        dataProvider.getActivityRecordsByDate(selectedDate!!).collect {
                            activityRecords = it
                        }
                    } else {
                        activityRecords = emptyList()
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (selectedDate != null) {
                            if (activityRecords.isNotEmpty()) {
                                Text(
                                    text = "$detailsTitle: $selectedDate",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Divider(
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                                Column {
                                    activityRecords.forEachIndexed { index, record ->
                                        Text(
                                            modifier = Modifier.padding(top = 10.dp),
                                            text = " ${record.title}:  ${record.value}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (index < activityRecords.size - 1) {
                                            Divider(
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.1f
                                                ),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = noRecordsMessage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Text(
                                text = emptyDetailsMessage,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// 显示日历信息的 Composable 函数
@Composable
private fun CalendarInfo(modifier : Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = "少", fontSize = 10.sp) // 显示"少"
        Level.entries.forEach { level ->
            LevelBox(color = level.color) // 显示所有等级的颜色块
        }
        Text(text = "多", fontSize = 10.sp) // 显示"多"
    }
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
private fun MonthNavigationBar(
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
                contentDescription = "上个月"
            )
        }
        Text(
            text = "${currentMonth.year}年${currentMonth.monthValue}月",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row {
            IconButton(onClick = goToCurrent) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "回到当前月",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = goToNext) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "下个月"
                )
            }
        }
    }
}

@Composable
fun DayContent(
    day : CalendarDay,
    dataProvider : CalendarDataProvider,
    onDayClick : (LocalDate) -> Unit,
    selectedDate : LocalDate?,
) {
    val activityRecords = remember { mutableStateOf<List<ActivityRecord>>(emptyList()) }
    val today = remember { LocalDate.now() }

    LaunchedEffect(day.date) {
        dataProvider.getActivityRecordsByDate(day.date).collect { records ->
            activityRecords.value = records
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = if (day.date == selectedDate)
                    MaterialTheme.colorScheme.primary.copy(0.5f)
                else Color.Transparent,   //容器颜色
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
            color = if (day.position == DayPosition.MonthDate)
                MaterialTheme.colorScheme.onSurface
            else Color.Gray.copy(alpha = 0.5f),
            fontSize = 16.sp
        )

        if (activityRecords.value.isNotEmpty()) {
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
        "${this.year}年${this.monthValue}月"
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