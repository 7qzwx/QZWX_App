package com.qzwx.feature_wordsmemory.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

@Composable
fun StatisticPage(wordViewModel : WordViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val endDate = remember { LocalDate.now() } // 当前日期作为结束日期
    val startDate = remember { endDate.minus(12, ChronoUnit.MONTHS) } // 12个月前的日期作为开始日期
    var wordData by remember { mutableStateOf<Map<LocalDate, Int>>(emptyMap()) } // 存储数据库中的数据
    var selection by remember { mutableStateOf<Pair<LocalDate, Int>?>(null) } // 存储用户选择的日期和单词数量
    var showLast6MonthsOnly by remember { mutableStateOf(true) } // 添加状态来控制是否只显示6个月数据
    // 初始化日历状态
    val state = rememberHeatMapCalendarState(
        startMonth = startDate.yearMonth,
        endMonth = endDate.yearMonth, // 设置为一个较远的结束月份，允许滑动切换
        firstVisibleMonth = endDate.yearMonth, // 初始可见月份
        firstDayOfWeek = firstDayOfWeekFromLocale(),
    )
    // 获取数据库中每个日期的单词数量
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            wordData = wordViewModel.getWordsByDate()
        }
    }
    // 处理数据，将 wordData 转换成按月份分组的数据
    val monthlyData = remember(wordData, showLast6MonthsOnly) {
        val filteredData = if (showLast6MonthsOnly) {
            val sixMonthsAgo = endDate.minus(6, ChronoUnit.MONTHS)
            wordData.filterKeys { it.isAfter(sixMonthsAgo.minusDays(1)) }
        } else {
            wordData
        }

        filteredData.entries
            .groupBy { it.key.yearMonth }
            .mapValues { it.value.sumOf { entry -> entry.value } }
            .toList()
            .sortedBy { it.first } // 按月份排序
    }

    fun showShorterToast(context : Context, message : String, durationMillis : Long) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
        // 使用 Handler 延迟取消 Toast
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, durationMillis)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Column {
                Text("一、热力图",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    style = MaterialTheme.typography.titleLarge)
                // 显示热力日历
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
                            // Show Toast when day is clicked
                            showShorterToast(QZWXApplication.getContext(),
                                "日期: ${formatter.format(clicked)}\n单词数量: ${wordData[clicked] ?: 0}",
                                800)
                        }
                    },
                    weekHeader = { WeekHeader(it) },
                    monthHeader = { MonthHeader(it, endDate, state) },
                )
                // Move CalendarInfo below heatmap
                CalendarInfo(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 20.dp)
                )
            }
        }

        item { Divider(modifier = Modifier.width(5.dp)) }
        // 标题和选择按钮并排显示
        item {
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
                // 添加选择按钮
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
            // 柱状图
            if (monthlyData.isNotEmpty()) {
                // 确定是否需要滚动
                val needScroll = !showLast6MonthsOnly && monthlyData.size > 6
                val scrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(300.dp) // 固定高度以便于滚动
                ) {
                    // 使用不同的布局方式处理滚动和非滚动情况
                    if (needScroll) {
                        // 需要滚动时的布局
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
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground) // 修改横轴文本颜色
                                    , enabled = true),
                                indicatorProperties = HorizontalIndicatorProperties(
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground) // 修改纵轴文本颜色
                                ),
                                data = monthlyData.map { (month, count) ->
                                    Bars(
                                        label = "${month.monthValue}月", // 格式化为 "x月"
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
                        // 滚动提示指示器
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
                        // 不需要滚动时的布局，直接填充可用宽度
                        ColumnChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp),
                            data = monthlyData.map { (month, count) ->
                                Bars(
                                    label = "${month.monthValue}月", // 格式化为 "x月"
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
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground) // 修改横轴文本颜色
                                , enabled = true),
                            indicatorProperties = HorizontalIndicatorProperties(
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground) // 修改纵轴文本颜色
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
        item {
            Text("三、单词掌握情况",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp),
                style = MaterialTheme.typography.titleLarge)
        }
        item {
            WordPieChart(wordViewModel)
        }
    }
}

