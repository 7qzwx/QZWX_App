package com.qzwx.diary.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qzwx.diary.data.DiaryViewModel

class XiuGaiRiJi : ComponentActivity() {
    private val viewModel: DiaryViewModel by viewModels() // 获取 ViewModel 实例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取传递的日记 ID
        val diaryId = intent.getIntExtra("DIARY_ID", -1)

        // 根据 ID 获取日记内容
        viewModel.getDiaryEntryById(diaryId).observe(this) { diaryEntry ->
            setContent {
                if (diaryEntry != null) {
                    EditDiaryScreen(diaryId, diaryEntry.title, diaryEntry.content, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDiaryScreen(diaryId: Int, initialTitle: String, initialContent: String, viewModel: DiaryViewModel) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("日记标题") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("日记内容") },
            modifier = Modifier.fillMaxHeight(0.5f) // 设置高度为总高度的一半
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // 保存修改，调用 ViewModel 更新数据库
            viewModel.updateDiaryEntry(diaryId, title, content)
        }) {
            Text("保存修改")
        }
    }
}
