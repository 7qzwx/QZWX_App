package com.qzwx.diary

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qzwx.diary.data.DiaryEntry
import com.qzwx.diary.data.DiaryViewModel
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
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
            DiaryTitleList(viewModel) // 显示日记标题列表
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryTitleList(viewModel: DiaryViewModel) {
    val diaryEntries by viewModel.diaryEntries.observeAsState(initial = emptyList())
    val context = LocalContext.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(150.dp),
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

    // 使用指定颜色集中的随机颜色，确保每次重组时都能生成新的颜色
    val colors = listOf(Color(0xFFF0A0A0), Color(0xFFA0F0A0), Color(0xFFA0A0F0), Color(0xFFF0F0A0))
    val backgroundColor = remember(diaryEntry.id) { colors.random() }

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
                .background(backgroundColor)
                .height(150.dp) // 固定卡片高度为150
        ) {
            // 标题部分
            Text(
                text = diaryEntry.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // 添加分割线
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 正文内容部分
            Text(
                text = diaryEntry.content,
                style = MaterialTheme.typography.bodySmall, // 使用较小的字体
                maxLines = 6, // 限制显示行数
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp).weight(1f) // 使用weight占满剩余空间
            )

            // 按钮部分
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End) ,// 使按钮靠右对齐
                horizontalArrangement = Arrangement.End
            ) {
                // 删除按钮
                IconButton(onClick = { showFirstDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_shanchu),
                        contentDescription = "删除",
                        modifier = Modifier.size(16.dp)
                    )
                }
                // 分享按钮
                IconButton(onClick = { onShare() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.svg_fenxiang),
                        contentDescription = "分享",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
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