//饼状图
@Composable
fun WordPieChart(wordViewModel : WordViewModel) {
    val tolearnWords by wordViewModel.toLearnWordsCount.collectAsState()
    val newWordsCount by wordViewModel.newWordsCount.collectAsState()
    val familiarWordsCount by wordViewModel.familiarWordsCount.collectAsState()
    val masteredWordsCount by wordViewModel.masteredWordsCount.collectAsState()
    var data by remember {
        mutableStateOf(
            listOf(
                Pie(label = "待学习",
                    data = tolearnWords.toDouble(),
                    color = Color(0xFF4FC3F7),
                    selectedColor = Color(0xFF0097A7),
                    selected = false),
                Pie(label = "生疏",
                    data = newWordsCount.toDouble(),
                    color = Color(0xFF9C27B0),
                    selectedColor = Color(0xFF6A1B9A),
                    selected = false),
                Pie(label = "熟悉",
                    data = familiarWordsCount.toDouble(),
                    color = Color(0xFF4CAF50),
                    selectedColor = Color(0xFF388E3C),
                    selected = false),
                Pie(label = "掌握",
                    data = masteredWordsCount.toDouble(),
                    color = Color(0xFFFFEB3B),
                    selectedColor = Color(0xFFFFA000),
                    selected = false),
            )
        )
    }
    var selectedText by remember { mutableStateOf("点击查看详情") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .weight(1f), // 确保饼状图有足够的空间
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.fillMaxSize(),
                data = data,
                onPieClick = {
                    val clickedIndex = data.indexOf(it)
                    data = data.mapIndexed { index, pie ->
                        pie.copy(selected = index == clickedIndex) // 只选中当前点击的部分
                    }
                    selectedText = "${it.label}: ${it.data.toInt()} 个单词"
                },
                selectedScale = 1.2f,
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
        }

        Spacer(modifier = Modifier.width(16.dp)) // 分隔饼图和图例
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            data.forEach { pie ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(pie.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${pie.label}: ${pie.data.toInt()}",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// 根据单词数量获取对应的等级和颜色
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

// 单个日期的显示 Composable 函数
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
        // 如果日期在指定范围内，显示对应等级的颜色块
        LevelBox(level.color) { onClick(day.date) }
    } else if (weekDates.contains(startDate)) {
        // 如果是第一周且包含开始日期，显示透明颜色块以对齐布局
        LevelBox(Color.Transparent)
    }
}

// 显示等级颜色块的 Composable 函数
@Composable
private fun LevelBox(color : Color, onClick : (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(daySize) // 设置固定大小
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp)) // 圆角
            .background(color = color) // 设置背景颜色
            .clickable(enabled = onClick != null) { onClick?.invoke() }, // 可点击
    )
}

// 显示星期头部的 Composable 函数
@Composable
private fun WeekHeader(dayOfWeek : DayOfWeek) {
    Box(
        modifier = Modifier
            .height(daySize) // 设置固定高度
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = dayOfWeek.displayText(),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 10.sp,
        )
    }
}

// 为 DayOfWeek 提供显示文本的方法
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

// 显示月份头部的 Composable 函数
@Composable
private fun MonthHeader(
    calendarMonth : CalendarMonth,
    endDate : LocalDate,
    state : HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstVisibleMonth = state.firstVisibleMonth // 获取当前第一个可见月份

    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val currentYear = LocalDate.now().year // 获取当前年份
        val title = if (calendarMonth.yearMonth == firstVisibleMonth) {
            // 如果是第一个完全可见的月份，显示"一月 2025"
            "${month.displayText1()} $currentYear"
        } else {
            // 其他月份仅显示月份名称，如"二月"
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

// 定义日历中每个日期的大小
private val daySize = 18.dp

/**
 * 返回 YearMonth 的中文月份名称。
 *
 * @return 中文月份名称，如"一月"。
 */
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

// 定义一个枚举类 Level，表示不同的等级，每个等级对应一种颜色
private enum class Level(val color : Color, val threshold : Int) {
    Zero(Color(0xFFEBEDF0), 0),
    One(Color(0xFF9BE9A8), 1),
    Two(Color(0xFF40C463), 5),
    Three(Color(0xFF30A14E), 10),
    Four(Color(0xFF216E3A), 15),
    Five(Color(0xFF004D20), 30)
}

// 日期格式化器
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")