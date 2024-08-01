package com.qzwx.diary

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qzwx.diary.data.DiaryEntry
import com.qzwx.diary.data.DiaryViewModel

@Composable
fun DianDiScreen(viewModel: DiaryViewModel) {
    // 获取当前上下文
    val context = LocalContext.current

    // 定义点滴页面内容
    Box(modifier = Modifier.fillMaxSize()) {
        // 包裹 LazyColumn 的 Box，只占据页面的一部分
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // 预留底部空间，避免被悬浮按钮遮挡
        ) {
            DiaryTitleList(viewModel)
        }

        // 在页面右下角添加一个悬浮按钮
        FloatingActionButton(
            onClick = {
                // 创建一个 Intent 用于启动 XieRiJi Activity
                val intent = Intent(context, com.qzwx.diary.ui.XieRiJi::class.java)
                // 启动 XieRiJi Activity
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 设置悬浮按钮的位置为右下角
                .padding(16.dp) // 设置悬浮按钮的内边距，以避免其贴边显示
                .size(56.dp) // 设置悬浮按钮大小
        ) {
            // 使用图片作为悬浮按钮的内容
            Icon(
                painter = painterResource(id = R.drawable.svg_diandi), // 指定按钮的图片资源
                contentDescription = "Floating Button" // 设置图片的描述文字（无障碍功能）
            )
        }
    }
}

@Composable
fun DiaryTitleList(viewModel: DiaryViewModel) {
    // 使用 remember 关键字来确保正确管理状态
    val diaryEntries by viewModel.diaryEntries.observeAsState(initial = emptyList())

    // 使用 LazyColumn 来展示日记标题列表
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(diaryEntries) { diaryEntry ->
            DiaryEntryCard(diaryEntry)
        }
    }
}

@Composable
fun DiaryEntryCard(diaryEntry: DiaryEntry) {
    // 获取当前上下文
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // 创建一个 Intent 用于启动查看日记内容的 Activity
                val intent = Intent(context, com.qzwx.diary.ui.RiJiXiangQing::class.java).apply {
                    putExtra("DIARY_ID", diaryEntry.id) // 假设你有一个日记 ID 字段
                }
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = diaryEntry.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = diaryEntry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
