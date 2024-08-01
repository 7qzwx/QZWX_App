package com.qzwx.diary

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qzwx.diary.data.DiaryEntry
import com.qzwx.diary.data.DiaryViewModel
import kotlinx.coroutines.launch

@Composable
fun DianDiScreen(viewModel: DiaryViewModel) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, start = 15.dp, end = 15.dp, top = 10.dp)
                .border(BorderStroke(2.dp, color = Color(0xFFFDBBED)), shape = RoundedCornerShape(18.dp))
        ) {
            DiaryTitleList(viewModel)
        }

        FloatingActionButton(
            onClick = {
                val intent = Intent(context, com.qzwx.diary.ui.XieRiJi::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.svg_diandi),
                contentDescription = "Floating Button"
            )
        }
    }
}

@Composable
fun DiaryTitleList(viewModel: DiaryViewModel) {
    val diaryEntries by viewModel.diaryEntries.observeAsState(initial = emptyList())
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(diaryEntries) { diaryEntry ->
            DiaryEntryCard(
                diaryEntry = diaryEntry,
                onDelete = { viewModel.deleteDiaryEntry(diaryEntry) },
                onShare = { /* 分享逻辑 */ },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun DiaryEntryCard(
    diaryEntry: DiaryEntry,
    onDelete: (DiaryEntry) -> Unit,
    onShare: () -> Unit,
    viewModel: DiaryViewModel
) {
    val context = LocalContext.current
    var showFirstDialog by remember { mutableStateOf(false) }
    var showSecondDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .border(BorderStroke(2.dp, color = Color(0xFF9898EE)), shape = RoundedCornerShape(8.dp))
            .clickable {
                val intent = Intent(context, com.qzwx.diary.ui.RiJiXiangQing::class.java).apply {
                    putExtra("DIARY_ID", diaryEntry.id)
                }
                context.startActivity(intent)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = diaryEntry.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // 保留编辑图标，但不实现点击事件
                IconButton(onClick = { /* 编辑功能已被删除 */ }, modifier = Modifier.padding(0.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_bianji),
                        contentDescription = "编辑",
                        modifier = Modifier.size(12.dp)
                    )
                }
                // 删除按钮
                IconButton(onClick = { showFirstDialog = true }, modifier = Modifier.padding(0.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_shanchu),
                        contentDescription = "删除",
                        modifier = Modifier.size(12.dp)
                    )
                }
                // 分享按钮
                IconButton(onClick = { onShare() }, modifier = Modifier.padding(0.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_fenxiang),
                        contentDescription = "分享",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Text(
                text = diaryEntry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    // 删除确认对话框
    if (showFirstDialog) {
        AlertDialog(
            onDismissRequest = { showFirstDialog = false },
            title = { Text("确认删除") },
            text = { Text("您确定要删除这条日记吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        showFirstDialog = false
                        showSecondDialog = true
                    }
                ) {
                    Text("是")
                }
            },
            dismissButton = {
                Button(onClick = { showFirstDialog = false }) {
                    Text("否")
                }
            }
        )
    }

    // 第二次确认对话框
    if (showSecondDialog) {
        AlertDialog(
            onDismissRequest = { showSecondDialog = false },
            title = { Text("最终确认", color = Color.Red) },
            text = { Text("七种文学警告：数据无价！你真的要删除吗？此操作不可逆！", color = Color.Red) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDiaryEntry(diaryEntry)
                        Toast.makeText(context, "日记已删除", Toast.LENGTH_SHORT).show()
                        showSecondDialog = false
                    },
                ) {
                    Text("是", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showSecondDialog = false }) {
                    Text("否")
                }
            }
        )
    }
}
