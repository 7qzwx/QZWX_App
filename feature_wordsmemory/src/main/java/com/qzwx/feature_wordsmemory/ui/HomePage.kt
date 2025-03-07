package com.qzwx.feature_wordsmemory.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.components.BottomBar
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import java.util.Calendar

@Composable
fun HomePage(modifier : Modifier = Modifier,
    navController : NavController,
    viewModel : WordViewModel) {
    // 从 ViewModel 中获取单词列表
    val words by viewModel.allWords.collectAsState(initial = emptyList())
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // 获取当前小时（24小时制）
    // 根据时间设置问候语
    val greeting = when (hour) {
        in 6..11  -> "早上好,开始新的一天吧!"
        in 12..17 -> "下午好,背背单词休息会!"
        in 18..23 -> "晚上好,回忆一下单词吧!"
        else      -> "凌晨了，早点休息吧！"
    }

    Scaffold(
        topBar = { TopAppBar(greeting) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addnewwordspage") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Word")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp, start = 26.dp, end = 26.dp)
                    .border(border = BorderStroke(2.dp, color = Color.Gray),
                        shape = RoundedCornerShape(2.dp)),
                horizontalArrangement = Arrangement.Center,
            ) {
                InfoCard(label = "已学单词", count = 30)
                InfoCard(label = "单词库", count = 4)
                InfoCard(label = "学习天数", count = 3)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /*TODO: Start learning action*/ },
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 56.dp, end = 56.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "开始学习")
            }
            Spacer(modifier = Modifier.height(16.dp))
            WordList(words = words)
        }
    }
}

@Composable
fun TopAppBar(greeting : String) {
    TopAppBar(
        title = {
            Text(
                text = greeting,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.h5
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Filter action */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        },
        backgroundColor = MaterialTheme.colors.primarySurface,
        elevation = 4.dp
    )
}

@Composable
fun WordList(words : List<Word>) {
    LazyColumn {
        items(words) { word ->
            WordCard(word = word)
        }
    }
}

@Composable
fun WordCard(word : Word) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = word.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Chip(word.pos)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = word.definition, // 直接显示完整释义
                fontSize = 14.sp,
                color = Color.Gray
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = 0.5f // 示例进度
                )
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Outlined.ExpandMore, contentDescription = "Expand")
            }
        }
    }
}

@Composable
fun Chip(text : String) {
    Box(
        modifier = Modifier
            .background(Color.Blue, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun InfoCard(label : String, count : Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(5.dp)
    ) {
        Text(
            text = label,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
