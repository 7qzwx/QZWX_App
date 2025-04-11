package com.qzwx.feature_wordsmemory.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPage(
    modifier : Modifier = Modifier,
    navController : NavController,
    viewModel : WordViewModel
) {
    var isDialogOpen by remember { mutableStateOf(true) }
    var selectedMode by remember { mutableStateOf<String?>(null) }
    var selectedReviewType by remember { mutableStateOf<String?>(null) }
    var isConfirmed by remember { mutableStateOf(false) }
    val reviewModes = listOf("卡片模式", "选择题模式")
    val reviewTypes = listOf("随机", "生疏", "熟悉", "掌握")
    // 显示选择对话框
    if (isDialogOpen) {
        ImprovedStepDialog(
            reviewModes = reviewModes,
            selectedMode = selectedMode,
            onModeSelected = { mode -> selectedMode = mode },
            reviewTypes = reviewTypes,
            selectedReviewType = selectedReviewType,
            onReviewTypeSelected = { type -> selectedReviewType = type },
            onConfirm = {
                if (selectedMode != null && selectedReviewType != null) {
                    isConfirmed = true
                    isDialogOpen = false
                }
            },
            onDismiss = { navController.popBackStack() }
        )
    }
    // 一旦确认，加载过滤后的单词
    if (isConfirmed) {
        val words by viewModel.filteredWords.collectAsState(initial = emptyList())
        var currentIndex by remember { mutableStateOf(0) }
        // 修复：使用Set<String>而不是Set<Any>来存储已完成的单词ID
        var completedWords : Set<Any> by remember { mutableStateOf(setOf<String>()) }
        // 修复：基于选择类型对单词进行高效过滤
        val filteredWords = remember(words, selectedReviewType) {
            when (selectedReviewType) {
                "随机" -> words.shuffled()
                "生疏" -> words.filter { it.tag == "生疏" }
                "熟悉" -> words.filter { it.tag == "熟悉" }
                "掌握" -> words.filter { it.tag == "掌握" }
                else   -> words
            }
        }
        // 修复：确保filteredWords不为空时才计算进度
        val progressPercentage = remember(completedWords, filteredWords) {
            derivedStateOf {
                if (filteredWords.isNotEmpty()) {
                    completedWords.size.toFloat() / filteredWords.size
                } else {
                    0f
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("复习") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent // 让背景透明，使用 Box 叠加渐变色
                    ),
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 进度卡片 - 提升视觉效果
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "学习进度",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${completedWords.size} / ${filteredWords.size}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // 修复：改进进度条动画效果
                val animatedProgress = animateFloatAsState(
                    targetValue = progressPercentage.value,
                    animationSpec = tween(300)
                ).value

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(vertical = 0.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 导航按钮 - 改进样式和可用性
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NavigateBefore, contentDescription = "上一个")
                            Text("上一个", modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    Button(
                        onClick = {
                            if (currentIndex < filteredWords.size - 1) currentIndex++
                        },
                        enabled = currentIndex < filteredWords.size - 1,
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("下一个", modifier = Modifier.padding(end = 4.dp))
                            Icon(Icons.Default.NavigateNext, contentDescription = "下一个")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // 基于选择的模式显示卡片或选择题
                if (filteredWords.isEmpty()) {
                    EmptyStateMessage()
                } else {
                    when (selectedMode) {
                        "卡片模式"   -> {
                            FlashCard(
                                word = filteredWords[currentIndex],
                                onReviewComplete = { tag ->
                                    // 更新单词标签
                                    viewModel.updateWordTag(filteredWords[currentIndex].id, tag)
                                    // 添加到已完成单词集合
                                    completedWords = completedWords + filteredWords[currentIndex].id
                                    // 自动前进到下一个单词
                                    if (currentIndex < filteredWords.size - 1) currentIndex++
                                }
                            )
                        }

                        "选择题模式" -> {
                            TestMode(
                                word = filteredWords[currentIndex],
                                viewModel = viewModel,
                                onTestComplete = {
                                    // 添加到已完成单词集合
                                    completedWords = completedWords + filteredWords[currentIndex].id
                                    // 自动前进到下一个单词
                                    if (currentIndex < filteredWords.size - 1) currentIndex++
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "当前没有单词可复习",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "请选择其他类别或添加新单词",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImprovedStepDialog(
    reviewModes : List<String>,
    selectedMode : String?,
    onModeSelected : (String) -> Unit,
    reviewTypes : List<String>,
    selectedReviewType : String?,
    onReviewTypeSelected : (String) -> Unit,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    var showSecondStep by remember { mutableStateOf(false) }
    // 使用 remember 缓存常用值
    val dialogContainerColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        ),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(16.dp),
        containerColor = dialogContainerColor,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp)
                )
                Text(
                    "选择复习方式",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                // 优化进度指示器动画
                val progress by animateFloatAsState(
                    targetValue = if (showSecondStep) 0.5f else 0f,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Text(
                    "选择复习模式：",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                // 优化复习模式选择的布局
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reviewModes.forEach { mode ->
                        key(mode) {  // 添加 key 以优化重组
                            ElevatedFilterChip(
                                selected = selectedMode == mode,
                                onClick = {
                                    onModeSelected(mode)
                                    showSecondStep = true
                                },
                                label = { Text(mode) },
                                leadingIcon = if (selectedMode == mode) {
                                    {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = primaryColor
                                        )
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                // 优化第二步的动画过渡
                AnimatedVisibility(
                    visible = showSecondStep,
                    enter = fadeIn(
                        animationSpec = tween(200, easing = LinearEasing)
                    ) + expandVertically(
                        animationSpec = tween(200, easing = LinearEasing)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(200, easing = LinearEasing)
                    ) + shrinkVertically(
                        animationSpec = tween(200, easing = LinearEasing)
                    )
                ) {
                    Column {
                        Text(
                            "选择词库来源：",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                        // 优化网格布局
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth(),
                            state = rememberLazyGridState()  // 添加状态管理
                        ) {
                            items(
                                items = reviewTypes,
                                key = { it }  // 添加 key 以优化重组
                            ) { type ->
                                val icon = remember(type) {  // 缓存图标映射
                                    when (type) {
                                        "随机" -> Icons.Default.Shuffle
                                        "生疏" -> Icons.Default.NewReleases
                                        "熟悉" -> Icons.Default.StarHalf
                                        "掌握" -> Icons.Default.Star
                                        else   -> Icons.Default.Folder
                                    }
                                }

                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onReviewTypeSelected(type) },
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = if (selectedReviewType == type)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            dialogContainerColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = if (selectedReviewType == type)
                                                primaryColor
                                            else
                                                onSurfaceVariantColor
                                        )
                                        Text(
                                            type,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (selectedReviewType == type)
                                                primaryColor
                                            else
                                                onSurfaceVariantColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedMode != null && selectedReviewType != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("开始复习")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
fun TestMode(word : Word, viewModel : WordViewModel, onTestComplete : () -> Unit) {
    val currentWordId = word.id
    var randomWords by remember { mutableStateOf<List<Word>>(emptyList()) }
    var isLoaded by remember { mutableStateOf(false) }
    // 加载随机单词用于测试选项
    LaunchedEffect(currentWordId) {
        val randomList = viewModel.getRandomWordsExcluding(currentWordId)
        randomWords = randomList
        isLoaded = true
    }
    // 生成选项，包括正确答案
    val options = remember(randomWords, word) {
        if (isLoaded) {
            val incorrectOptions = randomWords.map { it.definition }
            (incorrectOptions + word.definition).shuffled()
        } else {
            emptyList()
        }
    }
    // 测试状态管理
    var selectedOption by remember(currentWordId) { mutableStateOf<String?>(null) }
    var isAnswered by remember(currentWordId) { mutableStateOf(false) }
    var isCorrect by remember(currentWordId) { mutableStateOf(false) }
    val correctOption = word.definition

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 单词显示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    word.word,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (word.pos.isNotEmpty()) {
                    Text(
                        word.pos,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // 选项按钮
        options.forEach { option ->
            val backgroundColor = when {
                isAnswered && option == correctOption                             -> Color.Green.copy(
                    alpha = 0.7f)

                isAnswered && option == selectedOption && option != correctOption -> Color.Red.copy(
                    alpha = 0.7f)

                else                                                              -> MaterialTheme.colorScheme.primary
            }
            val textColor = when {
                isAnswered && (option == correctOption || option == selectedOption) -> Color.White
                else                                                                -> Color.White
            }

            Button(
                onClick = {
                    if (!isAnswered) {
                        selectedOption = option
                        isAnswered = true
                        isCorrect = option == correctOption
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(option, color = textColor)

                    if (isAnswered) {
                        if (option == correctOption) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "正确",
                                tint = Color.White
                            )
                        } else if (option == selectedOption) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = "错误",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 答案反馈和下一步按钮
        if (isAnswered) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect)
                        Color.Green.copy(alpha = 0.2f)
                    else
                        Color.Red.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCorrect) {
                        Text(
                            "回答正确!",
                            color = Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                        // 自动进入下一题
                        LaunchedEffect(isCorrect) {
                            delay(1000)
                            onTestComplete()
                        }
                    } else {
                        Text(
                            "回答错误!",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "正确答案是: ${correctOption}",
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Button(
                            onClick = { onTestComplete() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("下一题")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashCard(
    word : Word,
    onReviewComplete : (String) -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 改进卡片部分
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { isFlipped = !isFlipped },
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 卡片正面内容
                CardContent(
                    visible = !isFlipped,
                    content = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                word.word,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (word.pos.isNotEmpty()) {
                                Text(
                                    word.pos,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // 翻转提示
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "点击卡片查看释义",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                )
                // 卡片反面内容
                CardContent(
                    visible = isFlipped,
                    content = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                "释义:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                word.definition,
                                fontSize = 20.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            // 翻转提示
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "点击卡片查看单词",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                )
            }
        }
        // 记忆评价按钮
        Text(
            "标记掌握程度:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val buttonModifier = Modifier
                .weight(1f) // 让按钮均分宽度
                .padding(horizontal = 2.dp) // 适当减少间距，避免超出屏幕

            OutlinedButton(
                onClick = { onReviewComplete("生疏") },
                modifier = buttonModifier,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE53935)
                ),
                border = BorderStroke(2.dp, Color.Red)
            ) {
                Icon(
                    Icons.Default.NewReleases,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp) // 适当缩小图标，给文字留空间
                )
                Spacer(modifier = Modifier.width(6.dp)) // 让文字和图标有间距
                Text(
                    "生疏",
                    fontSize = 16.sp, // 提高字号
                    fontWeight = FontWeight.Bold, // 加粗，增强可读性
                    color = Color(0xFFE53935) // 让文字颜色更突出
                )
            }

            OutlinedButton(
                onClick = { onReviewComplete("熟悉") },
                modifier = buttonModifier,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF66BB6A)
                ),
                border = BorderStroke(2.dp, Color(0xFF66BB6A))
            ) {
                Icon(
                    Icons.Default.StarHalf,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "熟悉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF66BB6A)
                )
            }

            OutlinedButton(
                onClick = { onReviewComplete("掌握") },
                modifier = buttonModifier,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFFB300)
                ),
                border = BorderStroke(2.dp, Color(0xFFFFB300))
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "掌握",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300)
                )
            }
        }
    }
}

@Composable
fun CardContent(
    visible : Boolean,
    content : @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
    ) {
        content()
    }
}