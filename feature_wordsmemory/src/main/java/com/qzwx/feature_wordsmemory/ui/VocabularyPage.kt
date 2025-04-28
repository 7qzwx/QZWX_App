package com.qzwx.feature_wordsmemory.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.HourglassEmpty
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyPage(
    navController : NavController,
    viewModel : WordViewModel
) {
    // 控制是否显示释义
    val isGlobalDefinitionVisible = remember { mutableStateOf(false) }
    // 控制是否显示标签栏
    var isTagSectionVisible by remember { mutableStateOf(true) }
    // 标签选择状态
    val selectedTag by viewModel.selectedTag.collectAsState()
    // 排序方式
    val sortOrder = remember { mutableStateOf("插入时间") }
    // 从 ViewModel 获取单词列表
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())
    // 排序后的单词列表
    val sortedWords by remember {
        derivedStateOf {
            when (sortOrder.value) {
                "插入时间" -> words.sortedByDescending { it.id }
                "字母顺序" -> words.sortedBy { it.word.lowercase() }
                "字母逆序" -> words.sortedByDescending { it.word.lowercase() }
                "随机顺序" -> words.shuffled()
                else       -> words
            }
        }
    }
    // 分步加载逻辑
    val loadSize = 30 // 每次加载的单词数量
    var loadedWordsCount by remember { mutableStateOf(loadSize) } // 当前已加载的单词数量
    val displayedWords = sortedWords.take(loadedWordsCount) // 当前显示的单词列表
    // 加载状态
    var isLoading by remember { mutableStateOf(false) }
    // LazyColumn 的滑动状态
    val lazyListState = rememberLazyListState()
    
    // 修改滚动行为
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    // 添加滚动状态
    var isVisible by remember { mutableStateOf(true) }
    var previousOffset by remember { mutableStateOf(0) }
    var accumulatedOffset by remember { mutableStateOf(0f) }
    
    // 监听滚动位置
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset }
            .collect { (_, offset) ->
                val delta = offset - previousOffset
                previousOffset = offset
                
                // 计算累积偏移量
                if (delta > 0) { // 向下滚动
                    accumulatedOffset = 0f // 重置向上滚动的累积量
                    isVisible = false
                } else if (delta < 0) { // 向上滚动
                    accumulatedOffset += -delta // 累积向上滚动的距离
                    if (accumulatedOffset >= 100) { // 当向上滚动超过100像素时显示
                        isVisible = true
                    }
                }
            }
    }

    // 监听滑动位置，触发加载逻辑
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                // 检查是否滑动到底部
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                val totalItemsCount = layoutInfo.totalItemsCount

                if (lastVisibleItemIndex >= totalItemsCount - 1 && !isLoading) {
                    isLoading = true
                    delay(500) // 模拟加载延迟
                    loadedWordsCount += loadSize
                    isLoading = false
                }
            }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                // 标题栏
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "单词表",
                                textAlign = TextAlign.Center,
                                fontSize = 26.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        // 添加标签栏显示/隐藏按钮
                        IconButton(
                            onClick = { isTagSectionVisible = !isTagSectionVisible },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isTagSectionVisible) Icons.Default.FilterListOff else Icons.Default.FilterList,
                                contentDescription = if (isTagSectionVisible) "隐藏标签栏" else "显示标签栏",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // 释义显示/隐藏按钮
                        IconButton(
                            onClick = {
                                isGlobalDefinitionVisible.value = !isGlobalDefinitionVisible.value
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isGlobalDefinitionVisible.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isGlobalDefinitionVisible.value) "隐藏释义" else "显示释义",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                
                // 使用动画可见性控制
                AnimatedVisibility(
                    visible = isTagSectionVisible,
                    enter = fadeIn() + expandVertically(
                        expandFrom = Alignment.Top,
                        initialHeight = { 0 }
                    ),
                    exit = fadeOut() + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        targetHeight = { 0 }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        FilterTagSection(selectedTag) { tag ->
                            viewModel.setSelectedTag(tag)
                        }
                        WordCountAndSortSection(
                            wordCount = words.size,
                            currentSortOrder = sortOrder.value,
                            onSortOrderChanged = { newOrder ->
                                sortOrder.value = newOrder
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // 优化 LazyColumn 性能
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            // 显示已加载的单词
            if (loadedWordsCount < sortedWords.size) {
                items(
                    items = displayedWords,
                    key = { it.id }
                ) { word ->
                    WordItemOptimized(
                        word = word,
                        isGlobalDefinitionVisible = isGlobalDefinitionVisible.value,
                        onTagChanged = { newTag ->
                            viewModel.updateWordTag(word.id, newTag)
                        },
                        onDeleteWord = { wordId ->
                            viewModel.deleteWord(wordId)
                        }
                    )
                }
                // 加载指示器
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                items(
                    items = displayedWords,
                    key = { it.id }
                ) { word ->
                    WordItemOptimized(
                        word = word,
                        isGlobalDefinitionVisible = isGlobalDefinitionVisible.value,
                        onTagChanged = { newTag ->
                            viewModel.updateWordTag(word.id, newTag)
                        },
                        onDeleteWord = { wordId ->
                            viewModel.deleteWord(wordId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterTagSection(selectedTag : String, onTagSelected : (String) -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF64B3F4), Color(0xFFC2E59C)), // 渐变色
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ),
                shape = RoundedCornerShape(12.dp) // 可选，控制圆角
            )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "选择复习状态",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabs = listOf("全部", "生疏", "熟悉", "掌握")
                tabs.forEach { tag ->
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = { onTagSelected(tag) },
                        label = { Text(tag) },
                        border = BorderStroke(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFD5DEE7), // 浅灰蓝
                                    Color(0xFFFFAFBD), // 粉色
                                    Color(0xFFC9FFBF)  // 浅绿色
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f) // 方向可调
                            )
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        leadingIcon = if (selectedTag == tag) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
fun WordCountAndSortSection(
    wordCount : Int,
    currentSortOrder : String,
    onSortOrderChanged : (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "共 $wordCount 词",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        var showSortMenu by remember { mutableStateOf(false) }

        Box {
            FilledTonalButton(
                onClick = { showSortMenu = true },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "排序方式",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(currentSortOrder, style = MaterialTheme.typography.bodySmall)
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                listOf("插入时间", "字母顺序", "字母逆序", "随机顺序").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSortOrderChanged(option)
                            showSortMenu = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordItemOptimized(
    word : Word,
    isGlobalDefinitionVisible : Boolean,
    onTagChanged : (String) -> Unit,
    onDeleteWord : (Int) -> Unit
) {
    // 减少状态变量，使用不可变参数
    var isLocalDefinitionVisible by remember { mutableStateOf(isGlobalDefinitionVisible) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    // 只在全局状态变化时更新本地状态
    LaunchedEffect(isGlobalDefinitionVisible) {
        isLocalDefinitionVisible = isGlobalDefinitionVisible
    }
    // 删除确认对话框 - 只在需要时显示
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除单词 \"${word.word}\" 吗？此操作不可撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWord(word.id)
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
    val checkCirclePainter = rememberVectorPainter(Icons.TwoTone.CheckCircle)
    val hourglassEmptyPainter = rememberVectorPainter(Icons.TwoTone.HourglassEmpty)
    val starPainter = rememberVectorPainter(Icons.TwoTone.Star)
    val deletePainter = rememberVectorPainter(Icons.TwoTone.Delete)
    val swipbgcolor = MaterialTheme.colorScheme.primary
    val onbgcolor = MaterialTheme.colorScheme.onBackground
    // 预计算滑动动作以避免重组
    val swipeActions = remember {
        SwipeActionsData(
            markAsLearned = SwipeAction(
                icon = checkCirclePainter,
                background = Color.Green,
                onSwipe = { onTagChanged("熟悉") }
            ),
            markAsToLearn = SwipeAction(
                icon = hourglassEmptyPainter,
                background = swipbgcolor,
                onSwipe = { onTagChanged("生疏") }
            ),
            markAsFavorite = SwipeAction(
                icon = starPainter,
                background = Color(0xFFFFA000),
                onSwipe = { onTagChanged("掌握") }
            ),
            deleteAction = SwipeAction(
                icon = deletePainter,
                background = Color.Red,
                onSwipe = { showDeleteConfirmation = true }
            )
        )
    }
    // 使用 remember 预计算样式相关值，避免重组时重新计算
    val tagVisualData = remember(word.tag) {
        when (word.tag) {
            "生疏" -> TagVisualData(
                icon = Icons.Default.HourglassEmpty,
                color = swipbgcolor,
                backgroundColor = Color.Gray.copy(alpha = 0.3f),
                textColor = Color.Gray
            )

            "掌握" -> TagVisualData(
                icon = Icons.Default.Star,
                color = Color(0xFFFFA000),
                backgroundColor = Color(0xFFFFA000).copy(alpha = 0.3f),
                textColor = Color(0xFFFFA000)
            )

            "熟悉" -> TagVisualData(
                icon = Icons.Default.CheckCircle,
                color = Color.Green,
                backgroundColor = Color.Green.copy(alpha = 0.3f),
                textColor = Color.Green
            )

            else   -> TagVisualData(
                icon = Icons.Default.HourglassEmpty,
                color = onbgcolor,
                backgroundColor = Color.Gray.copy(alpha = 0.1f),
                textColor = Color.Gray
            )
        }
    }

    SwipeableActionsBox(
        startActions = with(swipeActions) { listOf(markAsToLearn, markAsLearned, deleteAction) },
        endActions = listOf(swipeActions.markAsFavorite)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 单词头部区域
                WordItemHeader(
                    word = word.word,
                    pos = word.pos,
                    tag = word.tag,
                    tagVisualData = tagVisualData
                )

                Spacer(modifier = Modifier.height(12.dp))
                // 定义部分
                DefinitionBox(
                    definition = word.definition,
                    isVisible = isLocalDefinitionVisible,
                    onClick = { isLocalDefinitionVisible = true }
                )
            }
        }
    }
}

@Composable
private fun WordItemHeader(
    word : String,
    pos : String,
    tag : String,
    tagVisualData : TagVisualData
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：单词和图标
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = tagVisualData.color.copy(alpha = 0.1f),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = tagVisualData.icon,
                    contentDescription = "单词状态",
                    tint = tagVisualData.color,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = word,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        // 右侧：词性和标签
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = pos,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = tagVisualData.backgroundColor
            ) {
                Text(
                    text = tag,
                    fontSize = 12.sp,
                    color = tagVisualData.textColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DefinitionBox(
    definition : String,
    isVisible : Boolean,
    onClick : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !isVisible) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = if (isVisible) Alignment.CenterStart else Alignment.Center
    ) {
        if (isVisible) {
            Text(
                text = definition,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "释义已隐藏，点击查看",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// 数据类，用于缓存计算结果
data class SwipeActionsData(
    val markAsLearned : SwipeAction,
    val markAsToLearn : SwipeAction,
    val markAsFavorite : SwipeAction,
    val deleteAction : SwipeAction
)

// 用于缓存标签视觉样式的数据类
data class TagVisualData(
    val icon : ImageVector,
    val color : Color,
    val backgroundColor : Color,
    val textColor : Color
)