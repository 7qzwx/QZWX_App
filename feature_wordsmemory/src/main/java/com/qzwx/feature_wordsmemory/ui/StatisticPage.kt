package com.qzwx.feature_wordsmemory.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapWeek
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.*
import com.qzwx.core.QZWXApplication
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// 添加缓存对象
private object StatisticsCache {
    private var wordData : Map<LocalDate, Int> = emptyMap()
    private var lastUpdateTime : Long = 0
    private const val CACHE_DURATION = 5 * 60 * 1000 // 5分钟缓存

    fun getCachedData() : Map<LocalDate, Int>? {
        return if (System.currentTimeMillis() - lastUpdateTime < CACHE_DURATION) {
            wordData
        } else null
    }

    fun updateCache(data : Map<LocalDate, Int>) {
        wordData = data
        lastUpdateTime = System.currentTimeMillis()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatisticPage(wordViewModel : WordViewModel) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showLast6MonthsOnly by rememberSaveable { mutableStateOf(true) }
    var selection by rememberSaveable { mutableStateOf<Pair<LocalDate, Int>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    // 添加动画过渡状态
    val contentAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    val contentScale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val dates = remember {
        val endDate = LocalDate.now()
        val startDate = endDate.minus(12, ChronoUnit.MONTHS)
        Pair(startDate, endDate)
    }
    val (startDate, endDate) = dates
    val showShorterToast = remember {
        { context : Context, message : String, durationMillis : Long ->
            val handler = Handler(Looper.getMainLooper())
            var toast : Toast? = null
            {
                toast?.cancel()
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                toast?.show()
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    toast?.cancel()
                    toast = null
                }, durationMillis)
            }
        }
    }
    var wordData by remember { mutableStateOf<Map<LocalDate, Int>>(emptyMap()) }
    // 优化月度数据计算
    val monthlyData by remember(wordData, showLast6MonthsOnly, endDate) {
        derivedStateOf {
            val sixMonthsAgo = if (showLast6MonthsOnly) {
                endDate.minus(6, ChronoUnit.MONTHS)
            } else null

            wordData.entries
                .asSequence()
                .filter { sixMonthsAgo == null || it.key.isAfter(sixMonthsAgo.minusDays(1)) }
                .groupBy { it.key.yearMonth }
                .mapValues { it.value.sumOf { entry -> entry.value } }
                .toList()
                .sortedBy { it.first }
        }
    }
    val state = rememberHeatMapCalendarState(
        startMonth = startDate.yearMonth,
        endMonth = endDate.yearMonth,
        firstVisibleMonth = endDate.yearMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    val lazyListState = rememberLazyListState()
    // 使用缓存机制加载数据
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            // 先尝试从缓存获取数据
            StatisticsCache.getCachedData()?.let { cachedData ->
                wordData = cachedData
                isLoading = false
                return@LaunchedEffect
            }
            // 如果缓存无效，则从网络获取
            val newData = wordViewModel.getWordsByDate()
            if (newData != wordData) {
                wordData = newData
                StatisticsCache.updateCache(newData)
            }
            error = null
        } catch (e : Exception) {
            error = "加载数据失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                error = null
                                try {
                                    val newData = wordViewModel.getWordsByDate()
                                    if (newData != wordData) {
                                        wordData = newData
                                        StatisticsCache.updateCache(newData)
                                    }
                                } catch (e : Exception) {
                                    error = "重试失败: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    ) {
                        Text("重试")
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isLoading && error == null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = contentAlpha
                        scaleX = contentScale
                        scaleY = contentScale
                    },
                state = lazyListState,
            ) {
                item(key = "heatmap") {
                    Column {
                        Text(
                            "一、热力图",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(26.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        HeatMapCalendar(
                            modifier = Modifier.padding(vertical = 10.dp),
                            state = state,
                            contentPadding = PaddingValues(end = 6.dp),
                            dayContent = { day, week ->
                                val count = wordData[day.date] ?: 0
                                val level = getLevelByCount(count)
                                Day(
                                    day = day,
                                    startDate = startDate,
                                    endDate = endDate,
                                    week = week,
                                    level = level,
                                ) { clicked ->
                                    selection = Pair(clicked, wordData[clicked] ?: 0)
                                    showShorterToast(
                                        QZWXApplication.getContext(),
                                        "日期: ${formatter.format(clicked)}\n单词数量: ${wordData[clicked] ?: 0}",
                                        800
                                    )
                                }
                            },
                            weekHeader = { WeekHeader(it) },
                            monthHeader = { MonthHeader(it, endDate, state) },
                        )
                        CalendarInfo(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 20.dp)
                        )
                    }
                }

                item(key = "divider") {
                    Divider(modifier = Modifier.width(5.dp))
                }

                item(key = "wordcount") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "二、新增单词",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "仅最近六月",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = showLast6MonthsOnly,
                                onCheckedChange = { showLast6MonthsOnly = it }
                            )
                        }
                    }
                }

                item(key = "barchart") {
                    if (monthlyData.isNotEmpty()) {
                        val needScroll = !showLast6MonthsOnly && monthlyData.size > 6
                        val scrollState = rememberScrollState()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .height(300.dp)
                        ) {
                            if (needScroll) {
                                val calculatedWidth = (monthlyData.size * 50).dp

                                Column(
                                    modifier = Modifier
                                        .horizontalScroll(scrollState)
                                        .width(calculatedWidth)
                                ) {
                                    ColumnChart(
                                        modifier = Modifier
                                            .width(calculatedWidth)
                                            .padding(horizontal = 22.dp),
                                        labelProperties = LabelProperties(
                                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                                            enabled = true
                                        ),
                                        indicatorProperties = HorizontalIndicatorProperties(
                                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                                        ),
                                        data = monthlyData.map { (month, count) ->
                                            Bars(
                                                label = "${month.monthValue}月",
                                                values = listOf(
                                                    Bars.Data(
                                                        label = "新增单词数",
                                                        value = count.toDouble(),
                                                        color = SolidColor(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                                                    )
                                                )
                                            )
                                        },
                                        barProperties = BarProperties(
                                            spacing = 3.dp,
                                            thickness = 20.dp
                                        ),
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 4.dp)
                                        .alpha(0.7f)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "向右滚动",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                ColumnChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 22.dp),
                                    data = monthlyData.map { (month, count) ->
                                        Bars(
                                            label = "${month.monthValue}月",
                                            values = listOf(
                                                Bars.Data(
                                                    label = "新增单词数",
                                                    value = count.toDouble(),
                                                    color = SolidColor(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
                                                )
                                            )
                                        )
                                    },
                                    labelProperties = LabelProperties(
                                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                                        enabled = true
                                    ),
                                    indicatorProperties = HorizontalIndicatorProperties(
                                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                                    ),
                                    barProperties = BarProperties(
                                        spacing = 3.dp,
                                        thickness = 20.dp
                                    ),
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "暂无数据",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item(key = "piechart_title") {
                    Text(
                        "三、单词掌握情况",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item(key = "piechart") {
                    WordPieChart(wordViewModel)
                }
            }
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WordPieChart(wordViewModel : WordViewModel) {
    // 优化饼图数据计算
    val pieData by remember(
        wordViewModel.toLearnWordsCount.value,
        wordViewModel.newWordsCount.value,
        wordViewModel.familiarWordsCount.value,
        wordViewModel.masteredWordsCount.value
    ) {
        derivedStateOf {
            listOf(
                Pie(
                    label = "待学习",
                    data = wordViewModel.toLearnWordsCount.value.toDouble(),
                    color = Color(0xFF4FC3F7),
                    selectedColor = Color(0xFF0097A7),
                    selected = false
                ),
                Pie(
                    label = "生疏",
                    data = wordViewModel.newWordsCount.value.toDouble(),
                    color = Color(0xFF9C27B0),
                    selectedColor = Color(0xFF6A1B9A),
                    selected = false
                ),
                Pie(
                    label = "熟悉",
                    data = wordViewModel.familiarWordsCount.value.toDouble(),
                    color = Color(0xFF4CAF50),
                    selectedColor = Color(0xFF388E3C),
                    selected = false
                ),
                Pie(
                    label = "掌握",
                    data = wordViewModel.masteredWordsCount.value.toDouble(),
                    color = Color(0xFFFFEB3B),
                    selectedColor = Color(0xFFFFA000),
                    selected = false
                )
            )
        }
    }
    var selectedPie by remember { mutableStateOf<Pie?>(null) }
    // 优化选中状态计算
    val selectedPieInfo by remember(selectedPie, pieData) {
        derivedStateOf {
            if (selectedPie == null) return@derivedStateOf null
            val totalValue = pieData.sumOf { it.data }
            var startAngle = 0f

            pieData.find { it == selectedPie }?.let { pie ->
                val angle = (pie.data / totalValue * 360f).toFloat()
                Pair(startAngle + angle / 2, pie)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.fillMaxSize(0.8f),
                data = pieData,
                onPieClick = { clickedPie ->
                    selectedPie = if (selectedPie == clickedPie) null else clickedPie
                },
                selectedScale = 1.1f,
                scaleAnimEnterSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Fill
            )
            selectedPieInfo?.let { (angle, pie) ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${pie.label}\n${pie.data.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = pie.color,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            pieData.forEach { pie ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedPie = if (selectedPie == pie) null else pie
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(pie.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${pie.label}: ${pie.data.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (pie.selected) pie.color
                        else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

private fun getLevelByCount(count : Int) : Level {
    return when (count) {
        0         -> Level.Zero
        in 1..4   -> Level.One
        in 5..9   -> Level.Two
        in 10..14 -> Level.Three
        in 15..29 -> Level.Four
        else      -> Level.Five
    }
}

@Composable
private fun CalendarInfo(modifier : Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = "少", fontSize = 10.sp)
        Level.entries.forEach { level ->
            LevelBox(color = level.color)
        }
        Text(text = "多", fontSize = 10.sp)
    }
}

@Composable
private fun Day(
    day : CalendarDay,
    startDate : LocalDate,
    endDate : LocalDate,
    week : HeatMapWeek,
    level : Level,
    onClick : (LocalDate) -> Unit,
) {
    val weekDates = week.days.map { it.date }
    if (day.date in startDate..endDate) {
        LevelBox(level.color) { onClick(day.date) }
    } else if (weekDates.contains(startDate)) {
        LevelBox(Color.Transparent)
    }
}

@Composable
private fun LevelBox(color : Color, onClick : (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(daySize)
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = color)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
    )
}

@Composable
private fun WeekHeader(dayOfWeek : DayOfWeek) {
    Box(
        modifier = Modifier
            .height(daySize)
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = dayOfWeek.displayText(),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 10.sp,
        )
    }
}

fun DayOfWeek.displayText() : String {
    return when (this) {
        DayOfWeek.MONDAY    -> "周一"
        DayOfWeek.TUESDAY   -> "周二"
        DayOfWeek.WEDNESDAY -> "周三"
        DayOfWeek.THURSDAY  -> "周四"
        DayOfWeek.FRIDAY    -> "周五"
        DayOfWeek.SATURDAY  -> "周六"
        DayOfWeek.SUNDAY    -> "周日"
    }
}

@Composable
private fun MonthHeader(
    calendarMonth : CalendarMonth,
    endDate : LocalDate,
    state : HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstVisibleMonth = state.firstVisibleMonth

    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val currentYear = LocalDate.now().year
        val title = if (calendarMonth.yearMonth == firstVisibleMonth) {
            "${month.displayText1()} $currentYear"
        } else {
            month.displayText1()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp, start = 2.dp),
        ) {
            Text(text = title, fontSize = 10.sp)
        }
    }
}

private val daySize = 18.dp

fun YearMonth.displayText1() : String {
    val monthNames = mapOf(
        1 to "一月",
        2 to "二月",
        3 to "三月",
        4 to "四月",
        5 to "五月",
        6 to "六月",
        7 to "七月",
        8 to "八月",
        9 to "九月",
        10 to "十月",
        11 to "十一月",
        12 to "十二月"
    )
    return monthNames[this.month.value] ?: "未知月份"
}

private enum class Level(val color : Color, val threshold : Int) {
    Zero(Color(0xFFEBEDF0), 0),
    One(Color(0xFF9BE9A8), 1),
    Two(Color(0xFF40C463), 5),
    Three(Color(0xFF30A14E), 10),
    Four(Color(0xFF216E3A), 15),
    Five(Color(0xFF004D20), 30)
}

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")